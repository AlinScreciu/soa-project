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

const PasswordField = forwardRef<
  HTMLInputElement,
  ControllerRenderProps<LoginFormSchema, 'password'>
>((field, ref) => {
  return (
    <FormItem className="w-full">
      <FormLabel>Password</FormLabel>
      <FormControl>
        <Input type="password" {...field} ref={ref} />
      </FormControl>
      <FormMessage />
    </FormItem>
  );
});

export default React.memo(PasswordField);
