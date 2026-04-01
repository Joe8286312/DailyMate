<template>
  <div class="register-container">
    <div class="register-wrapper">
      <div class="register-left">
        <div class="welcome-content">
          <div class="logo-wrapper">
            <el-icon :size="64"><DataLine /></el-icon>
          </div>
          <h1 class="welcome-title">加入 DailyMate</h1>
          <p class="welcome-text">
            开启高效管理之旅<br>
            让每一天都井井有条
          </p>
          <div class="benefits">
            <div class="benefit-item">
              <el-icon :size="20"><CircleCheck /></el-icon>
              <span>免费使用所有功能</span>
            </div>
            <div class="benefit-item">
              <el-icon :size="20"><CircleCheck /></el-icon>
              <span>数据安全加密存储</span>
            </div>
            <div class="benefit-item">
              <el-icon :size="20"><CircleCheck /></el-icon>
              <span>多设备同步支持</span>
            </div>
          </div>
        </div>
      </div>

      <div class="register-right">
        <div class="register-card">
          <div class="register-header">
            <h1 class="title">注册</h1>
            <p class="subtitle">创建您的账户，开始使用 DailyMate</p>
          </div>

          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="rules"
            class="register-form"
            @keyup.enter="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="用户名"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="邮箱（可选）"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="密码"
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="确认密码"
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="register-btn"
                @click="handleRegister"
              >
                注册
              </el-button>
            </el-form-item>

            <div class="register-links">
              <span>已有账户？</span>
              <router-link to="/login" class="link">立即登录</router-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, DataLine, CircleCheck } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await authStore.register({
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password
      })
      ElMessage.success('注册成功！即将跳转到登录页...')
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } catch (error) {
      console.error('注册失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
  padding: 20px;
  overflow: hidden;
}

.register-wrapper {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
    animation: pulse 15s ease-in-out infinite;
  }

  @keyframes pulse {
    0%, 100% { transform: translate(0, 0) scale(1); }
    50% { transform: translate(-10%, -10%) scale(1.1); }
  }
}

.welcome-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: white;

  .logo-wrapper {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 100px;
    height: 100px;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    border-radius: 50%;
    margin-bottom: 24px;
    color: white;
  }

  .welcome-title {
    font-size: 36px;
    font-weight: 700;
    margin: 0 0 16px 0;
    letter-spacing: 1px;
  }

  .welcome-text {
    font-size: 15px;
    line-height: 1.8;
    opacity: 0.9;
    margin: 0 0 32px 0;
  }

  .benefits {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .benefit-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 16px;
      background: rgba(255, 255, 255, 0.15);
      backdrop-filter: blur(10px);
      border-radius: 10px;
      font-size: 14px;
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.25);
        transform: translateX(8px);
      }
    }
  }
}

.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 50px;
  background: white;
}

.register-card {
  width: 100%;
  max-width: 360px;
}

.register-header {
  text-align: center;
  margin-bottom: 36px;

  .title {
    font-size: 28px;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 8px 0;
  }

  .subtitle {
    font-size: 14px;
    color: #64748b;
    margin: 0;
  }
}

.register-form {
  .el-form-item {
    margin-bottom: 18px;

    :deep(.el-input__wrapper) {
      border-radius: 12px;
      padding: 12px 16px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      &.is-focus {
        box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15);
      }
    }
  }

  .register-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #10b981, #059669);
    border: none;
    border-radius: 12px;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 30px rgba(16, 185, 129, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

.register-links {
  text-align: center;
  font-size: 14px;
  color: #64748b;
  margin-top: 20px;

  .link {
    color: #10b981;
    text-decoration: none;
    font-weight: 600;
    margin-left: 6px;
    transition: all 0.3s;

    &:hover {
      color: #059669;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .register-wrapper {
    flex-direction: column;
    max-width: 420px;
  }

  .register-left {
    padding: 40px 20px;

    .welcome-content {
      .logo-wrapper {
        width: 80px;
        height: 80px;
      }

      .welcome-title {
        font-size: 28px;
      }

      .benefits {
        display: none;
      }
    }
  }

  .register-right {
    padding: 40px 30px;
  }
}
</style>
