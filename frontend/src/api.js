async function getJson(url) {
  const response = await fetch(url, {
    headers: { Accept: 'application/json' },
    credentials: 'same-origin'
  })
  if (!response.ok) {
    throw new Error('Errore ' + response.status)
  }
  return response.json()
}

export function getScreeningDates(festivalId) {
  return getJson('/api/festivals/' + festivalId + '/screening-dates')
}

export function getScreenings(festivalId, date) {
  const url = '/api/festivals/' + festivalId + '/screenings'
  return getJson(date ? url + '?date=' + date : url)
}
