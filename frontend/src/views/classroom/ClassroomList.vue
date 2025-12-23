<template>
  <div class="classroom-list">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>课堂列表</h1>
          <el-button type="primary" @click="createDialogVisible = true" v-if="isTeacher">
            <el-icon><Plus /></el-icon>
            创建课堂
          </el-button>
        </div>
      </el-header>
      <el-main>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="room in classrooms" :key="room.id">
            <el-card class="classroom-card" @click="enterClassroom(room.id)">
              <img :src="room.coverImage || '/default-classroom.png'" class="cover" />
              <h3>{{ room.name }}</h3>
              <p class="teacher">教师：{{ room.teacherName }}</p>
              <p class="members">
                <el-icon><User /></el-icon>
                {{ room.currentMembers }} / {{ room.maxMembers }}
              </p>
              <el-tag :type="getStatusType(room.status)">{{ getStatusText(room.status) }}</el-tag>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>

    <!-- 创建课堂对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建课堂" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="课堂名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="课堂描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="form.maxMembers" :min="1" :max="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, User } from '@element-plus/icons-vue'

const router = useRouter()
const createDialogVisible = ref(false)
const isTeacher = ref(true) // TODO: 根据用户角色判断

const classrooms = ref([
  { id: 1, name: 'Java 高级编程', teacherName: '张老师', currentMembers: 30, maxMembers: 50, status: 1, coverImage: '' },
  { id: 2, name: 'Spring Boot 实战', teacherName: '李老师', currentMembers: 25, maxMembers: 40, status: 1, coverImage: '' },
  { id: 3, name: '数据结构与算法', teacherName: '王老师', currentMembers: 45, maxMembers: 50, status: 1, coverImage: '' }
])

const form = reactive({
  name: '',
  description: '',
  maxMembers: 50
})

const getStatusType = (status: number) => {
  return status === 1 ? 'success' : status === 0 ? 'info' : 'danger'
}

const getStatusText = (status: number) => {
  return status === 1 ? '进行中' : status === 0 ? '未开始' : '已结束'
}

const enterClassroom = (id: number) => {
  router.push(`/classroom/${id}`)
}

const handleCreate = () => {
  // TODO: 调用创建课堂接口
  createDialogVisible.value = false
}
</script>

<style scoped>
.classroom-list {
  height: 100%;
  background: #f5f7fa;
}

.el-header {
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.classroom-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s;
}

.classroom-card:hover {
  transform: translateY(-5px);
}

.classroom-card .cover {
  width: 100%;
  height: 140px;
  object-fit: cover;
}

.classroom-card h3 {
  margin: 10px 0;
  font-size: 16px;
}

.classroom-card .teacher,
.classroom-card .members {
  color: #909399;
  font-size: 14px;
  margin: 5px 0;
}

.classroom-card .members {
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>
