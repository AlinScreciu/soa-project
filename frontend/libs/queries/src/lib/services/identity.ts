import { getJwtToken } from '@frontend/token-lib';
import axios, { type AxiosInstance } from 'axios';

import { loginResponseSchema, userSchema } from '../../schemas';

class IdentityService {
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

  getUserInfo = async (username: string) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    const res = await this.axiosInstance.get(`/api/users/${username}`, {
      headers: headers,
    });

    return userSchema.parse(res.data);
  };

  login = async (data: { username: string; password: string }) => {
    const res = await this.axiosInstance.post('/auth/login', data);
    return loginResponseSchema.parse(res.data);
  };

  register = (data: { username: string; password: string }) => {
    return this.axiosInstance.post('/auth/register', {
      ...data,
      displayName: data.username,
      contact: '',
    });
  };
}

const identityService = new IdentityService(
  process.env.NX_PUBLIC_IDENTITY_SERVICE_URL
);

export { identityService };
