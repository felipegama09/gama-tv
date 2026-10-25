import React, { useState, useMemo } from 'react';
import Navbar from './components/Navbar';
import HeroBanner from './components/HeroBanner';
import CategoryBar from './components/CategoryBar';
import MediaRow from './components/MediaRow';
import LiveTvEpgWidget from './components/LiveTvEpgWidget';
import VideoPlayerModal from './components/VideoPlayerModal';
import {
  HERO_BANNERS,
  CATEGORIES,
  FEATURED_MOVIES,
  FEATURED_SERIES,
  LIVE_EPG_DATA
} from './data/mockData';

export default function App() {
  const [activeTab, setActiveTab] = useState('home');
  const [activeBannerIndex, setActiveBannerIndex] = useState(0);
  const [selectedCategory, setSelectedCategory] = useState('acao');
  const [searchQuery, setSearchQuery] = useState('');
  const [playingMedia, setPlayingMedia] = useState(null);
  const [myList, setMyList] = useState([]);

  const handleToggleMyList = (item) => {
    setMyList((prev) => {
      const exists = prev.some((i) => i.id === item.id);
      if (exists) {
        return prev.filter((i) => i.id !== item.id);
      }
      return [...prev, item];
    });
  };

  const filteredMovies = useMemo(() => {
    return FEATURED_MOVIES.filter((movie) => {
      const matchesSearch =
        movie.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        movie.subtitle.toLowerCase().includes(searchQuery.toLowerCase());
      return matchesSearch;
    });
  }, [searchQuery]);

  const filteredSeries = useMemo(() => {
    return FEATURED_SERIES.filter((serie) => {
      const matchesSearch =
        serie.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        serie.subtitle.toLowerCase().includes(searchQuery.toLowerCase());
      return matchesSearch;
    });
  }, [searchQuery]);

  return (
    <div className="min-h-screen bg-brand-dark text-white flex flex-col justify-between pb-6 relative overflow-x-hidden">
      <div className="pointer-events-none fixed top-0 left-1/4 w-[600px] h-[300px] bg-brand-red/5 blur-[150px] rounded-full" />

      <div className="max-w-[1600px] w-full mx-auto">
        <Navbar
          activeTab={activeTab}
          setActiveTab={setActiveTab}
          searchQuery={searchQuery}
          setSearchQuery={setSearchQuery}
          myListCount={myList.length}
          onOpenFilters={() => setSelectedCategory(null)}
        />

        <HeroBanner
          banners={HERO_BANNERS}
          activeIndex={activeBannerIndex}
          setActiveIndex={setActiveBannerIndex}
          onPlay={(item) => setPlayingMedia(item)}
          onToggleMyList={handleToggleMyList}
          myList={myList}
        />

        <CategoryBar
          categories={CATEGORIES}
          selectedCategory={selectedCategory}
          onSelectCategory={setSelectedCategory}
        />

        <main className="px-6 grid grid-cols-1 lg:grid-cols-12 gap-6 items-stretch">
          <div className="lg:col-span-8 flex flex-col justify-between">
            <MediaRow
              title="Filmes em destaque"
              items={filteredMovies}
              onSelectMedia={(item) => setPlayingMedia(item)}
              onSeeAll={() => setActiveTab('filmes')}
            />
            <MediaRow
              title="Séries"
              items={filteredSeries}
              onSelectMedia={(item) => setPlayingMedia(item)}
              onSeeAll={() => setActiveTab('series')}
            />
          </div>

          <div className="lg:col-span-4">
            <LiveTvEpgWidget
              epgData={LIVE_EPG_DATA}
              onPlayLive={(liveItem) => setPlayingMedia(liveItem)}
            />
          </div>
        </main>
      </div>

      <VideoPlayerModal
        media={playingMedia}
        onClose={() => setPlayingMedia(null)}
      />
    </div>
  );
}
