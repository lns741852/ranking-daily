import { lazy } from 'react';
import { Navigate, type RouteObject } from 'react-router-dom';

const Login = lazy(() => import("@/pages/login"));


const routers: RouteObject[] = [
    {
        path: "/",
        element: <Navigate to="/login"></Navigate>
    },
    {
        path: "/login",
        element: <Login />
    }
]


export default routers