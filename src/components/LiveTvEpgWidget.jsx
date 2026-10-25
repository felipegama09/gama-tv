import React, { useState, useEffect } from 'react';
import { Tv, Settings, ChevronRight, Play, CheckCircle2 } from 'lucide-react';

export default function LiveTvEpgWidget({ epgData, onPlayLive }) {
  const [epgUrl, setEpgUrl] = useState(epgData.defaultEpgUrl);
  const [savedToast, setSavedToast] = useState(false);

  useEffect(() => {
    const storedUrl = localStorage.getItem('gama_tv_epg_url');
    if (storedUrl) setEpgUrl(storedUrl);
  }, []);

  const handleSaveEpg = (e) => {
    e.preventDefault();
    localStorage.setItem('gama_tv_epg_url', epgUrl);
    setSavedToast(true);
    setTimeout(() => setSavedToast(false), 2500);
  };

  const { currentProgram, upcoming } = epgData;

  return (
    <aside className="flex flex-col gap-3.5 h-full">
      <div className="bg-[#12141C]/95 border border-[#232634] rounded-2xl p-4 shadow-card-dark flex-1 flex flex-col justify-between">
        <div>
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              <Tv className="w-4 h-4 text-gray-300" />
              <h3 className="text-white font-bold text-xs md:text-sm">EPG - Programação da TV</h3>
            </div>
            <button onClick={() => onPlayLive(currentProgram)} className="text-gray-400 hover:text-white text-xs font-medium transition-colors">Ver todos</button>
          </div>
          <div className="grid grid-cols-12 gap-3.5 items-center mb-4">
            <div onClick={() => onPlayLive(currentProgram)} className="col-span-5 relative aspect-video rounded-xl overflow-hidden border border-[#2B3042] cursor-pointer group">
              <img src={currentProgram.thumbnail} alt={currentProgram.title} className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
              <div className="absolute inset-0 bg-gradient-to-tr from-blue-950/70 via-transparent to-black/30" />
              <div className="absolute left-2.5 top-1/2 -translate-y-1/2 w-8 h-8 rounded-full bg-gradient-to-tr from-blue-500 via-indigo-200 to-white shadow-md flex items-center justify-center opacity-90">
                <div className="w-5 h-3.5 rounded-sm bg-gradient-to-r from-orange-500 via-yellow-400 to-green-500 flex items-center justify-center">
                  <div className="w-2 h-2 rounded-full bg-white" />
                </div>
              </div>
              <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 flex items-center justify-center transition-opacity">
                <div className="w-8 h-8 rounded-full bg-brand-red flex items-center justify-center shadow-red-glow">
                  <Play className="w-3.5 h-3.5 text-white fill-white ml-0.5" />
                </div>
              </div>
            </div>
            <div className="col-span-7 flex flex-col">
              <span className="text-brand-red text-[10px] font-bold tracking-wide mb-0.5">{currentProgram.statusLabel}</span>
              <h4 className="text-white font-extrabold text-sm md:text-base leading-tight mb-1.5">{currentProgram.title}</h4>
              <div className="flex items-center justify-between mb-1.5">
                <div className="flex items-center gap-2">
                  <span className="px-2 py-0.5 rounded bg-brand-red text-white font-extrabold text-[9px] uppercase tracking-wider animate-pulse">AO VIVO</span>
                  <span className="text-gray-300 text-[11px] font-medium">{currentProgram.timeRange}</span>
                </div>
                <span className="w-4 h-4 rounded bg-emerald-600 text-white font-black text-[10px] flex items-center justify-center">{currentProgram.rating}</span>
              </div>
              <p className="text-gray-400 text-[11px] leading-snug line-clamp-2">{currentProgram.description}</p>
            </div>
          </div>
        </div>
        <div className="divide-y divide-[#1E212D] border-t border-[#1E212D] pt-1">
          {upcoming.map((slot, index) => (
            <div key={index} onClick={() => onPlayLive(currentProgram)} className="py-2 flex items-center gap-2.5 text-xs hover:bg-white/5 px-2 rounded-lg cursor-pointer transition-colors">
              <span className="text-gray-400 font-semibold w-10">{slot.time}</span>
              <ChevronRight className="w-3.5 h-3.5 text-gray-500" />
              <span className="text-gray-200 font-medium truncate">{slot.title}</span>
            </div>
          ))}
        </div>
      </div>
      <form onSubmit={handleSaveEpg} className="bg-[#12141C]/95 border border-[#232634] rounded-2xl p-4 shadow-card-dark">
        <div className="flex items-center justify-between mb-2.5">
          <div className="flex items-center gap-2">
            <Settings className="w-4 h-4 text-gray-300" />
            <h3 className="text-white font-bold text-xs md:text-sm">Configurações do EPG</h3>
          </div>
          {savedToast && (
            <span className="flex items-center gap-1 text-emerald-400 text-[11px] font-semibold">
              <CheckCircle2 className="w-3.5 h-3.5" /> Salvo!
            </span>
          )}
        </div>
        <div className="flex items-center gap-2.5">
          <input type="text" value={epgUrl} onChange={(e) => setEpgUrl(e.target.value)} placeholder="http://servidor/xmltv.php?username=..." className="tv-focusable flex-1 bg-[#0C0E14] text-gray-200 text-[11px] px-3.5 py-2.5 rounded-xl border border-[#232634] focus:border-brand-red focus:outline-none truncate" />
          <button type="submit" className="tv-focusable px-5 py-2.5 rounded-xl bg-brand-red hover:bg-brand-redDark text-white font-bold text-xs shadow-red-glow-sm transition-all shrink-0">Salvar</button>
        </div>
      </form>
    </aside>
  );
}
