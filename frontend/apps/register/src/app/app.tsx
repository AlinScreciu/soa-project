import wall from '../assets/wall.jpg';
import { useLocation } from 'react-router-dom';
import { SignupForm } from './features/signup';
import { LoginForm } from './features/login';
import { QueryClient, QueryClientProvider } from 'react-query';
import { Toaster } from '@frontend/ui/toaster';

const queryClient = new QueryClient();

export function App() {
  const location = useLocation();
  return (
    <QueryClientProvider client={queryClient}>
      <div className="w-screen h-screen bg-neutral-900 flex items-center justify-center">
        <div className="w-[70rem] h-[40rem] rounded-xl flex">
          <div className="w-full h-full rounded-l-xl flex justify-center items-center bg-white">
            {location.pathname.startsWith('/login') ? (
              <LoginForm />
            ) : location.pathname.startsWith('/signup') ? (
              <SignupForm />
            ) : (
              ''
            )}
          </div>
          <div className="w-full h-full rounded-r-xl">
            <img
              alt="abstract art"
              className="w-full h-full object-fill rounded-r-xl"
              src={wall}
            />
          </div>
        </div>
      </div>
      <Toaster />
    </QueryClientProvider>
  );
}

export default App;
