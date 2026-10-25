import React from 'react';
import {
  Clapperboard,
  Smile,
  Theater,
  Users,
  FileText,
  Trophy,
  Tv,
  Radio
} from 'lucide-react';

const ICON_MAP = {
  Clapperboard,
  Smile,
  Theater,
  Users,
  FileText,
  Trophy,
  Tv,
  Radio
};

export default function CategoryBar({
  categories,
  selectedCategory,
  onSelectCategory
}) {
  return (
    <section className="px-6 mb-5">
      <h2 className="text-white font-bold text-sm mb-2.5 tracking-tight">
        Categorias
      </h2>

      <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-3">
        {categories.map((cat) => {
          const IconComponent = ICON_MAP[cat.icon] || Clapperboard;
          const isSelected = selectedCategory === cat.id;

          return (
            <button
              key={cat.id}
              onClick={() =>
                onSelectCategory(isSelected ? null : cat.id)
              }
              className={`tv-focusable relative h-20 rounded-xl overflow-hidden group text-left transition-all duration-200 ${
                isSelected
                  ? 'ring-2 ring-brand-red shadow-red-glow border border-brand-red'
                  : 'border border-[#232634] hover:border-gray-500'
              }`}
            >
              {/* Imagem de Fundo do Card */}
              <img
                src={cat.image}
                alt={cat.name}
                className="w-full h-full object-cover object-center group-hover:scale-110 transition-transform duration-500"
              />

              {/* Gradiente escuro para leitura do rótulo */}
              <div
                className={`absolute inset-0 transition-opacity ${
                  isSelected
                    ? 'bg-gradient-to-t from-black/90 via-brand-red/25 to-black/20'
                    : 'bg-gradient-to-t from-black/90 via-black/45 to-transparent'
                }`}
              />

              {/* Ícone + Nome da Categoria na Base Esquerda */}
              <div className="absolute bottom-2.5 left-3 right-2 flex items-center gap-2">
                <div className="p-1 rounded-md bg-white/15 backdrop-blur-sm">
                  <IconComponent className="w-3.5 h-3.5 text-white" />
                </div>
                <span className="text-white font-bold text-xs truncate drop-shadow">
                  {cat.name}
                </span>
              </div>
            </button>
          );
        })}
      </div>
    </section>
  );
}
