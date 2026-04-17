import { useState } from 'react';
import { Settings, Palette, Database, Check } from 'lucide-react';
import { useToast } from '../context/ToastContext';

interface AppSettings {
  currency: string;
}

const SETTINGS_KEY = 'pb_settings';
const DEFAULTS: AppSettings = { currency: '$' };

const CURRENCIES = [
  { value: '$',   label: '$ — Dollar canadien' },
  { value: 'US$', label: 'US$ — Dollar américain' },
  { value: '€',   label: '€ — Euro' },
  { value: '£',   label: '£ — Livre sterling' },
];

function loadSettings(): AppSettings {
  try {
    const raw = localStorage.getItem(SETTINGS_KEY);
    if (!raw) return { ...DEFAULTS };
    return { ...DEFAULTS, ...JSON.parse(raw) };
  } catch {
    return { ...DEFAULTS };
  }
}

function SectionHeader({ icon: Icon, title, color }: { icon: React.ElementType; title: string; color: string }) {
  return (
    <div className="flex items-center gap-3 mb-5">
      <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${color}`}>
        <Icon size={16} className="text-white" />
      </div>
      <h2 className="text-base font-semibold text-gray-900">{title}</h2>
    </div>
  );
}

export function SettingsPage() {
  const toast = useToast();
  const [settings, setSettings] = useState<AppSettings>(loadSettings);

  function handleSave() {
    localStorage.setItem(SETTINGS_KEY, JSON.stringify(settings));
    toast.success('Paramètres enregistrés');
  }

  function handleReset() {
    setSettings({ ...DEFAULTS });
    localStorage.setItem(SETTINGS_KEY, JSON.stringify(DEFAULTS));
    toast.success('Paramètres réinitialisés');
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Paramètres</h1>
        <p className="text-sm text-gray-500 mt-0.5">Personnalisez votre expérience PocketBudget</p>
      </div>

      {/* Display section */}
      <div className="bg-white rounded-xl border p-6">
        <SectionHeader icon={Settings} title="Affichage" color="bg-green-700" />

        {/* Currency */}
        <div className="mb-6">
          <label className="block text-sm font-medium text-gray-700 mb-3">Devise</label>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
            {CURRENCIES.map(c => (
              <button
                key={c.value}
                type="button"
                onClick={() => setSettings(s => ({ ...s, currency: c.value }))}
                className={`flex items-center justify-between px-4 py-3 rounded-lg border text-sm transition-colors ${
                  settings.currency === c.value
                    ? 'bg-green-50 border-green-500 text-green-800 font-medium'
                    : 'border-gray-200 text-gray-600 hover:bg-gray-50'
                }`}
              >
                <span>{c.label}</span>
                {settings.currency === c.value && <Check size={15} className="text-green-600 shrink-0" />}
              </button>
            ))}
          </div>
        </div>

        {/* Language (read-only) */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Langue</label>
          <div className="flex items-center gap-3 px-4 py-3 bg-gray-50 rounded-lg border border-gray-200">
            <span className="text-sm text-gray-700 font-medium">🇨🇦 Français (Canada)</span>
            <span className="text-xs text-gray-400 ml-auto">Seule langue disponible</span>
          </div>
        </div>
      </div>

      {/* Appearance section */}
      <div className="bg-white rounded-xl border p-6">
        <SectionHeader icon={Palette} title="Apparence" color="bg-teal-600" />
        <div className="flex items-center justify-between px-4 py-3 bg-gray-50 rounded-lg border border-gray-200">
          <div>
            <p className="text-sm font-medium text-gray-700">Thème</p>
            <p className="text-xs text-gray-400 mt-0.5">Thème clair actif</p>
          </div>
          <span className="text-xs text-gray-400 bg-gray-200 px-2 py-1 rounded-full">Bientôt disponible</span>
        </div>
      </div>

      {/* Data section */}
      <div className="bg-white rounded-xl border p-6">
        <SectionHeader icon={Database} title="Données" color="bg-gray-500" />
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-700">Réinitialiser les paramètres</p>
            <p className="text-xs text-gray-400 mt-0.5">Restaure tous les paramètres par défaut</p>
          </div>
          <button
            onClick={handleReset}
            className="text-sm text-red-600 border border-red-200 px-4 py-2 rounded-lg hover:bg-red-50 transition-colors"
          >
            Réinitialiser
          </button>
        </div>
      </div>

      {/* Save button */}
      <div className="flex items-center justify-between">
        <p className="text-xs text-gray-400">Les modifications s'appliquent au rechargement de la page</p>
        <button
          onClick={handleSave}
          className="bg-green-700 text-white px-6 py-2.5 rounded-lg text-sm font-medium hover:bg-green-800 transition-colors"
        >
          Enregistrer
        </button>
      </div>
    </div>
  );
}
