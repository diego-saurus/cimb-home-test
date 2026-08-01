import nuxtUI from '@nuxt/ui/vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    nuxtUI({
      colorMode: false,
      ui: {
        colors: {
          primary: 'raw-sienna',
          secondary: 'cutty-sark',
          destructive: 'coral-red',
          success: 'vista-green',
          info: 'powder-blue',
          warning: 'candlelight',
          neutral: 'marshland',
        },
      },
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
