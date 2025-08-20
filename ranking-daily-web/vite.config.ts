import path from 'path'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'


export default defineConfig({
  plugins:
    [
      react()
    ],
  /**
   * 別名配置
   * __dirname 當前目錄的絕對路徑
   */
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  /**
   * less 配置
   */
  css: {
    preprocessorOptions: {
      less: {
        javascriptEnabled: true,
      }
    }
  }

})

