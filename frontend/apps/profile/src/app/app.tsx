import '@frontend/ui/styles/global.css';
import '../styles.css';

import { useParams } from 'react-router-dom';
import { getCurrentUser } from '@frontend/token-lib';

import { QueryClient, QueryClientProvider } from 'react-query';

import UserProfile from '../features/profile/user-profile';

const queryClient = new QueryClient();
const App = () => {
  const { username } = useParams();
  const currentUser = getCurrentUser();
  if (!username || !currentUser) {
    return <div>404</div>;
  }

  return (
    <QueryClientProvider client={queryClient}>
      <div className="">
        <UserProfile username={username ?? currentUser.username} />
      </div>
    </QueryClientProvider>
  );
};

export default App;
