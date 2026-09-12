import axios from 'axios'
import type { Film } from './types'

function baseUrl(): string {
  const el = document.getElementById('film-react-root')
  return el?.dataset?.apiBase ?? '/api'
}

export const api = axios.create({
  baseURL: baseUrl(),
  timeout: 10_000,
})

export async function getFilm(titolo?: string): Promise<Film[]> {
  const { data } = await api.get<Film[]>('/movies', {
    params: titolo && titolo.trim() !== '' ? { titolo } : undefined,
  })
  return data
}
