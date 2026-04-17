import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Plus, TrendingDown, TrendingUp } from 'lucide-react';
import { transactionApi, accountApi, budgetApi } from '../api';
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
            <button key={t} type="button"
              onClick={() => set('type', t)}
              className={`flex-1 flex items-center justify-center gap-2 py-2 rounded-lg border text-sm font-medium transition-colors ${
                form.type === t
                  ? t === 'DEBIT' ? 'bg-red-50 border-red-400 text-red-700' : 'bg-green-50 border-green-400 text-green-700'
                  : 'border-gray-200 text-gray-500 hover:bg-gray-50'
              }`}
            >
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
      <button type="submit" disabled={mutation.isPending} className="w-full bg-indigo-600 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-indigo-700 disabled:opacity-60 transition-colors">
        {mutation.isPending ? 'Enregistrement...' : 'Ajouter la transaction'}
      </button>
    </form>
  );
}

export function TransactionList() {
  const [open, setOpen] = useState(false);
  const { data: transactions = [], isLoading } = useQuery({ queryKey: ['transactions'], queryFn: transactionApi.getAll });
  const { data: accounts = [] } = useQuery({ queryKey: ['accounts'], queryFn: accountApi.getAll });

  const accountName = (id: string) => accounts.find(a => a.accountId === id)?.name ?? id.slice(0, 8);

  // Groupe par date
  const grouped = transactions.reduce<Record<string, typeof transactions>>((acc, t) => {
    (acc[t.date] = acc[t.date] ?? []).push(t);
    return acc;
  }, {});

  const formatDate = (d: string) => new Date(d + 'T00:00:00').toLocaleDateString('fr-CA', { weekday: 'long', day: 'numeric', month: 'long' });

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Transactions</h1>
          <p className="text-sm text-gray-500 mt-0.5">{transactions.length} transaction{transactions.length !== 1 ? 's' : ''}</p>
        </div>
        <button onClick={() => setOpen(true)} className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">
          <Plus size={16} /> Nouvelle transaction
        </button>
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-gray-400">Chargement...</div>
      ) : transactions.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-xl border">
          <ArrowLeftRightIcon />
          <p className="text-gray-500 mt-3">Aucune transaction pour le moment</p>
          <button onClick={() => setOpen(true)} className="mt-4 text-indigo-600 text-sm hover:underline">Ajouter une transaction</button>
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
                    <p className={`text-sm font-semibold ${t.type === 'DEBIT' ? 'text-red-600' : 'text-green-600'}`}>
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

function ArrowLeftRightIcon() {
  return (
    <div className="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center mx-auto">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="text-gray-400">
        <path d="m21 7-18 0M3 7l4-4M3 7l4 4M21 17H3M21 17l-4-4M21 17l-4 4"/>
      </svg>
    </div>
  );
}
