import { z } from 'zod';

const userSchema = z.object({
  username: z.string(),
  displayName: z.string(),
  contact: z.string().optional(),
  profilePic: z.string(),
});
type User = z.infer<typeof userSchema>;

const loginResponseSchema = z
  .object({
    token: z.string(),
  })
  .merge(userSchema);

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

enum FeedType {
  ALL = 'ALL',
  FOLLOWING = 'FOLLOWING',
  SELF = 'SELF',
}

const likeSchema = z.object({
  id: z.number(),
  userId: z.string(),
  postId: z.number(),
});

export type { User, Post };
export { userSchema, loginResponseSchema, postSchema, likeSchema, FeedType };
