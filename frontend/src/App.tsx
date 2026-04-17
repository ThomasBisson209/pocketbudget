import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useState } from 'react';
import { Menu } from 'lucide-react';
import { clearAuth, getUsername, isAuthenticated } from './auth';
import { ToastProvider } from './context/ToastContext';
import { AuthPage } from './components/AuthPage';
import { Sidebar } from './components/Sidebar';
import { DashboardView } from './components/DashboardView';
import { TransactionList } from './components/TransactionList';
import { AccountList } from './components/AccountList';
import { BudgetList } from './components/BudgetList';
import { ProfilePage } from './components/ProfilePage';
import { SettingsPage } from './components/SettingsPage';

const queryClient = new QueryClient();

type Tab = 'dashboard' | 'transactions' | 'accounts' | 'budgets' | 'profile' | 'settings';

const TAB_LABELS: Record<Tab, string> = {
  dashboard:    'Tableau de bord',
  transactions: 'Transactions',
  accounts:     'Comptes',
  budgets:      'Budgets',
  profile:      'Profil',
  settings:     'Paramètres',
};

function MainApp() {
  const [tab, setTab] = useState<Tab>('dashboard');
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const username = getUsername();

  function handleLogout() {
    clearAuth();
    queryClient.clear();
    window.location.reload();
  }

  return (
    <div className="flex h-screen overflow-hidden bg-gray-50">
      {/* Mobile overlay */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-20 bg-black/40 md:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 z-30 h-screen transition-transform duration-200 md:relative md:translate-x-0 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}`}>
        <Sidebar
          activeTab={tab}
          onTabChange={setTab}
          username={username}
          onLogout={handleLogout}
          onClose={() => setSidebarOpen(false)}
        />
      </div>

      {/* Main content */}
      <main className="flex-1 overflow-y-auto">
        {/* Mobile top bar */}
        <div className="md:hidden sticky top-0 bg-white border-b px-4 py-3 flex items-center gap-3 z-10 shadow-sm">
          <button
            onClick={() => setSidebarOpen(true)}
            className="text-green-800 hover:text-green-600"
          >
            <Menu size={22} />
          </button>
          <span className="font-semibold text-green-900">PocketBudget</span>
          <span className="text-gray-400 text-sm ml-auto">{TAB_LABELS[tab]}</span>
        </div>

        <div className="max-w-4xl mx-auto px-4 sm:px-8 py-6 sm:py-8">
          {tab === 'dashboard'    && <DashboardView />}
          {tab === 'transactions' && <TransactionList />}
          {tab === 'accounts'     && <AccountList />}
          {tab === 'budgets'      && <BudgetList />}
          {tab === 'profile'      && <ProfilePage />}
          {tab === 'settings'     && <SettingsPage />}
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
