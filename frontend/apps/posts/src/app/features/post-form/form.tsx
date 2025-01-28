import React, { forwardRef, useCallback } from 'react';
import { FormField, Form, FormItem, FormControl } from '@frontend/ui/form';
import { zodResolver } from '@hookform/resolvers/zod';
import { ControllerRenderProps, useForm } from 'react-hook-form';
import { Input } from '@frontend/ui/input';

import { z } from 'zod';
import { Button } from '@frontend/ui/button';
import { useCreatePost } from '../../service/post';

const postFormSchema = z.object({
  content: z.string().min(1),
});

export type PostFormSchema = z.infer<typeof postFormSchema>;

const ContentField = forwardRef<
  HTMLInputElement,
  ControllerRenderProps<PostFormSchema, 'content'> & {
    onKeyPress: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  }
>(({ onKeyPress, ...field }, ref) => {
  return (
    <FormItem className="w-full h-full">
      <FormControl>
        <Input
          placeholder="Post something!"
          className="border-0 focus:border-0 text-xl focus-visible:ring-0"
          {...field}
          onKeyUp={(e) => onKeyPress(e)}
          ref={ref}
          autoComplete="off"
        />
      </FormControl>
    </FormItem>
  );
});

const PostForm = () => {
  const form = useForm<PostFormSchema>({
    resolver: zodResolver(postFormSchema),
    defaultValues: {
      content: '',
    },
  });
  const { reset } = form;
  const { mutate: createPost } = useCreatePost();

  const onSubmit = useCallback(
    ({ content }: PostFormSchema) => {
      createPost(content);
      reset();
    },
    [createPost, reset]
  );

  return (
    <div className="w-full border-black border-b p-6 text-white text-2xl flex flex-col">
      <Form {...form}>
        <FormField
          control={form.control}
          name="content"
          render={({ field }) => (
            <ContentField
              onKeyPress={(e) => {
                if (e.key === 'Enter') {
                  form.handleSubmit(onSubmit)();
                }
              }}
              {...field}
            />
          )}
        />
        <Button
          className="w-fit self-end"
          variant={'default'}
          type="submit"
          onClick={form.handleSubmit(onSubmit)}
        >
          Post
        </Button>
      </Form>
    </div>
  );
};

export default PostForm;
