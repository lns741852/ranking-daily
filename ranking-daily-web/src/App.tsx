import { useRoutes } from "react-router-dom"
import routers from "./config/router"
import RDHeader from "./components/header"


function App() {

  return (
    <div className="App">
      <RDHeader></RDHeader>
      <div className="main">{useRoutes(routers)}</div>
    </div>
  )
}

export default App
