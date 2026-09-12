import { useEffect, useState } from 'react'
import { getScreeningDates, getScreenings } from './api.js'

const STATUS_LABELS = {
  SCHEDULED: 'In programma',
  COMPLETED: 'Conclusa',
  CANCELLED: 'Annullata'
}

function formatDate(iso) {
  const [year, month, day] = iso.split('-')
  return day + '/' + month + '/' + year
}

export default function App({ festivalId }) {
  const [dates, setDates] = useState([])
  const [selectedDate, setSelectedDate] = useState('')
  const [screenings, setScreenings] = useState([])
  const [filter, setFilter] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getScreeningDates(festivalId)
      .then(setDates)
      .catch(e => setError(e.message))
  }, [festivalId])

  useEffect(() => {
    setLoading(true)
    setError('')
    getScreenings(festivalId, selectedDate)
      .then(setScreenings)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [festivalId, selectedDate])

  const text = filter.toLowerCase()
  const visible = screenings.filter(s =>
    s.movie.title.toLowerCase().includes(text) ||
    s.movie.director.fullName.toLowerCase().includes(text) ||
    s.room.name.toLowerCase().includes(text)
  )

  if (error) {
    return <p className="error">Impossibile caricare il programma. {error}</p>
  }

  return (
    <div className="program">
      <div className="filters">
        <label>
          Giorno
          <select value={selectedDate} onChange={e => setSelectedDate(e.target.value)}>
            <option value="">Tutte le date</option>
            {dates.map(d => <option key={d} value={d}>{formatDate(d)}</option>)}
          </select>
        </label>
        <label>
          Cerca
          <input
            type="text"
            value={filter}
            placeholder="film, regista o sala"
            onChange={e => setFilter(e.target.value)}
          />
        </label>
      </div>

      {loading && <p>Caricamento del programma...</p>}

      {!loading && (
        <p>Proiezioni trovate: {visible.length}</p>
      )}

      {!loading && visible.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Data</th>
              <th>Ora</th>
              <th>Film</th>
              <th>Regista</th>
              <th>Sala</th>
              <th>Stato</th>
            </tr>
          </thead>
          <tbody>
            {visible.map(s => (
              <tr key={s.id} className={s.status.toLowerCase()}>
                <td>{formatDate(s.date)}</td>
                <td>{s.time.slice(0, 5)}</td>
                <td><a href={'/movies/' + s.movie.id}>{s.movie.title}</a></td>
                <td>{s.movie.director.fullName}</td>
                <td>{s.room.name}</td>
                <td>
                  <span className={'stato stato-' + s.status.toLowerCase()}>
                    {STATUS_LABELS[s.status] ?? s.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
