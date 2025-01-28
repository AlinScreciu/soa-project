import React, {
  createContext,
  useState,
  useEffect,
  useCallback,
  useRef,
} from 'react';
import { Stomp, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getJwtToken } from '@frontend/token-lib';

// We'll store an array of notifications or at least an unread count
export interface Notification {
  content: string;
  read: boolean;
  id: string;
  // ... other fields if needed
}

interface NotificationsContextValue {
  notifications: Notification[];
  markAllAsRead: () => void;
}

export const NotificationsContext = createContext<NotificationsContextValue>({
  notifications: [],
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  markAllAsRead: () => {},
});

const notificationServiceUrl = process.env
  .NX_PUBLIC_NOTIFICATION_SERVICE_URL as string;

export const NotificationsProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const subscriptionRef = useRef<StompSubscription>();

  // WebSocket / STOMP connection logic
  useEffect(() => {
    const stompClient = Stomp.over(() => {
      return new SockJS(`${notificationServiceUrl}/ws`);
    });

    stompClient.connect(
      { Authorization: `Bearer ${getJwtToken()}` },
      () => {
        subscriptionRef.current = stompClient.subscribe(
          '/user/queue/notifications',
          (message) => {
            const newNotification: Notification = {
              read: false,
              content: message.body,
              id: message.headers['message-id'],
            };

            // Assume server sets read=false by default
            setNotifications((prev) => [newNotification, ...prev]);
          }
        );
      },
      (error: unknown) => {
        console.error('STOMP connection error:', error);
      }
    );

    return () => {
      if (subscriptionRef.current) subscriptionRef.current.unsubscribe();
      stompClient.disconnect();
    };
  }, []);

  const markAllAsRead = useCallback(() => {
    // 1) Send an update to the server if needed or just mark locally
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));

    // Optionally call an API to persist that "read" state, or send a STOMP message
  }, []);

  return (
    <NotificationsContext.Provider value={{ notifications, markAllAsRead }}>
      {children}
    </NotificationsContext.Provider>
  );
};
