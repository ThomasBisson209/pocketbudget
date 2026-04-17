import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { TrendingDown, TrendingUp, Wallet, CreditCard } from 'lucide-react';
import { dashboardApi, accountApi } from '../api';
import { Badge } from './ui/Badge';

const CATEGORY_LABELS: Record<string, string> = {
  FOOD: 'Alimentation', TRANSPORT: 'Transport', HOUSING: 'Logement',
  ENTERTAINMENT: 'Divertissement', HEALTH: 'Santé', EDUCATION: 'Éducation',
  SAVINGS: 'Épargne', OTHER: 'Autre',
};

const PIE_COLORS = ['#6366f1','#f43f5e','#f59e0b','#10b981','#3b82f6','#8b5cf6','#ec4899','#14b8a6'];

function StatCard({ label, value, sub, icon: Icon, color }: { label: string; value: string; sub?: string; icon: React.ElementType; color: string }) {
  return (
    <div className="bg-white rounded-xl border p-5 flex items-start gap-4">
      <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${color}`}>
        <Icon size={20} className="text-white" />
      </div>
      <div>
        <p className="text-sm text-gray-500">{label}</p>
        <p className="text-xl font-bold text-gray-900 mt-0.5">{value}</p>
        {sub && <p className="text-xs text-gray-400 mt-0.5">{sub}</p>}
      </div>
    </div>
  );
}

export function DashboardView() {
  const now = new Date();
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const { data: accounts = [] } = useQuery({ queryKey: ['accounts'], queryFn: accountApi.getAll });

  const { data, isLoading } = useQuery({
    queryKey: ['dashboard', month, year],
    queryFn: () => dashboardApi.get(month, year),
  });

  const accountName = (id: string) => accounts.find(a => a.accountId === id)?.name ?? id.slice(0, 8);

  const pieData = (data?.budgetSummaries ?? [])
    .filter(s => s.spent > 0)
    .map(s => ({ name: CATEGORY_LABELS[s.category] ?? s.category, value: s.spent }));

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Tableau de bord</h1>
        <div className="flex gap-2">
          <select className="border rounded-lg px-3 py-1.5 text-sm" value={month} onChange={e => setMonth(Number(e.target.value))}>
            {Array.from({ length: 12 }, (_, i) => (
              <option key={i + 1} value={i + 1}>{new Date(2000, i).toLocaleString('fr-CA', { month: 'long' })}</option>
            ))}
          </select>
          <input className="border rounded-lg px-3 py-1.5 text-sm w-24" type="number" value={year} onChange={e => setYear(Number(e.target.value))} />
        </div>
      </div>

      {isLoading || !data ? (
        <div className="text-center py-16 text-gray-400">Chargement...</div>
      ) : (
        <div className="space-y-6">
          {/* Stat cards */}
          <div className="grid grid-cols-2 gap-4">
            <StatCard label="Solde total" value={`${data.totalBalance.toFixed(2)} $`} sub={`${data.totalAccounts} compte${data.totalAccounts !== 1 ? 's' : ''}`} icon={Wallet} color="bg-indigo-500" />
            <StatCard label="Total budgeté" value={`${data.totalBudgeted.toFixed(2)} $`} sub={`${data.budgetSummaries.length} catégorie${data.budgetSummaries.length !== 1 ? 's' : ''}`} icon={CreditCard} color="bg-blue-500" />
            <StatCard label="Total dépensé" value={`${data.totalSpent.toFixed(2)} $`} icon={TrendingDown} color="bg-red-500" />
            <StatCard label="Restant" value={`${data.totalRemaining.toFixed(2)} $`} icon={TrendingUp} color={data.totalRemaining >= 0 ? 'bg-green-500' : 'bg-orange-500'} />
          </div>

          <div className="grid grid-cols-2 gap-6">
            {/* Pie chart */}
            {pieData.length > 0 ? (
              <div className="bg-white rounded-xl border p-5">
                <h2 className="text-sm font-semibold text-gray-700 mb-4">Dépenses par catégorie</h2>
                <ResponsiveContainer width="100%" height={220}>
                  <PieChart>
                    <Pie data={pieData} cx="50%" cy="50%" innerRadius={55} outerRadius={85} paddingAngle={3} dataKey="value">
                      {pieData.map((_, i) => <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />)}
                    </Pie>
                    <Tooltip formatter={(v) => [`${Number(v).toFixed(2)} $`]} />
                    <Legend iconType="circle" iconSize={8} />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            ) : (
              <div className="bg-white rounded-xl border p-5 flex items-center justify-center">
                <p className="text-sm text-gray-400">Aucune dépense ce mois-ci</p>
              </div>
            )}

            {/* Budget progress */}
            <div className="bg-white rounded-xl border p-5">
              <h2 className="text-sm font-semibold text-gray-700 mb-4">Budgets du mois</h2>
              {data.budgetSummaries.length === 0 ? (
                <p className="text-sm text-gray-400">Aucun budget ce mois-ci</p>
              ) : (
                <div className="space-y-3">
                  {data.budgetSummaries.map(s => {
                    const pct = Math.min((s.spent / s.limit) * 100, 100);
                    const color = pct >= 90 ? 'bg-red-500' : pct >= 70 ? 'bg-yellow-400' : 'bg-green-500';
                    return (
                      <div key={s.category}>
                        <div className="flex justify-between text-xs mb-1">
                          <span className="font-medium text-gray-700">{CATEGORY_LABELS[s.category] ?? s.category}</span>
                          <span className="text-gray-400">{s.spent.toFixed(0)} / {s.limit.toFixed(0)} $</span>
                        </div>
                        <div className="w-full bg-gray-100 rounded-full h-1.5">
                          <div className={`h-1.5 rounded-full transition-all ${color}`} style={{ width: `${pct}%` }} />
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </div>

          {/* Recent transactions */}
          {data.recentTransactions.length > 0 && (
            <div className="bg-white rounded-xl border p-5">
              <h2 className="text-sm font-semibold text-gray-700 mb-4">Transactions récentes</h2>
              <div className="divide-y">
                {data.recentTransactions.map(t => (
                  <div key={t.transactionId} className="flex items-center gap-3 py-3 first:pt-0 last:pb-0">
                    <div className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${t.type === 'DEBIT' ? 'bg-red-100' : 'bg-green-100'}`}>
                      {t.type === 'DEBIT' ? <TrendingDown size={14} className="text-red-600" /> : <TrendingUp size={14} className="text-green-600" />}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm text-gray-800 truncate">{t.description || '—'}</p>
                      <div className="flex items-center gap-2">
                        <p className="text-xs text-gray-400">{accountName(t.accountId)}</p>
                        {t.budgetCategory && <Badge variant="info">{CATEGORY_LABELS[t.budgetCategory] ?? t.budgetCategory}</Badge>}
                      </div>
                    </div>
                    <div className="text-right">
                      <p className={`text-sm font-semibold ${t.type === 'DEBIT' ? 'text-red-600' : 'text-green-600'}`}>
                        {t.type === 'DEBIT' ? '-' : '+'}{t.amount.toFixed(2)} $
                      </p>
                      <p className="text-xs text-gray-400">{t.date}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
