<template>
  <div class="page-container">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>

    <div class="main-content">
      <div class="row justify-content-center">
        <div class="col-lg-10 col-xl-9">
          <!-- 返回按钮 -->
          <button class="back-btn" @click="$router.push('/home')">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            返回
          </button>

          <!-- 在线提交报修表单 -->
          <div class="form-section">
            <div class="form-header">
              <h2 class="form-title">📝 在线提交报修</h2>
            </div>

            <div class="form-group">
              <label>报修类别 <span class="required">*</span></label>
              <div class="category-grid">
                <div 
                  v-for="cat in categories" 
                  :key="cat.value"
                  class="category-item"
                  :class="{ active: formData.category === cat.value }"
                  @click="formData.category = cat.value"
                >
                  <span class="category-icon">{{ cat.icon }}</span>
                  <span class="category-name">{{ cat.label }}</span>
                </div>
              </div>
            </div>

            <div class="form-group">
              <label>报修地点 <span class="required">*</span></label>
              <input v-model="formData.location" type="text" placeholder="如：学生宿舍3号楼201室" />
            </div>

            <div class="form-group">
              <label>问题描述 <span class="required">*</span></label>
              <textarea v-model="formData.description" placeholder="请详细描述报修问题..." rows="4"></textarea>
            </div>

            <div class="form-group">
              <label>联系电话 <span class="required">*</span></label>
              <input v-model="formData.userPhone" type="tel" placeholder="方便维修人员联系你" />
            </div>

            <button class="submit-btn" @click="submitMaintenance">提交报修</button>
          </div>

          <!-- 我的报修记录 -->
          <div class="records-section">
            <div class="section-header">
              <h3>📋 我的报修记录</h3>
              <div class="header-actions">
                <label class="toggle-switch">
                  <input type="checkbox" v-model="showCompleted">
                  <span class="toggle-slider"></span>
                  <span class="toggle-label">显示已完成</span>
                </label>
                <button class="refresh-btn" @click="loadMaintenanceList">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
                  </svg>
                  刷新
                </button>
              </div>
            </div>

            <div v-if="loading" class="loading-state">
              <div class="spinner"></div>
              <p>加载中...</p>
            </div>

            <div v-else-if="filteredList.length === 0" class="empty-state">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="#cbd5e1" viewBox="0 0 24 24">
                <path d="M19 5v14H5V5h14m0-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z"/>
              </svg>
              <p>{{ showCompleted ? '暂无报修记录' : '暂无待处理记录' }}</p>
            </div>

            <div v-else class="repair-list">
              <div v-for="item in filteredList" :key="item.id" class="repair-item">
                <div class="repair-item-header">
                  <span class="type-tag" :class="item.category">{{ getCategoryName(item.category) }}</span>
                  <span class="status-tag" :class="'status-' + item.status">{{ getStatusName(item.status) }}</span>
                </div>
                <div class="repair-item-body">
                  <p class="repair-location">📍 {{ item.location }}</p>
                  <p class="repair-desc">{{ item.description }}</p>
                  <p class="repair-phone">📞 {{ item.userPhone }}</p>
                </div>
                <div class="repair-item-footer">
                  <span class="repair-time">{{ formatTime(item.createTime) }}</span>
                  <div class="action-buttons">
                    <button 
                      v-if="item.status === 2" 
                      class="delete-btn"
                      @click="handleDelete(item.id, $event)"
                    >
                      删除
                    </button>
                    <button 
                      v-if="item.status !== 2" 
                      class="complete-btn"
                      @click="handleComplete(item.id, $event)"
                    >
                      完成
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script>
export default {
  name: 'Maintenance',
  data() {
    return {
      formData: {
        userId: null,
        userName: '',
        userPhone: '',
        category: '',
        location: '',
        description: ''
      },
      maintenanceList: [],
      showCompleted: false,  // 默认隐藏已完成项目
      showToast: false,
      toastMessage: '',
      loading: false,
      categories: [
        { value: 'water', label: '水电维修', icon: '💧' },
        { value: 'furniture', label: '家具维修', icon: '🛋️' },
        { value: 'network', label: '网络报修', icon: '📶' },
        { value: 'door', label: '门窗维修', icon: '🚪' },
        { value: 'other', label: '其他', icon: '📦' }
      ]
    }
  },
  computed: {
    // 根据 showCompleted 过滤显示的列表
    filteredList() {
      if (this.showCompleted) {
        return this.maintenanceList.filter(item => item.status === 2)  // 只显示已完成的
      } else {
        return this.maintenanceList.filter(item => item.status !== 2)  // 只显示未完成的
      }
    }
  },
  mounted() {
    const userId = this.$cookies.get('userId')
    if (userId) {
      this.formData.userId = parseInt(userId)
    }
    this.loadMaintenanceList()
  },
  methods: {
    callPhone(phone) {
      window.location.href = 'tel:' + phone
    },
    showToastMessage(msg) {
      this.toastMessage = msg
      this.showToast = true
      setTimeout(() => { this.showToast = false }, 2000)
    },
    loadMaintenanceList() {
      // 直接从 cookie 获取 userId，而不是依赖 formData
      const userId = this.$cookies.get('userId')
      if (!userId) {
        console.log('未登录，无法加载报修记录')
        this.loading = false
        return
      }
      this.loading = true
      this.$axios.get(`/maintenance/list?userId=${userId}`)
        .then(res => {
          if (res.data.code === 200) {
            this.maintenanceList = res.data.data || []
          }
        })
        .catch(err => {
          console.error('加载报修列表失败:', err)
        })
        .finally(() => {
          this.loading = false
        })
    },
    async submitMaintenance() {
      // 获取最新的 userId
      const userId = this.$cookies.get('userId')
      if (!userId) {
        this.showToastMessage('请先登录')
        return
      }
      this.formData.userId = parseInt(userId)
      
      if (!this.formData.category) {
        this.showToastMessage('请选择报修类别')
        return
      }
      if (!this.formData.location) {
        this.showToastMessage('请填写报修地点')
        return
      }
      if (!this.formData.description) {
        this.showToastMessage('请填写问题描述')
        return
      }
      if (!this.formData.userPhone) {
        this.showToastMessage('请填写联系电话')
        return
      }
      console.log('提交报修数据:', this.formData)
      try {
        const res = await this.$axios.post('/maintenance/add', this.formData)
        console.log('提交响应:', res.data)
        if (res.data.code === 200) {
          this.showToastMessage('报修已提交，我们会尽快处理')
          this.formData = {
            userId: parseInt(userId),
            userName: '',
            userPhone: '',
            category: '',
            location: '',
            description: ''
          }
          this.loadMaintenanceList()
        } else {
          this.showToastMessage(res.data.message || '提交失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        this.showToastMessage('提交失败，请检查网络')
      }
    },
    handleComplete(id, event) {
      // 阻止事件冒泡，防止触发父元素的点击事件
      if (event) event.stopPropagation()
      this.markAsComplete(id)
    },
    handleDelete(id, event) {
      if (event) event.stopPropagation()
      if (confirm('确定要删除这条报修记录吗？')) {
        this.deleteMaintenance(id)
      }
    },
    async deleteMaintenance(id) {
      console.log('开始删除, id:', id)
      try {
        const res = await this.$axios.delete(`/maintenance/delete?id=${id}`)
        console.log('删除响应:', res.data)
        if (res.data.code === 200) {
          this.showToastMessage('已删除')
          this.loadMaintenanceList()
        } else {
          this.showToastMessage(res.data.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        this.showToastMessage('删除失败: ' + (error.message || '网络错误'))
      }
    },
    async markAsComplete(id) {
      console.log('开始标记完成, id:', id)
      try {
        const res = await this.$axios.put(`/maintenance/update?id=${id}&status=2`)
        console.log('请求响应:', res.data)
        if (res.data.code === 200) {
          this.showToastMessage('已标记为完成')
          this.loadMaintenanceList()
        } else {
          this.showToastMessage(res.data.message || '操作失败')
        }
      } catch (error) {
        console.error('标记完成失败:', error)
        this.showToastMessage('操作失败: ' + (error.message || '网络错误'))
      }
    },
    getCategoryName(category) {
      const cat = this.categories.find(c => c.value === category)
      return cat ? cat.icon + ' ' + cat.label : category
    },
    getStatusName(status) {
      const statusMap = {
        0: '待处理',
        1: '处理中',
        2: '已完成'
      }
      return statusMap[status] || '未知'
    },
    formatTime(time) {
      if (!time) return '-'
      const date = new Date(time)
      return date.getFullYear() + '年' + (date.getMonth() + 1) + '月' + date.getDate() + '日'
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 50%, #f0f2f5 100%);
}

.bg-decoration {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  pointer-events: none;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 20s ease-in-out infinite;
}

.circle-1 {
  width: 350px; height: 350px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  top: -80px; right: -80px;
}

.circle-2 {
  width: 300px; height: 300px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  bottom: -60px; left: -60px;
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, -20px) scale(1.05); }
}

.main-content {
  position: relative;
  z-index: 1;
  padding: 30px 20px 50px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 7px 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: white;
  cursor: pointer;
  transition: all 0.25s ease;
  margin-bottom: 16px;
  box-shadow: 0 3px 12px rgba(102, 126, 234, 0.3);
}

.back-btn:hover {
  transform: translateX(-3px) scale(1.03);
  box-shadow: 0 6px 18px rgba(102, 126, 234, 0.4);
}

/* 热线卡片区域 */
.hotline-section {
  animation: slideUp 0.6s ease-out;
  margin-bottom: 24px;
}

.page-header {
  animation: slideUp 0.6s ease-out;
}

.header-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 16px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.header-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.header-subtitle {
  color: #6b7280;
  font-size: 15px;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.hotline-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.hotline-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border: 2px solid transparent;
}

.hotline-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.hotline-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.hotline-info {
  flex: 1;
  min-width: 0;
}

.hotline-info h4 {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px;
}

.hotline-phone {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

.call-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 14px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  flex-shrink: 0;
}

.call-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
}

/* 表单区域 */
.form-section {
  background: white;
  border-radius: 20px;
  padding: 24px;
  animation: slideUp 0.6s ease-out 0.1s both;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
}

.form-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.form-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 14px 8px;
  background: #f8fafc;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.category-item:hover {
  border-color: #667eea;
  background: #f0f4ff;
}

.category-item.active {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.category-item.active .category-name {
  color: white;
}

.category-icon {
  font-size: 24px;
}

.category-name {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.required {
  color: #ef4444;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 14px 16px;
  border: 1.5px solid #e5e7eb;
  border-radius: 12px;
  font-size: 14px;
  color: #1a1a2e;
  transition: all 0.25s ease;
  font-family: inherit;
  background: #fafbfc;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: white;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #b0b8c4;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.35);
  margin-top: 8px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.45);
}

/* 报修记录 */
.records-section {
  animation: slideUp 0.6s ease-out 0.2s both;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 切换开关样式 */
.toggle-switch {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  user-select: none;
}

.toggle-switch input {
  display: none;
}

.toggle-slider {
  width: 36px;
  height: 20px;
  background: #d1d5db;
  border-radius: 10px;
  position: relative;
  transition: all 0.3s ease;
}

.toggle-slider::after {
  content: '';
  position: absolute;
  width: 16px;
  height: 16px;
  background: white;
  border-radius: 50%;
  top: 2px;
  left: 2px;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}

.toggle-switch input:checked + .toggle-slider {
  background: linear-gradient(135deg, #10b981, #059669);
}

.toggle-switch input:checked + .toggle-slider::after {
  left: 18px;
}

.toggle-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

.toggle-switch input:checked ~ .toggle-label {
  color: #10b981;
}

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: white;
  border: 1.5px solid #e5e7eb;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.repair-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.repair-item {
  background: white;
  border-radius: 16px;
  padding: 16px 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.repair-item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.repair-item-body {
  margin-bottom: 12px;
}

.repair-location,
.repair-phone {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 6px;
}

.repair-desc {
  font-size: 14px;
  color: #374151;
  margin: 0 0 6px;
  line-height: 1.5;
}

.repair-item-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.repair-time {
  font-size: 12px;
  color: #9ca3af;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.delete-btn {
  padding: 4px 12px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.delete-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
}

.complete-btn {
  padding: 4px 12px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.complete-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}

/* 类型标签 */
.type-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.type-tag.water { background: #dbeafe; color: #1d4ed8; }
.type-tag.furniture { background: #fef3c7; color: #b45309; }
.type-tag.network { background: #e0f2fe; color: #0369a1; }
.type-tag.door { background: #fee2e2; color: #dc2626; }
.type-tag.other { background: #f3f4f6; color: #64748b; }

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.status-tag.status-0 { background: #fef3c7; color: #b45309; }
.status-tag.status-1 { background: #dbeafe; color: #1d4ed8; }
.status-tag.status-2 { background: #d1fae5; color: #047857; }

/* 加载和空状态 */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.loading-state p,
.empty-state p {
  margin-top: 12px;
  color: #6b7280;
  font-size: 14px;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Toast */
.toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  z-index: 3000;
  animation: toastIn 0.3s ease;
}

@keyframes toastIn {
  from { opacity: 0; transform: translate(-50%, -50%) scale(0.9); }
  to { opacity: 1; transform: translate(-50%, -50%) scale(1); }
}

@media (max-width: 768px) {
  .main-content { padding: 20px 16px 40px; }
  .form-section { padding: 20px 16px; }
  .hotline-grid { grid-template-columns: 1fr; }
  .category-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 480px) {
  .category-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
