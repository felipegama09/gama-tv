import React, { useRef } from 'react';
import { ChevronRight, Play } from 'lucide-react';

export default function MediaRow({ title, items, onSelectMedia, onSeeAll }) {
  const rowRef = useRef(null);
  const handleScrollRight = () => { if (rowRef.current) { rowRef.current.scrollBy({ left: 420, behavior: 'smooth' }); } };

  return (
    <div className="mb-5 last:mb-0 relative">
      <div className="flex items-center justify-between mb-2.5 pr-2">
        <h3 className="text-white font-bold text-sm tracking-tight">{title}</h3>
        <button onClick={onSeeAll} className="flex items-center gap-1 text-gray-400 hover:text-white text-xs font-medium transition-colors">
          <span>Ver todos</span><ChevronRight className="w-3.5 h-3.5" />
        </button>
      </div>
      <div className="relative group/row">
        <div ref={rowRef} className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-3 overflow-x-auto no-scrollbar py-1 pr-6">
          {items.map((item) => (
            <div key={item.id} onClick={() => onSelectMedia(item)} tabIndex={0} onKeyDown={(e) => e.key === 'Enter' && onSelectMedia(item)} className="tv-focusable relative aspect-[2/2.5] rounded-xl overflow-hidden border border-[#232634] bg-brand-card cursor-pointer group transition-all duration-300 hover:scale-[1.04] hover:border-brand-red hover:shadow-red-glow-sm">
              <img src={item.poster} alt={item.title} className="w-full h-full object-cover object-center group-hover:scale-110 transition-transform duration-500" />
              <div className="absolute inset-0 bg-gradient-to-t from-black/95 via-black/30 to-transparent" />
              <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                <div className="w-10 h-10 rounded-full bg-brand-red flex items-center justify-center shadow-red-glow transform scale-75 group-hover:scale-100 transition-transform">
                  <Play className="w-4 h-4 text-white fill-white ml-0.5" />
                </div>
              </div>
              <div className="absolute bottom-2.5 inset-x-2 text-center flex flex-col items-center justify-end pointer-events-none">
                <span className={`uppercase whitespace-pre-line text-center leading-none mb-0.5 drop-shadow-[0_2px_4px_rgba(0,0,0,0.9)] ${item.titleStyle || 'text-white font-black text-sm'}`}>{item.title}</span>
                {item.subtitle && <span className={`uppercase tracking-wider ${item.subtitleStyle || 'text-gray-300 text-[9px] font-bold'}`}>{item.subtitle}</span>}
              </div>
            </div>
          ))}
        </div>
        <button onClick={handleScrollRight} aria-label="Avançar trilha" className="tv-focusable absolute -right-2 top-1/2 -translate-y-1/2 w-7 h-7 rounded-full bg-[#1C1F2B]/90 hover:bg-brand-red text-gray-300 hover:text-white border border-[#2E3346] flex items-center justify-center shadow-lg z-20 transition-all">
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
}
