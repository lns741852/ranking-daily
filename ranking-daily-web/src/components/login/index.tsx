import React from 'react';
import type { FormProps } from 'antd';
import { Button, Form, Input } from 'antd';
import { FormWapper } from './style';

type FieldType = {
    username?: string;
    password?: string;
};

const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080//oauth2/authorization/google"
}

const onFinish: FormProps<FieldType>['onFinish'] = (values) => {
    console.log('Success:', values);
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