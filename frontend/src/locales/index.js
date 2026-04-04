/**
 * i18n 国际化配置
 */
import { createI18n } from 'vue-i18n'
const localeModules = import.meta.glob('./*-*.js', { eager: true })

const messages = {}
const supportedLanguages = []

Object.entries(localeModules).forEach(([path, mod]) => {
  const fileName = path.split('/').pop() || ''
  const code = fileName.replace('.js', '')
  const messagePack = mod.default || {}
  const meta = mod.meta || {}

  messages[code] = messagePack
  supportedLanguages.push({
    code,
    name: meta.name || code,
    flag: meta.flag || '🌐'
  })
})

supportedLanguages.sort((a, b) => a.code.localeCompare(b.code))

const defaultLocale = messages['zh-CN'] ? 'zh-CN' : (supportedLanguages[0]?.code || 'en-US')

// 从 localStorage 获取语言设置，默认为默认语言
const savedLanguage = localStorage.getItem('dailyMate_language') || defaultLocale

const i18n = createI18n({
  legacy: false,
  locale: messages[savedLanguage] ? savedLanguage : defaultLocale,
  fallbackLocale: defaultLocale,
  messages,
  globalInjection: true
})

export default i18n

/**
 * 设置语言
 * @param {string} locale - 语言代码
 * @param {boolean} reload - 是否刷新页面（默认 false）
 */
export function setLocale (locale, reload = false) {
  if (supportedLanguages.some(lang => lang.code === locale)) {
    localStorage.setItem('dailyMate_language', locale)
    i18n.global.locale.value = locale
    document.documentElement.lang = locale

    if (reload) {
      window.location.reload()
    }
    return true
  }
  return false
}

/**
 * 仅更新 vue-i18n 语言，不刷新页面
 * @param {string} locale - 语言代码
 */
export function updateI18nLocale (locale) {
  if (supportedLanguages.some(lang => lang.code === locale)) {
    i18n.global.locale.value = locale
    document.documentElement.lang = locale
    return true
  }
  return false
}

export function getLocale () {
  return i18n.global.locale.value
}

export function getSupportedLanguages () {
  return supportedLanguages
}
