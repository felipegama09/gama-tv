import React from 'react';
import { Home, Tv, Clapperboard, Film, LayoutGrid, Calendar, Clock, Heart, Search, Filter } from 'lucide-react';
import GamaLogo from './GamaLogo';

const NAV_ITEMS = [
  { id: 'home', label: 'Home', icon: Home },
  { id: 'tv', label: 'TV', icon: Tv },
  { id: 'filmes', label: 'Filmes', icon: Clapperboard },
  { id: 'series', label: 'Séries', icon: Film },
  { id: 'categorias', label: 'Categorias', icon: LayoutGrid },
  { id: 'epg', label: 'EPG', icon: Calendar },
  { id: 'recentes', label: 'Recentes', icon: Clock },
  { id: 'minha-lista', label: 'Minha Lista', icon: Heart }
];

export default function Navbar({ activeTab, setActiveTab, searchQuery, setSearchQuery, myListCount, onOpenFilters }) {
  return (
    <header className="w-full px-6 pt-4 pb-3 flex flex-wrap items-center justify-between gap-4 bg-gradient-to-b from-black/90 to-transparent sticky top-0 z-40 backdrop-blur-sm">
      <div onClick={() => setActiveTab('home')}>
        <GamaLogo />
      </div>
      <nav className="flex items-center gap-1.5 overflow-x-auto no-scrollbar py-1">
        {NAV_ITEMS.map((item) => {
          const IconComponent = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`tv-focusable flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold transition-all duration-200 whitespace-nowrap ${
                isActive ? 'bg-[#2A080C] text-white border border-brand-red shadow-red-glow-sm' : 'text-gray-300 hover:text-white hover:bg-white/5 border border-transparent'
              }`}
            >
              <IconComponent className={`w-4 h-4 ${isActive ? 'text-brand-red' : 'text-gray-400'}`} />
              <span>{item.label}</span>
              {item.id === 'minha-lista' && myListCount > 0 && (
                <span className="ml-0.5 px-1.5 py-0.2 bg-brand-red text-white text-[10px] rounded-full font-bold">{myListCount}</span>
              )}
            </button>
          );
        })}
      </nav>
      <div className="flex items-center gap-2.5">
        <div className="relative flex items-center">
          <Search className="w-4 h-4 text-gray-400 absolute left-3.5 pointer-events-none" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Pesquisar conteúdos..."
            className="tv-focusable bg-[#13151C]/90 text-xs text-white placeholder-gray-400 pl-9 pr-4 py-2.5 rounded-xl border border-[#232634] focus:border-brand-red focus:outline-none w-52 lg:w-64 transition-all"
          />
        </div>
        <button onClick={onOpenFilters} className="tv-focusable flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-semibold bg-[#380A0E] hover:bg-brand-red text-white border border-brand-red shadow-red-glow-sm transition-all">
          <Filter className="w-3.5 h-3.5" />
          <span>Filtros</span>
        </button>
      </div>
    </header>
  );
}
