import { cn } from '@frontend/lib/utils';
import {
  useGetPostsLikedByUserQuery,
  useGetUserPosts,
} from '@frontend/queries';
import { Button } from '@frontend/ui/button';
import React, { useState } from 'react';
import PostList from '../posts/post-list';
import { getCurrentUser } from '@frontend/token-lib';

enum ProfileFeedType {
  POSTS = 'POSTS',
  LIKES = 'LIKES',
}

const FeedSelector: React.FC<{
  feedType: ProfileFeedType;
  setFeedType: (feedType: ProfileFeedType) => void;
}> = ({ feedType, setFeedType }) => {
  return (
    <div className="flex w-full justify-around border-y border-neutral-800 p-4 ">
      {Object.values(ProfileFeedType).map((t) => (
        <Button
          key={t}
          type="button"
          onClick={() => setFeedType(t)}
          className=" hover:bg-neutral-600"
          variant={'ghost'}
          size={'sm'}
        >
          <span
            className={cn(
              'text-xl capitalize font-semibold text-white',
              feedType === t && 'text-blue-500'
            )}
          >
            {t.toLowerCase()}
          </span>
        </Button>
      ))}
    </div>
  );
};

const PostFeed: React.FC<{
  username: string;
}> = ({ username }) => {
  const postQuery = useGetUserPosts(username);
  const currentUser = getCurrentUser();
  if (postQuery.isFetching || postQuery.isLoading || postQuery.isIdle) {
    return <>loading...</>;
  }

  if (postQuery.isError) {
    return <>{JSON.stringify(postQuery.error)}</>;
  }

  const posts = postQuery.data;

  return <PostList posts={posts} currentUsername={currentUser?.username} />;
};

const LikesFeed: React.FC<{
  username: string;
}> = ({ username }) => {
  const postQuery = useGetPostsLikedByUserQuery(username);
  const currentUser = getCurrentUser();
  if (postQuery.isFetching || postQuery.isLoading || postQuery.isIdle) {
    return <>loading...</>;
  }

  if (postQuery.isError) {
    return <>{JSON.stringify(postQuery.error)}</>;
  }

  const posts = postQuery.data;

  return <PostList posts={posts} currentUsername={currentUser?.username} />;
};

const ProfileFeed: React.FC<{
  username: string;
}> = ({ username }) => {
  const [feedType, setFeedType] = useState(ProfileFeedType.POSTS);

  return (
    <div className="w-full flex flex-col h-full">
      <FeedSelector feedType={feedType} setFeedType={setFeedType} />
      <div className="flex-1 w-full overflow-y-scroll  scrollbar-none pb-10">
        {feedType === ProfileFeedType.POSTS ? (
          <PostFeed username={username} />
        ) : (
          <LikesFeed username={username} />
        )}
      </div>
    </div>
  );
};

export default ProfileFeed;
