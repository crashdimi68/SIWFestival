import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import FilmSearchWidget from './components/FilmSearchWidget'

const mount = document.getElementById('film-react-root')

if (mount) {
  createRoot(mount).render(
    <StrictMode>
      <FilmSearchWidget />
    </StrictMode>,
  )
} else {
  console.warn('[film-react] mount point #film-react-root non trovato')
}
