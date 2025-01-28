import { User } from '@frontend/queries';
import React from 'react';

const ProfileInfo: React.FC<{ user: User }> = ({ user }) => {
  return (
    <div className="flex flex-col w-full overflow-visible ">
      {/* Banner / header area */}
      <div className="relative w-full h-52 -z-10">
        <img
          className="h-52 w-full"
          src="https://picsum.photos/600/200"
          alt="banner-pic"
        />
      </div>

      {/* Profile section (pulled upwards by negative margin) */}
      <div className="flex flex-col p-4 -mt-24 gap-4">
        <img
          className="size-40 rounded-full border-4 border-neutral-900 object-cover"
          src={user.profilePic}
          alt="Profile"
        />
        <div>
          <p className="font-bold text-2xl">{user.displayName}</p>
          <p className="text-gray-400">@{user.username}</p>
        </div>
      </div>
    </div>
  );
};

export default ProfileInfo;
