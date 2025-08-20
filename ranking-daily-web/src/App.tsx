import { useRoutes } from "react-router-dom"
import routers from "./config/router"

function App() {
  return (
    <div className="App">
      <div className="main">{useRoutes(routers)}</div>
    </div>
  )
}

export default App
