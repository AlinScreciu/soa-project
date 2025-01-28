import { useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { FormField, Form } from '@frontend/ui/form';
import { Button } from '@frontend/ui/button';
import { Link } from 'react-router-dom';
import UsernameField from '../../components/UsernameField';
import PasswordField from '../../components/PasswordField';
import { useRegisterMutation } from '@frontend/queries';

const signupFormSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1),
});

export type SignupFormSchema = z.infer<typeof signupFormSchema>;

const SignupForm = () => {
  const form = useForm<SignupFormSchema>({
    resolver: zodResolver(signupFormSchema),
    defaultValues: {
      password: '',
      username: '',
    },
  });
  const { mutate } = useRegisterMutation();
  const onSubmit = useCallback(
    (values: SignupFormSchema) => {
      mutate(values);
    },
    [mutate]
  );

  return (
    <div className="w-full h-full ">
      <div className="w-full text-center text-3xl pt-20 ">Sign up</div>
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
              Sign up
            </Button>
          </div>
          <div className="pt-4 text-sm text-gray-600">
            Already have an account?{' '}
            <Link to="/login" className="text-purple-600 hover:underline">
              Login
            </Link>
          </div>
        </form>
      </Form>
    </div>
  );
};

export default SignupForm;
