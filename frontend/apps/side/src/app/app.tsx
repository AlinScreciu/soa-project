import { cn } from '@frontend/lib/utils';
import { FaBell, FaHome, FaUser } from 'react-icons/fa';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { memo, useCallback, useContext, useMemo } from 'react';
import {
  getCurrentUser,
  removeCurrentUser,
  removeJwtToken,
} from '@frontend/token-lib';
import { Button } from '@frontend/ui/button';
import { MdLogout } from 'react-icons/md';
import { NotificationsContext } from '@frontend/notification-lib';

const items = [
  {
    title: 'Home',
    url: '/',
    icon: FaHome,
  },
  {
    title: 'Profile',
    url: '/profile/',
    icon: FaUser,
  },
  {
    title: 'Notifications',
    url: '/notifications',
    icon: FaBell,
  },
];

const AccountPreview: React.FC<{
  user: ReturnType<typeof getCurrentUser>;
}> = memo(({ user }) => {
  const navigate = useNavigate();
  const logout = useCallback(() => {
    removeCurrentUser();
    removeJwtToken();
    navigate({
      pathname: '/login',
    });
  }, [navigate]);

  if (!user) {
    return null;
  }
  return (
    <div className="flex items-center gap-4 p-2 text-white hover:bg-neutral-900 rounded-[2rem] ">
      <img
        src={user.profilePic}
        alt="profile"
        className="size-12 rounded-full"
      />
      <div className="flex flex-col">
        <span>{user.displayName}</span>
        <span className="text-gray-500">@{user.username}</span>
      </div>
      <Button
        type="button"
        onClick={logout}
        className="justify-self-end m-auto"
        variant={'ghost'}
      >
        <MdLogout />
      </Button>
    </div>
  );
});

const matchPathnameToNavUrl = (pathname: string, url: string) => {
  if (url === '/') return pathname === url;

  return pathname.startsWith(url);
};

export function App() {
  const location = useLocation();
  const currentUser = getCurrentUser();
  const { notifications } = useContext(NotificationsContext);

  // Calculate how many notifications are unread
  const unreadCount = useMemo(
    () => notifications.filter((n) => n.read === false).length,
    [notifications]
  );

  return (
    <div className="w-72 pt-11 flex flex-col h-full p-3">
      <div>
        {items.map((item) => (
          <div
            key={item.url}
            className="flex items-center gap-4 p-4 text-white"
          >
            <item.icon
              className={cn(
                'text-2xl',
                matchPathnameToNavUrl(location.pathname, item.url)
                  ? 'text-blue-500'
                  : 'text-white'
              )}
            />

            <Link
              className="text-xl"
              to={
                item.url === '/profile/'
                  ? `/profile/${currentUser?.username}`
                  : item.url
              }
            >
              <span>{item.title}</span>
            </Link>
            {/* For the Notifications link, show a badge if unread */}
            {item.url === '/notifications' && unreadCount > 0 && (
              <span className=" text-center bg-red-600 text-white rounded-full text-xs px-2.5 py-1">
                {unreadCount}
              </span>
            )}
          </div>
        ))}
      </div>
      <div className="mt-auto pb-5">
        <AccountPreview user={currentUser} />
      </div>
    </div>
  );
}

export default App;
