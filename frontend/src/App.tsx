import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useState } from 'react';
import { clearAuth, getUsername, isAuthenticated } from './auth';
import { ToastProvider } from './context/ToastContext';
import { AuthPage } from './components/AuthPage';
import { Sidebar } from './components/Sidebar';
import { DashboardView } from './components/DashboardView';
import { TransactionList } from './components/TransactionList';
import { AccountList } from './components/AccountList';
import { BudgetList } from './components/BudgetList';

const queryClient = new QueryClient();

type Tab = 'dashboard' | 'transactions' | 'accounts' | 'budgets';

function MainApp() {
  const [tab, setTab] = useState<Tab>('dashboard');
  const username = getUsername();

  function handleLogout() {
    clearAuth();
    queryClient.clear();
    window.location.reload();
  }

  return (
    <div className="flex h-screen overflow-hidden bg-gray-50">
      <Sidebar activeTab={tab} onTabChange={setTab} username={username} onLogout={handleLogout} />
      <main className="flex-1 overflow-y-auto">
        <div className="max-w-4xl mx-auto px-8 py-8">
          {tab === 'dashboard'    && <DashboardView />}
          {tab === 'transactions' && <TransactionList />}
          {tab === 'accounts'     && <AccountList />}
          {tab === 'budgets'      && <BudgetList />}
        </div>
      </main>
    </div>
  );
}

function App() {
  const [authed, setAuthed] = useState(isAuthenticated());

  return (
    <QueryClientProvider client={queryClient}>
      <ToastProvider>
        {authed ? <MainApp /> : <AuthPage onAuthenticated={() => setAuthed(true)} />}
      </ToastProvider>
    </QueryClientProvider>
  );
}

export default App;
