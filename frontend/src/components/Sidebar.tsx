import { LayoutDashboard, ArrowLeftRight, CreditCard, PiggyBank, LogOut } from 'lucide-react';

type Tab = 'dashboard' | 'transactions' | 'accounts' | 'budgets';

interface SidebarProps {
  activeTab: Tab;
  onTabChange: (tab: Tab) => void;
  username: string | null;
  onLogout: () => void;
}

const NAV_ITEMS: { tab: Tab; label: string; Icon: React.ElementType }[] = [
  { tab: 'dashboard',    label: 'Tableau de bord', Icon: LayoutDashboard },
  { tab: 'transactions', label: 'Transactions',     Icon: ArrowLeftRight },
  { tab: 'accounts',     label: 'Comptes',          Icon: CreditCard },
  { tab: 'budgets',      label: 'Budgets',          Icon: PiggyBank },
];

export function Sidebar({ activeTab, onTabChange, username, onLogout }: SidebarProps) {
  return (
    <aside className="w-60 shrink-0 bg-indigo-900 text-white flex flex-col h-screen sticky top-0">
      {/* Logo */}
      <div className="px-6 py-5 border-b border-indigo-800">
        <h1 className="text-xl font-bold tracking-tight">PocketBudget</h1>
        <p className="text-indigo-300 text-xs mt-0.5">Gestion financière</p>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 py-4 space-y-1">
        {NAV_ITEMS.map(({ tab, label, Icon }) => (
          <button
            key={tab}
            onClick={() => onTabChange(tab)}
            className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
              activeTab === tab
                ? 'bg-indigo-700 text-white'
                : 'text-indigo-200 hover:bg-indigo-800 hover:text-white'
            }`}
          >
            <Icon size={18} />
            {label}
          </button>
        ))}
      </nav>

      {/* User */}
      <div className="px-4 py-4 border-t border-indigo-800">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-8 h-8 rounded-full bg-indigo-600 flex items-center justify-center text-sm font-semibold">
            {username?.[0]?.toUpperCase() ?? '?'}
          </div>
          <span className="text-sm text-indigo-100 truncate">{username}</span>
        </div>
        <button
          onClick={onLogout}
          className="w-full flex items-center gap-2 px-3 py-2 rounded-lg text-sm text-indigo-300 hover:bg-indigo-800 hover:text-white transition-colors"
        >
          <LogOut size={16} />
          Déconnexion
        </button>
      </div>
    </aside>
  );
}
