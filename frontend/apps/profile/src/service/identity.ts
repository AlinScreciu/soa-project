import { getJwtToken } from '@frontend/token-lib';
import axios, { type AxiosInstance } from 'axios';
import { z } from 'zod';
import { useQuery } from 'react-query';

const userSchema = z.object({
  username: z.string(),
  displayName: z.string(),
  contact: z.string().optional(),
  profilePic: z.string(),
});

type User = z.infer<typeof userSchema>;
export type { User };

class IdentityService {
  axiosInstance: AxiosInstance;

  constructor(private readonly baseUrl: string | undefined) {
    this.axiosInstance = axios.create({
      baseURL: baseUrl,
    });

    this.axiosInstance.interceptors.response.use(
      function (response) {
        return response;
      },
      function (error) {
        if (error.status === 401) {
          localStorage.clear();
          window.location.replace('/login');
        }

        return Promise.reject(error);
      }
    );
  }

  getUserInfo = async (username: string) => {
    const headers = {
      Authorization: `Bearer ${getJwtToken()}`,
    };

    const res = await this.axiosInstance.get(`/api/users/${username}`, {
      headers: headers,
    });

    return userSchema.parse(res.data);
  };
}

const identityService = new IdentityService(
  process.env.NX_PUBLIC_IDENTITY_SERVICE_URL
);

const useUserInfoQuery = (username: string) => {
  return useQuery({
    queryKey: ['user', username],
    queryFn: () => identityService.getUserInfo(username),
  });
};

export { useUserInfoQuery, identityService };
