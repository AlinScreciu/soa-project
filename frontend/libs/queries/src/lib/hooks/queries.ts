import { useMutation, useQueries, useQuery, useQueryClient } from 'react-query';
import { identityService } from '../services/identity';
import { postService } from '../services/post';
import { useToast } from '@frontend/ui/hooks';
import { useNavigate } from 'react-router-dom';
import { setJwtToken, setCurrentUser } from '@frontend/token-lib';
import { AxiosError } from 'axios';
import { FeedType } from '../../schemas';
import { followService } from '../services/follow';
import { likeService } from '../services/like';

const useLoginMutation = () => {
  const { toast } = useToast();
  const navigate = useNavigate();
  return useMutation({
    mutationFn: identityService.login,
    onSuccess: (data) => {
      setJwtToken(data.token);
      setCurrentUser({
        username: data.username,
        displayName: data.displayName,
        contact: data.contact,
        profilePic: data.profilePic,
      });
      navigate('/', {
        state: {
          token: data.token,
          user: {
            username: data.username,
            displayName: data.displayName,
            contact: data.contact,
            profilePic: data.profilePic,
          },
        },
      });
    },
    onError: (err) => {
      if (err instanceof AxiosError) {
        toast({
          description: `Failed to login: ${err.message}`,
          variant: 'destructive',
        });
      }
    },
  });
};

const useRegisterMutation = () => {
  const { toast } = useToast();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: identityService.register,
    onSuccess: () => {
      navigate('/login');
    },
    onError: (err) => {
      if (err instanceof AxiosError) {
        toast({
          description: `Failed to register: ${err.message}`,
          variant: 'destructive',
        });
      }
    },
  });
};

const useLikePost = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: postService.likePost,
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
    mutationFn: postService.dislikePost,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['posts'],
      });
    },
  });
};

const useGetAllPosts = (type: FeedType) => {
  return useQuery({
    queryKey: ['posts', type],
    queryFn: () => postService.getAllWithFeedType(type),
  });
};

const useGetUserPosts = (username: string) => {
  return useQuery({
    queryKey: ['posts', username],
    queryFn: () => postService.getPostByUser(username),
  });
};
const useCreatePost = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: postService.createPost,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['posts'],
      });
    },
  });
};

const useFollowMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: followService.follow,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['followers'],
      });
      queryClient.invalidateQueries({
        queryKey: ['isFollowing'],
      });
      queryClient.invalidateQueries({
        queryKey: ['posts'],
      });
    },
  });
};

const useUnfollowMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: followService.unfollow,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['followers'],
      });
      queryClient.invalidateQueries({
        queryKey: ['isFollowing'],
      });
      queryClient.invalidateQueries({
        queryKey: ['posts'],
      });
    },
  });
};

const useIsFollowingQuery = (userId: string) => {
  return useQuery({
    queryKey: ['isFollowing', userId],
    queryFn: () => followService.isFollowing(userId),
  });
};

const useFollowersQuery = (userId: string) => {
  return useQuery({
    queryKey: ['followers', userId],
    queryFn: () => followService.getFollowers(userId),
  });
};

const useUserInfoAndPostsQuery = (username: string) => {
  return useQueries([
    {
      queryKey: ['user', username],
      queryFn: () => identityService.getUserInfo(username),
    },
    {
      queryKey: ['posts', username],
      queryFn: () => postService.getPostByUser(username),
    },
  ]);
};

const useUserLikesQuery = (username: string) => {
  return useQuery({
    queryKey: ['likes', username],
    queryFn: () => likeService.getUserLikes(username),
  });
};

const useGetAllPostsById = (idList: number[]) => {
  return useQuery({
    queryKey: ['posts'].concat(idList.map(toString)),
    queryFn: () => postService.getAllById(idList),
  });
};

const useGetPostsLikedByUserQuery = (username: string) => {
  return useQuery({
    queryKey: ['posts', 'likes', username],
    queryFn: async () => {
      const likes = await likeService.getUserLikes(username);

      return postService.getAllById(likes.map((like) => like.id));
    },
  });
};

export {
  useFollowMutation,
  useUnfollowMutation,
  useIsFollowingQuery,
  useFollowersQuery,
  useLoginMutation,
  useRegisterMutation,
  useCreatePost,
  useDislikePost,
  useGetAllPosts,
  useLikePost,
  useGetUserPosts,
  useUserInfoAndPostsQuery,
  useUserLikesQuery,
  useGetAllPostsById,
  useGetPostsLikedByUserQuery,
};
