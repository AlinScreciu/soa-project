import { useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { FormField, Form } from '@frontend/ui/form';
import { Button } from '@frontend/ui/button';
import { Link } from 'react-router-dom';
import UsernameField from '../../components/UsernameField';
import PasswordField from '../../components/PasswordField';
import { useLoginMutation } from '@frontend/queries';

const loginFormSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1),
});

export type LoginFormSchema = z.infer<typeof loginFormSchema>;

const LoginForm = () => {
  const form = useForm<LoginFormSchema>({
    resolver: zodResolver(loginFormSchema),
    defaultValues: {
      password: '',
      username: '',
    },
  });

  const { mutate } = useLoginMutation();

  const onSubmit = useCallback(
    (values: LoginFormSchema) => {
      mutate(values);
    },
    [mutate]
  );

  return (
    <div className="w-full h-full ">
      <div className="w-full text-center text-3xl pt-20 ">Login</div>
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="space-y-2 p-6 pt-20 w-full flex flex-col items-center"
        >
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => <UsernameField {...field} />}
          />
          <FormField
            control={form.control}
            name="password"
            render={({ field }) => <PasswordField {...field} />}
          />
          <div className="pt-4">
            <Button
              className="bg-purple-600 text-white w-fit"
              type="submit"
              size={'lg'}
            >
              Login
            </Button>
          </div>
          <div className="pt-4 text-sm text-gray-600">
            Don&rsquo;t have an account?{' '}
            <Link to="/signup" className="text-purple-600 hover:underline">
              Sign up
            </Link>
          </div>
        </form>
      </Form>
    </div>
  );
};

export default LoginForm;
