<template>
  <router-view />
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted } from 'vue'

// 全局消息提示配置
ElMessage.config({
  duration: 3000,
  max: 3
})

onMounted(() => {
  console.log('DailyMate 应用已启动')
})
</script>

<style lang="scss">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  width: 100%;
}

body {
  font-family: 'Helvetica Neue', Arial, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
}

#app {
  width: 100%;
  height: 100%;
}
</style>
