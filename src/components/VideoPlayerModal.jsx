import React, { useEffect, useRef } from 'react';
import Hls from 'hls.js';
import { X, Tv } from 'lucide-react';

export default function VideoPlayerModal({ media, onClose }) {
  const videoRef = useRef(null);

  useEffect(() => {
    if (!media || !videoRef.current) return;

    const video = videoRef.current;
    const streamUrl =
      media.streamUrl || 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8';

    let hls;

    if (Hls.isSupported()) {
      hls = new Hls({
        enableWorker: true,
        lowLatencyMode: true
      });
      hls.loadSource(streamUrl);
      hls.attachMedia(video);
      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        video.play().catch(() => {});
      });
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      video.src = streamUrl;
      video.addEventListener('loadedmetadata', () => {
        video.play().catch(() => {});
      });
    }

    const handleKeyDown = (e) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleKeyDown);

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      if (hls) {
        hls.destroy();
      }
    };
  }, [media, onClose]);

  if (!media) return null;

  return (
    <div className="fixed inset-0 z-50 bg-black/90 backdrop-blur-md flex items-center justify-center p-4 md:p-8 animate-fadeIn">
      <div className="relative w-full max-w-5xl bg-[#121318] border border-[#232634] rounded-2xl overflow-hidden shadow-2xl">
        {/* Barra Superior do Player */}
        <div className="flex items-center justify-between px-5 py-3.5 bg-[#0A0B0E] border-b border-[#232634]">
          <div className="flex items-center gap-2.5">
            <Tv className="w-4 h-4 text-brand-red" />
            <span className="text-white font-bold text-sm tracking-wide uppercase">
              {media.title?.replace('\n', ' ')}
            </span>
            <span className="px-2 py-0.5 rounded bg-brand-red/20 border border-brand-red text-brand-red text-[10px] font-bold">
              HLS STREAM
            </span>
          </div>

          <button
            onClick={onClose}
            className="tv-focusable p-1.5 rounded-lg bg-white/5 hover:bg-brand-red text-gray-300 hover:text-white transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Elemento de Vídeo HTML5 */}
        <div className="relative aspect-video bg-black">
          <video
            ref={videoRef}
            controls
            autoPlay
            playsInline
            className="w-full h-full object-contain"
          />
        </div>
      </div>
    </div>
  );
}
