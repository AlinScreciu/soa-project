import {
  useLoginMutation,
  useRegisterMutation,
  useCreatePost,
  useDislikePost,
  useGetAllPosts,
  useLikePost,
  useFollowMutation,
  useFollowersQuery,
  useIsFollowingQuery,
  useUnfollowMutation,
  useGetUserPosts,
  useUserInfoAndPostsQuery,
  useGetAllPostsById,
  useGetPostsLikedByUserQuery,
  useUserLikesQuery,
} from './lib/hooks/queries';
import {
  userSchema,
  loginResponseSchema,
  postSchema,
  FeedType,
} from './schemas';
import type { User, Post } from './schemas';

export type { User, Post };

export {
  useGetUserPosts,
  useUserInfoAndPostsQuery,
  useLoginMutation,
  useRegisterMutation,
  useCreatePost,
  useDislikePost,
  useGetAllPosts,
  useLikePost,
  useFollowMutation,
  useFollowersQuery,
  useIsFollowingQuery,
  useUnfollowMutation,
  useGetAllPostsById,
  useGetPostsLikedByUserQuery,
  useUserLikesQuery,
  userSchema,
  loginResponseSchema,
  postSchema,
  FeedType,
};
