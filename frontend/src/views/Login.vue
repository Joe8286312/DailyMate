<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-left">
        <div class="welcome-content">
          <div class="logo-wrapper">
            <el-icon :size="64"><DataLine /></el-icon>
          </div>
          <h1 class="welcome-title">Welcome to DailyMate</h1>
          <p class="welcome-text">
            一个现代化的个人日常管理工具<br />
            帮助您轻松管理待办事项和账单收支
          </p>
          <div class="features">
            <div class="feature-item">
              <el-icon :size="24"><List /></el-icon>
              <span>待办事项管理</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24"><Money /></el-icon>
              <span>账单收支记录</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24"><DataAnalysis /></el-icon>
              <span>数据统计分析</span>
            </div>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="login-card">
          <div class="login-header">
            <h1 class="title">登录</h1>
            <p class="subtitle">欢迎回来，请登录您的账户</p>
          </div>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="rules"
            class="login-form"
            @keyup.enter="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="用户名"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
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

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-btn"
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>

            <div class="login-links">
              <span>还没有账户？</span>
              <router-link to="/register" class="link">立即注册</router-link>
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
import { User, Lock, DataLine, List, Money, DataAnalysis } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为 6 位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await authStore.login(loginForm)
      ElMessage.success('登录成功！')
      router.push('/dashboard')
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
  padding: 20px;
  overflow: hidden;
}

.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

  .features {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .feature-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 20px;
      background: rgba(255, 255, 255, 0.15);
      backdrop-filter: blur(10px);
      border-radius: 12px;
      font-size: 14px;
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.25);
        transform: translateX(8px);
      }
    }
  }
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 50px;
  background: white;
}

.login-card {
  width: 100%;
  max-width: 360px;
}

.login-header {
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

.login-form {
  .el-form-item {
    margin-bottom: 20px;

    :deep(.el-input__wrapper) {
      border-radius: 12px;
      padding: 12px 16px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      &.is-focus {
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
      }
    }
  }

  .login-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border: none;
    border-radius: 12px;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

.login-links {
  text-align: center;
  font-size: 14px;
  color: #64748b;
  margin-top: 20px;

  .link {
    color: #667eea;
    text-decoration: none;
    font-weight: 600;
    margin-left: 6px;
    transition: all 0.3s;

    &:hover {
      color: #764ba2;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    max-width: 420px;
  }

  .login-left {
    padding: 40px 20px;

    .welcome-content {
      .logo-wrapper {
        width: 80px;
        height: 80px;
      }

      .welcome-title {
        font-size: 28px;
      }

      .features {
        display: none;
      }
    }
  }

  .login-right {
    padding: 40px 30px;
  }
}
</style>
