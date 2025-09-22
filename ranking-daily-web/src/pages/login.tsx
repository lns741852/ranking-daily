import { forwardRef, memo, type ForwardedRef, type ReactNode, type FC } from 'react'
import LoginForm from '../components/login'
import styled from "styled-components";

export const LoginWapper = styled.div`
  display: flex;
  height: 100vh;
  justify-content: center;
  align-items: center;

  .card_container {
    border-radius: 15px;
    width: 350px;
    padding: 55px 35px 25px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
  }
`;

interface Iprops {
    children?: ReactNode
    ref?: ForwardedRef<any>
}

const Login: FC<Iprops> = forwardRef(({ children, ...props }, ref) => {
    return (
        <LoginWapper  {...props}>
            <div className='card_container'>
                <LoginForm></LoginForm>
            </div>
        </LoginWapper>
    )
})

export default memo(Login)