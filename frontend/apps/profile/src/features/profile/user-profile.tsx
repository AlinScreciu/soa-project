import { Link } from 'react-router-dom';
import { FaArrowLeft } from 'react-icons/fa';

import ProfileInfo from './profile-info';
import ProfileFeed from './profile-feed';
import { useUserInfoQuery } from '../../service/identity';

const UserProfile: React.FC<{ username: string }> = ({ username }) => {
  const userQuery = useUserInfoQuery(username);

  if (userQuery.isLoading || userQuery.isFetching || userQuery.isIdle) {
    return <div className="text-white">Loading...</div>;
  }

  if (userQuery.isError)
    return (
      <div className="text-red-800">{JSON.stringify(userQuery.error)}</div>
    );

  const user = userQuery.data;

  return (
    <div className="w-[600px] h-screen flex flex-col items-center text-white">
      <div className="p-4 flex items-center w-full">
        <Link to={'/'}>
          <FaArrowLeft className="text-lg" />
        </Link>
        <div className="px-20 flex flex-col items-center">
          <h1>{user.displayName}</h1>
        </div>
      </div>

      <ProfileInfo user={user} />

      <ProfileFeed username={user.username} />
    </div>
  );
};

export default UserProfile;
