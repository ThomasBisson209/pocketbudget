import axios from 'axios';

const TOKEN_KEY = 'pb_token';
const USERNAME_KEY = 'pb_username';

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function getUsername(): string | null {
  return localStorage.getItem(USERNAME_KEY);
}

export function saveAuth(token: string, username: string) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USERNAME_KEY, username);
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USERNAME_KEY);
}

export function isAuthenticated(): boolean {
  return getToken() !== null;
}

const authHttp = axios.create({ baseURL: '/api' });

export async function register(username: string, password: string): Promise<{ token: string; username: string }> {
  const res = await authHttp.post('/auth/register', { username, password });
  return res.data;
}

export async function login(username: string, password: string): Promise<{ token: string; username: string }> {
  const res = await authHttp.post('/auth/login', { username, password });
  return res.data;
}
