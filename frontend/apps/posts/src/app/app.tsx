import '../styles.css';
import Feed from './features/feed/feed';
import { QueryClient, QueryClientProvider } from 'react-query';
import { ThemeProvider, useTheme } from './components/theme-provider';
import { memo, useLayoutEffect, useState } from 'react';
import PostForm from './features/post-form/form';
import { FeedType } from './service/post';
import { cn } from '@frontend/lib/utils';
import { getCurrentUser } from '@frontend/token-lib';
import React from 'react';
import { User } from '@frontend/queries';

const FeedTypeSwitcher: React.FC<{
  setFeedType: (type: FeedType) => void;
  feedType: FeedType;
}> = memo(({ setFeedType, feedType }) => {
  return (
    <div className="flex gap-4 py-4 bg-black w-full text-white text-xl justify-around ">
      <button
        className={cn(feedType === FeedType.ALL && 'border-b-2 border-white')}
        onClick={() => {
          setFeedType(FeedType.ALL);
        }}
      >
        ALL
      </button>
      <button
        className={cn(
          feedType === FeedType.FOLLOWING && 'border-b-2 border-white'
        )}
        onClick={() => {
          setFeedType(FeedType.FOLLOWING);
        }}
      >
        FOLLOWING
      </button>
    </div>
  );
});

const queryClient = new QueryClient();

const CurrentUserContext = React.createContext<User | null>(null);

export function App() {
  const { setTheme } = useTheme();
  const currentUser = getCurrentUser();
  const [feedType, setFeedType] = useState(FeedType.ALL);
  useLayoutEffect(() => {
    setTheme('dark');
  }, [setTheme]);

  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider defaultTheme="dark" storageKey="ui-theme">
        <CurrentUserContext.Provider value={currentUser}>
          <div className="w-[600px] bg-neutral-900 h-full flex flex-col items-center">
            <FeedTypeSwitcher setFeedType={setFeedType} feedType={feedType} />
            <PostForm />
            <Feed feedType={feedType} />
          </div>
        </CurrentUserContext.Provider>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export const useCurrentUser = () => {
  const user = React.useContext(CurrentUserContext);
  return user;
};

export default App;
