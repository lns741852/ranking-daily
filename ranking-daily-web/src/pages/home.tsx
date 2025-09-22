import { memo } from 'react'
import type { FC, ReactNode } from 'react'

interface Iprops {
    children?: ReactNode
}

const Home: FC<Iprops> = () => {
    return <div>Home</div>
}

export default memo(Home)