<template>
  <div class="knowledge-base">
    <el-container>
      <!-- 知识库列表 -->
      <el-aside width="320px" class="kb-list">
        <div class="list-header">
          <h3>知识库</h3>
          <el-button type="primary" @click="createKbDialogVisible = true">
            <el-icon><Plus /></el-icon>
            新建
          </el-button>
        </div>
        <el-input v-model="searchKeyword" placeholder="搜索知识库..." prefix-icon="Search" clearable />
        <div class="kb-items">
          <div
            v-for="kb in filteredKbs"
            :key="kb.id"
            :class="['kb-item', { active: selectedKb?.id === kb.id }]"
            @click="selectKb(kb)"
          >
            <div class="kb-icon">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="kb-info">
              <div class="kb-name">{{ kb.name }}</div>
              <div class="kb-meta">
                <span>{{ kb.docCount }} 文档</span>
                <span>{{ kb.category }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-aside>

      <!-- 知识库详情 -->
      <el-main class="kb-detail" v-if="selectedKb">
        <div class="detail-header">
          <div>
            <h2>{{ selectedKb.name }}</h2>
            <p>{{ selectedKb.description }}</p>
          </div>
          <div class="actions">
            <el-button @click="uploadDialogVisible = true">
              <el-icon><Upload /></el-icon>
              上传文档
            </el-button>
            <el-button type="danger" @click="handleDeleteKb">删除</el-button>
          </div>
        </div>

        <!-- 文档列表 -->
        <el-table :data="documents" style="margin-top: 20px">
          <el-table-column prop="title" label="文档标题" />
          <el-table-column prop="createdAt" label="上传时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewDoc(row)">查看</el-button>
              <el-button link type="danger" @click="deleteDoc(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-main>

      <!-- 空状态 -->
      <el-main v-else class="empty-state">
        <el-empty description="请选择或创建一个知识库" />
      </el-main>
    </el-container>

    <!-- 创建知识库对话框 -->
    <el-dialog v-model="createKbDialogVisible" title="创建知识库" width="500px">
      <el-form :model="kbForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="kbForm.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="kbForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="kbForm.category">
            <el-option label="私有" value="private" />
            <el-option label="公共" value="public" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="kbForm.tags" placeholder="用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createKbDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateKb">创建</el-button>
      </template>
    </el-dialog>

    <!-- 上传文档对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传文档" width="500px">
      <el-upload drag action="/api/knowledge/upload" multiple>
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">拖拽文件到此处 或 <em>点击上传</em></div>
      </el-upload>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus, Reading, Upload, UploadFilled } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'

const createKbDialogVisible = ref(false)
const uploadDialogVisible = ref(false)
const searchKeyword = ref('')
const selectedKb = ref<any>(null)

const kbForm = ref({
  name: '',
  description: '',
  category: 'private',
  tags: ''
})

const knowledgeBases = ref([
  { id: 1, name: 'Java 教程', description: 'Java 基础到高级', category: 'public', docCount: 15 },
  { id: 2, name: 'Spring 指南', description: 'Spring 全家桶教程', category: 'public', docCount: 8 },
  { id: 3, name: '个人笔记', description: '我的学习笔记', category: 'private', docCount: 23 }
])

const documents = ref([
  { id: 1, title: 'Java 基础语法.pdf', createdAt: new Date() },
  { id: 2, title: '面向对象编程.md', createdAt: new Date(Date.now() - 86400000) }
])

const filteredKbs = computed(() => {
  if (!searchKeyword.value) return knowledgeBases.value
  return knowledgeBases.value.filter(kb =>
    kb.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const formatTime = (date: Date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const selectKb = (kb: any) => {
  selectedKb.value = kb
  // TODO: 加载文档列表
}

const handleCreateKb = () => {
  // TODO: 调用创建接口
  knowledgeBases.value.push({
    id: Date.now(),
    name: kbForm.value.name,
    description: kbForm.value.description,
    category: kbForm.value.category,
    docCount: 0
  })
  createKbDialogVisible.value = false
  ElMessage.success('创建成功')
}

const handleDeleteKb = () => {
  ElMessageBox.confirm('确定删除该知识库吗？', '提示', {
    type: 'warning'
  }).then(() => {
    // TODO: 调用删除接口
    const index = knowledgeBases.value.findIndex(kb => kb.id === selectedKb.value.id)
    if (index > -1) {
      knowledgeBases.value.splice(index, 1)
    }
    selectedKb.value = null
    ElMessage.success('删除成功')
  })
}

const viewDoc = (doc: any) => {
  // TODO: 查看文档
}

const deleteDoc = (doc: any) => {
  // TODO: 删除文档
}
</script>

<style scoped>
.knowledge-base {
  height: 100%;
}

.kb-list {
  background: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  padding: 0;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.list-header h3 {
  margin: 0;
}

.el-input {
  margin: 10px 15px;
  width: calc(100% - 30px);
}

.kb-items {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.kb-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 5px;
}

.kb-item:hover {
  background: #e9ecf0;
}

.kb-item.active {
  background: #e6f7ff;
}

.kb-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.kb-info {
  flex: 1;
}

.kb-name {
  font-weight: 500;
  margin-bottom: 5px;
}

.kb-meta {
  font-size: 12px;
  color: #999;
}

.kb-meta span {
  margin-right: 10px;
}

.kb-detail {
  display: flex;
  flex-direction: column;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.detail-header h2 {
  margin: 0 0 5px 0;
}

.detail-header p {
  color: #666;
  margin: 0;
}

.actions {
  display: flex;
  gap: 10px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
