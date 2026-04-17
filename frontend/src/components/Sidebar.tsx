import { LayoutDashboard, ArrowLeftRight, CreditCard, PiggyBank, LogOut, X, User, Settings } from 'lucide-react';

type Tab = 'dashboard' | 'transactions' | 'accounts' | 'budgets' | 'profile' | 'settings';

interface SidebarProps {
  activeTab: Tab;
  onTabChange: (tab: Tab) => void;
  username: string | null;
  onLogout: () => void;
  onClose?: () => void;
}

const NAV_ITEMS: { tab: Tab; label: string; Icon: React.ElementType }[] = [
  { tab: 'dashboard',    label: 'Tableau de bord', Icon: LayoutDashboard },
  { tab: 'transactions', label: 'Transactions',     Icon: ArrowLeftRight },
  { tab: 'accounts',     label: 'Comptes',          Icon: CreditCard },
  { tab: 'budgets',      label: 'Budgets',          Icon: PiggyBank },
];

const BOTTOM_NAV: { tab: Tab; label: string; Icon: React.ElementType }[] = [
  { tab: 'profile',  label: 'Profil',      Icon: User },
  { tab: 'settings', label: 'Paramètres',  Icon: Settings },
];

export function Sidebar({ activeTab, onTabChange, username, onLogout, onClose }: SidebarProps) {
  return (
    <aside className="w-60 shrink-0 bg-green-900 text-white flex flex-col h-full">
      {/* Logo */}
      <div className="px-6 py-5 border-b border-green-800 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold tracking-tight">PocketBudget</h1>
          <p className="text-green-300 text-xs mt-0.5">Gestion financière</p>
        </div>
        {onClose && (
          <button onClick={onClose} className="md:hidden text-green-300 hover:text-white">
            <X size={20} />
          </button>
        )}
      </div>

      {/* Main nav */}
      <nav className="flex-1 px-3 py-4 space-y-1">
        {NAV_ITEMS.map(({ tab, label, Icon }) => (
          <button
            key={tab}
            onClick={() => { onTabChange(tab); onClose?.(); }}
            className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
              activeTab === tab
                ? 'bg-green-700 text-white'
                : 'text-green-200 hover:bg-green-800 hover:text-white'
            }`}
          >
            <Icon size={18} />
            {label}
          </button>
        ))}
      </nav>

      {/* Bottom nav (profile + settings) */}
      <div className="px-3 pb-2 space-y-1 border-t border-green-800 pt-3">
        {BOTTOM_NAV.map(({ tab, label, Icon }) => (
          <button
            key={tab}
            onClick={() => { onTabChange(tab); onClose?.(); }}
            className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
              activeTab === tab
                ? 'bg-green-700 text-white'
                : 'text-green-200 hover:bg-green-800 hover:text-white'
            }`}
          >
            <Icon size={18} />
            {label}
          </button>
        ))}
      </div>

      {/* User footer */}
      <div className="px-4 py-4 border-t border-green-800">
        <div
          className="flex items-center gap-3 mb-3 cursor-pointer group"
          onClick={() => { onTabChange('profile'); onClose?.(); }}
        >
          <div className="w-8 h-8 rounded-full bg-green-700 group-hover:bg-green-600 flex items-center justify-center text-sm font-semibold transition-colors">
            {username?.[0]?.toUpperCase() ?? '?'}
          </div>
          <span className="text-sm text-green-100 truncate group-hover:text-white transition-colors">{username}</span>
        </div>
        <button
          onClick={onLogout}
          className="w-full flex items-center gap-2 px-3 py-2 rounded-lg text-sm text-green-300 hover:bg-green-800 hover:text-white transition-colors"
        >
          <LogOut size={16} />
          Déconnexion
        </button>
      </div>
    </aside>
  );
}
