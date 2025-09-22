import { memo } from 'react'
import type { FC, ReactNode } from 'react'
import logo from '@/assets/img/logo.png'
import { NavLink } from 'react-router-dom'
import styled from "styled-components";

export const RDHeaderWrapper = styled.div`
  background-color: #333;
  .content {
    display: flex;
    justify-content: space-between;
    height: 55px;
    padding: 0 30px;

    .logo {
      .img {
        max-width: 100%;
        height: auto;
      }
    }

    .right_header {
      display: flex;
      align-items: center;
      font-size: 16px;
      gap: 20px;

      a {
        color: #fff;
        line-height: 55px;
        &:hover,
        &.active {
          text-decoration: none;
        }
      }

      .login button {
        padding: 5px 20px;
      }
    }
  }
`;


interface Iprops {
    children?: ReactNode
}

const RDHeader: FC<Iprops> = () => {
    return (
        <RDHeaderWrapper>
            <div className='content'>
                <div className='logo'>
                    <img src={logo} />
                </div>

                <div className='right_header'>
                    <NavLink to='/myRank'>
                        我的排名
                    </NavLink>
                    <div className='login'>
                        <button >登入</button>
                    </div>
                </div>
            </div>
        </RDHeaderWrapper>
    )
}

export default memo(RDHeader)