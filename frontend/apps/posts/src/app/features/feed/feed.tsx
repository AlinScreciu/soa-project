import { FeedType, useGetAllPosts } from '../../service/post';
import { AxiosError } from 'axios';
import { memo } from 'react';
import { PostList } from '../../components/index';
import { useCurrentUser } from '../../app';
import { useLocation } from 'react-router-dom';

const Feed: React.FC<{
  feedType: FeedType;
}> = memo(({ feedType }) => {
  const query = useGetAllPosts(feedType);
  const currentUser = useCurrentUser();
  const { state } = useLocation();
  const scrollToPost = state?.postId;

  if (query.isLoading) return <div>Loading..</div>;

  if (query.isError) return <div>{(query.error as AxiosError).message}</div>;

  if (!query.isSuccess) return <div>Failed</div>;

  const posts = query.data;
  return (
    <PostList
      posts={posts}
      currentUsername={currentUser?.username}
      scrollToPost={scrollToPost}
    />
  );
});

export default Feed;
