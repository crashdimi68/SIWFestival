export interface Film {
  id: number
  titolo: string
  anno: number
  durata: number
  genere: string
  paeseProduzione: string
  regista: string | null
}
