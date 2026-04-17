import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { User, Lock, Eye, EyeOff } from 'lucide-react';
import { userApi } from '../api';
import { getUsername } from '../auth';
import { useToast } from '../context/ToastContext';

function PasswordInput({ label, value, onChange, placeholder }: {
  label: string;
  value: string;
  onChange: (v: string) => void;
  placeholder?: string;
}) {
  const [show, setShow] = useState(false);
  return (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
      <div className="relative">
        <input
          required
          type={show ? 'text' : 'password'}
          value={value}
          onChange={e => onChange(e.target.value)}
          placeholder={placeholder}
          className="w-full border rounded-lg px-3 py-2 text-sm pr-10 focus:outline-none focus:ring-2 focus:ring-green-500"
        />
        <button
          type="button"
          onClick={() => setShow(s => !s)}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
        >
          {show ? <EyeOff size={16} /> : <Eye size={16} />}
        </button>
      </div>
    </div>
  );
}

export function ProfilePage() {
  const username = getUsername();
  const toast = useToast();
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [confirmError, setConfirmError] = useState('');

  const changeMutation = useMutation({
    mutationFn: userApi.changePassword,
    onSuccess: () => {
      toast.success('Mot de passe modifié avec succès');
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
      setConfirmError('');
    },
    onError: () => toast.error('Mot de passe actuel incorrect'),
  });

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      setConfirmError('Les mots de passe ne correspondent pas');
      return;
    }
    if (newPassword.length < 6) {
      setConfirmError('Le nouveau mot de passe doit contenir au moins 6 caractères');
      return;
    }
    setConfirmError('');
    changeMutation.mutate({ oldPassword, newPassword });
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Profil</h1>
        <p className="text-sm text-gray-500 mt-0.5">Gérez vos informations personnelles</p>
      </div>

      {/* Profile card */}
      <div className="bg-white rounded-xl border p-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-16 h-16 rounded-full bg-green-700 flex items-center justify-center text-white text-2xl font-bold">
            {username?.[0]?.toUpperCase() ?? '?'}
          </div>
          <div>
            <h2 className="text-lg font-semibold text-gray-900">{username}</h2>
            <span className="inline-flex items-center gap-1.5 text-xs text-green-700 bg-green-50 px-2 py-0.5 rounded-full mt-1">
              <User size={11} />
              Utilisateur actif
            </span>
          </div>
        </div>
        <div className="border-t pt-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <p className="text-xs text-gray-400 mb-1">Nom d'utilisateur</p>
              <p className="text-sm font-medium text-gray-800 bg-gray-50 rounded-lg px-3 py-2">{username}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400 mb-1">Rôle</p>
              <p className="text-sm font-medium text-gray-800 bg-gray-50 rounded-lg px-3 py-2">Utilisateur standard</p>
            </div>
          </div>
        </div>
      </div>

      {/* Change password card */}
      <div className="bg-white rounded-xl border p-6">
        <div className="flex items-center gap-3 mb-5">
          <div className="w-8 h-8 rounded-lg bg-green-100 flex items-center justify-center">
            <Lock size={16} className="text-green-700" />
          </div>
          <div>
            <h2 className="text-base font-semibold text-gray-900">Changer le mot de passe</h2>
            <p className="text-xs text-gray-400">Minimum 6 caractères</p>
          </div>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4 max-w-sm">
          <PasswordInput
            label="Mot de passe actuel"
            value={oldPassword}
            onChange={setOldPassword}
            placeholder="••••••••"
          />
          <PasswordInput
            label="Nouveau mot de passe"
            value={newPassword}
            onChange={v => { setNewPassword(v); setConfirmError(''); }}
            placeholder="••••••••"
          />
          <PasswordInput
            label="Confirmer le nouveau mot de passe"
            value={confirmPassword}
            onChange={v => { setConfirmPassword(v); setConfirmError(''); }}
            placeholder="••••••••"
          />
          {confirmError && (
            <p className="text-xs text-red-500">{confirmError}</p>
          )}
          <button
            type="submit"
            disabled={changeMutation.isPending}
            className="w-full bg-green-700 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-green-800 disabled:opacity-60 transition-colors"
          >
            {changeMutation.isPending ? 'Modification...' : 'Modifier le mot de passe'}
          </button>
        </form>
      </div>
    </div>
  );
}
