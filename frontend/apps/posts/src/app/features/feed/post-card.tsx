import React, { forwardRef } from 'react';
import { format } from 'date-fns';
import { cn } from '@frontend/lib/utils'; // or wherever your cn function lives

import type { Post } from '@frontend/queries';
import { Button } from '@frontend/ui/button';
import { Separator } from '@frontend/ui/separator';
import { IoHeartDislike, IoHeart } from 'react-icons/io5';
import { Link } from 'react-router-dom';

interface PostCardProps {
  onLike: (id: number) => void;
  onDislike: (id: number) => void;
  onViewProfile: (userId: string) => void;
  onFollow: (userId: string) => void;
  onUnfollow: (userId: string) => void;
  currentUsername?: string | null;
  className?: string; // NEW: optional className for styling
}

/**
 * Combine your custom props (PostCardProps) with the Post type
 * so we can use them all.
 * Then we pass this combined type to forwardRef.
 */
type PropsWithPost = PostCardProps & Post;

const PostCard = forwardRef<HTMLDivElement, PropsWithPost>(
  (
    {
      className,
      content,
      userId,
      createdAt,
      likesCount,
      displayUsername,
      userAvatar,
      id,
      onLike,
      onUnfollow,
      onFollow,
      onDislike,
      currentUsername,
      liked,
      following,
    },
    ref
  ) => {
    const createdAtDate = new Date(Number(createdAt));
    const createdAtFormatted = format(createdAtDate, 'p · MMM d, yyyy');
    const fromAuthor = userId === currentUsername;

    return (
      <div
        ref={ref}
        className={cn(
          // Default classes go here:
          'text-white shadow-sm w-full p-6',
          // We previously had "bg-black" here, but
          // now we may want to let the parent override the background
          // So you can keep 'bg-black' if you always want black behind
          // or omit it if you want only parent's className.
          'bg-black',
          // Merge the caller's classes
          className
        )}
      >
        <div className="flex justify-between items-start">
          <Link to={`/profile/${userId}`} className="flex items-center gap-4">
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
          </Link>

          {!fromAuthor && (
            <Button
              onClick={() =>
                following ? onUnfollow(userId) : onFollow(userId)
              }
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
  }
);

PostCard.displayName = 'PostCard'; // Good practice for forwardRef

export default PostCard;
