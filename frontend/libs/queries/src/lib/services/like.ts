import { getJwtToken } from '@frontend/token-lib';
import axios, { AxiosInstance } from 'axios';
import { likeSchema } from '../../schemas';

class LikeService {
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

  getUserLikes = async (username: string) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    const res = await this.axiosInstance.get(`/api/likes/user/${username}`, {
      headers: headers,
    });

    return likeSchema.array().parse(res.data);
  };
}

const likeService = new LikeService(process.env.NX_PUBLIC_POST_SERVICE_URL);

export { likeService };
