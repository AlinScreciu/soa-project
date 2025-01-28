import { memo, useEffect, useMemo, useRef, useState } from 'react';

import PostCard from '../features/feed/post-card';
import {
  useLikePost,
  useDislikePost,
  useFollowMutation,
  useUnfollowMutation,
} from '@frontend/queries';
import type { Post } from '@frontend/queries';
import { useNavigate } from 'react-router-dom';
import { cn } from '@frontend/lib/utils';

// Bump the highlight duration to 3 seconds
const HIGHLIGHT_DURATION = 3000;

const PostList: React.FC<{
  posts: Post[];
  currentUsername?: string;
  scrollToPost?: string;
}> = memo(({ posts, currentUsername, scrollToPost }) => {
  const { mutate: like } = useLikePost();
  const { mutate: dislike } = useDislikePost();
  const { mutate: follow } = useFollowMutation();
  const { mutate: unfollow } = useUnfollowMutation();

  const navigate = useNavigate();
  const postRefs = useRef<Record<string, HTMLDivElement | null>>({});
  const [highlightedPostId, setHighlightedPostId] = useState<string | null>(
    null
  );

  // Sort posts by date (descending)
  const memoizedPosts = useMemo(() => {
    return [...posts].sort((a, b) => Number(b.createdAt) - Number(a.createdAt));
  }, [posts]);

  useEffect(() => {
    if (!scrollToPost) {
      setHighlightedPostId(null);
      return;
    }

    const targetRef = postRefs.current[scrollToPost];
    if (targetRef) {
      // Scroll into view smoothly, centered
      targetRef.scrollIntoView({ behavior: 'smooth', block: 'center' });

      // Highlight
      setHighlightedPostId(scrollToPost);

      // Remove highlight after a short delay
      const timer = setTimeout(() => {
        setHighlightedPostId(null);
        // Clear out scrollToPost from router state
        navigate('.', { replace: true, state: {} });
      }, HIGHLIGHT_DURATION);

      return () => clearTimeout(timer);
    }
  }, [scrollToPost, navigate]);

  return (
    <div className="w-full flex flex-col divide-y divide-neutral-700 overflow-y-scroll scrollbar-none">
      {memoizedPosts.map((post) => {
        const isHighlighted = highlightedPostId === String(post.id);

        return (
          <PostCard
            key={post.id}
            ref={(el) => (postRefs.current[post.id] = el)}
            onLike={(id) => like(id)}
            onDislike={(id) => dislike(id)}
            onViewProfile={() => console.log('view profile')}
            onFollow={follow}
            onUnfollow={unfollow}
            currentUsername={currentUsername}
            // Here's where we make it look nicer:
            className={cn(
              'transition-colors duration-300 ease-in-out',
              isHighlighted
                ? // Animate a pulsing teal background + ring
                  'animate-pulse'
                : 'bg-black'
            )}
            {...post}
          />
        );
      })}
    </div>
  );
});

export default PostList;
