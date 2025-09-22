import { lazy } from 'react';
import { Navigate, type RouteObject } from 'react-router-dom';

const Login = lazy(() => import("@/pages/login"));
const Home = lazy(() => import("@/pages/home"));

const routers: RouteObject[] = [
    {
        path: "/",
        element: <Navigate to="/home"></Navigate>
    },
    {
        path: "/home",
        element: <Home />
    },
    {
        path: "/login",
        element: <Login />
    }
]


export default routers