import * as React from 'react';
import '../styles.css';
import '@frontend/ui/styles/global.css';
import { getJwtToken } from '@frontend/token-lib';
import { Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { NotificationsProvider } from '@frontend/notification-lib';

const Notifications = React.lazy(() => import('notifications/Module'));

const Profile = React.lazy(() => import('profile/Module'));

const Side = React.lazy(() => import('side/Module'));

const Register = React.lazy(() => import('register/Module'));
const Posts = React.lazy(() => import('posts/Module'));

export function App() {
  const navigate = useNavigate();
  const { pathname, state } = useLocation();
  const jwtToken = getJwtToken() ?? state?.token;

  React.useEffect(() => {
    if (
      jwtToken == null &&
      !pathname.startsWith('/signup') &&
      !pathname.startsWith('/login')
    ) {
      navigate('/login', {
        state: {
          callbackUrl: '/',
        },
      });
    }
  }, [jwtToken, navigate, pathname]);

  return (
    <React.Suspense fallback={null}>
      <NotificationsProvider>
        <div className="w-screen h-screen fixed bg-black flex justify-center pr-72">
          <div className="border-r border-neutral-800">
            <Side />
          </div>
          <div className="border-r border-neutral-800 ">
            <Routes>
              <Route path="/" element={<Posts />} />
              <Route path="/notifications" element={<Notifications />} />
              <Route path="/profile/:username" element={<Profile />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="/login" element={<Register />} />
              <Route path="/register" element={<Register />} />
              <Route path="/signup" element={<Register />} />
            </Routes>
          </div>
        </div>
      </NotificationsProvider>
    </React.Suspense>
  );
}

export default App;
