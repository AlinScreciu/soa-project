import { useQueries } from 'react-query';

import type { FeedType } from '../service/post';
import { identityService } from '../service/identity';
import { getAllPosts } from '../service/post';

const useUserInfoAndPostsQuery = (username: string, feedType: FeedType) => {
  return useQueries([
    {
      queryKey: ['user', username],
      queryFn: () => identityService.getUserInfo(username),
    },
    {
      queryKey: ['posts', feedType],
      queryFn: () => getAllPosts(feedType),
    },
  ]);
};

export { useUserInfoAndPostsQuery };
