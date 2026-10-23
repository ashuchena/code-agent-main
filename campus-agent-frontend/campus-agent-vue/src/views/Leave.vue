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
                <path d="M5 4a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1H5zm-.5 2.5A.5.5 0 0 1 5 6h6a.5.5 0 0 1 0 1H5a.5.5 0 0 1-.5-.5zM5 8a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1H5zm0 2a.5.5 0 0 0 0 1h3a.5.5 0 0 0 0-1H5z"/>
                <path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm10-1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1z"/>
              </svg>
            </div>
            <h2 class="header-title">请假申请</h2>
            <p class="header-subtitle">提交请假申请，实时跟踪审批进度</p>
          </div>
          
          <div class="content-card">
            <div class="card-header-custom">
              <div class="d-flex align-items-center">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16" class="me-2">
                  <path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"/>
                  <path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"/>
                </svg>
                我的请假
              </div>
              <span class="count-badge">{{ leaveList.length }} 条</span>
            </div>
            <div class="card-body-custom">
              <div class="list-header">
                <div class="tab-group">
                  <button class="tab-btn" :class="{ active: currentTab === 'all' }" @click="currentTab = 'all'">全部</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'pending' }" @click="currentTab = 'pending'">待审批</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'approved' }" @click="currentTab = 'approved'">已通过</button>
                </div>
                <button class="btn-add" @click="showAddModal = true">
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
                  </svg>
                  新建请假
                </button>
              </div>
              
              <div v-if="filteredList.length > 0" class="leave-list">
                <div v-for="item in filteredList" :key="item.id" class="leave-item">
                  <div class="leave-header">
                    <span class="leave-type" :class="item.leaveType">{{ item.leaveType }}</span>
                    <span class="leave-status" :class="'status-' + item.status">
                      {{ statusText[item.status] }}
                    </span>
                  </div>
                  <div class="leave-content">
                    <div class="leave-reason">{{ item.reason }}</div>
                    <div class="leave-info">
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M5 4a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1H5zm-.5 2.5A.5.5 0 0 1 5 6h6a.5.5 0 0 1 0 1H5a.5.5 0 0 1-.5-.5zM5 8a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1H5zm0 2a.5.5 0 0 0 0 1h3a.5.5 0 0 0 0-1H5z"/><path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm10-1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1z"/></svg> {{ formatDate(item.startDate) }} ~ {{ formatDate(item.endDate) }}</span>
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2zm.995-14.901a1 1 0 1 0-1.99 0A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-.2-6-.2-6a5.002 5.002 0 0 0-2.005-2.098zM8 5c0 1.098.5 6 2 7h8c1.5-1-.2-6-.2-6a5.002 5.002 0 0 0-2.005-2.098A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1 .2-6 .2-6a5.002 5.002 0 0 0-2.005-2.098A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7H6c1.5-1 2-5.902 2-7z"/></svg> {{ item.contactPhone }}</span>
                    </div>
                    <div v-if="item.approveRemark" class="leave-remark">
                      审批备注：{{ item.approveRemark }}
                    </div>
                  </div>
                  <div class="leave-time">申请时间：{{ formatTime(item.createTime) }}</div>
                </div>
              </div>
              
              <div v-else class="empty-state">
                <div class="empty-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M5 4a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1H5zm-.5 2.5A.5.5 0 0 1 5 6h6a.5.5 0 0 1 0 1H5a.5.5 0 0 1-.5-.5zM5 8a.5.5 0 0 0 0 1h6a.5.5 0 0 0 0-1H5zm0 2a.5.5 0 0 0 0 1h3a.5.5 0 0 0 0-1H5z"/>
                    <path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm10-1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1z"/>
                  </svg>
                </div>
                <div class="empty-text">暂无请假记录</div>
                <div class="empty-hint">点击右上角「新建请假」提交申请</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 新建请假弹窗 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>新建请假</h3>
          <button class="modal-close" @click="showAddModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>请假类型</label>
            <select v-model="newLeave.leaveType" class="form-input">
              <option value="事假">事假</option>
              <option value="病假">病假</option>
              <option value="公假">公假</option>
            </select>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>开始日期</label>
              <input v-model="newLeave.startDate" type="date" class="form-input">
            </div>
            <div class="form-group">
              <label>结束日期</label>
              <input v-model="newLeave.endDate" type="date" class="form-input">
            </div>
          </div>
          <div class="form-group">
            <label>请假原因</label>
            <textarea v-model="newLeave.reason" placeholder="请输入请假原因" class="form-input form-textarea"></textarea>
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input v-model="newLeave.contactPhone" type="tel" placeholder="请输入联系电话" class="form-input">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showAddModal = false">取消</button>
          <button class="btn-confirm" @click="submitLeave">提交申请</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Leave',
  data() {
    return {
      leaveList: [],
      currentTab: 'all',
      userId: this.$cookies.get('userId') || 1,
      showAddModal: false,
      newLeave: {
        leaveType: '事假',
        startDate: '',
        endDate: '',
        reason: '',
        contactPhone: ''
      },
      statusText: { 0: '待审批', 1: '已批准', 2: '已拒绝' }
    }
  },
  computed: {
    filteredList() {
      if (this.currentTab === 'all') return this.leaveList
      if (this.currentTab === 'pending') return this.leaveList.filter(i => i.status === 0)
      if (this.currentTab === 'approved') return this.leaveList.filter(i => i.status === 1)
      return this.leaveList
    }
  },
  mounted() {
    this.getLeaveList()
  },
  methods: {
    async getLeaveList() {
      try {
        const res = await this.$axios.get('/leave/list', { params: { userId: this.userId } })
        if (res.data.code === 200) {
          this.leaveList = res.data.data
        }
      } catch (err) {
        console.error(err)
      }
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString('zh-CN')
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.getFullYear() + '年' + (date.getMonth() + 1) + '月' + date.getDate() + '日'
    },
    async submitLeave() {
      if (!this.newLeave.reason) { alert('请输入请假原因'); return }
      if (!this.newLeave.startDate || !this.newLeave.endDate) { alert('请选择日期'); return }
      try {
        const res = await this.$axios.post('/leave/add', {
          userId: this.userId,
          ...this.newLeave
        })
        if (res.data.code === 200) {
          this.showAddModal = false
          this.newLeave = { leaveType: '事假', startDate: '', endDate: '', reason: '', contactPhone: '' }
          this.getLeaveList()
          alert('提交成功！')
        } else {
          alert('提交失败：' + (res.data.msg || '未知错误'))
        }
      } catch (err) {
        alert('提交失败')
      }
    }
  }
}
</script>

<style scoped>
.page-container { min-height: 100vh; position: relative; overflow: hidden; background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 50%, #f0f2f5 100%); }
.bg-decoration { position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: 0; }
.circle { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.5; animation: float 20s ease-in-out infinite; }
.circle-1 { width: 350px; height: 350px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); top: -80px; left: -80px; }
.circle-2 { width: 300px; height: 300px; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); bottom: -60px; right: -60px; animation-delay: -5s; }
@keyframes float { 0%, 100% { transform: translate(0, 0) scale(1); } 50% { transform: translate(20px, -20px) scale(1.05); } }
.main-content { position: relative; z-index: 1; padding: 30px 20px 50px; }
.page-header { animation: slideUp 0.6s ease-out; }
.header-icon { width: 64px; height: 64px; background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%); border-radius: 18px; display: flex; align-items: center; justify-content: center; color: white; margin: 0 auto 16px; box-shadow: 0 8px 24px rgba(245, 158, 11, 0.3); }
.header-title { font-size: 1.75rem; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.header-subtitle { color: #6b7280; font-size: 15px; }
.content-card { background: white; border-radius: 20px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08); overflow: hidden; animation: slideUp 0.6s ease-out 0.1s both; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.card-header-custom { display: flex; align-items: center; justify-content: space-between; padding: 18px 24px; background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%); color: white; font-weight: 600; font-size: 16px; }
.count-badge { background: rgba(255, 255, 255, 0.2); padding: 4px 12px; border-radius: 20px; font-size: 13px; font-weight: 500; }
.card-body-custom { padding: 20px; }
.list-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #eee; gap: 12px; flex-wrap: wrap; }
.tab-group { display: flex; gap: 8px; }
.tab-btn { padding: 6px 14px; background: #f5f5f5; border: none; border-radius: 20px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.25s ease; color: #666; }
.tab-btn.active { background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%); color: white; }
.btn-add { display: inline-flex; align-items: center; gap: 5px; padding: 8px 16px; background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%); color: white; border: none; border-radius: 20px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 8px rgba(245, 158, 11, 0.25); }
.btn-add:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(245, 158, 11, 0.35); }
.leave-list { display: flex; flex-direction: column; gap: 12px; }
.leave-item { padding: 16px 20px; background: #f8f9fa; border-radius: 12px; border: 1px solid #eee; transition: all 0.3s ease; }
.leave-item:hover { border-color: #f59e0b; transform: translateX(4px); }
.leave-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.leave-type { padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; color: white; }
.leave-type.事假 { background: linear-gradient(135deg, #3b82f6, #2563eb); }
.leave-type.病假 { background: linear-gradient(135deg, #ef4444, #dc2626); }
.leave-type.公假 { background: linear-gradient(135deg, #10b981, #059669); }
.leave-status { font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 12px; }
.status-0 { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
.status-1 { background: rgba(16, 185, 129, 0.15); color: #10b981; }
.status-2 { background: rgba(239, 68, 68, 0.15); color: #ef4444; }
.leave-content { margin-bottom: 8px; }
.leave-reason { font-size: 15px; font-weight: 500; color: #1a1a2e; margin-bottom: 8px; }
.leave-info { display: flex; gap: 16px; font-size: 13px; color: #6b7280; }
.leave-info span { display: flex; align-items: center; gap: 4px; }
.leave-remark { margin-top: 8px; padding: 8px 12px; background: rgba(245, 158, 11, 0.1); border-radius: 8px; font-size: 13px; color: #92400e; }
.leave-time { font-size: 12px; color: #9ca3af; }
.empty-state { text-align: center; padding: 48px 20px; }
.empty-icon { width: 80px; height: 80px; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #adb5bd; margin: 0 auto 20px; }
.empty-text { font-size: 18px; font-weight: 600; color: #495057; margin-bottom: 8px; }
.empty-hint { color: #6b7280; font-size: 14px; }
.back-btn { display: inline-flex; align-items: center; gap: 5px; padding: 7px 14px; background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%); border: none; border-radius: 20px; font-size: 13px; font-weight: 500; color: white; cursor: pointer; transition: all 0.25s ease; margin-bottom: 16px; box-shadow: 0 3px 12px rgba(245, 158, 11, 0.3); }
.back-btn:hover { transform: translateX(-3px) scale(1.03); }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; animation: fadeIn 0.2s ease; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.modal-content { background: white; border-radius: 16px; width: 90%; max-width: 420px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2); animation: slideUp 0.3s ease; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px; border-bottom: 1px solid #eee; }
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 600; color: #1a1a2e; }
.modal-close { width: 32px; height: 32px; background: #f5f5f5; border: none; border-radius: 50%; font-size: 20px; color: #666; cursor: pointer; transition: all 0.25s ease; }
.modal-close:hover { background: #e0e0e0; }
.modal-body { padding: 24px; }
.form-group { margin-bottom: 16px; }
.form-group:last-child { margin-bottom: 0; }
.form-group label { display: block; margin-bottom: 6px; font-size: 14px; font-weight: 500; color: #333; }
.form-input { width: 100%; padding: 12px 14px; border: 1px solid #e0e0e0; border-radius: 10px; font-size: 14px; transition: all 0.25s ease; box-sizing: border-box; }
.form-input:focus { outline: none; border-color: #f59e0b; box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.15); }
.form-textarea { min-height: 80px; resize: vertical; }
.form-row { display: flex; gap: 12px; }
.form-row .form-group { flex: 1; }
.modal-footer { display: flex; gap: 12px; padding: 16px 24px 24px; }
.btn-cancel, .btn-confirm { flex: 1; padding: 12px; border: none; border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.25s ease; }
.btn-cancel { background: #f5f5f5; color: #666; }
.btn-cancel:hover { background: #e0e0e0; }
.btn-confirm { background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%); color: white; }
.btn-confirm:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(245, 158, 11, 0.35); }
</style>
