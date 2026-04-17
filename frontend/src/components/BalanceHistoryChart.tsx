import { useQuery } from '@tanstack/react-query';
import { useState, useEffect } from 'react';
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer,
} from 'recharts';
import { TrendingUp } from 'lucide-react';
import { accountApi } from '../api';

export function BalanceHistoryChart() {
  const now = new Date();
  const [selectedAccountId, setSelectedAccountId] = useState<string>('');
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());

  const { data: accounts = [] } = useQuery({
    queryKey: ['accounts'],
    queryFn: accountApi.getAll,
  });

  // Auto-select first account
  useEffect(() => {
    if (accounts.length > 0 && !selectedAccountId) {
      setSelectedAccountId(accounts[0].accountId);
    }
  }, [accounts, selectedAccountId]);

  const { data: history, isLoading } = useQuery({
    queryKey: ['balance-history', selectedAccountId, month, year],
    queryFn: () => accountApi.getBalanceHistory(selectedAccountId, month, year),
    enabled: !!selectedAccountId,
  });

  const chartData = (history?.points ?? []).map(p => ({
    day: parseInt(p.date.split('-')[2], 10),
    balance: p.balance,
    fullDate: p.date,
  }));

  const minBalance = Math.min(...chartData.map(d => d.balance), 0);
  const maxBalance = Math.max(...chartData.map(d => d.balance), 100);
  const yDomain = [Math.floor(minBalance * 0.95), Math.ceil(maxBalance * 1.05)];

  if (accounts.length === 0) {
    return (
      <div className="bg-white rounded-xl border p-5 flex items-center justify-center min-h-[220px]">
        <p className="text-sm text-gray-400">Aucun compte disponible</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl border p-5">
      {/* Header */}
      <div className="flex flex-wrap gap-3 items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <div className="w-7 h-7 rounded-lg bg-green-100 flex items-center justify-center">
            <TrendingUp size={14} className="text-green-700" />
          </div>
          <h2 className="text-sm font-semibold text-gray-700">
            Évolution du solde{history ? ` — ${history.accountName}` : ''}
          </h2>
        </div>
        <div className="flex items-center gap-2">
          <select
            className="border rounded-lg px-2 py-1 text-xs"
            value={selectedAccountId}
            onChange={e => setSelectedAccountId(e.target.value)}
          >
            {accounts.map(a => (
              <option key={a.accountId} value={a.accountId}>{a.name}</option>
            ))}
          </select>
          <select
            className="border rounded-lg px-2 py-1 text-xs"
            value={month}
            onChange={e => setMonth(Number(e.target.value))}
          >
            {Array.from({ length: 12 }, (_, i) => (
              <option key={i + 1} value={i + 1}>
                {new Date(2000, i).toLocaleString('fr-CA', { month: 'short' })}
              </option>
            ))}
          </select>
          <input
            className="border rounded-lg px-2 py-1 text-xs w-16"
            type="number"
            value={year}
            onChange={e => setYear(Number(e.target.value))}
          />
        </div>
      </div>

      {/* Chart */}
      {isLoading ? (
        <div className="h-[200px] flex items-center justify-center text-gray-400 text-sm">Chargement...</div>
      ) : chartData.length === 0 ? (
        <div className="h-[200px] flex items-center justify-center text-gray-400 text-sm">Aucune donnée</div>
      ) : (
        <ResponsiveContainer width="100%" height={220}>
          <AreaChart data={chartData} margin={{ top: 5, right: 5, left: 0, bottom: 0 }}>
            <defs>
              <linearGradient id="balanceGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#15803d" stopOpacity={0.15} />
                <stop offset="95%" stopColor="#15803d" stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis
              dataKey="day"
              tick={{ fontSize: 11, fill: '#9ca3af' }}
              tickLine={false}
              axisLine={false}
              interval="preserveStartEnd"
            />
            <YAxis
              domain={yDomain}
              tick={{ fontSize: 11, fill: '#9ca3af' }}
              tickLine={false}
              axisLine={false}
              tickFormatter={v => `${v}$`}
              width={55}
            />
            <Tooltip
              formatter={(value) => [`${Number(value).toFixed(2)} $`, 'Solde']}
              labelFormatter={(day) => {
                const padded = String(day).padStart(2, '0');
                const m = String(month).padStart(2, '0');
                return `${year}-${m}-${padded}`;
              }}
              contentStyle={{ borderRadius: '8px', border: '1px solid #e5e7eb', fontSize: '12px' }}
            />
            <Area
              type="monotone"
              dataKey="balance"
              stroke="#15803d"
              strokeWidth={2}
              fill="url(#balanceGradient)"
              dot={false}
              activeDot={{ r: 4, fill: '#15803d' }}
            />
          </AreaChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}
