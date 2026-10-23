<template>
  <div class="page-container">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>

    <div class="main-content">
      <div class="row justify-content-center">
        <div class="col-lg-8 col-xl-7">
          <button class="back-btn" @click="$router.push('/schedule')">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            返回
          </button>

          <div class="page-header text-center mb-4">
            <div class="header-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 16 16">
                <path d="M8.95 8.367c.15-.322.305-.66.457-.997C10.102 6.207 10.5 5.13 10.5 4c0-.595-.182-1.157-.5-1.657C9.85 2.085 9.475 2 9 2c-.45 0-.826.075-1.125.217C7.425 2.545 6.5 3.65 6.5 5c0 .655-.197 1.255-.55 1.74l-.015.025C5.8 6.89 5.5 7.49 5.5 8c0 .675.18 1.31.5 1.855.35.6.81 1.09 1.35 1.485l.15.065V16h-.1l-.35-.01-.35.01H7v-.15c.36-.285.75-.63 1.1-.95zm.95.633c-.3-.39-.65-.77-1.05-1.12.45-.15.9-.27 1.35-.36V8.87c-.45-.03-.9-.02-1.35.13zm-.35 1.32c-.75.53-1.45 1.05-2.1 1.6v.37c.6-.3 1.2-.57 1.8-.81.45-.18.9-.31 1.35-.39l.15-.01.15.01c.45.05.9.12 1.35.21-.15.35-.3.69-.45 1.02h-.15l-.15-.01c-.3-.01-.6-.01-.9.01z"/>
                <path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm0-1A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0-1A6 6 0 1 0 8 1a6 6 0 0 0 0 12zm5.5-4a.5.5 0 0 0 0-1H9.5a.5.5 0 0 0 0 1h4z"/>
              </svg>
            </div>
            <h2 class="header-title">志愿者活动</h2>
            <p class="header-subtitle">参与志愿活动，传递爱心与温暖</p>
          </div>

          <div class="content-card">
            <div class="card-header-custom">
              <div class="d-flex align-items-center">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16" class="me-2">
                  <path d="M11 6.5a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1zm-3 0a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1zm-5 3a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1zm3 0a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1z"/>
                  <path d="M3.5 0a.5.5 0 0 1 .5.5V1h8V.5a.5.5 0 0 1 1 0V1h1a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h1V.5a.5.5 0 0 1 .5-.5z"/>
                </svg>
                活动列表
              </div>
              <span class="count-badge">{{ filteredList.length }} 个</span>
            </div>
            <div class="card-body-custom">
              <div class="list-header">
                <div class="tab-group">
                  <button class="tab-btn" :class="{ active: currentTab === 'available' }" @click="switchTab('available')">可报名</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'my' }" @click="switchTab('my')">我的报名</button>
                </div>
                <button class="btn-add" @click="showAddModal = true">
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
                  </svg>
                  发起活动
                </button>
              </div>

              <div v-if="filteredList.length > 0" class="activity-list">
                <div v-for="item in filteredList" :key="item.id" class="activity-item">
                  <div class="activity-header">
                    <span class="activity-status" :class="'status-' + normalizedStatus(item.status)">
                      {{ statusText[normalizedStatus(item.status)] }}
                    </span>
                    <span class="activity-hours">{{ item.hours }}h</span>
                  </div>
                  <div class="activity-content">
                    <div class="activity-title">{{ item.title }}</div>
                    <div class="activity-desc">{{ item.description }}</div>
                    <div class="activity-info">
                      <span>
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z"/></svg>
                        {{ item.location }}
                      </span>
                      <span>
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"/><path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"/></svg>
                        {{ formatDate(item.startDate) }} {{ item.startTime }}
                      </span>
                    </div>
                    <div class="activity-meta">
                      <span class="participants">
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10c-2.29 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10z"/></svg>
                        {{ item.currentParticipants || 0 }}/{{ item.maxParticipants }}
                      </span>
                      <span class="reward">志愿时长：{{ item.hours }} 小时</span>
                    </div>
                  </div>
                  <div class="activity-footer">
                    <span class="activity-time">发布于 {{ formatTime(item.createTime) }}</span>
                    <div class="activity-actions">
                      <button
                        v-if="currentTab === 'available' && normalizedStatus(item.status) === 0"
                        class="btn-signup"
                        @click="signUp(item)"
                        :disabled="Number(item.currentParticipants || 0) >= Number(item.maxParticipants || 0)"
                      >
                        {{ Number(item.currentParticipants || 0) >= Number(item.maxParticipants || 0) ? '已满' : '立即报名' }}
                      </button>
                      <button v-if="currentTab === 'my'" class="btn-cancel-signup" @click="cancelSignUp(item)">取消报名</button>
                    </div>
                  </div>
                </div>
              </div>

              <div v-else class="empty-state">
                <div class="empty-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M11 6.5a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1zm-3 0a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1zm-5 3a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1zm3 0a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1z"/>
                  </svg>
                </div>
                <div class="empty-text">暂无活动</div>
                <div class="empty-hint">点击右上角“发起活动”创建新活动</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>发起活动</h3>
          <button class="modal-close" @click="showAddModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>活动标题</label>
            <input v-model.trim="newActivity.title" type="text" placeholder="请输入活动标题" class="form-input">
          </div>
          <div class="form-group">
            <label>活动描述</label>
            <textarea v-model.trim="newActivity.description" placeholder="请输入活动描述" class="form-input form-textarea"></textarea>
          </div>
          <div class="form-group">
            <label>活动地点</label>
            <input v-model.trim="newActivity.location" type="text" placeholder="活动地点" class="form-input">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>活动日期</label>
              <input v-model="newActivity.startDate" type="date" class="form-input">
            </div>
            <div class="form-group">
              <label>志愿时长(h)</label>
              <input v-model.number="newActivity.hours" type="number" min="0.5" step="0.5" placeholder="如：3" class="form-input">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>开始时间</label>
              <input v-model="newActivity.startTime" type="time" class="form-input">
            </div>
            <div class="form-group">
              <label>结束时间</label>
              <input v-model="newActivity.endTime" type="time" class="form-input">
            </div>
          </div>
          <div class="form-group">
            <label>招募人数</label>
            <input v-model.number="newActivity.maxParticipants" type="number" min="1" placeholder="招募人数" class="form-input">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showAddModal = false">取消</button>
          <button class="btn-confirm" @click="submitActivity">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const createDefaultActivity = () => ({
  title: '',
  description: '',
  location: '',
  startDate: '',
  startTime: '09:00',
  endTime: '12:00',
  maxParticipants: 20,
  hours: 3
})

export default {
  name: 'Volunteer',
  data() {
    return {
      activityList: [],
      mySignUps: [],
      currentTab: 'available',
      userId: Number(this.$cookies.get('userId') || 1),
      username: this.$cookies.get('username') || '用户',
      showAddModal: false,
      newActivity: createDefaultActivity(),
      statusText: {
        0: '报名中',
        1: '进行中',
        2: '已结束'
      }
    }
  },
  computed: {
    filteredList() {
      return this.activityList
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    switchTab(tab) {
      if (this.currentTab !== tab) {
        this.currentTab = tab
      }
    },
    normalizedStatus(status) {
      const value = Number(status)
      return Number.isNaN(value) ? 0 : value
    },
    async loadData() {
      try {
        if (this.currentTab === 'available') {
          const res = await this.$axios.get('/volunteer/available')
          this.activityList = res.data.code === 200 ? (res.data.data || []) : []
          return
        }

        const signUpRes = await this.$axios.get('/volunteer/my', { params: { userId: this.userId } })
        if (signUpRes.data.code !== 200) {
          this.mySignUps = []
          this.activityList = []
          return
        }

        this.mySignUps = signUpRes.data.data || []
        if (this.mySignUps.length === 0) {
          this.activityList = []
          return
        }

        const detailRes = await this.$axios.get('/volunteer/list')
        if (detailRes.data.code !== 200) {
          this.activityList = []
          return
        }

        const myIds = this.mySignUps.map(item => item.activityId)
        this.activityList = (detailRes.data.data || []).filter(item => myIds.includes(item.id))
      } catch (error) {
        this.activityList = []
      }
    },
    formatDate(date) {
      if (!date) return ''
      const parsed = new Date(date)
      return Number.isNaN(parsed.getTime()) ? String(date) : parsed.toLocaleDateString('zh-CN')
    },
    formatTime(time) {
      if (!time) return ''
      const parsed = new Date(time)
      if (Number.isNaN(parsed.getTime())) {
        return String(time)
      }
      return `${parsed.getFullYear()}年${parsed.getMonth() + 1}月${parsed.getDate()}日`
    },
    validateActivity() {
      const { title, description, location, startDate, startTime, endTime, maxParticipants, hours } = this.newActivity
      if (!title || !description || !location) {
        return '请完整填写活动标题、描述和地点'
      }
      if (!startDate) {
        return '请选择活动日期'
      }
      if (!startTime || !endTime) {
        return '请选择完整的开始和结束时间'
      }
      if (startTime >= endTime) {
        return '结束时间必须晚于开始时间'
      }
      if (!Number(maxParticipants) || Number(maxParticipants) <= 0) {
        return '招募人数必须大于 0'
      }
      if (!Number(hours) || Number(hours) <= 0) {
        return '志愿时长必须大于 0'
      }
      return ''
    },
    async submitActivity() {
      const validationMessage = this.validateActivity()
      if (validationMessage) {
        alert(validationMessage)
        return
      }

      try {
        const payload = {
          ...this.newActivity,
          publisherId: this.userId,
          maxParticipants: Number(this.newActivity.maxParticipants),
          hours: Number(this.newActivity.hours)
        }
        const res = await this.$axios.post('/volunteer/add', payload)
        if (res.data.code === 200) {
          this.showAddModal = false
          this.newActivity = createDefaultActivity()
          this.loadData()
          alert('发布成功，等待管理员审核')
          return
        }
        alert(res.data.msg || '发布失败')
      } catch (error) {
        const message = error.response?.data?.msg || error.response?.data?.message || '发布失败'
        alert(message)
      }
    },
    async signUp(item) {
      try {
        const res = await this.$axios.post('/volunteer/signup', null, {
          params: { activityId: item.id, userId: this.userId, userName: this.username }
        })
        if (res.data.code === 200) {
          alert('报名成功')
          this.loadData()
        } else {
          alert(res.data.msg || '报名失败')
        }
      } catch (error) {
        const message = error.response?.data?.msg || '报名失败'
        alert(message)
      }
    },
    async cancelSignUp(item) {
      try {
        const res = await this.$axios.delete('/volunteer/cancel', {
          params: { activityId: item.id, userId: this.userId }
        })
        if (res.data.code === 200) {
          alert('已取消报名')
          this.loadData()
        } else {
          alert(res.data.msg || '取消失败')
        }
      } catch (error) {
        const message = error.response?.data?.msg || '取消失败'
        alert(message)
      }
    }
  },
  watch: {
    currentTab() {
      this.loadData()
    }
  }
}
</script>

<style scoped>
.page-container { min-height: 100vh; position: relative; overflow: hidden; background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 50%, #f0f2f5 100%); }
.bg-decoration { position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: 0; }
.circle { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.5; animation: float 20s ease-in-out infinite; }
.circle-1 { width: 350px; height: 350px; background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); top: -80px; left: -80px; }
.circle-2 { width: 300px; height: 300px; background: linear-gradient(135deg, #22d3ee 0%, #06b6d4 100%); bottom: -60px; right: -60px; animation-delay: -5s; }
@keyframes float { 0%, 100% { transform: translate(0, 0) scale(1); } 50% { transform: translate(20px, -20px) scale(1.05); } }
.main-content { position: relative; z-index: 1; padding: 30px 20px 50px; }
.page-header { animation: slideUp 0.6s ease-out; }
.header-icon { width: 64px; height: 64px; background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); border-radius: 18px; display: flex; align-items: center; justify-content: center; color: white; margin: 0 auto 16px; box-shadow: 0 8px 24px rgba(6, 182, 212, 0.3); }
.header-title { font-size: 1.75rem; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.header-subtitle { color: #6b7280; font-size: 15px; }
.content-card { background: white; border-radius: 20px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08); overflow: hidden; animation: slideUp 0.6s ease-out 0.1s both; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.card-header-custom { display: flex; align-items: center; justify-content: space-between; padding: 18px 24px; background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); color: white; font-weight: 600; font-size: 16px; }
.count-badge { background: rgba(255, 255, 255, 0.2); padding: 4px 12px; border-radius: 20px; font-size: 13px; font-weight: 500; }
.card-body-custom { padding: 20px; }
.list-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #eee; gap: 12px; flex-wrap: wrap; }
.tab-group { display: flex; gap: 8px; }
.tab-btn { padding: 6px 14px; background: #f5f5f5; border: none; border-radius: 20px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.25s ease; color: #666; }
.tab-btn.active { background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); color: white; }
.btn-add { display: inline-flex; align-items: center; gap: 5px; padding: 8px 16px; background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); color: white; border: none; border-radius: 20px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 8px rgba(6, 182, 212, 0.25); }
.btn-add:hover { transform: translateY(-1px); }
.activity-list { display: flex; flex-direction: column; gap: 12px; }
.activity-item { padding: 16px 20px; background: #f8f9fa; border-radius: 12px; border: 1px solid #eee; transition: all 0.3s ease; }
.activity-item:hover { border-color: #06b6d4; transform: translateX(4px); }
.activity-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.activity-status { font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 12px; }
.status-0 { background: rgba(6, 182, 212, 0.15); color: #06b6d4; }
.status-1 { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
.status-2 { background: rgba(107, 114, 128, 0.15); color: #6b7280; }
.activity-hours { font-size: 18px; font-weight: 700; color: #06b6d4; }
.activity-title { font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 6px; }
.activity-desc { font-size: 14px; color: #4b5563; margin-bottom: 8px; line-height: 1.5; }
.activity-info { display: flex; gap: 16px; font-size: 13px; color: #6b7280; margin-bottom: 8px; flex-wrap: wrap; }
.activity-info span { display: flex; align-items: center; gap: 4px; }
.activity-meta { display: flex; gap: 16px; font-size: 13px; flex-wrap: wrap; }
.participants { display: flex; align-items: center; gap: 4px; color: #06b6d4; font-weight: 500; }
.reward { color: #10b981; font-weight: 500; }
.activity-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; padding-top: 10px; border-top: 1px dashed #e5e7eb; gap: 10px; flex-wrap: wrap; }
.activity-time { font-size: 12px; color: #9ca3af; }
.activity-actions { display: flex; gap: 8px; }
.btn-signup { padding: 6px 16px; background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); color: white; border: none; border-radius: 12px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.25s ease; }
.btn-signup:hover:not(:disabled) { transform: scale(1.05); }
.btn-signup:disabled { background: #e5e7eb; color: #9ca3af; cursor: not-allowed; }
.btn-cancel-signup { padding: 6px 16px; background: rgba(239, 68, 68, 0.12); color: #ef4444; border: none; border-radius: 12px; font-size: 13px; font-weight: 500; cursor: pointer; }
.btn-cancel-signup:hover { background: #ef4444; color: white; }
.empty-state { text-align: center; padding: 48px 20px; }
.empty-icon { width: 80px; height: 80px; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #adb5bd; margin: 0 auto 20px; }
.empty-text { font-size: 18px; font-weight: 600; color: #495057; margin-bottom: 8px; }
.empty-hint { color: #6b7280; font-size: 14px; }
.back-btn { display: inline-flex; align-items: center; gap: 5px; padding: 7px 14px; background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); border: none; border-radius: 20px; font-size: 13px; font-weight: 500; color: white; cursor: pointer; transition: all 0.25s ease; margin-bottom: 16px; box-shadow: 0 3px 12px rgba(6, 182, 212, 0.3); }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; animation: fadeIn 0.2s ease; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.modal-content { background: white; border-radius: 16px; width: 90%; max-width: 420px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2); animation: slideUp 0.3s ease; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px; border-bottom: 1px solid #eee; }
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 600; color: #1a1a2e; }
.modal-close { width: 32px; height: 32px; background: #f5f5f5; border: none; border-radius: 50%; font-size: 20px; color: #666; cursor: pointer; }
.modal-body { padding: 24px; }
.form-group { margin-bottom: 14px; }
.form-group:last-child { margin-bottom: 0; }
.form-group label { display: block; margin-bottom: 6px; font-size: 14px; font-weight: 500; color: #333; }
.form-input { width: 100%; padding: 12px 14px; border: 1px solid #e0e0e0; border-radius: 10px; font-size: 14px; transition: all 0.25s ease; box-sizing: border-box; }
.form-input:focus { outline: none; border-color: #06b6d4; box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.15); }
.form-textarea { min-height: 80px; resize: vertical; }
.form-row { display: flex; gap: 12px; }
.form-row .form-group { flex: 1; }
.modal-footer { display: flex; gap: 12px; padding: 16px 24px 24px; }
.btn-cancel, .btn-confirm { flex: 1; padding: 12px; border: none; border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.25s ease; }
.btn-cancel { background: #f5f5f5; color: #666; }
.btn-confirm { background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%); color: white; }
.btn-confirm:hover { transform: translateY(-2px); }

@media (max-width: 576px) {
  .main-content { padding: 20px 14px 36px; }
  .card-header-custom,
  .card-body-custom,
  .modal-header,
  .modal-body,
  .modal-footer { padding-left: 16px; padding-right: 16px; }
  .form-row { flex-direction: column; gap: 0; }
}
</style>
