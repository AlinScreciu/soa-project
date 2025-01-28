import React, { forwardRef } from 'react';
import { ControllerRenderProps } from 'react-hook-form';
import { LoginFormSchema } from '../features/login';
import {
  FormControl,
  FormItem,
  FormLabel,
  FormMessage,
} from '@frontend/ui/form';
import { Input } from '@frontend/ui/input';

const UsernameField = forwardRef<
  HTMLInputElement,
  ControllerRenderProps<LoginFormSchema, 'username'>
>((field, ref) => {
  return (
    <FormItem className="w-full">
      <FormLabel>Username</FormLabel>
      <FormControl>
        <Input placeholder="" {...field} ref={ref} />
      </FormControl>
      <FormMessage />
    </FormItem>
  );
});

export default React.memo(UsernameField);
