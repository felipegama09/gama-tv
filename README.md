# 📺 GAMA TV — Plataforma IPTV, VOD & Streaming (10-Foot UI)

Interface de alta performance construída com **React 18 + Vite + Tailwind CSS + HLS.js**, projetada sob medida para Smart TVs (Android TV, Samsung Tizen, LG WebOS) e navegadores Widescreen.

## 📂 Estrutura de Pastas

```text
gama-tv/
├── .gitignore
├── index.html
├── package.json
├── postcss.config.js
├── README.md
├── tailwind.config.js
├── vite.config.js
└── src/
    ├── App.jsx
    ├── index.css
    ├── main.jsx
    ├── components/
    │   ├── CategoryBar.jsx
    │   ├── GamaLogo.jsx
    │   ├── HeroBanner.jsx
    │   ├── LiveTvEpgWidget.jsx
    │   ├── MediaRow.jsx
    │   ├── Navbar.jsx
    │   └── VideoPlayerModal.jsx
    └── data/
        └── mockData.js
```

## 🚀 Como importar diretamente no GitHub Dev (`github.dev`)

1. Extraia o arquivo **`gama-tv-projeto-completo.zip`** no seu computador.
2. Abra o seu repositório no **github.dev** (pressionando a tecla `.` no repositório do GitHub).
3. Arraste toda a estrutura extraída (arquivos da raiz e a pasta `src`) diretamente para a barra lateral **Explorer** do GitHub Dev. O editor criará automaticamente todas as subpastas (`src/components`, `src/data`) nos locais corretos!
4. Faça o commit das alterações pelo ícone de Controle de Código-Fonte (`Ctrl+Shift+G`).
5. Para rodar o projeto em nuvem (Codespaces) ou localmente:
   ```bash
   npm install
   npm run dev
   ```
