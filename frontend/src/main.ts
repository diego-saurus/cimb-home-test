import './assets/css/main.css'
import ui from '@nuxt/ui/vue-plugin'
import { PiniaColada } from '@pinia/colada'
import { createPinia } from 'pinia'
import { createApp } from 'vue'

import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

app.use(router)
app.use(ui)
app.use(pinia)
app.use(PiniaColada)

app.mount('#app')
