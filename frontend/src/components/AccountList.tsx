import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Plus, Landmark, PiggyBank, Wallet, Trash2 } from 'lucide-react';
import { accountApi } from '../api';
import type { CreateAccountRequest } from '../types';
import { Modal } from './ui/Modal';
import { Badge } from './ui/Badge';
import { useToast } from '../context/ToastContext';

const ACCOUNT_ICONS: Record<string, React.ElementType> = {
  CHECKING: Landmark,
  SAVINGS: PiggyBank,
  CASH: Wallet,
};

const ACCOUNT_LABELS: Record<string, string> = {
  CHECKING: 'Courant',
  SAVINGS: 'Épargne',
  CASH: 'Espèces',
};

const ACCOUNT_COLORS: Record<string, string> = {
  CHECKING: 'bg-blue-500',
  SAVINGS:  'bg-green-500',
  CASH:     'bg-orange-500',
};

export function AccountList() {
  const toast = useToast();
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState<CreateAccountRequest>({ name: '', type: 'CHECKING', initialBalance: 0 });

  const { data: accounts = [], isLoading } = useQuery({ queryKey: ['accounts'], queryFn: accountApi.getAll });

  const createMutation = useMutation({
    mutationFn: accountApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['accounts'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      toast.success('Compte créé avec succès');
      setOpen(false);
      setForm({ name: '', type: 'CHECKING', initialBalance: 0 });
    },
    onError: () => toast.error('Erreur lors de la création du compte'),
  });

  const deleteMutation = useMutation({
    mutationFn: accountApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['accounts'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      toast.success('Compte supprimé');
    },
    onError: () => toast.error('Erreur lors de la suppression'),
  });

  const totalBalance = accounts.reduce((sum, a) => sum + a.balance, 0);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Comptes</h1>
          <p className="text-sm text-gray-500 mt-0.5">Solde total : <span className="font-semibold text-gray-700">{totalBalance.toFixed(2)} $</span></p>
        </div>
        <button onClick={() => setOpen(true)} className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">
          <Plus size={16} /> Nouveau compte
        </button>
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-gray-400">Chargement...</div>
      ) : accounts.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-xl border">
          <div className="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <Wallet size={20} className="text-gray-400" />
          </div>
          <p className="text-gray-500">Aucun compte. Créez-en un pour commencer.</p>
          <button onClick={() => setOpen(true)} className="mt-4 text-indigo-600 text-sm hover:underline">Créer un compte</button>
        </div>
      ) : (
        <div className="grid gap-4">
          {accounts.map(account => {
            const Icon = ACCOUNT_ICONS[account.type] ?? Wallet;
            const color = ACCOUNT_COLORS[account.type] ?? 'bg-gray-500';
            return (
              <div key={account.accountId} className="bg-white rounded-xl border p-5 flex items-center gap-4 hover:shadow-sm transition-shadow">
                <div className={`w-11 h-11 rounded-xl flex items-center justify-center shrink-0 ${color}`}>
                  <Icon size={20} className="text-white" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-gray-800">{account.name}</p>
                  <Badge variant="info">{ACCOUNT_LABELS[account.type] ?? account.type}</Badge>
                </div>
                <div className="text-right">
                  <p className={`text-xl font-bold ${account.balance >= 0 ? 'text-gray-900' : 'text-red-600'}`}>
                    {account.balance.toFixed(2)} $
                  </p>
                </div>
                <button onClick={() => deleteMutation.mutate(account.accountId)} className="text-gray-300 hover:text-red-500 transition-colors ml-2" title="Supprimer">
                  <Trash2 size={17} />
                </button>
              </div>
            );
          })}
        </div>
      )}

      <Modal isOpen={open} onClose={() => setOpen(false)} title="Nouveau compte">
        <form onSubmit={e => { e.preventDefault(); createMutation.mutate(form); }} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Nom du compte</label>
            <input required className="w-full border rounded-lg px-3 py-2 text-sm" placeholder="Ex: Compte courant Desjardins" value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
            <div className="grid grid-cols-3 gap-2">
              {(['CHECKING', 'SAVINGS', 'CASH'] as const).map(t => {
                const Icon = ACCOUNT_ICONS[t];
                return (
                  <button key={t} type="button" onClick={() => setForm(f => ({ ...f, type: t }))}
                    className={`flex flex-col items-center gap-1.5 py-3 rounded-lg border text-xs font-medium transition-colors ${form.type === t ? 'bg-indigo-50 border-indigo-400 text-indigo-700' : 'border-gray-200 text-gray-500 hover:bg-gray-50'}`}>
                    <Icon size={18} />
                    {ACCOUNT_LABELS[t]}
                  </button>
                );
              })}
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Solde initial ($)</label>
            <input type="number" min="0" step="0.01" className="w-full border rounded-lg px-3 py-2 text-sm" value={form.initialBalance} onChange={e => setForm(f => ({ ...f, initialBalance: Number(e.target.value) }))} />
          </div>
          <button type="submit" disabled={createMutation.isPending} className="w-full bg-indigo-600 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-indigo-700 disabled:opacity-60 transition-colors">
            {createMutation.isPending ? 'Création...' : 'Créer le compte'}
          </button>
        </form>
      </Modal>
    </div>
  );
}
