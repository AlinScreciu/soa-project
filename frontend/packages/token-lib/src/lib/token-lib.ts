import { z } from 'zod';

const userSchema = z.object({
  username: z.string(),
  displayName: z.string(),
  contact: z.string().optional(),
  profilePic: z.string(),
});

export function tokenLib(): string {
  return 'token-lib';
}

const JWT_TOKEN_SESSION_STORAGE_KEY = 'soa-jwt-auth-token';

const CURRENT_USER_SESSION_STORAGE_KEY = 'soa-current-user';

export function getJwtToken() {
  return localStorage.getItem(JWT_TOKEN_SESSION_STORAGE_KEY);
}

export function setJwtToken(token: string) {
  localStorage.setItem(JWT_TOKEN_SESSION_STORAGE_KEY, token);
}

export function getCurrentUser() {
  const user = localStorage.getItem(CURRENT_USER_SESSION_STORAGE_KEY);
  return user ? userSchema.parse(JSON.parse(user)) : null;
}

export function setCurrentUser(user: unknown) {
  localStorage.setItem(CURRENT_USER_SESSION_STORAGE_KEY, JSON.stringify(user));
}

export function removeCurrentUser() {
  localStorage.removeItem(CURRENT_USER_SESSION_STORAGE_KEY);
}

export function removeJwtToken() {
  localStorage.removeItem(JWT_TOKEN_SESSION_STORAGE_KEY);
}
