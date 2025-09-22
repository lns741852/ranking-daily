import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import { HashRouter } from 'react-router-dom'
/**
 * react 16~18 
 * import "antd/dist/reset.css"
 */
import '@ant-design/v5-patch-for-react-19';
import '@/assets/css/base.less'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <HashRouter>
      <App />
    </HashRouter>
  </StrictMode>,
)
