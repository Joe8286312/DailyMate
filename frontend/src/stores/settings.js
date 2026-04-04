import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getSettings as getSettingsApi, updateSettings as updateSettingsApi, resetSettings as resetSettingsApi } from '@/api/settings'
import { updateI18nLocale } from '@/locales/index'

/**
 * 主题配置
 */
export const themes = {
  light: {
    name: 'light',
    label: '明亮',
    primary: '#409EFF',
    success: '#67C23A',
    warning: '#E6A23C',
    danger: '#F56C6C',
    info: '#909399',
    background: 'linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%)',
    cardBg: '#ffffff',
    textPrimary: '#303133',
    textSecondary: '#606266'
  },
  dark: {
    name: 'dark',
    label: '黑暗',
    primary: '#409EFF',
    success: '#67C23A',
    warning: '#E6A23C',
    danger: '#F56C6C',
    info: '#909399',
    background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)',
    cardBg: '#2d2d3a',
    textPrimary: '#e0e0e0',
    textSecondary: '#a0a0a0'
  },
  blue: {
    name: 'blue',
    label: '蓝色',
    primary: '#3b82f6',
    success: '#10b981',
    warning: '#f59e0b',
    danger: '#ef4444',
    info: '#6b7280',
    background: 'linear-gradient(135deg, #1e3a8a 0%, #3b82f6 50%, #60a5fa 100%)',
    cardBg: 'rgba(255, 255, 255, 0.95)',
    textPrimary: '#1e3a8a',
    textSecondary: '#4b5563'
  },
  green: {
    name: 'green',
    label: '绿色',
    primary: '#10b981',
    success: '#059669',
    warning: '#f59e0b',
    danger: '#ef4444',
    info: '#6b7280',
    background: 'linear-gradient(135deg, #064e3b 0%, #10b981 50%, #34d399 100%)',
    cardBg: 'rgba(255, 255, 255, 0.95)',
    textPrimary: '#064e3b',
    textSecondary: '#4b5563'
  },
  purple: {
    name: 'purple',
    label: '紫色',
    primary: '#8b5cf6',
    success: '#10b981',
    warning: '#f59e0b',
    danger: '#ef4444',
    info: '#6b7280',
    background: 'linear-gradient(135deg, #581c87 0%, #8b5cf6 50%, #a78bfa 100%)',
    cardBg: 'rgba(255, 255, 255, 0.95)',
    textPrimary: '#581c87',
    textSecondary: '#4b5563'
  }
}

/**
 * 预设背景图
 */
export const presetBackgrounds = [
  {
    id: 'default',
    name: '默认渐变',
    type: 'gradient',
    url: ''
  },
  {
    id: 'nature',
    name: '自然风光',
    type: 'image',
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&q=80'
  },
  {
    id: 'ocean',
    name: '海洋',
    type: 'image',
    url: 'https://images.unsplash.com/photo-1505118380757-91f5f5632de0?w=1920&q=80'
  },
  {
    id: 'mountain',
    name: '山川',
    type: 'image',
    url: 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=1920&q=80'
  },
  {
    id: 'sunset',
    name: '日落',
    type: 'image',
    url: 'https://images.unsplash.com/photo-1495616811223-4d98c6e9d869?w=1920&q=80'
  },
  {
    id: 'stars',
    name: '星空',
    type: 'image',
    url: 'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=1920&q=80'
  },
  {
    id: 'minimal',
    name: '简约',
    type: 'gradient',
    url: ''
  },
  {
    id: 'forest',
    name: '森林',
    type: 'image',
    url: 'https://images.unsplash.com/photo-1448375240586-882707db888b?w=1920&q=80'
  }
]

export const useSettingsStore = defineStore('settings', () => {
  // 状态
  const settings = ref({
    language: 'zh-CN',
    theme: 'light',
    backgroundType: 'color', // 'color' 或 'image'
    backgroundColor: '#f5f7fa',
    backgroundUrl: '',
    backgroundOpacity: 100, // 背景透明度 0-100
    backgroundSize: 'cover', // cover, contain, 100%, auto 100%
    backgroundPosition: 'center', // center, left top, right top 等
    notificationEnabled: true,
    soundEnabled: false,
    animationEnabled: true,
    defaultPriority: 2,
    defaultReminderOffset: 0,
    defaultHomeView: 'dashboard'
  })

  const loading = ref(false)

  // 计算属性
  const currentTheme = computed(() => themes[settings.value.theme] || themes.light)
  const currentLanguage = computed(() => settings.value.language)
  const backgroundStyle = computed(() => {
    if (settings.value.backgroundType === 'image' && settings.value.backgroundUrl) {
      const opacity = (settings.value.backgroundOpacity || 100) / 100
      return {
        backgroundImage: `linear-gradient(rgba(255, 255, 255, ${1 - opacity}), rgba(255, 255, 255, ${1 - opacity})), url(${settings.value.backgroundUrl})`,
        backgroundSize: settings.value.backgroundSize || 'cover',
        backgroundPosition: settings.value.backgroundPosition || 'center',
        backgroundAttachment: 'fixed',
        backgroundRepeat: 'no-repeat'
      }
    }
    return {
      background: settings.value.backgroundColor || '#f5f7fa'
    }
  })

  // 方法
  /**
   * 获取用户设置
   */
  async function fetchSettings (userId) {
    if (!userId) return
    loading.value = true
    try {
      const res = await getSettingsApi(userId)
      settings.value = normalizeSettings({ ...settings.value, ...res.data })
      // 应用语言设置
      applyLanguage(settings.value.language)
      // 应用主题
      applyTheme(settings.value.theme)
      saveToLocalStorage()
    } catch (error) {
      console.error('获取设置失败:', error)
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新用户设置
   */
  async function updateSettings (userId, newSettings) {
    loading.value = true
    try {
      const res = await updateSettingsApi(userId, newSettings)
      settings.value = normalizeSettings({ ...settings.value, ...res.data })
      
      // 应用变更
      if (newSettings.language) {
        applyLanguage(newSettings.language)
      }
      if (newSettings.theme) {
        applyTheme(newSettings.theme)
      }
      saveToLocalStorage()
      return { success: true }
    } catch (error) {
      console.error('更新设置失败:', error)
      return { success: false, error }
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新单个设置项
   */
  async function updateSetting (userId, key, value) {
    settings.value[key] = value
    
    // 立即应用某些设置
    if (key === 'language') {
      applyLanguage(value)
    } else if (key === 'theme') {
      applyTheme(value)
    }
    
    // 保存到服务器（防抖处理）
    debounceSaveSettings(userId)
    saveToLocalStorage()
  }

  /**
   * 重置设置
   */
  async function resetSettings (userId) {
    loading.value = true
    try {
      await resetSettingsApi(userId)
      // 重置为默认值
      settings.value = {
        language: 'zh-CN',
        theme: 'light',
        backgroundType: 'color',
        backgroundColor: '#f5f7fa',
        backgroundUrl: '',
        backgroundOpacity: 100,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        notificationEnabled: true,
        soundEnabled: false,
        animationEnabled: true,
        defaultPriority: 2,
        defaultReminderOffset: 0,
        defaultHomeView: 'dashboard'
      }
      applyLanguage('zh-CN')
      applyTheme('light')
      saveToLocalStorage()
      return { success: true }
    } catch (error) {
      console.error('重置设置失败:', error)
      return { success: false, error }
    } finally {
      loading.value = false
    }
  }

  /**
   * 应用语言设置 - 仅更新 vue-i18n，不刷新页面
   */
  function applyLanguage (locale) {
    updateI18nLocale(locale)
  }

  /**
   * 应用主题
   */
  function applyTheme (themeName) {
    const theme = themes[themeName] || themes.light
    const styleId = 'theme-styles'
    
    let styleEl = document.getElementById(styleId)
    if (!styleEl) {
      styleEl = document.createElement('style')
      styleEl.id = styleId
      document.head.appendChild(styleEl)
    }
    
    const css = `
      :root {
        --theme-primary: ${theme.primary};
        --theme-success: ${theme.success};
        --theme-warning: ${theme.warning};
        --theme-danger: ${theme.danger};
        --theme-info: ${theme.info};
        --theme-card-bg: ${theme.cardBg};
        --theme-text-primary: ${theme.textPrimary};
        --theme-text-secondary: ${theme.textSecondary};
      }
      
      /* Element Plus 主题色覆盖 */
      .el-button--primary {
        --el-button-bg-color: ${theme.primary};
        --el-button-border-color: ${theme.primary};
        --el-button-hover-color: ${adjustColor(theme.primary, -10)};
      }
      
      .el-tabs__item.is-active {
        color: ${theme.primary};
      }
      
      .el-tabs__active-bar {
        background-color: ${theme.primary};
      }
    `
    
    styleEl.textContent = css
    
    // 添加主题类名到 body
    document.body.className = `theme-${themeName}`
  }

  /**
   * 调整颜色亮度
   */
  function adjustColor (color, amount) {
    const usePound = color.startsWith('#')
    if (usePound) {
      color = color.slice(1)
    }
    const num = parseInt(color, 16)
    let r = (num >> 16) + amount
    let b = ((num >> 8) & 0x00FF) + amount
    let g = (num & 0x0000FF) + amount
    
    if (r > 255) r = 255
    else if (r < 0) r = 0
    if (b > 255) b = 255
    else if (b < 0) b = 0
    if (g > 255) g = 255
    else if (g < 0) g = 0
    
    return '#' + (g | (b << 8) | (r << 16)).toString(16).padStart(6, '0')
  }

  /**
   * 防抖保存设置
   */
  let saveTimeout = null
  async function debounceSaveSettings (userId) {
    if (saveTimeout) {
      clearTimeout(saveTimeout)
    }
    saveTimeout = setTimeout(async () => {
      try {
        await updateSettingsApi(userId, settings.value)
      } catch (error) {
        console.error('保存设置失败:', error)
      }
    }, 500)
  }

  /**
   * 从本地存储加载设置
   */
  function loadFromLocalStorage () {
    const saved = localStorage.getItem('dailyMate_settings')
    if (saved) {
      try {
        const parsed = JSON.parse(saved)
        settings.value = normalizeSettings({ ...settings.value, ...parsed })
        applyLanguage(settings.value.language)
        applyTheme(settings.value.theme)
      } catch (e) {
        console.error('解析本地设置失败:', e)
      }
    }
  }

  function normalizeSettings (raw) {
    const normalized = { ...raw }
    if (!normalized.backgroundType) {
      normalized.backgroundType = normalized.backgroundUrl ? 'image' : 'color'
    }
    if (!normalized.backgroundColor) normalized.backgroundColor = '#f5f7fa'
    if (normalized.backgroundOpacity == null) normalized.backgroundOpacity = 100
    if (!normalized.backgroundSize) normalized.backgroundSize = 'cover'
    if (!normalized.backgroundPosition) normalized.backgroundPosition = 'center'
    if (!normalized.theme) normalized.theme = 'light'
    if (!normalized.language) normalized.language = 'zh-CN'
    return normalized
  }

  /**
   * 保存到本地存储
   */
  function saveToLocalStorage () {
    localStorage.setItem('dailyMate_settings', JSON.stringify(settings.value))
  }

  return {
    // 状态
    settings,
    loading,
    // 计算属性
    currentTheme,
    currentLanguage,
    backgroundStyle,
    // 方法
    fetchSettings,
    updateSettings,
    updateSetting,
    resetSettings,
    loadFromLocalStorage,
    saveToLocalStorage
  }
})
