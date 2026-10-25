/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          red: '#E50914',
          redDark: '#B80710',
          dark: '#0A0B0E',
          surface: '#121318',
          card: '#161821',
          border: '#232634',
          muted: '#9E9EA7'
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        display: ['Montserrat', 'Inter', 'sans-serif']
      },
      boxShadow: {
        'red-glow': '0 0 18px rgba(229, 9, 20, 0.45)',
        'red-glow-sm': '0 0 10px rgba(229, 9, 20, 0.35)',
        'card-dark': '0 10px 30px -5px rgba(0, 0, 0, 0.7)'
      }
    },
  },
  plugins: [],
};
