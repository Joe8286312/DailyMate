<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { userApi, type User } from '../api/user'
import EditUserModal from '../components/EditUserModal.vue'
import PasswordModal from '../components/PasswordModal.vue'

const users = ref<User[]>([])
const loading = ref(false)
const currentUser = ref<User | null>(null)
const showEditModal = ref(false)
const showPasswordModal = ref(false)
const selectedUser = ref<User | null>(null)

const loadUsers = async () => {
  loading.value = true
  try {
    users.value = await userApi.getUserList()
  } catch (err) {
    console.error('Failed to load users:', err)
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/login'
  }
}

const handleEditUser = (user: User) => {
  selectedUser.value = user
  showEditModal.value = true
}

const handleChangePassword = (user: User) => {
  selectedUser.value = user
  showPasswordModal.value = true
}

const handleDeleteUser = async (user: User) => {
  if (confirm(`确定要删除用户 "${user.username}" 吗？此操作不可恢复！`)) {
    try {
      await userApi.deleteAccount()
      alert('用户删除成功')
      loadUsers()
    } catch (err) {
      alert('删除失败，请重试')
    }
  }
}

const closeEditModal = () => {
  showEditModal.value = false
  selectedUser.value = null
  loadUsers()
}

const closePasswordModal = () => {
  showPasswordModal.value = false
  selectedUser.value = null
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  }
  loadUsers()
})
</script>

<template>
  <div class="admin-container">
    <header class="admin-header">
      <div class="header-content">
        <h1 class="header-title">用户管理系统</h1>
        <div class="header-actions">
          <span class="user-info">{{ currentUser?.username }}</span>
          <button @click="handleLogout" class="logout-button">退出登录</button>
        </div>
      </div>
    </header>

    <main class="admin-main">
      <div class="table-container">
        <div class="table-header">
          <h2>用户列表</h2>
          <button @click="loadUsers" class="refresh-button" :disabled="loading">
            {{ loading ? '加载中...' : '刷新' }}
          </button>
        </div>

        <div v-if="loading" class="loading">加载中...</div>

        <table v-else class="user-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>邮箱</th>
              <th>头像</th>
              <th>注册时间</th>
              <th>更新时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email || '-' }}</td>
              <td>
                <img
                  v-if="user.avatar"
                  :src="user.avatar"
                  alt="avatar"
                  class="avatar"
                />
                <span v-else>-</span>
              </td>
              <td>{{ formatDate(user.createdAt) }}</td>
              <td>{{ formatDate(user.updatedAt) }}</td>
              <td class="action-buttons">
                <button @click="handleEditUser(user)" class="btn btn-edit">编辑</button>
                <button @click="handleChangePassword(user)" class="btn btn-password">改密码</button>
                <button @click="handleDeleteUser(user)" class="btn btn-delete">删除</button>
              </td>
            </tr>
            <tr v-if="users.length === 0">
              <td colspan="7" class="empty-message">暂无用户数据</td>
            </tr>
          </tbody>
        </table>
      </div>
    </main>

    <EditUserModal
      v-if="showEditModal"
      :user="selectedUser"
      @close="closeEditModal"
    />

    <PasswordModal
      v-if="showPasswordModal"
      :user="selectedUser"
      @close="closePasswordModal"
    />
  </div>
</template>

<style scoped>
.admin-container {
  min-height: 100vh;
  background: #f7fafc;
}

.admin-header {
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px 24px;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  color: #4a5568;
  font-weight: 500;
}

.logout-button {
  padding: 8px 16px;
  background: #e53e3e;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-button:hover {
  background: #c53030;
}

.admin-main {
  max-width: 1400px;
  margin: 32px auto;
  padding: 0 24px;
}

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.table-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
}

.refresh-button {
  padding: 8px 16px;
  background: #4299e1;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.refresh-button:hover:not(:disabled) {
  background: #3182ce;
}

.refresh-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  padding: 48px;
  color: #718096;
  font-size: 16px;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th,
.user-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

.user-table th {
  background: #f7fafc;
  color: #4a5568;
  font-weight: 600;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.user-table td {
  color: #2d3748;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit {
  background: #48bb78;
  color: white;
}

.btn-edit:hover {
  background: #38a169;
}

.btn-password {
  background: #ed8936;
  color: white;
}

.btn-password:hover {
  background: #dd6b20;
}

.btn-delete {
  background: #f56565;
  color: white;
}

.btn-delete:hover {
  background: #e53e3e;
}

.empty-message {
  text-align: center;
  color: #a0aec0;
  padding: 48px;
}
</style>
