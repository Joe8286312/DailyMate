import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'

import './styles/index.scss'

import { setupGlobalErrorHandler } from '@/utils/errorHandler'
import { piniaPersistPlugin } from '@/stores/plugins/persist'

const app = createApp(App)
const pinia = createPinia()

// 注册 Pinia 持久化插件
pinia.use(piniaPersistPlugin)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

// 设置全局错误处理（在 app 创建之后）
setupGlobalErrorHandler(app)

app.mount('#app')
