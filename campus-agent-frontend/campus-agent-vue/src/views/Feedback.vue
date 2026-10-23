<template>
  <div class="feedback-page">
    <!-- 返回按钮 -->
    <div class="back-bar">
      <button class="back-btn" @click="$router.push('/home')">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
          <path d="M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"/>
        </svg>
        返回
      </button>
    </div>

    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
          <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 12H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
        </svg>
      </div>
      <h1 class="header-title">问题反馈</h1>
      <p class="header-subtitle">您的反馈是我们改进的动力</p>
    </div>

    <!-- 反馈类型网格 -->
    <div class="type-grid">
      <div
        v-for="item in feedbackTypes"
        :key="item.value"
        class="type-card"
        :class="{ active: selectedType === item.value }"
        @click="goToFeedback(item.value)"
      >
        <div class="type-icon" :style="{ background: item.color }">
          <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24" v-html="item.svg"></svg>
        </div>
        <div class="type-content">
          <h3 class="type-name">{{ item.label }}</h3>
          <p class="type-desc">{{ item.desc }}</p>
        </div>
        <div class="type-arrow">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </div>
      </div>
    </div>

    <!-- 我的反馈记录 -->
    <div class="records-section">
      <div class="section-header">
        <div class="section-title-wrap">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
          </svg>
          <h3>我的反馈记录</h3>
        </div>
        <button class="refresh-btn" @click="loadFeedbackList">
          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 24 24">
            <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
          </svg>
          刷新
        </button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="feedbackList.length === 0" class="empty-state">
        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="#cbd5e1" viewBox="0 0 24 24">
          <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
        </svg>
        <p>暂无反馈记录</p>
        <span>选择上方类型发起反馈吧</span>
      </div>

      <!-- 反馈列表 -->
      <div v-else class="feedback-list">
        <div
          v-for="item in feedbackList"
          :key="item.id"
          class="feedback-card"
          @click="toggleExpand(item.id)"
        >
          <div class="card-header">
            <span class="type-badge" :class="item.type">{{ getTypeName(item.type) }}</span>
            <span class="status-badge" :class="'status-' + item.status">{{ getStatusName(item.status) }}</span>
          </div>

          <h4 class="card-title">{{ item.title || '无标题' }}</h4>
          <p class="card-content">{{ item.content }}</p>

          <div v-if="item.location" class="card-info">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
            </svg>
            {{ item.location }}
          </div>

          <!-- 展开的回复内容 -->
          <div v-if="expandedId === item.id && item.reply" class="card-reply">
            <div class="reply-header">
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 24 24">
                <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
              </svg>
              <span>官方回复</span>
            </div>
            <p>{{ item.reply }}</p>
          </div>

          <div class="card-footer">
            <span class="card-time">{{ formatTime(item.createTime) }}</span>
            <div class="card-actions">
              <button
                v-if="item.status !== 3 && item.status !== 2"
                class="action-btn complete"
                @click.stop="markAsComplete(item.id)"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                完成
              </button>
              <button class="action-btn expand" :class="{ rotated: expandedId === item.id }">
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Feedback',
  data() {
    return {
      expandedId: null,
      formData: { userId: null },
      feedbackList: [],
      loading: false,
      feedbackTypes: [
        { value: 'ask', label: '向老师提问', desc: '向老师请教问题、课程咨询', color: 'linear-gradient(135deg, #667eea, #764ba2)', svg: '<path d="M12 14l9-5-9-5-9 5 9 5zm0 0l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14zm-4 6v-7.5l4-2.222"/>' },
        { value: 'suggest', label: '投诉与建议', desc: '提出意见或投诉反馈', color: 'linear-gradient(135deg, #f093fb, #f5576c)', svg: '<path d="M9 21.75l2.25-6.75-5.25-2.25 10.5-1.5-2.25-6.75 6 4.5-2.25 6.75 5.25 2.25-10.5 1.5 2.25 6.75-6-4.5z"/>' },
        { value: 'dining', label: '食堂餐饮反馈', desc: '食堂卫生、服务投诉', color: 'linear-gradient(135deg, #4facfe, #00f2fe)', svg: '<path d="M11 9H9V2H7v7H5V2H3v7c0 2.12 1.66 3.84 3.75 3.97V22h2.5v-9.03C11.34 12.84 13 11.12 13 9V2h-2v7zm5-3v8h2.5v8H21V2c-2.76 0-5 2.24-5 4z"/>' },
        { value: 'dorm', label: '宿舍管理反馈', desc: '宿舍设施、室友问题', color: 'linear-gradient(135deg, #43e97b, #38f9d7)', svg: '<path d="M7 14c1.66 0 3-1.34 3-3S8.66 8 7 8s-3 1.34-3 3 1.34 3 3 3zm0-4c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm12-3h-8v8H3V5H1v15h2v-3h18v3h2v-9c0-2.21-1.79-4-4-4zm2 8h-8V9h6c1.1 0 2 .9 2 2v4z"/>' },
        { value: 'course', label: '课程教学反馈', desc: '教学质量、课程建议', color: 'linear-gradient(135deg, #fa709a, #fee140)', svg: '<path d="M5 13.18v4L12 21l7-3.82v-4L12 17l-7-3.82zM12 3L1 9l11 6 9-4.91V17h2V9L12 3z"/>' },
        { value: 'safety', label: '校园安全隐患', desc: '安全隐患上报处理', color: 'linear-gradient(135deg, #ff0844, #ffb199)', svg: '<path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>' }
      ]
    }
  },
  mounted() {
    const userId = this.$cookies.get('userId')
    if (userId) {
      this.formData.userId = parseInt(userId)
    }
    this.loadFeedbackList()
  },
  methods: {
    toggleExpand(id) {
      this.expandedId = this.expandedId === id ? null : id
    },
    goToFeedback(type) {
      this.$router.push(`/feedback/${type}`)
    },
    loadFeedbackList() {
      if (!this.formData.userId) return
      this.loading = true
      this.$axios.get(`/feedback/list?userId=${this.formData.userId}`)
        .then(res => {
          if (res.data.code === 200) {
            // 过滤掉已完成的记录（status=3）
            this.feedbackList = (res.data.data || []).filter(item => item.status !== 3)
          }
        })
        .catch(err => {
          console.error('加载反馈列表失败:', err)
        })
        .finally(() => {
          this.loading = false
        })
    },
    getTypeName(type) {
      const item = this.feedbackTypes.find(t => t.value === type)
      return item ? item.label : type
    },
    getStatusName(status) {
      const statusMap = { 0: '待处理', 1: '处理中', 2: '已回复', 3: '已完成' }
      return statusMap[status] || '未知'
    },
    formatTime(time) {
      if (!time) return '-'
      const date = new Date(time)
      return date.getFullYear() + '年' + (date.getMonth() + 1) + '月' + date.getDate() + '日'
    },
    async markAsComplete(id) {
      try {
        await this.$axios.put(`/feedback/update?id=${id}&status=3`)
        // 从列表中移除该记录
        this.feedbackList = this.feedbackList.filter(f => f.id !== id)
        alert('已标记为完成')
      } catch (error) {
        console.error('标记完成失败:', error)
        alert('操作失败，请稍后重试')
      }
    }
  }
}
</script>

<style scoped>
.feedback-page {
  padding: 32px 24px;
  max-width: 900px;
  margin: 0 auto;
}

/* 返回按钮栏 */
.back-bar {
  margin-bottom: 20px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 8px 18px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 50px;
  font-size: 13px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3);
}

.back-btn:hover {
  transform: translateX(-3px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.45);
  opacity: 0.92;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.header-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.35);
}

.header-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1e1b4b;
  margin: 0 0 8px;
}

.header-subtitle {
  color: #6b7280;
  font-size: 15px;
  margin: 0;
}

/* 反馈类型网格 - 2列布局 */
.type-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
  margin-bottom: 32px;
}

.type-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border: 2px solid #f1f5f9;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.type-card:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
}

.type-card.active {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
}

.type-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 14px;
  flex-shrink: 0;
}

.type-content {
  flex: 1;
  min-width: 0;
}

.type-name {
  font-size: 14px;
  font-weight: 700;
  color: #1e1b4b;
  margin: 0 0 3px;
}

.type-desc {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.type-arrow {
  color: #cbd5e1;
  margin-left: 8px;
  transition: all 0.2s ease;
}

.type-card:hover .type-arrow {
  color: #667eea;
  transform: translateX(3px);
}

/* 反馈记录区域 */
.records-section {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #667eea;
}

.section-title-wrap h3 {
  font-size: 16px;
  font-weight: 700;
  color: #1e1b4b;
  margin: 0;
}

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  transform: scale(1.03);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
}

/* 加载和空状态 */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px;
}

.empty-state span {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 4px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 反馈卡片列表 */
.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feedback-card {
  background: #f8fafc;
  border: 1.5px solid #f1f5f9;
  border-radius: 14px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.feedback-card:hover {
  border-color: #667eea;
  background: white;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.type-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
}

.type-badge.ask { background: #e0e7ff; color: #4338ca; }
.type-badge.suggest { background: #fce7f3; color: #be185d; }
.type-badge.dining { background: #e0f2fe; color: #0284c7; }
.type-badge.dorm { background: #dcfce7; color: #15803d; }
.type-badge.course { background: #fef3c7; color: #b45309; }
.type-badge.safety { background: #fee2e2; color: #dc2626; }

.status-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
}

.status-badge.status-0 { background: #fef3c7; color: #b45309; }
.status-badge.status-1 { background: #dbeafe; color: #1d4ed8; }
.status-badge.status-2 { background: #e0f2fe; color: #0284c7; }
.status-badge.status-3 { background: #dcfce7; color: #15803d; }

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: #1e1b4b;
  margin: 0 0 6px;
}

.card-content {
  font-size: 13px;
  color: #64748b;
  margin: 0 0 8px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-info {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 10px;
}

.card-reply {
  background: white;
  border-radius: 10px;
  padding: 12px;
  margin: 12px 0;
  border-left: 3px solid #667eea;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #667eea;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
}

.card-reply p {
  font-size: 13px;
  color: #374151;
  margin: 0;
  line-height: 1.5;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid #f1f5f9;
}

.card-time {
  font-size: 12px;
  color: #94a3b8;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 10px;
  border: none;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn.complete {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}

.action-btn.complete:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.35);
}

.action-btn.expand {
  background: #e2e8f0;
  color: #64748b;
  padding: 5px 8px;
}

.action-btn.expand:hover {
  background: #667eea;
  color: white;
}

.action-btn.expand.rotated svg {
  transform: rotate(180deg);
}

/* 响应式 */
@media (max-width: 640px) {
  .feedback-page {
    padding: 20px 16px;
  }

  .type-grid {
    grid-template-columns: 1fr;
  }

  .records-section {
    padding: 16px;
  }

  .type-desc {
    white-space: normal;
  }
}
</style>
