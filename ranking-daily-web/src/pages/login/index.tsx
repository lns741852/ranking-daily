import { memo } from 'react'
import type { FC, ReactNode } from 'react'

import LoginForm from '../../components/login'
import { LoginWapper } from './style'

interface Iprops {
    children?: ReactNode
}


const Login: FC<Iprops> = () => {

    return (
        <LoginWapper>
            <div className='board'>
                <LoginForm></LoginForm>
            </div>
        </LoginWapper>
    )
}

export default memo(Login)