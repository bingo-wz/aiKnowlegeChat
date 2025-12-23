<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>AI 知识教研室</h2>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, type LoginRequest } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const formRef = ref()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive<LoginRequest>({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const response = await login(form)
    userStore.setToken(response.token)
    userStore.setUserInfo({
      id: response.userId,
      username: response.username,
      nickname: response.nickname,
      avatar: response.avatar,
      email: '',
      role: response.role
    })
    ElMessage.success('登录成功')
    router.push('/classroom')
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}

.login-card :deep(.el-card__header) {
  text-align: center;
}

h2 {
  margin: 0;
  color: #409eff;
}
</style>
