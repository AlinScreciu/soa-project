import { memo, useMemo } from 'react';

import {
  useLikePost,
  useDislikePost,
  useFollowMutation,
  useUnfollowMutation,
} from '@frontend/queries';
import type { Post } from '@frontend/queries';
import PostCard from './post-card';

const PostList: React.FC<{ posts: Post[]; currentUsername?: string }> = memo(
  ({ posts, currentUsername }) => {
    const { mutate: like } = useLikePost();
    const { mutate: dislike } = useDislikePost();

    const { mutate: follow } = useFollowMutation();
    const { mutate: unfollow } = useUnfollowMutation();
    const memoizedPosts = useMemo(() => {
      // Sort posts by date, most recent first
      return posts.sort((a, b) => Number(b.createdAt) - Number(a.createdAt));
    }, [posts]);
    return (
      <div className="w-full flex flex-col divide-y divide-neutral-700 overflow-y-scroll  scrollbar-none ">
        {memoizedPosts.map((post) => (
          <PostCard
            key={post.id}
            onLike={(id) => like(id)}
            onDislike={(id) => dislike(id)}
            onViewProfile={() => console.log('view profile')}
            onFollow={follow}
            onUnfollow={unfollow}
            currentUsername={currentUsername}
            {...post}
          />
        ))}
      </div>
    );
  }
);

export default PostList;
