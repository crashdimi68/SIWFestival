import { useEffect, useMemo, useState } from 'react'
import { getFilm } from '../api'
import type { Film } from '../types'

const inputStyle: React.CSSProperties = {
  padding: '0.6rem 1rem',
  background: '#0b0509',
  border: '1px solid #5a2338',
  borderRadius: '6px',
  color: '#ffffff',
  fontFamily: 'inherit',
  fontSize: '0.9rem',
  minWidth: '260px',
  outline: 'none',
}

const filtersRowStyle: React.CSSProperties = {
  display: 'flex',
  gap: '1rem',
  flexWrap: 'wrap',
  marginBottom: '1.5rem',
}

export default function FilmSearchWidget() {
  const [film, setFilm] = useState<Film[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [errore, setErrore] = useState<string | null>(null)
  const [titolo, setTitolo] = useState<string>('')
  const [genereSelezionato, setGenereSelezionato] = useState<string>('')

  // La ricerca per titolo viene fatta dal backend (GET /api/movies?titolo=...),
  // con un piccolo debounce per non sparare una richiesta a ogni tasto premuto.
  useEffect(() => {
    let annullato = false
    const timer = setTimeout(async () => {
      try {
        setLoading(true)
        const dati = await getFilm(titolo)
        if (!annullato) {
          setFilm(dati)
          setErrore(null)
        }
      } catch (e) {
        if (!annullato) setErrore('Impossibile caricare i film.')
      } finally {
        if (!annullato) setLoading(false)
      }
    }, 300)

    return () => {
      annullato = true
      clearTimeout(timer)
    }
  }, [titolo])

  const generiDisponibili = useMemo(() => {
    const generi = new Set(film.map(f => f.genere).filter(Boolean))
    return Array.from(generi).sort()
  }, [film])

  // Il filtro per genere e' invece puramente lato client sui risultati gia' ottenuti.
  const filmFiltrati = useMemo(() => {
    if (!genereSelezionato) return film
    return film.filter(f => f.genere === genereSelezionato)
  }, [film, genereSelezionato])

  if (errore) {
    return (
      <p style={{ color: '#f87171', textAlign: 'center', padding: '1rem 0' }}>{errore}</p>
    )
  }

  return (
    <div>
      <div style={filtersRowStyle}>
        <input
          type="text"
          value={titolo}
          onChange={e => setTitolo(e.target.value)}
          placeholder="Cerca per titolo..."
          style={inputStyle}
        />
        <select
          value={genereSelezionato}
          onChange={e => setGenereSelezionato(e.target.value)}
          style={inputStyle}
        >
          <option value="">-- Tutti i generi --</option>
          {generiDisponibili.map(genere => (
            <option key={genere} value={genere}>{genere}</option>
          ))}
        </select>
      </div>

      {loading && (
        <p style={{ color: 'rgba(255,255,255,0.7)', textAlign: 'center', padding: '2rem 0' }}>
          Caricamento...
        </p>
      )}

      {!loading && filmFiltrati.length === 0 && (
        <div className="empty-state">
          <p>Nessun film corrisponde ai criteri di ricerca.</p>
        </div>
      )}

      {!loading && filmFiltrati.length > 0 && (
        <table className="data-table">
          <thead>
            <tr>
              <th>Titolo</th>
              <th>Regista</th>
              <th>Genere</th>
              <th>Paese</th>
              <th style={{ textAlign: 'right' }}>Anno</th>
              <th style={{ textAlign: 'right' }}>Durata</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {filmFiltrati.map(f => (
              <tr key={f.id}>
                <td>{f.titolo}</td>
                <td>{f.regista ?? '-'}</td>
                <td>{f.genere}</td>
                <td>{f.paeseProduzione}</td>
                <td style={{ textAlign: 'right' }}>{f.anno}</td>
                <td style={{ textAlign: 'right' }}>{f.durata} min</td>
                <td>
                  <a href={`/film/${f.id}`} className="btn-small">Dettagli</a>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
