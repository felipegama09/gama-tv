export const HERO_BANNERS = [
  {
    id: 'hero-1',
    tag: 'FILME EM DESTAQUE',
    title: 'DEADPOOL\n& WOLVERINE',
    year: '2024',
    genres: ['Ação', 'Comédia'],
    rating: '16',
    synopsis: 'Deadpool se une a Wolverine em uma missão que pode mudar o destino do multiverso. Mais ação, humor e caos em uma aventura épica da Marvel.',
    backdropUrl: 'https://images.unsplash.com/photo-1635805737707-575885ab0820?auto=format&fit=crop&w=1920&q=85',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 'hero-2',
    tag: 'FILME EM DESTAQUE',
    title: 'DUNA:\nPARTE DOIS',
    year: '2024',
    genres: ['Ficção', 'Aventura'],
    rating: '14',
    synopsis: 'Paul Atreides se une a Chani e aos Fremen em uma jornada de vingança contra os conspiradores que destruíram sua família.',
    backdropUrl: 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=1920&q=85',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 'hero-3',
    tag: 'SÉRIE EM DESTAQUE',
    title: 'THE LAST\nOF US',
    year: '2023',
    genres: ['Drama', 'Ação'],
    rating: '16',
    synopsis: 'Vinte anos após a destruição da civilização moderna, Joel é contratado para tirar Ellie de uma zona de quarentena opressiva.',
    backdropUrl: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=1920&q=85',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 'hero-4',
    tag: 'FILME EM DESTAQUE',
    title: 'OPPENHEIMER',
    year: '2023',
    genres: ['Drama', 'História'],
    rating: '16',
    synopsis: 'O físico J. Robert Oppenheimer trabalha com uma equipe de cientistas durante o Projeto Manhattan, levando ao desenvolvimento da bomba atômica.',
    backdropUrl: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1920&q=85',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 'hero-5',
    tag: 'SÉRIE EM DESTAQUE',
    title: 'HOUSE OF\nTHE DRAGON',
    year: '2024',
    genres: ['Drama', 'Fantasia'],
    rating: '18',
    synopsis: 'A história da Casa Targaryen, 200 anos antes dos eventos de Game of Thrones, marcada pela guerra civil conhecida como A Dança dos Dragões.',
    backdropUrl: 'https://images.unsplash.com/photo-1578632767115-351597cf2477?auto=format&fit=crop&w=1920&q=85',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  }
];

export const CATEGORIES = [
  {
    id: 'acao',
    name: 'Ação',
    icon: 'Clapperboard',
    image: 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=500&q=80'
  },
  {
    id: 'comedia',
    name: 'Comédia',
    icon: 'Smile',
    image: 'https://images.unsplash.com/photo-1514306191717-452ec28c7814?auto=format&fit=crop&w=500&q=80'
  },
  {
    id: 'drama',
    name: 'Drama',
    icon: 'Theater',
    image: 'https://images.unsplash.com/photo-1518676590629-3dcbd9c5a5c9?auto=format&fit=crop&w=500&q=80'
  },
  {
    id: 'infantil',
    name: 'Infantil',
    icon: 'Users',
    image: 'https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?auto=format&fit=crop&w=500&q=80'
  },
  {
    id: 'documentarios',
    name: 'Documentários',
    icon: 'FileText',
    image: 'https://images.unsplash.com/photo-1557050543-4d5f4e07ef46?auto=format&fit=crop&w=500&q=80'
  },
  {
    id: 'esportes',
    name: 'Esportes',
    icon: 'Trophy',
    image: 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=500&q=80'
  },
  {
    id: 'novelas',
    name: 'Novelas',
    icon: 'Tv',
    image: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=500&q=80'
  },
  {
    id: 'canais-abertos',
    name: 'Canais Abertos',
    icon: 'Radio',
    image: 'https://images.unsplash.com/photo-1519501025264-65ba15a82390?auto=format&fit=crop&w=500&q=80'
  }
];

export const FEATURED_MOVIES = [
  {
    id: 'm1',
    title: 'DEADPOOL',
    subtitle: '& WOLVERINE',
    titleStyle: 'text-yellow-400 font-black tracking-tighter',
    subtitleStyle: 'text-white font-extrabold text-[10px]',
    category: 'acao',
    year: '2024',
    rating: '16',
    poster: 'https://images.unsplash.com/photo-1635805737707-575885ab0820?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 'm2',
    title: 'D U N A',
    subtitle: 'PARTE DOIS',
    titleStyle: 'text-amber-100 font-light tracking-[0.25em]',
    subtitleStyle: 'text-amber-200/80 font-medium tracking-widest text-[9px]',
    category: 'acao',
    year: '2024',
    rating: '14',
    poster: 'https://images.unsplash.com/photo-1542224566-6e85f2e6772f?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 'm3',
    title: 'CORINGA',
    subtitle: 'DELÍRIO A DOIS',
    titleStyle: 'text-white font-serif font-extrabold tracking-tight',
    subtitleStyle: 'text-gray-300 font-semibold tracking-wider text-[9px]',
    category: 'drama',
    year: '2024',
    rating: '16',
    poster: 'https://images.unsplash.com/photo-1509248961158-e54f6934749c?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 'm4',
    title: 'GODZILLA\nE KONG',
    subtitle: 'O NOVO IMPÉRIO',
    titleStyle: 'text-gray-100 font-black leading-none tracking-tight',
    subtitleStyle: 'text-rose-400 font-bold tracking-widest text-[9px]',
    category: 'acao',
    year: '2024',
    rating: '12',
    poster: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 'm5',
    title: 'PLANETA\nDOS MACACOS',
    subtitle: 'O REINADO',
    titleStyle: 'text-stone-100 font-black leading-tight tracking-tight',
    subtitleStyle: 'text-amber-400 font-bold tracking-widest text-[9px]',
    category: 'acao',
    year: '2024',
    rating: '14',
    poster: 'https://images.unsplash.com/photo-1511497584788-876760111969?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 'm6',
    title: 'OPPENHEIMER',
    subtitle: 'UM FILME DE CHRISTOPHER NOLAN',
    titleStyle: 'text-amber-500 font-bold tracking-[0.18em] text-xs',
    subtitleStyle: 'text-amber-200/70 font-medium text-[8px]',
    category: 'drama',
    year: '2023',
    rating: '16',
    poster: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  }
];

export const FEATURED_SERIES = [
  {
    id: 's1',
    title: 'THE LAST OF US',
    subtitle: 'SÉRIE ORIGINAL',
    titleStyle: 'text-white font-black tracking-tight text-sm',
    subtitleStyle: 'text-gray-400 text-[8px] tracking-widest',
    category: 'drama',
    year: '2023',
    rating: '16',
    poster: 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 's2',
    title: 'ROUND 6',
    subtitle: '2ª TEMPORADA',
    titleStyle: 'text-white font-black tracking-wider text-sm',
    subtitleStyle: 'text-pink-500 font-bold text-[9px]',
    category: 'drama',
    year: '2024',
    rating: '16',
    poster: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 's3',
    title: 'HOUSE\nOF DRAGON',
    subtitle: 'HBO ORIGINAL',
    titleStyle: 'text-amber-300 font-serif font-bold leading-tight text-xs',
    subtitleStyle: 'text-amber-500/80 text-[8px] tracking-widest',
    category: 'drama',
    year: '2024',
    rating: '18',
    poster: 'https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 's4',
    title: 'Fallout',
    subtitle: 'PRIME ORIGINAL',
    titleStyle: 'text-yellow-300 font-black italic tracking-tight text-base',
    subtitleStyle: 'text-blue-300 text-[8px] font-bold tracking-widest',
    category: 'acao',
    year: '2024',
    rating: '16',
    poster: 'https://images.unsplash.com/photo-1508614589041-895b88991e3e?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 's5',
    title: 'THE BOYS',
    subtitle: '4ª TEMPORADA',
    titleStyle: 'text-red-600 font-black tracking-tighter text-base drop-shadow',
    subtitleStyle: 'text-gray-300 text-[8px] font-bold',
    category: 'acao',
    year: '2024',
    rating: '18',
    poster: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  {
    id: 's6',
    title: 'STRANGER\nTHINGS',
    subtitle: 'TEMPORADA FINAL',
    titleStyle: 'text-red-500 font-serif font-black leading-none tracking-widest text-xs',
    subtitleStyle: 'text-red-300/70 text-[8px]',
    category: 'drama',
    year: '2025',
    rating: '14',
    poster: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=500&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  }
];

export const LIVE_EPG_DATA = {
  channelName: 'TV Globo HD',
  currentProgram: {
    statusLabel: 'Programa atual',
    title: 'Jornal Nacional',
    timeRange: '20:30 - 21:20',
    rating: 'L',
    description: 'As principais notícias do Brasil e do mundo, com análises e reportagens exclusivas.',
    thumbnail: 'https://images.unsplash.com/photo-1585829365295-ab7cd400c167?auto=format&fit=crop&w=600&q=80',
    streamUrl: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
  },
  upcoming: [
    { time: '21:20', title: 'Renascer' },
    { time: '22:10', title: 'Big Brother Brasil' },
    { time: '23:15', title: 'Altas Horas' }
  ],
  defaultEpgUrl: 'http://vinagre.one/xmltv.php?username=91511958&password=748590'
};
