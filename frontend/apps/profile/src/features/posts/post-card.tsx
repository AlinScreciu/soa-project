import React from 'react';
import { format } from 'date-fns'; // For time formatting
import type { Post } from '@frontend/queries';
import { Button } from '@frontend/ui/button';
import { Separator } from '@frontend/ui/separator';
import { IoHeartDislike, IoHeart } from 'react-icons/io5';

interface PostCardProps {
  onLike: (id: number) => void;
  onDislike: (id: number) => void;
  onViewProfile: (userId: string) => void;
  onFollow: (userId: string) => void;
  onUnfollow: (userId: string) => void;
  currentUsername?: string | null;
}

const PostCard: React.FC<PostCardProps & Post> = ({
  content,
  userId,
  createdAt,
  likesCount,
  displayUsername,
  userAvatar,
  id,
  onLike,
  onViewProfile,
  onUnfollow,
  onFollow,
  onDislike,
  currentUsername,
  liked,
  following,
}) => {
  const createdAtDate = new Date(Number(createdAt));
  const createdAtFormatted = format(createdAtDate, 'p · MMM d, yyyy'); // Format: 9:15 AM · Dec 29, 2024
  const fromAuthor = userId === currentUsername;

  return (
    <div className="text-white shadow-sm w-full bg-black  p-6">
      <div className="flex justify-between items-start">
        <div className="flex items-center gap-4">
          <img
            src={userAvatar}
            alt="user avatar"
            className="size-12 rounded-full"
          />
          <div className="leading-none">
            <div className="text-white font-semibold text-lg">
              {displayUsername}
            </div>
            <div className="text-gray-400">@{userId}</div>
          </div>
        </div>

        {!fromAuthor && (
          <Button
            onClick={() => (following ? onUnfollow(userId) : onFollow(userId))}
            className="text-sm"
          >
            {following ? 'Unfollow' : 'Follow'}
          </Button>
        )}
      </div>
      <div className="pt-2">{content}</div>
      <div className="text-sm text-gray-400 font-extralight pt-2">
        {createdAtFormatted}
      </div>
      <Separator className="bg-gray-600 my-2" />
      <div className="flex justify-end">
        <div className="flex items-center justify-between w-14">
          {liked ? (
            <IoHeartDislike
              onClick={() => onDislike(id)}
              className="text-red-600 hover:cursor-pointer text-2xl transition-transform duration-300 ease-in-out transform hover:scale-110 select-none"
            />
          ) : (
            <IoHeart
              onClick={() => onLike(id)}
              className="text-white hover:cursor-pointer text-2xl transition-all duration-300 ease-in-out transform hover:scale-110 hover:text-red-600 select-none"
            />
          )}
          <div>{likesCount}</div>
        </div>
      </div>
    </div>
  );
};

export default PostCard;
