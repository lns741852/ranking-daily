import React from 'react';
import type { FormProps } from 'antd';
import { Button, Form, Input } from 'antd';
import styled from 'styled-components';
import { postLoginAPI } from '@/api/user';

export const FormWapper = styled.div`
  .formItem {
    text-align: center;
    color: #505458;
  }

  .submit {
    width: 100%;
    display: block;
    margin-top: 20px;
  }
`;


interface FieldType {
    username: string;
    password: string;
};

const handleGoogleLogin = () => {
    window.location.href = "http://127.0.0.1:8080/oauth2/authorization/google"
}

const onFinish: FormProps<FieldType>['onFinish'] = async (values) => {

    try {
        const data = await postLoginAPI(values)
        console.log(data)
    } catch (error) {
        console.log(error)
    }

};


const LoginForm: React.FC = () => (
    <FormWapper>
        <Form
            onFinish={onFinish}
        >
            <Form.Item<FieldType>
                label="Username"
                name="username"
                rules={[{ required: true, message: 'Please input your username!' }]}
                className='formItem'
            >
                <Input />
            </Form.Item>

            <Form.Item<FieldType>
                label="Password"
                name="password"
                rules={[{ required: true, message: 'Please input your password!' }]}
                className='formItem'
            >
                <Input.Password />
            </Form.Item>

            <Button type="primary" htmlType="submit" className='submit'>
                登入
            </Button>

            <Button
                type="primary"
                danger
                className='submit'
                onClick={handleGoogleLogin}
            >
                使用 Google 登入
            </Button>
        </Form>
    </FormWapper>


);

export default LoginForm;