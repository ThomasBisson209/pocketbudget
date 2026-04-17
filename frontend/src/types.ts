export interface Account {
  accountId: string;
  name: string;
  type: 'CHECKING' | 'SAVINGS' | 'CASH';
  balance: number;
}

export interface CreateAccountRequest {
  name: string;
  type: string;
  initialBalance: number;
}

export interface Budget {
  budgetId: string;
  category: string;
  monthlyLimit: number;
  currentSpent: number;
  remainingAmount: number;
  month: number;
  year: number;
}

export interface CreateBudgetRequest {
  category: string;
  monthlyLimit: number;
  month: number;
  year: number;
}

export interface Transaction {
  transactionId: string;
  accountId: string;
  budgetCategory: string | null;
  description: string;
  amount: number;
  date: string;
  type: 'DEBIT' | 'CREDIT';
}

export interface CreateTransactionRequest {
  accountId: string;
  budgetCategory?: string;
  description: string;
  amount: number;
  date: string;
  type: string;
}

export interface BudgetSummary {
  category: string;
  limit: number;
  spent: number;
  remaining: number;
}

export interface RecentTransaction {
  transactionId: string;
  description: string;
  amount: number;
  type: 'DEBIT' | 'CREDIT';
  date: string;
  accountId: string;
  budgetCategory: string | null;
}

export interface Dashboard {
  totalBalance: number;
  totalAccounts: number;
  totalBudgeted: number;
  totalSpent: number;
  totalRemaining: number;
  budgetSummaries: BudgetSummary[];
  recentTransactions: RecentTransaction[];
}
