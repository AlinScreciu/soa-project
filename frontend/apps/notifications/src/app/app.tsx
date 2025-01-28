import { useContext, useEffect, useMemo } from 'react';
import {
  NotificationsContext,
  type Notification,
} from '@frontend/notification-lib';

import '../styles.css';
import { cn } from '@frontend/lib/utils';
import { Link } from 'react-router-dom';
function hashCode(str: string) {
  let hash = 0;
  for (let i = 0, len = str.length; i < len; i++) {
    const chr = str.charCodeAt(i);
    hash = (hash << 5) - hash + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return hash;
}

function hashToGradientDirection(hash: number) {
  if (hash % 2 === 0) {
    return 'bg-gradient-to-r';
  }
  if (hash % 3 === 0) {
    return 'bg-gradient-to-l';
  }
  if (hash % 5 === 0) {
    return 'bg-gradient-to-tr';
  }
  return 'bg-gradient-to-t';
}

const NotificationItem: React.FC<{ notification: Notification }> = ({
  notification,
}) => {
  const hasPostId = useMemo(() => {
    return notification.content.includes('!postId');
  }, [notification]);

  return (
    <div
      className={cn(
        'group w-full p-4 mb-2 border border-neutral-700 rounded-md  from-blue-800 to-indigo-900 font-semibold text-3xl  transition-colors cursor-pointer',
        hashToGradientDirection(hashCode(notification.id))
      )}
    >
      {/* Optional small heading or metadata */}

      <div className="text-base">
        {hasPostId ? (
          <div>
            <span>{notification.content.split('!postId=').at(0)?.trim()} </span>
            <Link
              to="/"
              state={{
                postId: notification.content.split('!postId=').at(-1)?.trim(),
              }}
            >
              post
            </Link>
          </div>
        ) : (
          notification.content
        )}
      </div>
    </div>
  );
};

export function App() {
  const { markAllAsRead, notifications } = useContext(NotificationsContext);

  useEffect(() => {
    if (notifications.some((n) => !n.read)) {
      markAllAsRead();
    }
  }, [markAllAsRead, notifications]);

  return (
    <div className="w-[600px] h-full flex flex-col items-center text-white">
      <span className="p-4 text-3xl font-bold border-b-2 border-neutral-800 w-full text-center">
        Notifications
      </span>

      {/* Container for the notification list */}
      <div className="p-4 w-full max-w-2xl overflow-y-auto scrollbar-thin scrollbar-track-black scrollbar-thumb-neutral-800">
        {notifications.map((notification) => (
          <NotificationItem key={notification.id} notification={notification} />
        ))}
      </div>
    </div>
  );
}

export default App;
