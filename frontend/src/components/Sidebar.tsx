import { LayoutDashboard, ArrowLeftRight, CreditCard, PiggyBank, LogOut, X, Settings } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { budgetApi } from '../api';

type Tab = 'dashboard' | 'transactions' | 'accounts' | 'budgets' | 'profile' | 'settings';

interface SidebarProps {
  activeTab: Tab;
  onTabChange: (tab: Tab) => void;
  username: string | null;
  onLogout: () => void;
  onClose?: () => void;
}

export function Sidebar({ activeTab, onTabChange, username, onLogout, onClose }: SidebarProps) {
  const now = new Date();

  // Fetch current month budgets to show alert badge
  const { data: budgets = [] } = useQuery({
    queryKey: ['budgets', now.getMonth() + 1, now.getFullYear()],
    queryFn: () => budgetApi.getByMonth(now.getMonth() + 1, now.getFullYear()),
    staleTime: 60_000,
  });

  const alertCount = budgets.filter(b => b.monthlyLimit > 0 && (b.currentSpent / b.monthlyLimit) >= 0.8).length;

  function NavButton({ tab, label, Icon, badge }: { tab: Tab; label: string; Icon: React.ElementType; badge?: number }) {
    return (
      <button
        onClick={() => { onTabChange(tab); onClose?.(); }}
        className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
          activeTab === tab
            ? 'bg-green-700 text-white'
            : 'text-green-200 hover:bg-green-800 hover:text-white'
        }`}
      >
        <Icon size={18} />
        <span className="flex-1 text-left">{label}</span>
        {badge != null && badge > 0 && (
          <span className="w-5 h-5 rounded-full bg-red-500 text-white text-[10px] font-bold flex items-center justify-center shrink-0">
            {badge > 9 ? '9+' : badge}
          </span>
        )}
      </button>
    );
  }

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
        <NavButton tab="dashboard"    label="Tableau de bord" Icon={LayoutDashboard} />
        <NavButton tab="transactions" label="Transactions"     Icon={ArrowLeftRight} />
        <NavButton tab="accounts"     label="Comptes"          Icon={CreditCard} />
        <NavButton tab="budgets"      label="Budgets"          Icon={PiggyBank} badge={alertCount} />
      </nav>

      {/* Bottom nav (settings only) */}
      <div className="px-3 pb-2 space-y-1 border-t border-green-800 pt-3">
        <NavButton tab="settings" label="Paramètres" Icon={Settings} />
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
