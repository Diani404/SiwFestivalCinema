import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import './App.css'

const container = document.getElementById('root')
const festivalId = container.dataset.festivalId

createRoot(container).render(<App festivalId={festivalId} />)
