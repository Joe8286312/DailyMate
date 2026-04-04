<template>
  <div class="settings-container">
    <div class="settings-card">
      <el-card shadow="never" class="settings-header">
        <template #header>
          <div class="card-header">
            <el-icon><Setting /></el-icon>
            <span>{{ t('settings.title') }}</span>
          </div>
        </template>
      </el-card>

      <div class="settings-body">
        <el-tabs v-model="activeTab" class="settings-tabs">
          <!-- 通用设置 -->
          <el-tab-pane :label="t('settings.general')" name="general">
            <el-form :model="settingsForm" label-width="140px" class="settings-form">
              <el-form-item :label="t('settings.language')">
                <el-select v-model="settingsForm.language" @change="handleLanguageChange">
                  <el-option
                    v-for="lang in supportedLanguages"
                    :key="lang.code"
                    :label="`${lang.flag} ${lang.name}`"
                    :value="lang.code"
                  />
                </el-select>
              </el-form-item>

              <el-form-item :label="t('settings.theme')">
                <el-select v-model="settingsForm.theme" @change="handleThemeChange">
                  <el-option
                    v-for="(theme, key) in themeOptions"
                    :key="key"
                    :label="theme.label"
                    :value="key"
                  >
                    <div class="theme-option">
                      <span class="theme-color" :style="{ background: theme.color }" />
                      <span>{{ theme.label }}</span>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 背景设置 -->
          <el-tab-pane :label="t('settings.background')" name="background">
            <el-form :model="settingsForm" label-width="140px" class="settings-form">
              <el-form-item label="背景类型">
                <el-radio-group v-model="settingsForm.backgroundType">
                  <el-radio :value="'color'">纯色背景</el-radio>
                  <el-radio :value="'image'">图片背景</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item v-if="settingsForm.backgroundType === 'color'" label="背景颜色">
                <el-color-picker v-model="settingsForm.backgroundColor" />
              </el-form-item>

              <template v-if="settingsForm.backgroundType === 'image'">
                <el-form-item :label="t('settingsPage.presetBackground')">
                  <div class="background-grid">
                    <div
                      v-for="bg in presetBackgrounds"
                      :key="bg.id"
                      class="background-item"
                      :class="{ active: selectedPresetId === bg.id }"
                      @click="selectPresetBackground(bg)"
                    >
                      <div class="background-preview">
                        <img v-if="bg.url" :src="bg.url" alt="" class="preview-image-bg" />
                        <div v-else class="gradient-preview" :style="{ background: getGradient(bg.id) }" />
                      </div>
                      <div class="background-name">{{ bg.name }}</div>
                      <el-icon v-if="selectedPresetId === bg.id" class="check-icon">
                        <Check />
                      </el-icon>
                    </div>
                  </div>
                </el-form-item>

                <el-divider>{{ t('settingsPage.or') }}</el-divider>

                <el-form-item :label="t('settingsPage.customImage')">
                  <div class="custom-image-section">
                    <el-upload
                      class="image-uploader"
                      :show-file-list="false"
                      :auto-upload="false"
                      :on-change="handleImageSelect"
                      :disabled="compressing"
                      accept="image/*"
                    >
                      <el-button type="primary" :loading="compressing">
                        <el-icon><Upload /></el-icon>
                        {{ compressing ? t('settingsPage.compressing') : t('settingsPage.chooseLocalImage') }}
                      </el-button>
                    </el-upload>
                    <el-input
                      v-model="customBackgroundUrl"
                      :placeholder="t('settingsPage.imageUrlPlaceholder')"
                      clearable
                      @change="handleCustomUrlChange"
                      class="url-input"
                      :disabled="compressing"
                    />
                  </div>
                  <div v-if="previewImage" class="image-preview-box">
                    <div class="preview-label">{{ t('settingsPage.preview') }}</div>
                    <img :src="previewImage" alt="预览" class="preview-image" />
                    <el-button size="small" type="danger" @click="clearCustomImage" :disabled="compressing">
                      <el-icon><Delete /></el-icon>
                      {{ t('settingsPage.clear') }}
                    </el-button>
                  </div>
                  <div class="form-tip">
                    <el-icon><InfoFilled /></el-icon>
                    {{ t('settingsPage.imageTip') }}
                  </div>
                </el-form-item>

                <el-form-item :label="t('settingsPage.backgroundOpacity')">
                  <el-slider
                    v-model="settingsForm.backgroundOpacity"
                    :min="0"
                    :max="100"
                    :step="5"
                    show-input
                  />
                  <div class="form-tip">
                    <el-icon><InfoFilled /></el-icon>
                    {{ t('settingsPage.opacityTip') }}
                  </div>
                </el-form-item>

                <el-form-item :label="t('settingsPage.backgroundSize')">
                  <el-select v-model="settingsForm.backgroundSize">
                    <el-option :label="t('settingsPage.sizeCover')" value="cover" />
                    <el-option :label="t('settingsPage.sizeContain')" value="contain" />
                    <el-option :label="t('settingsPage.sizeOriginal')" value="100%" />
                    <el-option :label="t('settingsPage.sizeAutoWidth')" value="auto 100%" />
                  </el-select>
                  <div class="form-tip">
                    <el-icon><InfoFilled /></el-icon>
                    {{ t('settingsPage.sizeTip') }}
                  </div>
                </el-form-item>

                <el-form-item :label="t('settingsPage.backgroundPosition')">
                  <el-select v-model="settingsForm.backgroundPosition">
                    <el-option :label="t('settingsPage.posCenter')" value="center" />
                    <el-option :label="t('settingsPage.posLeftTop')" value="left top" />
                    <el-option :label="t('settingsPage.posRightTop')" value="right top" />
                    <el-option :label="t('settingsPage.posLeftBottom')" value="left bottom" />
                    <el-option :label="t('settingsPage.posRightBottom')" value="right bottom" />
                  </el-select>
                </el-form-item>
              </template>
            </el-form>
          </el-tab-pane>

          <!-- 通知设置 -->
          <el-tab-pane :label="t('settings.notification')" name="notification">
            <el-form :model="settingsForm" label-width="140px" class="settings-form">
              <el-form-item :label="t('settings.notificationEnabled')">
                <el-switch v-model="settingsForm.notificationEnabled" />
                <div class="form-tip">{{ t('settingsPage.notificationEnabledTip') }}</div>
              </el-form-item>

              <el-form-item :label="t('settings.soundEnabled')">
                <el-switch v-model="settingsForm.soundEnabled" :disabled="!settingsForm.notificationEnabled" />
                <div class="form-tip">{{ t('settingsPage.soundEnabledTip') }}</div>
              </el-form-item>

              <el-form-item :label="t('settings.animationEnabled')">
                <el-switch v-model="settingsForm.animationEnabled" />
                <div class="form-tip">{{ t('settingsPage.animationEnabledTip') }}</div>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 默认设置 -->
          <el-tab-pane :label="t('settings.defaults')" name="defaults">
            <el-form :model="settingsForm" label-width="140px" class="settings-form">
              <el-form-item :label="t('settings.defaultPriority')">
                <el-select v-model="settingsForm.defaultPriority">
                  <el-option :label="`🔴 ${t('todo.high')}`" :value="1" />
                  <el-option :label="`🟡 ${t('todo.medium')}`" :value="2" />
                  <el-option :label="`🟢 ${t('todo.low')}`" :value="3" />
                </el-select>
                <div class="form-tip">{{ t('settingsPage.defaultPriorityTip') }}</div>
              </el-form-item>

              <el-form-item :label="t('settings.defaultReminderOffset')">
                <el-select v-model="settingsForm.defaultReminderOffset">
                  <el-option :label="t('todoPage.onTime')" :value="0" />
                  <el-option :label="t('todoPage.offset5m')" :value="5" />
                  <el-option :label="t('todoPage.offset10m')" :value="10" />
                  <el-option :label="t('todoPage.offset15m')" :value="15" />
                  <el-option :label="t('todoPage.offset30m')" :value="30" />
                  <el-option :label="t('todoPage.reminderOffsetTextHour')" :value="60" />
                  <el-option :label="t('todoPage.offset2h')" :value="120" />
                  <el-option :label="t('todoPage.reminderOffsetTextDay')" :value="1440" />
                </el-select>
                <div class="form-tip">{{ t('settingsPage.defaultReminderOffsetTip') }}</div>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 底部操作栏 -->
      <div class="settings-footer">
        <el-button @click="handleReset">
          <el-icon><RefreshLeft /></el-icon>
          {{ t('settings.reset') }}
        </el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          <el-icon><Check /></el-icon>
          {{ t('common.save') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore, presetBackgrounds as storePresets } from '@/stores/settings'
import { getSupportedLanguages, setLocale } from '@/locales/index'
import { smartCompressImage } from '@/utils/imageCompressor'

const { t } = useI18n()
const authStore = useAuthStore()
const settingsStore = useSettingsStore()

const activeTab = ref('general')
const saving = ref(false)
const customBackgroundUrl = ref('')
const previewImage = ref('')
const selectedPresetId = ref('default')
const compressing = ref(false)

const supportedLanguages = getSupportedLanguages()

const themeOptions = computed(() => ({
  light: { label: t('settings.themeOptions.light'), color: '#f5f7fa' },
  dark: { label: t('settings.themeOptions.dark'), color: '#1a1a2e' },
  blue: { label: t('settings.themeOptions.blue'), color: '#3b82f6' },
  green: { label: t('settings.themeOptions.green'), color: '#10b981' },
  purple: { label: t('settings.themeOptions.purple'), color: '#8b5cf6' }
}))

const presetBackgrounds = computed(() => {
  const baseList = storePresets.filter(
    bg => bg.type === 'image' || bg.id === 'default' || bg.id === 'minimal'
  )
  if (previewImage.value) {
    const customCard = {
      id: 'custom',
      name: '自定义图片',
      type: 'image',
      url: previewImage.value
    }
    const filtered = baseList.filter(bg => bg.id !== 'default')
    return [customCard, ...filtered]
  }
  return baseList
})

const settingsForm = reactive({
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
})

const applyPreviewSettings = () => {
  Object.assign(settingsStore.settings, {
    backgroundType: settingsForm.backgroundType,
    backgroundColor: settingsForm.backgroundColor,
    backgroundUrl: settingsForm.backgroundUrl,
    backgroundOpacity: settingsForm.backgroundOpacity,
    backgroundSize: settingsForm.backgroundSize,
    backgroundPosition: settingsForm.backgroundPosition,
    theme: settingsForm.theme
  })
  settingsStore.saveToLocalStorage()
  const layer = document.querySelector('.background-layer')
  if (layer) {
    if (settingsForm.backgroundType === 'image' && settingsForm.backgroundUrl) {
      const opacity = (settingsForm.backgroundOpacity ?? 100) / 100
      layer.style.backgroundImage = `linear-gradient(rgba(255,255,255,${1 - opacity}), rgba(255,255,255,${1 - opacity})), url(${settingsForm.backgroundUrl})`
      layer.style.backgroundSize = settingsForm.backgroundSize || 'cover'
      layer.style.backgroundPosition = settingsForm.backgroundPosition || 'center'
      layer.style.backgroundRepeat = 'no-repeat'
      layer.style.backgroundAttachment = 'fixed'
    } else {
      layer.style.backgroundImage = ''
      layer.style.backgroundColor = settingsForm.backgroundColor || '#f5f7fa'
      layer.style.backgroundSize = ''
      layer.style.backgroundPosition = ''
      layer.style.backgroundRepeat = ''
      layer.style.backgroundAttachment = ''
    }
  }
  console.log('[Settings] background preview', {
    backgroundType: settingsForm.backgroundType,
    backgroundUrl: settingsForm.backgroundUrl,
    backgroundOpacity: settingsForm.backgroundOpacity,
    backgroundSize: settingsForm.backgroundSize,
    backgroundPosition: settingsForm.backgroundPosition
  })
}

const getGradient = (id) => {
  const gradients = {
    default: 'linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%)',
    minimal: 'linear-gradient(135deg, #e0e0e0 0%, #f5f5f5 100%)'
  }
  return gradients[id] || gradients.default
}

const loadSettings = () => {
  const userId = authStore.userInfo?.id
  if (!userId) return

  const saved = settingsStore.settings
  const isImageBg = saved.backgroundUrl && saved.backgroundUrl !== ''

  // 从 localStorage 获取当前语言，而不是从后端
  const currentLang = localStorage.getItem('dailyMate_language') || 'zh-CN'

  Object.assign(settingsForm, {
    language: currentLang, // 使用 localStorage 中的语言
    theme: saved.theme || 'light',
    backgroundType: isImageBg ? 'image' : (saved.backgroundType || 'color'),
    backgroundColor: saved.backgroundColor || '#f5f7fa',
    backgroundUrl: saved.backgroundUrl || '',
    backgroundOpacity: saved.backgroundOpacity ?? 100,
    backgroundSize: saved.backgroundSize || 'cover',
    backgroundPosition: saved.backgroundPosition || 'center',
    notificationEnabled: saved.notificationEnabled ?? true,
    soundEnabled: saved.soundEnabled ?? false,
    animationEnabled: saved.animationEnabled ?? true,
    defaultPriority: saved.defaultPriority ?? 2,
    defaultReminderOffset: saved.defaultReminderOffset ?? 0,
    defaultHomeView: saved.defaultHomeView || 'dashboard'
  })

  const presetIndex = storePresets.findIndex(bg => bg.url === saved.backgroundUrl)
  if (presetIndex !== -1) {
    selectedPresetId.value = storePresets[presetIndex].id
    previewImage.value = ''
    customBackgroundUrl.value = ''
  } else if (saved.backgroundUrl && saved.backgroundUrl.startsWith('data:image')) {
    previewImage.value = saved.backgroundUrl
    selectedPresetId.value = 'custom'
    customBackgroundUrl.value = ''
  } else if (saved.backgroundUrl) {
    customBackgroundUrl.value = saved.backgroundUrl
    previewImage.value = saved.backgroundUrl
    selectedPresetId.value = 'custom'
  }
}

const selectPresetBackground = (bg) => {
  selectedPresetId.value = bg.id
  settingsForm.backgroundUrl = bg.url || ''
  settingsForm.backgroundType = 'image'
  if (bg.id !== 'custom') {
    previewImage.value = ''
    customBackgroundUrl.value = ''
  }
  applyPreviewSettings()
}

const handleCustomUrlChange = (url) => {
  if (url) {
    settingsForm.backgroundUrl = url
    settingsForm.backgroundType = 'image'
    previewImage.value = url
    selectedPresetId.value = ''
    applyPreviewSettings()
  }
}

const handleImageUpload = async (file) => {
  const maxSize = 5 * 1024 * 1024 // 5MB
  const targetSize = 500 // KB
  
  if (file.size > maxSize) {
    ElMessage.error(t('settingsPage.imageTooLarge'))
    return false
  }
  if (!file.type.startsWith('image/')) {
    ElMessage.error(t('settingsPage.imageTypeInvalid'))
    return false
  }

  try {
    compressing.value = true
    
    // 显示压缩进度提示
    const loadingMsg = ElMessage({
      message: t('settingsPage.compressingImage'),
      type: 'info',
      duration: 0
    })

    // 智能压缩图片
    const base64 = await smartCompressImage(file, {
      targetSize,
      maxWidth: 1920,
      maxHeight: 1080
    })

    loadingMsg.close()

    // 获取压缩后信息
    const sizeInKB = Math.round((base64.length * 3) / 4 / 1024)
    
    settingsForm.backgroundUrl = base64
    settingsForm.backgroundType = 'image'
    previewImage.value = base64
    selectedPresetId.value = 'custom'
    customBackgroundUrl.value = ''
    applyPreviewSettings()
    
    ElMessage.success(t('settingsPage.imageCompressed', { size: sizeInKB }))
  } catch (error) {
    console.error('图片压缩失败:', error)
    ElMessage.error(t('settingsPage.imageProcessFailed', { message: error.message }))
  } finally {
    compressing.value = false
  }
  
  return false
}

const handleImageSelect = async (uploadFile) => {
  const rawFile = uploadFile?.raw || uploadFile
  if (!rawFile) return
  await handleImageUpload(rawFile)
}

const clearCustomImage = () => {
  previewImage.value = ''
  customBackgroundUrl.value = ''
  settingsForm.backgroundUrl = ''
  settingsForm.backgroundType = 'color'
  selectedPresetId.value = 'default'
  applyPreviewSettings()
}

const handleLanguageChange = (lang) => {
  console.log('[Settings.vue] 用户切换语言:', lang)
  
  // 只更新 settingsForm 中的语言，不保存到后端
  settingsForm.language = lang
  
  const result = setLocale(lang, true)
  console.log('[Settings.vue] setLocale 返回结果:', result)
}

const handleThemeChange = (theme) => {
  ElMessage.success(t('settingsPage.themeSwitched', { theme: themeOptions.value[theme]?.label }))
  applyPreviewSettings()
}

const handleSave = async () => {
  saving.value = true
  try {
    const userId = authStore.userInfo?.id
    if (!userId) {
      ElMessage.error(t('settingsPage.loginFirst'))
      return
    }
    await settingsStore.updateSettings(userId, settingsForm)
    ElMessage.success(t('message.saveSuccess'))
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error(t('message.networkError'))
  } finally {
    saving.value = false
  }
}

const handleReset = async () => {
  try {
    await ElMessageBox.confirm(t('settings.resetConfirm'), t('common.tip'), {
      type: 'warning'
    })
    const userId = authStore.userInfo?.id
    if (!userId) return
    await settingsStore.resetSettings(userId)
    loadSettings()
    ElMessage.success(t('message.saveSuccess'))
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('message.networkError'))
    }
  }
}

onMounted(() => {
  const userId = authStore.userInfo?.id
  if (userId) {
    settingsStore.fetchSettings(userId)
  }
  loadSettings()
})

watch(
  () => [
    settingsForm.backgroundType,
    settingsForm.backgroundColor,
    settingsForm.backgroundUrl,
    settingsForm.backgroundOpacity,
    settingsForm.backgroundSize,
    settingsForm.backgroundPosition
  ],
  () => {
    applyPreviewSettings()
  }
)
</script>

<style lang="scss" scoped>
.settings-container {
  padding: 16px;
  min-height: calc(100vh - 56px);
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.settings-card {
  width: 100%;
  max-width: 900px;
  background: rgba(255, 255, 255, 0.92);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 100px);
}

.settings-header {
  flex-shrink: 0;

  :deep(.el-card__header) {
    padding: 16px 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-bottom: none;
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    color: white;
    font-size: 18px;
    font-weight: 600;

    .el-icon {
      font-size: 22px;
    }
  }
}

.settings-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.settings-tabs {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 20px;
    background: #f5f7fa;
    border-bottom: 1px solid #e0e0e0;
    flex-shrink: 0;
  }

  :deep(.el-tabs__content) {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
  }

  :deep(.el-tab-pane) {
    height: 100%;
    overflow-y: auto;
  }
}

.settings-form {
  max-width: 600px;

  .form-tip {
    margin-top: 8px;
    font-size: 12px;
    color: #909399;
    display: flex;
    align-items: flex-start;
    gap: 6px;
    line-height: 1.5;

    .el-icon {
      flex-shrink: 0;
      margin-top: 2px;
    }
  }
}

.theme-option {
  display: flex;
  align-items: center;
  gap: 8px;

  .theme-color {
    width: 20px;
    height: 20px;
    border-radius: 4px;
    border: 1px solid #e0e0e0;
  }
}

.background-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
  width: 100%;
}

.background-item {
  position: relative;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  &.active {
    border-color: #409EFF;
  }

  .background-preview {
    width: 100%;
    height: 100px;
    position: relative;

    .gradient-preview {
      width: 100%;
      height: 100%;
    }

    .preview-image-bg {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }
  }

  .background-name {
    padding: 8px;
    text-align: center;
    font-size: 13px;
    background: #f5f7fa;
  }

  .check-icon {
    position: absolute;
    top: 8px;
    right: 8px;
    width: 24px;
    height: 24px;
    background: #409EFF;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.custom-image-section {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;

  .url-input {
    flex: 1;
    min-width: 200px;
  }
}

.image-preview-box {
  margin-top: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;

  .preview-label {
    font-size: 13px;
    color: #606266;
    white-space: nowrap;
  }

  .preview-image {
    max-width: 200px;
    max-height: 100px;
    border-radius: 4px;
    object-fit: cover;
  }
}

.settings-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #e0e0e0;
  background: #f5f7fa;
  flex-shrink: 0;
}
</style>
