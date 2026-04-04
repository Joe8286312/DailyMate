import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import i18n from './locales/index'
import { elementLocaleMap } from './locales/element-locale'

import './styles/index.scss'

import { setupGlobalErrorHandler } from '@/utils/errorHandler'
import { piniaPersistPlugin } from '@/stores/plugins/persist'

console.log('[main.js] 应用启动')

const app = createApp(App)
const pinia = createPinia()

// 注册 Pinia 持久化插件
pinia.use(piniaPersistPlugin)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 获取语言设置：优先从 URL 参数读取，其次从 localStorage 读取
function getInitialLanguage () {
  const urlParams = new URLSearchParams(window.location.search)
  const urlLang = urlParams.get('lang')
  if (urlLang && elementLocaleMap[urlLang]) {
    console.log('[main.js] 从 URL 参数读取语言:', urlLang)
    // 同步到 localStorage
    localStorage.setItem('dailyMate_language', urlLang)
    // 清理 URL 参数
    const cleanUrl = window.location.origin + window.location.pathname
    window.history.replaceState({}, '', cleanUrl)
    return urlLang
  }

  const savedLanguage = localStorage.getItem('dailyMate_language') || 'zh-CN'
  console.log('[main.js] 从 localStorage 读取语言:', savedLanguage)
  return savedLanguage
}

const savedLanguage = getInitialLanguage()
const currentLocale = elementLocaleMap[savedLanguage]
console.log('[main.js] Element Plus locale:', currentLocale ? savedLanguage : 'undefined，使用默认 zh-CN')

app.use(pinia)
app.use(router)
app.use(i18n)
app.use(ElementPlus, { locale: currentLocale || elementLocaleMap['zh-CN'] })

// 设置全局错误处理
setupGlobalErrorHandler(app)

app.mount('#app')

setTimeout(() => {
  const layer = document.querySelector('.background-layer')
  const style = layer ? getComputedStyle(layer) : null
  console.log('[main.js] bg layer', {
    exists: !!layer,
    bgImage: style?.backgroundImage,
    bgColor: style?.backgroundColor
  })
}, 1000)
