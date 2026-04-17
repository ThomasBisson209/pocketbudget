import axios from 'axios';
import { clearAuth, getToken } from './auth';
import type { Account, BalanceHistory, Budget, ChangePasswordRequest, CreateAccountRequest, CreateBudgetRequest, CreateTransactionRequest, Dashboard, Transaction, UserProfile } from './types';

const http = axios.create({ baseURL: '/api' });

http.interceptors.request.use(config => {
  const token = getToken();
  if (token) config.headers['Authorization'] = `Bearer ${token}`;
  return config;
});

http.interceptors.response.use(
  res => res,
  err => {
    if (err?.response?.status === 401) { clearAuth(); window.location.reload(); }
    return Promise.reject(err);
  }
);

export const accountApi = {
  getAll: () => http.get<Account[]>('/accounts').then(r => r.data),
  create: (data: CreateAccountRequest) => http.post<Account>('/accounts', data).then(r => r.data),
  delete: (id: string) => http.delete(`/accounts/${id}`),
  getBalanceHistory: (id: string, month: number, year: number) =>
    http.get<BalanceHistory>(`/accounts/${id}/balance-history/month/${month}/year/${year}`).then(r => r.data),
};

export const budgetApi = {
  getAll: () => http.get<Budget[]>('/budgets').then(r => r.data),
  getByMonth: (month: number, year: number) =>
    http.get<Budget[]>(`/budgets/month/${month}/year/${year}`).then(r => r.data),
  create: (data: CreateBudgetRequest) => http.post<Budget>('/budgets', data).then(r => r.data),
  delete: (id: string) => http.delete(`/budgets/${id}`),
};

export const transactionApi = {
  getAll: () => http.get<Transaction[]>('/transactions').then(r => r.data),
  getByAccount: (accountId: string) => http.get<Transaction[]>(`/transactions/account/${accountId}`).then(r => r.data),
  create: (data: CreateTransactionRequest) => http.post<Transaction>('/transactions', data).then(r => r.data),
};

export const dashboardApi = {
  get: (month: number, year: number) =>
    http.get<Dashboard>(`/dashboard/month/${month}/year/${year}`).then(r => r.data),
};

export const userApi = {
  getProfile: () => http.get<UserProfile>('/users/me').then(r => r.data),
  changePassword: (data: ChangePasswordRequest) => http.put('/users/me/password', data),
};
