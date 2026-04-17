import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState, useMemo } from 'react';
import { Plus, TrendingDown, TrendingUp, Search, X, Download } from 'lucide-react';
import { transactionApi, accountApi, budgetApi } from '../api';
import type { Transaction, Account } from '../types';
import type { CreateTransactionRequest } from '../types';
import { Modal } from './ui/Modal';
import { Badge } from './ui/Badge';
import { useToast } from '../context/ToastContext';

const CATEGORY_LABELS: Record<string, string> = {
  FOOD: 'Alimentation', TRANSPORT: 'Transport', HOUSING: 'Logement',
  ENTERTAINMENT: 'Divertissement', HEALTH: 'Santé', EDUCATION: 'Éducation',
  SAVINGS: 'Épargne', OTHER: 'Autre',
};

const now = new Date();

// ── CSV Export ────────────────────────────────────────────────────────────────
function exportCSV(transactions: Transaction[], accounts: Account[]) {
  const accountName = (id: string) => accounts.find(a => a.accountId === id)?.name ?? id;
  const header = 'Date,Description,Compte,Type,Montant ($),Catégorie';
  const rows = transactions.map(t => {
    const amount = t.type === 'DEBIT' ? -t.amount : t.amount;
    const cat = t.budgetCategory ? (CATEGORY_LABELS[t.budgetCategory] ?? t.budgetCategory) : '';
    return `${t.date},"${t.description}","${accountName(t.accountId)}",${t.type},${amount},"${cat}"`;
  });
  const csv = '\ufeff' + [header, ...rows].join('\n');
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `transactions-${new Date().toISOString().slice(0, 7)}.csv`;
  a.click();
  URL.revokeObjectURL(url);
}

// ── Transaction form ──────────────────────────────────────────────────────────
function TransactionForm({ onClose }: { onClose: () => void }) {
  const toast = useToast();
  const queryClient = useQueryClient();
  const { data: accounts = [] } = useQuery({ queryKey: ['accounts'], queryFn: accountApi.getAll });
  const { data: budgets = [] } = useQuery({
    queryKey: ['budgets', now.getMonth() + 1, now.getFullYear()],
    queryFn: () => budgetApi.getByMonth(now.getMonth() + 1, now.getFullYear()),
  });

  const [form, setForm] = useState<CreateTransactionRequest>({
    accountId: '', description: '', amount: 0,
    date: now.toISOString().split('T')[0], type: 'DEBIT',
  });

  const mutation = useMutation({
    mutationFn: transactionApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transactions'] });
      queryClient.invalidateQueries({ queryKey: ['accounts'] });
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['balance-history'] });
      toast.success('Transaction ajoutée');
      onClose();
    },
    onError: (err: any) => toast.error(err?.response?.data?.error ?? 'Erreur lors de la transaction'),
  });

  const set = (k: keyof CreateTransactionRequest, v: any) => setForm(f => ({ ...f, [k]: v }));

  return (
    <form onSubmit={e => { e.preventDefault(); mutation.mutate(form); }} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Compte</label>
        <select required className="w-full border rounded-lg px-3 py-2 text-sm" value={form.accountId} onChange={e => set('accountId', e.target.value)}>
          <option value="">Sélectionner un compte...</option>
          {accounts.map(a => <option key={a.accountId} value={a.accountId}>{a.name} ({a.balance.toFixed(2)} $)</option>)}
        </select>
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
        <div className="flex gap-2">
          {(['DEBIT', 'CREDIT'] as const).map(t => (
            <button key={t} type="button" onClick={() => set('type', t)}
              className={`flex-1 flex items-center justify-center gap-2 py-2 rounded-lg border text-sm font-medium transition-colors ${
                form.type === t
                  ? t === 'DEBIT' ? 'bg-red-50 border-red-400 text-red-700' : 'bg-green-50 border-green-400 text-green-700'
                  : 'border-gray-200 text-gray-500 hover:bg-gray-50'
              }`}>
              {t === 'DEBIT' ? <TrendingDown size={15} /> : <TrendingUp size={15} />}
              {t === 'DEBIT' ? 'Dépense' : 'Revenu'}
            </button>
          ))}
        </div>
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
        <input required className="w-full border rounded-lg px-3 py-2 text-sm" value={form.description} onChange={e => set('description', e.target.value)} placeholder="Ex: Épicerie IGA" />
      </div>
      <div className="flex gap-3">
        <div className="flex-1">
          <label className="block text-sm font-medium text-gray-700 mb-1">Montant ($)</label>
          <input required type="number" min="0.01" step="0.01" className="w-full border rounded-lg px-3 py-2 text-sm" value={form.amount} onChange={e => set('amount', Number(e.target.value))} />
        </div>
        <div className="flex-1">
          <label className="block text-sm font-medium text-gray-700 mb-1">Date</label>
          <input required type="date" className="w-full border rounded-lg px-3 py-2 text-sm" value={form.date} onChange={e => set('date', e.target.value)} />
        </div>
      </div>
      {form.type === 'DEBIT' && (
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Catégorie budget <span className="text-gray-400 font-normal">(optionnel)</span></label>
          <select className="w-full border rounded-lg px-3 py-2 text-sm" value={form.budgetCategory ?? ''} onChange={e => set('budgetCategory', e.target.value || undefined)}>
            <option value="">Aucune catégorie</option>
            {budgets.map(b => <option key={b.budgetId} value={b.category}>{CATEGORY_LABELS[b.category]} ({b.remainingAmount.toFixed(2)} $ restant)</option>)}
          </select>
        </div>
      )}
      <button type="submit" disabled={mutation.isPending} className="w-full bg-green-700 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-green-800 disabled:opacity-60 transition-colors">
        {mutation.isPending ? 'Enregistrement...' : 'Ajouter la transaction'}
      </button>
    </form>
  );
}

// ── Main component ────────────────────────────────────────────────────────────
type FilterType = 'ALL' | 'DEBIT' | 'CREDIT';

export function TransactionList() {
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState('');
  const [filterType, setFilterType] = useState<FilterType>('ALL');
  const [filterAccountId, setFilterAccountId] = useState('');
  const [filterMonth, setFilterMonth] = useState<number>(0); // 0 = all months
  const [filterYear, setFilterYear] = useState<number>(now.getFullYear());

  const { data: transactions = [], isLoading } = useQuery({ queryKey: ['transactions'], queryFn: transactionApi.getAll });
  const { data: accounts = [] } = useQuery({ queryKey: ['accounts'], queryFn: accountApi.getAll });

  const accountName = (id: string) => accounts.find(a => a.accountId === id)?.name ?? id.slice(0, 8);

  // ── Client-side filtering ──────────────────────────────────────────────────
  const filtered = useMemo(() => {
    return transactions.filter(t => {
      if (search && !t.description.toLowerCase().includes(search.toLowerCase())) return false;
      if (filterType !== 'ALL' && t.type !== filterType) return false;
      if (filterAccountId && t.accountId !== filterAccountId) return false;
      if (filterMonth > 0) {
        const [y, m] = t.date.split('-').map(Number);
        if (m !== filterMonth || y !== filterYear) return false;
      }
      return true;
    });
  }, [transactions, search, filterType, filterAccountId, filterMonth, filterYear]);

  const hasFilters = search !== '' || filterType !== 'ALL' || filterAccountId !== '' || filterMonth > 0;

  function clearFilters() {
    setSearch('');
    setFilterType('ALL');
    setFilterAccountId('');
    setFilterMonth(0);
  }

  // ── Totals for filtered results ────────────────────────────────────────────
  const totalDebit  = filtered.filter(t => t.type === 'DEBIT').reduce((s, t) => s + t.amount, 0);
  const totalCredit = filtered.filter(t => t.type === 'CREDIT').reduce((s, t) => s + t.amount, 0);

  // ── Group by date ──────────────────────────────────────────────────────────
  const grouped = filtered.reduce<Record<string, typeof filtered>>((acc, t) => {
    (acc[t.date] = acc[t.date] ?? []).push(t);
    return acc;
  }, {});

  const formatDate = (d: string) => new Date(d + 'T00:00:00').toLocaleDateString('fr-CA', { weekday: 'long', day: 'numeric', month: 'long' });

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Transactions</h1>
          <p className="text-sm text-gray-500 mt-0.5">{transactions.length} au total</p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => exportCSV(filtered, accounts)}
            title="Exporter en CSV"
            className="flex items-center gap-2 border border-gray-200 text-gray-600 px-3 py-2.5 rounded-lg text-sm font-medium hover:bg-gray-50 transition-colors"
          >
            <Download size={16} />
            <span className="hidden sm:inline">CSV</span>
          </button>
          <button
            onClick={() => setOpen(true)}
            className="flex items-center gap-2 bg-green-700 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-800 transition-colors"
          >
            <Plus size={16} />
            <span className="hidden sm:inline">Nouvelle transaction</span>
            <span className="sm:hidden">Ajouter</span>
          </button>
        </div>
      </div>

      {/* Filter bar */}
      <div className="bg-white rounded-xl border p-4 mb-4 space-y-3">
        {/* Search */}
        <div className="relative">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Rechercher une description..."
            className="w-full border rounded-lg pl-9 pr-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          {search && (
            <button onClick={() => setSearch('')} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-300 hover:text-gray-500">
              <X size={14} />
            </button>
          )}
        </div>

        {/* Filter pills */}
        <div className="flex flex-wrap items-center gap-2">
          {/* Type filter */}
          {(['ALL', 'DEBIT', 'CREDIT'] as const).map(t => (
            <button key={t} onClick={() => setFilterType(t)}
              className={`px-3 py-1 rounded-full text-xs font-medium border transition-colors ${
                filterType === t
                  ? t === 'DEBIT' ? 'bg-red-100 border-red-300 text-red-700'
                    : t === 'CREDIT' ? 'bg-green-100 border-green-300 text-green-700'
                    : 'bg-green-700 border-green-700 text-white'
                  : 'border-gray-200 text-gray-500 hover:bg-gray-50'
              }`}>
              {t === 'ALL' ? 'Tout' : t === 'DEBIT' ? '↓ Dépenses' : '↑ Revenus'}
            </button>
          ))}

          {/* Account filter */}
          <select
            className="border rounded-full px-3 py-1 text-xs text-gray-600 focus:outline-none focus:ring-2 focus:ring-green-500"
            value={filterAccountId}
            onChange={e => setFilterAccountId(e.target.value)}
          >
            <option value="">Tous les comptes</option>
            {accounts.map(a => <option key={a.accountId} value={a.accountId}>{a.name}</option>)}
          </select>

          {/* Month filter */}
          <div className="flex items-center gap-1">
            <select
              className="border rounded-full px-3 py-1 text-xs text-gray-600 focus:outline-none"
              value={filterMonth}
              onChange={e => setFilterMonth(Number(e.target.value))}
            >
              <option value={0}>Tous les mois</option>
              {Array.from({ length: 12 }, (_, i) => (
                <option key={i + 1} value={i + 1}>
                  {new Date(2000, i).toLocaleString('fr-CA', { month: 'short' })}
                </option>
              ))}
            </select>
            {filterMonth > 0 && (
              <input
                type="number"
                className="border rounded-full px-2 py-1 text-xs w-16"
                value={filterYear}
                onChange={e => setFilterYear(Number(e.target.value))}
              />
            )}
          </div>
        </div>

        {/* Active filters summary */}
        {hasFilters && (
          <div className="flex items-center justify-between text-xs">
            <span className="text-gray-500">
              <span className="font-semibold text-gray-700">{filtered.length}</span> résultat{filtered.length !== 1 ? 's' : ''} —
              <span className="text-red-600 ml-1">↓ {totalDebit.toFixed(2)} $</span>
              <span className="text-green-600 ml-2">↑ {totalCredit.toFixed(2)} $</span>
            </span>
            <button onClick={clearFilters} className="text-green-700 hover:underline flex items-center gap-1">
              <X size={11} /> Effacer les filtres
            </button>
          </div>
        )}
      </div>

      {/* Transaction list */}
      {isLoading ? (
        <div className="text-center py-12 text-gray-400">Chargement...</div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-xl border">
          <div className="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <Search size={20} className="text-gray-400" />
          </div>
          <p className="text-gray-500">{hasFilters ? 'Aucun résultat pour ces filtres' : 'Aucune transaction pour le moment'}</p>
          {hasFilters
            ? <button onClick={clearFilters} className="mt-3 text-green-700 text-sm hover:underline">Effacer les filtres</button>
            : <button onClick={() => setOpen(true)} className="mt-3 text-green-700 text-sm hover:underline">Ajouter une transaction</button>
          }
        </div>
      ) : (
        <div className="space-y-6">
          {Object.entries(grouped).sort(([a], [b]) => b.localeCompare(a)).map(([date, txs]) => (
            <div key={date}>
              <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-2">{formatDate(date)}</p>
              <div className="bg-white rounded-xl border divide-y">
                {txs.map(t => (
                  <div key={t.transactionId} className="flex items-center gap-4 px-5 py-3.5">
                    <div className={`w-9 h-9 rounded-full flex items-center justify-center shrink-0 ${t.type === 'DEBIT' ? 'bg-red-100' : 'bg-green-100'}`}>
                      {t.type === 'DEBIT' ? <TrendingDown size={16} className="text-red-600" /> : <TrendingUp size={16} className="text-green-600" />}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-800 truncate">{t.description || '—'}</p>
                      <div className="flex items-center gap-2 mt-0.5">
                        <p className="text-xs text-gray-400">{accountName(t.accountId)}</p>
                        {t.budgetCategory && <Badge variant="info">{CATEGORY_LABELS[t.budgetCategory] ?? t.budgetCategory}</Badge>}
                      </div>
                    </div>
                    <p className={`text-sm font-semibold shrink-0 ${t.type === 'DEBIT' ? 'text-red-600' : 'text-green-600'}`}>
                      {t.type === 'DEBIT' ? '-' : '+'}{t.amount.toFixed(2)} $
                    </p>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal isOpen={open} onClose={() => setOpen(false)} title="Nouvelle transaction">
        <TransactionForm onClose={() => setOpen(false)} />
      </Modal>
    </div>
  );
}
