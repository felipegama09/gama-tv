import React, { useEffect } from 'react';
import { Play, Plus, Check } from 'lucide-react';

export default function HeroBanner({
  banners,
  activeIndex,
  setActiveIndex,
  onPlay,
  onToggleMyList,
  myList
}) {
  const current = banners[activeIndex] || banners[0];
  const isInList = myList.some((item) => item.id === current.id);

  // Rotação automática do carrossel a cada 8 segundos
  useEffect(() => {
    const timer = setInterval(() => {
      setActiveIndex((prev) => (prev + 1) % banners.length);
    }, 8000);
    return () => clearInterval(timer);
  }, [banners.length, setActiveIndex]);

  return (
    <section className="px-6 mb-5">
      <div className="relative w-full h-[340px] md:h-[370px] rounded-2xl overflow-hidden border border-[#232634] shadow-card-dark group">
        {/* Imagem de Fundo Panorâmica com foco à direita */}
        <img
          src={current.backdropUrl}
          alt={current.title}
          className="w-full h-full object-cover object-center scale-105 transition-transform duration-1000"
        />

        {/* Camadas de Degradê para leitura perfeita à esquerda e na base */}
        <div className="absolute inset-0 bg-gradient-to-r from-[#0A0B0E] via-[#0A0B0E]/85 to-transparent w-[75%]" />
        <div className="absolute inset-0 bg-gradient-to-t from-[#0A0B0E] via-transparent to-black/30" />

        {/* Brilho ambiental avermelhado sutil no centro-direito */}
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_65%_40%,rgba(229,9,20,0.18),transparent_55%)] pointer-events-none" />

        {/* Conteúdo Textual do Banner */}
        <div className="absolute inset-0 p-7 md:p-9 flex flex-col justify-center max-w-xl z-10">
          <span className="text-brand-red font-bold text-[11px] tracking-widest uppercase mb-1.5">
            {current.tag}
          </span>

          <h1 className="font-display font-black text-3xl md:text-5xl text-white uppercase leading-[0.95] tracking-tight drop-shadow-[0_4px_12px_rgba(0,0,0,0.9)] whitespace-pre-line mb-3">
            {current.title}
          </h1>

          {/* Metadados Inline */}
          <div className="flex items-center gap-2.5 text-xs mb-3">
            <span className="text-white font-semibold">{current.year}</span>
            {current.genres.map((genre) => (
              <span
                key={genre}
                className="px-2.5 py-0.5 rounded-md bg-white/10 border border-white/10 text-gray-200 text-[11px] font-medium backdrop-blur-sm"
              >
                {genre}
              </span>
            ))}
            <span className="px-1.5 py-0.5 rounded bg-white/15 border border-white/20 text-white text-[11px] font-bold">
              {current.rating}
            </span>
          </div>

          {/* Sinopse em até 3 linhas */}
          <p className="text-gray-300 text-xs md:text-[13px] leading-relaxed line-clamp-3 mb-5 max-w-md">
            {current.synopsis}
          </p>

          {/* Botões de Ação (CTAs) */}
          <div className="flex items-center gap-3">
            <button
              onClick={() => onPlay(current)}
              className="tv-focusable flex items-center gap-2.5 px-6 py-2.5 rounded-xl bg-brand-red hover:bg-brand-redDark text-white font-bold text-xs shadow-red-glow transition-all transform active:scale-95"
            >
              <Play className="w-4 h-4 fill-white" />
              <span>Assistir agora</span>
            </button>

            <button
              onClick={() => onToggleMyList(current)}
              className="tv-focusable flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#161821]/85 hover:bg-[#232634] text-white font-semibold text-xs border border-[#2E3244] backdrop-blur-md transition-all"
            >
              {isInList ? (
                <>
                  <Check className="w-4 h-4 text-brand-red" />
                  <span>Na Minha Lista</span>
                </>
              ) : (
                <>
                  <Plus className="w-4 h-4 text-gray-300" />
                  <span>Minha Lista</span>
                </>
              )}
            </button>
          </div>
        </div>

        {/* Indicadores de Paginação (Dots) na Base Central */}
        <div className="absolute bottom-3.5 left-1/2 -translate-x-1/2 flex items-center gap-2 z-20">
          {banners.map((b, idx) => (
            <button
              key={b.id}
              onClick={() => setActiveIndex(idx)}
              aria-label={`Slide ${idx + 1}`}
              className={`h-2 rounded-full transition-all duration-300 ${
                idx === activeIndex
                  ? 'w-2.5 bg-brand-red shadow-[0_0_8px_#E50914]'
                  : 'w-2 bg-gray-500/60 hover:bg-gray-300'
              }`}
            />
          ))}
        </div>
      </div>
    </section>
  );
}
