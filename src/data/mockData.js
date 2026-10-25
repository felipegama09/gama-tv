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
  }
];

export const CATEGORIES = [
  { id: 'acao', name: 'Ação', icon: 'Clapperboard', image: 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=500&q=80' },
  { id: 'comedia', name: 'Comédia', icon: 'Smile', image: 'https://images.unsplash.com/photo-1514306191717-452ec28c7814?auto=format&fit=crop&w=500&q=80' }
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
    { time: '22:10', title: 'Big Brother Brasil' }
  ],
  defaultEpgUrl: 'http://vinagre.one/xmltv.php?username=91511958&password=748590'
};
