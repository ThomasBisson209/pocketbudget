import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Plus, Trash2, PiggyBank } from 'lucide-react';
import { budgetApi } from '../api';
import type { CreateBudgetRequest } from '../types';
import { Modal } from './ui/Modal';
import { Badge } from './ui/Badge';
import { useToast } from '../context/ToastContext';

const CATEGORIES = ['FOOD','TRANSPORT','HOUSING','ENTERTAINMENT','HEALTH','EDUCATION','SAVINGS','OTHER'] as const;
const CATEGORY_LABELS: Record<string, string> = {
  FOOD: 'Alimentation', TRANSPORT: 'Transport', HOUSING: 'Logement',
  ENTERTAINMENT: 'Divertissement', HEALTH: 'Santé', EDUCATION: 'Éducation',
  SAVINGS: 'Épargne', OTHER: 'Autre',
};

function ProgressBar({ pct }: { pct: number }) {
  const color = pct >= 90 ? 'bg-red-500' : pct >= 70 ? 'bg-yellow-400' : 'bg-green-500';
  const badge: 'danger' | 'warning' | 'success' = pct >= 90 ? 'danger' : pct >= 70 ? 'warning' : 'success';
  return (
    <div className="flex items-center gap-3">
      <div className="flex-1 bg-gray-100 rounded-full h-2">
        <div className={`h-2 rounded-full transition-all ${color}`} style={{ width: `${Math.min(pct, 100)}%` }} />
      </div>
      <Badge variant={badge}>{Math.round(pct)}%</Badge>
    </div>
  );
}

export function BudgetList() {
  const toast = useToast();
  const queryClient = useQueryClient();
  const now = new Date();
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState<CreateBudgetRequest>({ category: 'FOOD', monthlyLimit: 0, month: now.getMonth() + 1, year: now.getFullYear() });

  const { data: budgets = [], isLoading } = useQuery({
    queryKey: ['budgets', month, year],
    queryFn: () => budgetApi.getByMonth(month, year),
  });

  const createMutation = useMutation({
    mutationFn: budgetApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      toast.success('Budget créé avec succès');
      setOpen(false);
      setForm(f => ({ ...f, monthlyLimit: 0 }));
    },
    onError: () => toast.error('Erreur lors de la création du budget'),
  });

  const deleteMutation = useMutation({
    mutationFn: budgetApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      toast.success('Budget supprimé');
    },
    onError: () => toast.error('Erreur lors de la suppression'),
  });

  const totalBudgeted = budgets.reduce((s, b) => s + b.monthlyLimit, 0);
  const totalSpent = budgets.reduce((s, b) => s + b.currentSpent, 0);

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Budgets</h1>
          <p className="text-sm text-gray-500 mt-0.5">{totalSpent.toFixed(2)} $ / {totalBudgeted.toFixed(2)} $ dépensé</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="flex gap-2">
            <select className="border rounded-lg px-3 py-1.5 text-sm" value={month} onChange={e => setMonth(Number(e.target.value))}>
              {Array.from({ length: 12 }, (_, i) => (
                <option key={i + 1} value={i + 1}>{new Date(2000, i).toLocaleString('fr-CA', { month: 'short' })}</option>
              ))}
            </select>
            <input className="border rounded-lg px-2 py-1.5 text-sm w-20" type="number" value={year} onChange={e => setYear(Number(e.target.value))} />
          </div>
          <button onClick={() => setOpen(true)} className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-indigo-700 transition-colors">
            <Plus size={16} /> Nouveau budget
          </button>
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-gray-400">Chargement...</div>
      ) : budgets.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-xl border">
          <div className="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <PiggyBank size={20} className="text-gray-400" />
          </div>
          <p className="text-gray-500">Aucun budget pour ce mois.</p>
          <button onClick={() => setOpen(true)} className="mt-4 text-indigo-600 text-sm hover:underline">Créer un budget</button>
        </div>
      ) : (
        <div className="grid gap-4">
          {budgets.map(budget => {
            const pct = budget.monthlyLimit > 0 ? (budget.currentSpent / budget.monthlyLimit) * 100 : 0;
            const isOver = budget.currentSpent > budget.monthlyLimit;
            return (
              <div key={budget.budgetId} className="bg-white rounded-xl border p-5">
                <div className="flex items-start justify-between mb-3">
                  <div>
                    <p className="font-semibold text-gray-800">{CATEGORY_LABELS[budget.category] ?? budget.category}</p>
                    <p className="text-sm text-gray-400 mt-0.5">
                      {budget.currentSpent.toFixed(2)} $ / {budget.monthlyLimit.toFixed(2)} $
                      {isOver && <span className="text-red-500 ml-2">• Dépassé!</span>}
                    </p>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="text-right">
                      <p className="text-sm font-semibold text-gray-700">{budget.remainingAmount.toFixed(2)} $ restant</p>
                    </div>
                    <button onClick={() => deleteMutation.mutate(budget.budgetId)} className="text-gray-300 hover:text-red-500 transition-colors">
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>
                <ProgressBar pct={pct} />
              </div>
            );
          })}
        </div>
      )}

      <Modal isOpen={open} onClose={() => setOpen(false)} title="Nouveau budget">
        <form onSubmit={e => { e.preventDefault(); createMutation.mutate({ ...form, month, year }); }} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Catégorie</label>
            <div className="grid grid-cols-2 gap-2">
              {CATEGORIES.map(c => (
                <button key={c} type="button" onClick={() => setForm(f => ({ ...f, category: c }))}
                  className={`py-2 px-3 rounded-lg border text-sm text-left transition-colors ${form.category === c ? 'bg-indigo-50 border-indigo-400 text-indigo-700 font-medium' : 'border-gray-200 text-gray-600 hover:bg-gray-50'}`}>
                  {CATEGORY_LABELS[c]}
                </button>
              ))}
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Limite mensuelle ($)</label>
            <input required type="number" min="1" step="0.01" className="w-full border rounded-lg px-3 py-2 text-sm" value={form.monthlyLimit} onChange={e => setForm(f => ({ ...f, monthlyLimit: Number(e.target.value) }))} placeholder="Ex: 400.00" />
          </div>
          <button type="submit" disabled={createMutation.isPending} className="w-full bg-indigo-600 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-indigo-700 disabled:opacity-60 transition-colors">
            {createMutation.isPending ? 'Création...' : 'Créer le budget'}
          </button>
        </form>
      </Modal>
    </div>
  );
}
