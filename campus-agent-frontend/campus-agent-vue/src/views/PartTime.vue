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
                <path d="M0 12.5A1.5 1.5 0 0 0 1.5 14h13a1.5 1.5 0 0 0 1.5-1.5v-7h1v7a2.5 2.5 0 0 1-2.5 2.5h-13A2.5 2.5 0 0 1 0 12.5v-7h1v7z"/>
                <path d="M4.5 9.5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7zm0 2.5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7zm0 2.5a.5.5 0 0 0 0 1h3a.5.5 0 0 0 0-1h-3z"/>
              </svg>
            </div>
            <h2 class="header-title">勤工俭学</h2>
            <p class="header-subtitle">校内兼职信息，积累社会经验</p>
          </div>
          
          <div class="content-card">
            <div class="card-header-custom">
              <div class="d-flex align-items-center">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16" class="me-2">
                  <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
                </svg>
                兼职信息
              </div>
              <span class="count-badge">{{ jobList.length }} 条</span>
            </div>
            <div class="card-body-custom">
              <div class="list-header">
                <div class="tab-group">
                  <button class="tab-btn" :class="{ active: currentTab === 'all' }" @click="currentTab = 'all'">全部</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'campus' }" @click="currentTab = 'campus'">校内</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'outside' }" @click="currentTab = 'outside'">校外</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'my' }" @click="currentTab = 'my'">我的发布</button>
                </div>
                <button class="btn-add" @click="showAddModal = true">
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
                  </svg>
                  发布兼职
                </button>
              </div>
              
              <div v-if="filteredList.length > 0" class="job-list">
                <div v-for="item in filteredList" :key="item.id" class="job-item">
                  <div class="job-header">
                    <span class="job-type" :class="item.workType">{{ item.workType === '校内' ? '校内' : '校外' }}</span>
                    <span class="job-status" :class="item.status === 0 ? 'recruiting' : 'closed'">
                      {{ item.status === 0 ? '招聘中' : '已结束' }}
                    </span>
                  </div>
                  <div class="job-content">
                    <div class="job-title">{{ item.title }}</div>
                    <div class="job-desc">{{ item.description }}</div>
                    <div class="job-info">
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z"/></svg> {{ item.location }}</span>
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"/><path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"/></svg> {{ item.workTime }}</span>
                    </div>
                    <div class="job-salary">
                      <span class="salary-label">薪资</span>
                      <span class="salary-value">¥{{ item.salary }}/小时</span>
                    </div>
                    <div v-if="item.requirement" class="job-requirement">
                      要求：{{ item.requirement }}
                    </div>
                    <div class="job-contact">
                      联系方式：{{ item.contactPhone }}
                    </div>
                  </div>
                  <div class="job-footer">
                    <span class="job-time">发布于 {{ formatTime(item.createTime) }}</span>
                    <div class="job-actions">
                      <button v-if="item.status === 0 && currentTab === 'my'" class="btn-close" @click="closeRecruitment(item)">
                        结束
                      </button>
                      <button v-if="currentTab === 'my'" class="btn-delete" @click="deleteJob(item)">删除</button>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else class="empty-state">
                <div class="empty-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M0 12.5A1.5 1.5 0 0 0 1.5 14h13a1.5 1.5 0 0 0 1.5-1.5v-7h1v7a2.5 2.5 0 0 1-2.5 2.5h-13A2.5 2.5 0 0 1 0 12.5v-7h1v7z"/>
                  </svg>
                </div>
                <div class="empty-text">暂无信息</div>
                <div class="empty-hint">点击右上角「发布兼职」发布招聘信息</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 发布兼职弹窗 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>发布兼职信息</h3>
          <button class="modal-close" @click="showAddModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>工作类型</label>
            <div class="type-selector">
              <button class="type-btn" :class="{ active: newJob.workType === '校内' }" @click="newJob.workType = '校内'">校内兼职</button>
              <button class="type-btn" :class="{ active: newJob.workType === '校外' }" @click="newJob.workType = '校外'">校外兼职</button>
            </div>
          </div>
          <div class="form-group">
            <label>职位标题</label>
            <input v-model="newJob.title" type="text" placeholder="如：图书馆助理" class="form-input">
          </div>
          <div class="form-group">
            <label>工作描述</label>
            <textarea v-model="newJob.description" placeholder="请描述工作内容" class="form-input form-textarea"></textarea>
          </div>
          <div class="form-group">
            <label>工作地点</label>
            <input v-model="newJob.location" type="text" placeholder="工作地点" class="form-input">
          </div>
          <div class="form-group">
            <label>工作时间</label>
            <input v-model="newJob.workTime" type="text" placeholder="如：周一至周五 18:00-21:00" class="form-input">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>薪资/小时</label>
              <input v-model="newJob.salary" type="number" placeholder="如：25" class="form-input">
            </div>
            <div class="form-group">
              <label>联系电话</label>
              <input v-model="newJob.contactPhone" type="tel" placeholder="联系电话" class="form-input">
            </div>
          </div>
          <div class="form-group">
            <label>要求（可选）</label>
            <input v-model="newJob.requirement" type="text" placeholder="其他要求" class="form-input">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showAddModal = false">取消</button>
          <button class="btn-confirm" @click="submitJob">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PartTime',
  data() {
    return {
      jobList: [],
      currentTab: 'all',
      userId: this.$cookies.get('userId') || 1,
      showAddModal: false,
      newJob: {
        workType: '校内',
        title: '',
        description: '',
        location: '',
        workTime: '',
        salary: 20,
        contactPhone: '',
        requirement: ''
      }
    }
  },
  computed: {
    filteredList() {
      return this.jobList
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      if (this.currentTab === 'my') {
        const res = await this.$axios.get('/parttime/my', { params: { userId: this.userId } })
        if (res.data.code === 200) this.jobList = res.data.data
      } else {
        const type = this.currentTab === 'campus' ? '校内' : this.currentTab === 'outside' ? '校外' : null
        const res = await this.$axios.get('/parttime/list', { params: { workType: type, userId: this.userId } })
        if (res.data.code === 200) this.jobList = res.data.data
      }
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.getFullYear() + '年' + (date.getMonth() + 1) + '月' + date.getDate() + '日'
    },
    async submitJob() {
      if (!this.newJob.title || !this.newJob.description || !this.newJob.location) {
        alert('请填写完整信息')
        return
      }
      try {
        const res = await this.$axios.post('/parttime/add', {
          publisherId: this.userId,
          ...this.newJob
        })
        if (res.data.code === 200) {
          this.showAddModal = false
          this.newJob = { workType: '校内', title: '', description: '', location: '', workTime: '', salary: 20, contactPhone: '', requirement: '' }
          this.loadData()
          alert('发布成功！')
        }
      } catch (err) {
        alert('发布失败')
      }
    },
    async closeRecruitment(item) {
      try {
        const res = await this.$axios.put('/parttime/close', null, { params: { id: item.id } })
        if (res.data.code === 200) {
          item.status = 1
        }
      } catch (err) {
        alert('操作失败')
      }
    },
    async deleteJob(item) {
      if (!confirm('确定删除吗？')) return
      try {
        const res = await this.$axios.delete('/parttime/delete', { params: { id: item.id } })
        if (res.data.code === 200) this.loadData()
      } catch (err) {
        alert('删除失败')
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
.circle-1 { width: 350px; height: 350px; background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); top: -80px; left: -80px; }
.circle-2 { width: 300px; height: 300px; background: linear-gradient(135deg, #fb923c 0%, #f97316 100%); bottom: -60px; right: -60px; animation-delay: -5s; }
@keyframes float { 0%, 100% { transform: translate(0, 0) scale(1); } 50% { transform: translate(20px, -20px) scale(1.05); } }
.main-content { position: relative; z-index: 1; padding: 30px 20px 50px; }
.page-header { animation: slideUp 0.6s ease-out; }
.header-icon { width: 64px; height: 64px; background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); border-radius: 18px; display: flex; align-items: center; justify-content: center; color: white; margin: 0 auto 16px; box-shadow: 0 8px 24px rgba(249, 115, 22, 0.3); }
.header-title { font-size: 1.75rem; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.header-subtitle { color: #6b7280; font-size: 15px; }
.content-card { background: white; border-radius: 20px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08); overflow: hidden; animation: slideUp 0.6s ease-out 0.1s both; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.card-header-custom { display: flex; align-items: center; justify-content: space-between; padding: 18px 24px; background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); color: white; font-weight: 600; font-size: 16px; }
.count-badge { background: rgba(255, 255, 255, 0.2); padding: 4px 12px; border-radius: 20px; font-size: 13px; font-weight: 500; }
.card-body-custom { padding: 20px; }
.list-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #eee; gap: 12px; flex-wrap: wrap; }
.tab-group { display: flex; gap: 6px; flex-wrap: wrap; }
.tab-btn { padding: 6px 12px; background: #f5f5f5; border: none; border-radius: 20px; font-size: 12px; font-weight: 500; cursor: pointer; transition: all 0.25s ease; color: #666; }
.tab-btn.active { background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); color: white; }
.btn-add { display: inline-flex; align-items: center; gap: 5px; padding: 8px 16px; background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); color: white; border: none; border-radius: 20px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 8px rgba(249, 115, 22, 0.25); }
.btn-add:hover { transform: translateY(-1px); }
.job-list { display: flex; flex-direction: column; gap: 12px; }
.job-item { padding: 16px 20px; background: #f8f9fa; border-radius: 12px; border: 1px solid #eee; transition: all 0.3s ease; }
.job-item:hover { border-color: #f97316; transform: translateX(4px); }
.job-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.job-type { padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; color: white; }
.job-type.校内 { background: linear-gradient(135deg, #10b981, #059669); }
.job-type.校外 { background: linear-gradient(135deg, #3b82f6, #2563eb); }
.job-status { font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 12px; }
.job-status.recruiting { background: rgba(16, 185, 129, 0.15); color: #10b981; }
.job-status.closed { background: rgba(107, 114, 128, 0.15); color: #6b7280; }
.job-title { font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 6px; }
.job-desc { font-size: 14px; color: #4b5563; margin-bottom: 8px; line-height: 1.5; }
.job-info { display: flex; gap: 16px; font-size: 13px; color: #6b7280; margin-bottom: 8px; }
.job-info span { display: flex; align-items: center; gap: 4px; }
.job-salary { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.salary-label { font-size: 13px; color: #6b7280; }
.salary-value { font-size: 18px; font-weight: 700; color: #f97316; }
.job-requirement { font-size: 13px; color: #6b7280; margin-bottom: 6px; }
.job-contact { font-size: 13px; color: #3b82f6; }
.job-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 14px; padding-top: 12px; border-top: 1px dashed #e5e7eb; flex-wrap: nowrap; }
.job-time { font-size: 12px; color: #9ca3af; flex-shrink: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-right: 12px; }
.job-actions { display: flex; gap: 10px; flex-shrink: 0; align-items: center; }
.btn-close { display: inline-flex; align-items: center; justify-content: center; min-width: 70px; padding: 6px 12px; background: linear-gradient(135deg, #fefce8 0%, #fef9c3 100%); color: #a16207; border: 1.5px solid #fca5a5; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; white-space: nowrap; transition: all 0.2s ease; box-shadow: 0 1px 4px rgba(234,179,8,0.15); }
.btn-close:hover { background: linear-gradient(135deg, #eab308 0%, #ca8a04 100%); color: white; border-color: transparent; box-shadow: 0 4px 12px rgba(234,179,8,0.4); transform: translateY(-1px); }
.btn-delete { display: inline-flex; align-items: center; justify-content: center; min-width: 70px; padding: 6px 12px; background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%); color: #b91c1c; border: 1.5px solid #fca5a5; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; white-space: nowrap; transition: all 0.2s ease; box-shadow: 0 1px 4px rgba(239,68,68,0.15); }
.btn-delete:hover { background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); color: white; border-color: transparent; box-shadow: 0 4px 12px rgba(239,68,68,0.4); transform: translateY(-1px); }
.empty-state { text-align: center; padding: 48px 20px; }
.empty-icon { width: 80px; height: 80px; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #adb5bd; margin: 0 auto 20px; }
.empty-text { font-size: 18px; font-weight: 600; color: #495057; margin-bottom: 8px; }
.empty-hint { color: #6b7280; font-size: 14px; }
.back-btn { display: inline-flex; align-items: center; gap: 5px; padding: 7px 14px; background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); border: none; border-radius: 20px; font-size: 13px; font-weight: 500; color: white; cursor: pointer; transition: all 0.25s ease; margin-bottom: 16px; box-shadow: 0 3px 12px rgba(249, 115, 22, 0.3); }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; animation: fadeIn 0.2s ease; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.modal-content { background: white; border-radius: 16px; width: 90%; max-width: 420px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2); animation: slideUp 0.3s ease; max-height: 85vh; overflow-y: auto; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px; border-bottom: 1px solid #eee; position: sticky; top: 0; background: white; }
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 600; color: #1a1a2e; }
.modal-close { width: 32px; height: 32px; background: #f5f5f5; border: none; border-radius: 50%; font-size: 20px; color: #666; cursor: pointer; }
.modal-body { padding: 24px; }
.form-group { margin-bottom: 14px; }
.form-group:last-child { margin-bottom: 0; }
.form-group label { display: block; margin-bottom: 6px; font-size: 14px; font-weight: 500; color: #333; }
.form-input { width: 100%; padding: 12px 14px; border: 1px solid #e0e0e0; border-radius: 10px; font-size: 14px; transition: all 0.25s ease; box-sizing: border-box; }
.form-input:focus { outline: none; border-color: #f97316; box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.15); }
.form-textarea { min-height: 80px; resize: vertical; }
.type-selector { display: flex; gap: 8px; }
.type-btn { flex: 1; padding: 10px; border: 2px solid #e0e0e0; border-radius: 10px; background: white; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.25s ease; }
.type-btn.active { border-color: #f97316; background: rgba(249, 115, 22, 0.1); color: #f97316; }
.form-row { display: flex; gap: 12px; }
.form-row .form-group { flex: 1; }
.modal-footer { display: flex; gap: 12px; padding: 16px 24px 24px; position: sticky; bottom: 0; background: white; }
.btn-cancel, .btn-confirm { flex: 1; padding: 12px; border: none; border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.25s ease; }
.btn-cancel { background: #f5f5f5; color: #666; }
.btn-confirm { background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); color: white; }
.btn-confirm:hover { transform: translateY(-2px); }
</style>
