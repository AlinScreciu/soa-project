import { getJwtToken } from '@frontend/token-lib';
import axios, { AxiosInstance } from 'axios';

import { z } from 'zod';

class FollowService {
  axiosInstance: AxiosInstance;

  constructor(private readonly baseUrl: string | undefined) {
    this.axiosInstance = axios.create({
      baseURL: baseUrl,
    });

    this.axiosInstance.interceptors.response.use(
      function (response) {
        return response;
      },
      function (error) {
        if (error.status === 401) {
          localStorage.clear();
          window.location.replace('/login');
        }

        return Promise.reject(error);
      }
    );
  }

  follow = async (userId: string) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    await this.axiosInstance.post(`/api/follow/${userId}/follow`, null, {
      headers: headers,
    });
  };

  unfollow = async (userId: string) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    await this.axiosInstance.delete(`/api/follow/${userId}/follow`, {
      headers: headers,
    });
  };

  isFollowing = async (userId: string) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    const res = await this.axiosInstance.get(
      `/api/follow/${userId}/is-following`,
      {
        headers: headers,
      }
    );

    return z.boolean().parse(res.data);
  };

  getFollowers = async (userId: string) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    const res = await this.axiosInstance.get(
      `/api/follow/${userId}/followers`,
      {
        headers: headers,
      }
    );

    return z.array(z.string()).parse(res.data);
  };
}

const followService = new FollowService(
  process.env.NX_PUBLIC_FOLLOW_SERVICE_URL
);

export { followService };
