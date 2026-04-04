<template>
  <div id="app-container">
    <div class="background-layer" :style="backgroundStyle" />
    <div class="app-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { watch, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useSettingsStore, themes } from '@/stores/settings'

// 全局消息提示配置
ElMessage.config({
  duration: 3000,
  max: 3
})

const settingsStore = useSettingsStore()

// 背景样式：直接使用 store 的 computed 值
const { backgroundStyle } = storeToRefs(settingsStore)

// 监听设置变化并应用
const applyBodyBackground = () => {
  document.body.style.backgroundImage = ''
  document.body.style.backgroundColor = 'transparent'
  document.body.style.backgroundSize = ''
  document.body.style.backgroundPosition = ''
  document.body.style.backgroundRepeat = ''
  document.body.style.backgroundAttachment = ''
}

watch(
  () => settingsStore.settings,
  (newSettings) => {
    document.body.className = `theme-${newSettings.theme}`
    updateThemeVariables(newSettings.theme)
    applyBodyBackground()
    const computedBg = getComputedStyle(document.body).backgroundImage
    const layer = document.querySelector('.background-layer')
    const layerComputed = layer ? getComputedStyle(layer) : null
    console.log('[App] background apply', {
      backgroundType: newSettings.backgroundType,
      backgroundColor: newSettings.backgroundColor,
      backgroundUrl: newSettings.backgroundUrl,
      backgroundOpacity: newSettings.backgroundOpacity,
      backgroundSize: newSettings.backgroundSize,
      backgroundPosition: newSettings.backgroundPosition,
      bodyStyleBg: document.body.style.backgroundImage,
      bodyComputedBg: computedBg,
      layerStyle: backgroundStyle.value,
      layerComputedBg: layerComputed?.backgroundImage,
      layerComputedColor: layerComputed?.backgroundColor
    })
  },
  { deep: true, immediate: true }
)

// 更新主题变量
function updateThemeVariables (themeName) {
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
    .el-button--primary {
      --el-button-bg-color: ${theme.primary};
      --el-button-border-color: ${theme.primary};
    }
    .el-tabs__item.is-active {
      color: ${theme.primary};
    }
    .el-tabs__active-bar {
      background-color: ${theme.primary};
    }
  `

  styleEl.textContent = css
}

onMounted(() => {
  settingsStore.loadFromLocalStorage()
})
</script>

<style lang="scss">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app, #app-container {
  height: 100%;
  width: 100%;
}

#app-container {
  position: relative;
  z-index: 0;
}

.background-layer {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  transition: opacity 0.3s ease, background 0.3s ease;
  pointer-events: none;
}

.app-content {
  position: relative;
  z-index: 1;
}

body {
  font-family: 'Helvetica Neue', Arial, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

#app {
  width: 100%;
  height: 100%;
}

.theme-light {
  --el-bg-color: #ffffff;
  --el-text-color-primary: #303133;
}

.theme-dark {
  --el-bg-color: #2d2d3a;
  --el-text-color-primary: #e0e0e0;
}

.theme-blue {
  --el-bg-color: rgba(255, 255, 255, 0.95);
  --el-text-color-primary: #1e3a8a;
}

.theme-green {
  --el-bg-color: rgba(255, 255, 255, 0.95);
  --el-text-color-primary: #064e3b;
}

.theme-purple {
  --el-bg-color: rgba(255, 255, 255, 0.95);
  --el-text-color-primary: #581c87;
}
</style>
