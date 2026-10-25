import React from 'react';

export default function GamaLogo() {
  return (
    <div className="flex items-center gap-2.5 select-none cursor-pointer group">
      <div className="relative w-10 h-10 flex items-center justify-center transition-transform duration-300 group-hover:scale-105">
        <svg viewBox="0 0 120 120" className="w-full h-full drop-shadow-[0_0_12px_rgba(229,9,20,0.65)]" fill="none" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="gamaRedMain" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#FF3B3B" />
              <stop offset="45%" stopColor="#E50914" />
              <stop offset="85%" stopColor="#8A0007" />
              <stop offset="100%" stopColor="#420003" />
            </linearGradient>
            <linearGradient id="gamaRedInner" x1="100%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" stopColor="#6E0005" />
              <stop offset="50%" stopColor="#B80710" />
              <stop offset="100%" stopColor="#FF2A2A" />
            </linearGradient>
            <linearGradient id="gamaHighlight" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" stopColor="#FFFFFF" stopOpacity="0.8" />
              <stop offset="100%" stopColor="#FF4D4D" stopOpacity="0" />
            </linearGradient>
          </defs>
          <path d="M42 26 C26 42, 26 78, 42 94 C54 106, 78 106, 94 90 L64 68 L42 82 Z" fill="url(#gamaRedInner)" />
          <path d="M96 34 C80 14, 46 14, 26 34 C8 52, 8 82, 28 98 C48 114, 82 110, 100 90 L100 56 L62 56 L62 74 L82 74 C72 88, 50 90, 38 78 C26 66, 26 46, 40 34 C54 22, 74 26, 84 40 Z" fill="url(#gamaRedMain)" stroke="#FF6B6B" strokeWidth="1.5" />
          <path d="M92 32 C76 16, 46 16, 28 34 C14 48, 14 70, 22 86" stroke="url(#gamaHighlight)" strokeWidth="2.5" strokeLinecap="round" />
        </svg>
      </div>
      <span className="font-display font-black text-2xl tracking-wider bg-gradient-to-b from-[#FF4B4B] via-[#E50914] to-[#99050C] bg-clip-text text-transparent drop-shadow-[0_2px_10px_rgba(229,9,20,0.45)]" style={{ WebkitTextStroke: '0.5px rgba(255, 120, 120, 0.35)' }}>
        GAMA
      </span>
    </div>
  );
}
