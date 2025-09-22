/****   request.ts   ****/

import axios, { AxiosError, type AxiosResponse } from "axios";
// 获取个人信息，主要是token
// import { useSettingStore } from "@/store/user";
// 消息提示组件
import { message } from "antd";

message.config({
  maxCount: 1, //最大顯示數量
});

// 创建新的axios实例
const service = axios.create({
  // 公共接口
  baseURL: import.meta.env.VITE_APP_BASE_API,
  // 超时时间 单位是ms，这里设置了5s的超时时间
  timeout: 5000,
});

// 添加一个请求拦截器
service.interceptors.request.use(
  (config) => {
    // 发请求前做的一些处理，数据转化，配置请求头，设置token,设置loading等
    // 每次发送请求之前判断pinia中是否存在token,如果存在，则统一在http请求的header都加上token，这样后台根据token判断你的登录情况
    const token = null;

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    //设置loading
    message.loading({
      content: "加载中...",
      duration: 0, //一直存在
    });

    return config;
  },
  (error: AxiosError) => {
    // 出现请求错误，清除toast
    message.destroy();
    // 请求错误，这里可以用全局提示框进行提示
    message.error({
      content: "请求错误，请稍后再试",
      duration: 5,
    });
    return Promise.reject(error);
  }
);

// 添加一个响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { status, data } = response;
    if (status === 200) {
      return Promise.resolve(data);
    }
    return Promise.reject(data);
  },
  (error: AxiosError) => {
    const { response, message: msg } = error;
    // 响应失败，关闭等待提示
    message.destroy();
    // 提示错误信息
    if (msg.includes("Network Error")) {
      message.error({
        content: "网络超时",
        duration: 5,
      });
    }

    // 根据响应的错误状态码，做不同的处理，此处只是作为示例，请根据实际业务处理
    if (response) {
      if (response.status === 400) {
        message.error({
          content: "报错信息。。。",
          duration: 5,
        });
      } else if (response.status === 401) {
        message.error({
          content: "报错信息。。。",
          duration: 5,
        });
      } else {
        message.error({
          content: "报错信息。。。",
          duration: 5,
        });
      }
    }

    return Promise.reject(error);
  }
);

export default service;
