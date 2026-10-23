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
          <button class="back-btn" @click="$router.push('/feedback')">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            返回
          </button>

          <!-- 页面标题 -->
          <div class="page-header text-center mb-4">
            <div class="header-icon" :style="{ background: currentType.color }">
              <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24" v-html="currentType.svg"></svg>
            </div>
            <h2 class="header-title">{{ currentType.label }}</h2>
            <p class="header-subtitle">{{ currentType.desc }}</p>
          </div>

          <!-- 反馈表单 -->
          <div class="form-section">
            <h3 class="section-title">📝 填写反馈内容</h3>
            
            <!-- 向老师提问 -->
            <template v-if="type === 'ask'">
              <div class="form-group">
                <label>提问标题 <span class="required">*</span></label>
                <input v-model="formData.title" type="text" placeholder="简要描述你的问题" />
              </div>
              <div class="form-group">
                <label>目标老师（可选）</label>
                <input v-model="formData.targetTeacher" type="text" placeholder="如：张老师" />
              </div>
              <div class="form-group">
                <label>问题详情 <span class="required">*</span></label>
                <textarea v-model="formData.content" placeholder="请详细描述你的问题..." rows="5"></textarea>
              </div>
            </template>

            <!-- 投诉与建议 -->
            <template v-else-if="type === 'suggest'">
              <div class="form-group">
                <label>建议标题 <span class="required">*</span></label>
                <input v-model="formData.title" type="text" placeholder="简要描述您的建议" />
              </div>
              <div class="form-group">
                <label>建议类型 <span class="required">*</span></label>
                <div class="urgency-grid">
                  <div 
                    v-for="level in urgencyLevels" 
                    :key="level.value"
                    class="urgency-item"
                    :class="{ active: formData.urgency === level.value }"
                    @click="formData.urgency = level.value"
                  >
                    {{ level.label }}
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label>建议内容 <span class="required">*</span></label>
                <textarea v-model="formData.content" placeholder="请详细描述您的建议或投诉..." rows="5"></textarea>
              </div>
            </template>

            <!-- 食堂餐饮反馈 -->
            <template v-else-if="type === 'dining'">
              <div class="form-group">
                <label>反馈标题 <span class="required">*</span></label>
                <input v-model="formData.title" type="text" placeholder="简要描述问题" />
              </div>
              <div class="form-group">
                <label>问题地点 <span class="required">*</span></label>
                <input v-model="formData.location" type="text" placeholder="如：第一食堂三楼" />
              </div>
              <div class="form-group">
                <label>问题详情 <span class="required">*</span></label>
                <textarea v-model="formData.content" placeholder="请详细描述食堂的问题..." rows="5"></textarea>
              </div>
            </template>

            <!-- 宿舍管理反馈 -->
            <template v-else-if="type === 'dorm'">
              <div class="form-group">
                <label>反馈标题 <span class="required">*</span></label>
                <input v-model="formData.title" type="text" placeholder="简要描述问题" />
              </div>
              <div class="form-group">
                <label>宿舍位置 <span class="required">*</span></label>
                <input v-model="formData.location" type="text" placeholder="如：3号楼201室" />
              </div>
              <div class="form-group">
                <label>问题详情 <span class="required">*</span></label>
                <textarea v-model="formData.content" placeholder="请详细描述宿舍的问题..." rows="5"></textarea>
              </div>
            </template>

            <!-- 课程教学反馈 -->
            <template v-else-if="type === 'course'">
              <div class="form-group">
                <label>反馈标题 <span class="required">*</span></label>
                <input v-model="formData.title" type="text" placeholder="简要描述问题" />
              </div>
              <div class="form-group">
                <label>课程名称 <span class="required">*</span></label>
                <input v-model="formData.courseName" type="text" placeholder="如：高等数学" />
              </div>
              <div class="form-group">
                <label>问题详情 <span class="required">*</span></label>
                <textarea v-model="formData.content" placeholder="请详细描述课程或教学的问题..." rows="5"></textarea>
              </div>
            </template>

            <!-- 校园安全隐患 -->
            <template v-else-if="type === 'safety'">
              <div class="form-group">
                <label>隐患标题 <span class="required">*</span></label>
                <input v-model="formData.title" type="text" placeholder="简要描述安全隐患" />
              </div>
              <div class="form-group">
                <label>隐患地点 <span class="required">*</span></label>
                <input v-model="formData.location" type="text" placeholder="如：图书馆门口" />
              </div>
              <div class="form-group">
                <label>紧急程度</label>
                <div class="urgency-grid">
                  <div 
                    v-for="level in urgencyLevels" 
                    :key="level.value"
                    class="urgency-item"
                    :class="{ active: formData.urgency === level.value }"
                    @click="formData.urgency = level.value"
                  >
                    {{ level.label }}
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label>隐患详情 <span class="required">*</span></label>
                <textarea v-model="formData.content" placeholder="请详细描述发现的安全隐患..." rows="5"></textarea>
              </div>
            </template>

            <!-- 通用字段 -->
            <div class="form-group">
              <label>联系电话</label>
              <input v-model="formData.userPhone" type="tel" placeholder="方便我们联系您（选填）" />
            </div>

            <button class="submit-btn" @click="submitFeedback">提交反馈</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script>
export default {
  name: 'FeedbackForm',
  data() {
    return {
      type: '',
      formData: {
        userId: null,
        userName: '',
        userPhone: '',
        type: '',
        title: '',
        content: '',
        targetTeacher: '',
        courseName: '',
        location: '',
        urgency: 0
      },
      showToast: false,
      toastMessage: '',
      feedbackTypes: [
        { value: 'ask', label: '向老师提问', desc: '向老师请教问题、课程咨询', color: 'linear-gradient(135deg, #3b82f6, #1d4ed8)', svg: '<path d="M12 14l9-5-9-5-9 5 9 5zm0 0l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14zm-4 6v-7.5l4-2.222"/>' },
        { value: 'suggest', label: '投诉与建议', desc: '提出意见或投诉反馈', color: 'linear-gradient(135deg, #f59e0b, #d97706)', svg: '<path d="M9 21.75l2.25-6.75-5.25-2.25 10.5-1.5-2.25-6.75 6 4.5-2.25 6.75 5.25 2.25-10.5 1.5 2.25 6.75-6-4.5z"/>' },
        { value: 'dining', label: '食堂餐饮反馈', desc: '食堂卫生、服务投诉', color: 'linear-gradient(135deg, #10b981, #059669)', svg: '<path d="M11 9H9V2H7v7H5V2H3v7c0 2.12 1.66 3.84 3.75 3.97V22h2.5v-9.03C11.34 12.84 13 11.12 13 9V2h-2v7zm5-3v8h2.5v8H21V2c-2.76 0-5 2.24-5 4z"/>' },
        { value: 'dorm', label: '宿舍管理反馈', desc: '宿舍设施、室友问题', color: 'linear-gradient(135deg, #8b5cf6, #7c3aed)', svg: '<path d="M7 14c1.66 0 3-1.34 3-3S8.66 8 7 8s-3 1.34-3 3 1.34 3 3 3zm0-4c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm12-3h-8v8H3V5H1v15h2v-3h18v3h2v-9c0-2.21-1.79-4-4-4zm2 8h-8V9h6c1.1 0 2 .9 2 2v4z"/>' },
        { value: 'course', label: '课程教学反馈', desc: '教学质量、课程建议', color: 'linear-gradient(135deg, #ec4899, #db2777)', svg: '<path d="M5 13.18v4L12 21l7-3.82v-4L12 17l-7-3.82zM12 3L1 9l11 6 9-4.91V17h2V9L12 3z"/>' },
        { value: 'safety', label: '校园安全隐患', desc: '安全隐患上报处理', color: 'linear-gradient(135deg, #ef4444, #dc2626)', svg: '<path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>' }
      ],
      urgencyLevels: [
        { value: 0, label: '普通' },
        { value: 1, label: '紧急' },
        { value: 2, label: '非常紧急' }
      ]
    }
  },
  computed: {
    currentType() {
      return this.feedbackTypes.find(t => t.value === this.type) || this.feedbackTypes[0]
    }
  },
  mounted() {
    this.type = this.$route.params.type || 'ask'
    const userId = this.$cookies.get('userId')
    if (userId) {
      this.formData.userId = parseInt(userId)
    }
  },
  methods: {
    showToastMessage(msg) {
      this.toastMessage = msg
      this.showToast = true
      setTimeout(() => { this.showToast = false }, 2000)
    },
    async submitFeedback() {
      const userId = this.$cookies.get('userId')
      if (!userId) {
        this.showToastMessage('请先登录')
        return
      }
      this.formData.userId = parseInt(userId)
      this.formData.type = this.type
      
      if (!this.formData.title && this.type !== 'safety') {
        this.showToastMessage('请填写标题')
        return
      }
      if (this.type === 'safety' && !this.formData.title) {
        this.showToastMessage('请填写隐患标题')
        return
      }
      if (!this.formData.content) {
        this.showToastMessage('请填写反馈内容')
        return
      }
      
      try {
        const res = await this.$axios.post('/feedback/add', this.formData)
        if (res.data.code === 200) {
          this.showToastMessage('反馈已提交，我们会尽快处理')
          setTimeout(() => {
            this.$router.push('/feedback')
          }, 1500)
        } else {
          this.showToastMessage(res.data.message || '提交失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        this.showToastMessage('提交失败，请检查网络')
      }
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
  background: linear-gradient(135deg, #f59e0b 0%, #ef4444 100%);
  top: -80px; right: -80px;
}

.circle-2 {
  width: 300px; height: 300px;
  background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%);
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
  background: linear-gradient(135deg, #f59e0b 0%, #ef4444 100%);
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: white;
  cursor: pointer;
  transition: all 0.25s ease;
  margin-bottom: 16px;
  box-shadow: 0 3px 12px rgba(245, 158, 11, 0.3);
}

.back-btn:hover {
  transform: translateX(-3px) scale(1.03);
  box-shadow: 0 6px 18px rgba(245, 158, 11, 0.4);
}

.page-header {
  animation: slideUp 0.6s ease-out;
}

.header-icon {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
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

/* 表单区域 */
.form-section {
  background: white;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 20px;
  animation: slideUp 0.6s ease-out 0.1s both;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 16px;
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
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  background: white;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #b0b8c4;
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

/* 紧急程度 */
.urgency-grid {
  display: flex;
  gap: 10px;
}

.urgency-item {
  flex: 1;
  padding: 10px 16px;
  background: #f8fafc;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  text-align: center;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: all 0.25s ease;
}

.urgency-item:hover {
  border-color: #3b82f6;
}

.urgency-item.active {
  border-color: #ef4444;
  background: #fef2f2;
  color: #ef4444;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #f59e0b 0%, #ef4444 100%);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.35);
  margin-top: 8px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(245, 158, 11, 0.45);
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
  .urgency-grid { flex-direction: column; }
}
</style>