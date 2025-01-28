import { getJwtToken } from '@frontend/token-lib';
import axios, { AxiosInstance } from 'axios';
import { FeedType, postSchema } from '../../schemas';

class PostService {
  private axiosInstance: AxiosInstance;

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

  getAllWithFeedType = async (type: FeedType) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };
    const res = await this.axiosInstance.get('/api/posts', {
      headers: headers,
      params: {
        feedType: type,
      },
    });
    return postSchema.array().parse(res.data);
  };

  getPostByUser = async (username: string) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };
    const res = await this.axiosInstance.get('/api/posts', {
      headers: headers,
      params: {
        userId: username,
      },
    });
    return postSchema.array().parse(res.data);
  };
  likePost = async (id: number) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    await this.axiosInstance.post(`/api/posts/${id}/like`, null, {
      headers: headers,
    });
  };

  dislikePost = async (id: number) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };
    await this.axiosInstance.delete(`/api/posts/${id}/like`, {
      headers: headers,
    });
  };

  createPost = async (content: string) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    await this.axiosInstance.post('/api/posts', { content }, { headers });
  };

  getAllById = async (idList: number[]) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    const res = await this.axiosInstance.post(`/api/posts/bulk`, idList, {
      headers,
    });
    return postSchema.array().parse(res.data);
  };
}

const postService = new PostService(process.env.NX_PUBLIC_POST_SERVICE_URL);

export { postService };
