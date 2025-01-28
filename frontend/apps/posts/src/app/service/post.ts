import { getJwtToken } from '@frontend/token-lib';
import axios, { AxiosError } from 'axios';
import { useMutation, useQuery, useQueryClient } from 'react-query';
import { z } from 'zod';

const postSchema = z.object({
  id: z.number(),
  content: z.string(),
  userId: z.string(),
  displayUsername: z.string(),
  createdAt: z.string(),
  userAvatar: z.string(),
  likesCount: z.number(),
  liked: z.boolean(),
  following: z.boolean(),
});

type Post = z.infer<typeof postSchema>;

const postInstance = axios.create({
  baseURL: process.env.NX_PUBLIC_POST_SERVICE_URL,
});
postInstance.interceptors.response.use(
  function (response) {
    // Any status code that lie within the range of 2xx cause this function to trigger
    // Do something with response data
    return response;
  },
  function (error: AxiosError) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error

    if (error.status === 401) {
      localStorage.clear();
      window.location.replace('/login');
    }

    return Promise.reject(error);
  }
);

const getAllPosts = async (type: FeedType) => {
  const headers = {
    Authorization: `Bearer ${getJwtToken()}`,
  };
  const res = await postInstance.get('/api/posts', {
    headers: headers,
    params: {
      feedType: type,
    },
  });
  return postSchema.array().parse(res.data);
};

const likePost = async (id: number) => {
  const headers = {
    Authorization: `Bearer ${getJwtToken()}`,
  };

  await postInstance.post(`/api/posts/${id}/like`, null, {
    headers: headers,
  });
};
const dislikePost = async (id: number) => {
  const headers = {
    Authorization: `Bearer ${getJwtToken()}`,
  };
  await postInstance.delete(`/api/posts/${id}/like`, {
    headers: headers,
  });
};

const useLikePost = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: likePost,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['posts'],
      });
    },
  });
};

const useDislikePost = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: dislikePost,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['posts'],
      });
    },
  });
};
// Enum for ALL or FOLLOWING posts
export enum FeedType {
  ALL = 'ALL',
  FOLLOWING = 'FOLLOWING',
}

const useGetAllPosts = (type: FeedType) => {
  return useQuery({
    queryKey: ['posts', type],
    queryFn: () => getAllPosts(type),
  });
};

const createPost = async (content: string) => {
  const headers = {
    Authorization: `Bearer ${getJwtToken()}`,
  };

  await postInstance.post('/api/posts', { content }, { headers });
};

const useCreatePost = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createPost,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['posts'],
      });
    },
  });
};

export { useGetAllPosts, Post, useDislikePost, useLikePost, useCreatePost };
