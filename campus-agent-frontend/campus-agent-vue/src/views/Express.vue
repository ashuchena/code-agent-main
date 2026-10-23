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
                <path d="M2.5 3.5a.5.5 0 0 1 0-1h11a.5.5 0 0 1 0 1h-11zm2-2a.5.5 0 0 1 0-1h7a.5.5 0 0 1 0 1h-7zM.5 5.5a.5.5 0 0 0 0 1h15a.5.5 0 0 0 0-1h-15zm.5 2.5a.5.5 0 0 1 1 0v7a.5.5 0 0 1-1 0v-7zm2.5 0a.5.5 0 0 1 1 0v7a.5.5 0 0 1-1 0v-7zm2.5 0a.5.5 0 0 1 1 0v7a.5.5 0 0 1-1 0v-7z"/>
                <path d="M4 8a.5.5 0 0 0 .5.5h7a.5.5 0 0 0 0-1h-7A.5.5 0 0 0 4 8zm-2 3a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5A.5.5 0 0 1 2 11z"/>
              </svg>
            </div>
            <h2 class="header-title">快递代取</h2>
            <p class="header-subtitle">互帮互助，快速解决快递难题</p>
          </div>
          
          <div class="content-card">
            <div class="card-header-custom">
              <div class="d-flex align-items-center">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16" class="me-2">
                  <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
                </svg>
                代取任务
              </div>
              <span class="count-badge">{{ expressList.length }} 条</span>
            </div>
            <div class="card-body-custom">
              <div class="list-header">
                <div class="tab-group">
                  <button class="tab-btn" :class="{ active: currentTab === 'available' }" @click="currentTab = 'available'">待接单</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'my' }" @click="currentTab = 'my'">我的发布</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'helper' }" @click="currentTab = 'helper'">我帮代的</button>
                </div>
                <button class="btn-add" @click="showAddModal = true">
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
                  </svg>
                  发布任务
                </button>
              </div>
              
              <div v-if="filteredList.length > 0" class="express-list">
                <div v-for="item in filteredList" :key="item.id" class="express-item">
                  <div class="express-header">
                    <span class="express-company">{{ item.expressCompany }}</span>
                    <span class="express-status" :class="'status-' + item.status">
                      {{ statusText[item.status] }}
                    </span>
                  </div>
                  <div class="express-content">
                    <div class="express-code">
                      <span class="label">取件码</span>
                      <span class="value">{{ item.pickupCode }}</span>
                    </div>
                    <div class="express-info">
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z"/></svg> {{ item.location }}</span>
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2zm.995-14.901a1 1 0 1 0-1.99 0A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-.2-6-.2-6a5.002 5.002 0 0 0-2.005-2.098z"/></svg> {{ item.publisherPhone }}</span>
                    </div>
                    <div class="express-reward">
                      <span class="reward-label">报酬</span>
                      <span class="reward-value">¥{{ item.reward }}</span>
                    </div>
                    <div v-if="item.helperName" class="express-helper">
                      接单者：{{ item.helperName }}
                    </div>
                  </div>
                  <div class="express-footer">
                    <span class="express-time">{{ formatTime(item.createTime) }}</span>
                    <div class="express-actions">
                      <button v-if="item.status === 0 && currentTab === 'available'" class="btn-accept" @click="acceptOrder(item)">接单</button>
                      <button v-if="item.status === 1 && currentTab === 'helper'" class="btn-complete" @click="completeOrder(item)">已完成</button>
                      <button v-if="item.status === 2" class="btn-done" disabled>已完成</button>
                      <button v-if="item.status === 0 && currentTab === 'my'" class="btn-delete" @click="deleteItem(item)">取消</button>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else class="empty-state">
                <div class="empty-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M2.5 3.5a.5.5 0 0 1 0-1h11a.5.5 0 0 1 0 1h-11zm2-2a.5.5 0 0 1 0-1h7a.5.5 0 0 1 0 1h-7z"/>
                  </svg>
                </div>
                <div class="empty-text">暂无任务</div>
                <div class="empty-hint">点击右上角「发布任务」发布代取需求</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 发布任务弹窗 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>发布代取任务</h3>
          <button class="modal-close" @click="showAddModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>快递公司</label>
            <select v-model="newExpress.expressCompany" class="form-input">
              <option value="菜鸟驿站">菜鸟驿站</option>
              <option value="顺丰">顺丰</option>
              <option value="京东">京东</option>
              <option value="圆通">圆通</option>
              <option value="中通">中通</option>
              <option value="韵达">韵达</option>
              <option value="其他">其他</option>
            </select>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>快递单号</label>
              <input v-model="newExpress.trackingNo" type="text" placeholder="快递单号" class="form-input">
            </div>
            <div class="form-group">
              <label>取件码</label>
              <input v-model="newExpress.pickupCode" type="text" placeholder="取件码" class="form-input">
            </div>
          </div>
          <div class="form-group">
            <label>快递地点</label>
            <input v-model="newExpress.location" type="text" placeholder="如：菜鸟驿站（东门）" class="form-input">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>联系电话</label>
              <input v-model="newExpress.publisherPhone" type="tel" placeholder="联系电话" class="form-input">
            </div>
            <div class="form-group">
              <label>报酬(元)</label>
              <input v-model="newExpress.reward" type="number" placeholder="如：3" class="form-input">
            </div>
          </div>
          <div class="form-group">
            <label>备注</label>
            <input v-model="newExpress.remark" type="text" placeholder="备注信息（可选）" class="form-input">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showAddModal = false">取消</button>
          <button class="btn-confirm" @click="submitExpress">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Express',
  data() {
    return {
      expressList: [],
      currentTab: 'available',
      userId: this.$cookies.get('userId') || 1,
      showAddModal: false,
      newExpress: {
        expressCompany: '菜鸟驿站',
        trackingNo: '',
        pickupCode: '',
        location: '',
        publisherPhone: '',
        reward: 3,
        remark: ''
      },
      statusText: { 0: '待接单', 1: '进行中', 2: '已完成' }
    }
  },
  computed: {
    filteredList() {
      return this.expressList
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      if (this.currentTab === 'available') {
        const res = await this.$axios.get('/express/available')
        if (res.data.code === 200) this.expressList = res.data.data
      } else if (this.currentTab === 'my') {
        const res = await this.$axios.get('/express/my', { params: { userId: this.userId } })
        if (res.data.code === 200) this.expressList = res.data.data
      } else if (this.currentTab === 'helper') {
        const res = await this.$axios.get('/express/helper', { params: { userId: this.userId } })
        if (res.data.code === 200) this.expressList = res.data.data
      }
    },
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString('zh-CN')
    },
    async submitExpress() {
      if (!this.newExpress.trackingNo || !this.newExpress.pickupCode || !this.newExpress.location) {
        alert('请填写完整信息')
        return
      }
      try {
        const res = await this.$axios.post('/express/add', {
          publisherId: this.userId,
          ...this.newExpress
        })
        if (res.data.code === 200) {
          this.showAddModal = false
          this.newExpress = { expressCompany: '菜鸟驿站', trackingNo: '', pickupCode: '', location: '', publisherPhone: '', reward: 3, remark: '' }
          this.loadData()
          alert('发布成功！')
        }
      } catch (err) {
        alert('发布失败')
      }
    },
    async acceptOrder(item) {
      try {
        const res = await this.$axios.put('/express/accept', null, { params: { id: item.id, helperId: this.userId } })
        if (res.data.code === 200) {
          this.loadData()
          alert('接单成功！')
        }
      } catch (err) {
        alert('接单失败')
      }
    },
    async completeOrder(item) {
      try {
        const res = await this.$axios.put('/express/complete', null, { params: { id: item.id } })
        if (res.data.code === 200) {
          this.loadData()
          alert('确认完成！')
        }
      } catch (err) {
        alert('操作失败')
      }
    },
    async deleteItem(item) {
      if (!confirm('确定取消吗？')) return
      try {
        const res = await this.$axios.delete('/express/delete', { params: { id: item.id } })
        if (res.data.code === 200) this.loadData()
      } catch (err) {
        alert('取消失败')
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
.circle-1 { width: 350px; height: 350px; background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%); top: -80px; left: -80px; }
.circle-2 { width: 300px; height: 300px; background: linear-gradient(135deg, #ec4899 0%, #f472b6 100%); bottom: -60px; right: -60px; animation-delay: -5s; }
@keyframes float { 0%, 100% { transform: translate(0, 0) scale(1); } 50% { transform: translate(20px, -20px) scale(1.05); } }
.main-content { position: relative; z-index: 1; padding: 30px 20px 50px; }
.page-header { animation: slideUp 0.6s ease-out; }
.header-icon { width: 64px; height: 64px; background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%); border-radius: 18px; display: flex; align-items: center; justify-content: center; color: white; margin: 0 auto 16px; box-shadow: 0 8px 24px rgba(139, 92, 246, 0.3); }
.header-title { font-size: 1.75rem; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.header-subtitle { color: #6b7280; font-size: 15px; }
.content-card { background: white; border-radius: 20px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08); overflow: hidden; animation: slideUp 0.6s ease-out 0.1s both; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.card-header-custom { display: flex; align-items: center; justify-content: space-between; padding: 18px 24px; background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%); color: white; font-weight: 600; font-size: 16px; }
.count-badge { background: rgba(255, 255, 255, 0.2); padding: 4px 12px; border-radius: 20px; font-size: 13px; font-weight: 500; }
.card-body-custom { padding: 20px; }
.list-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #eee; gap: 12px; flex-wrap: wrap; }
.tab-group { display: flex; gap: 8px; }
.tab-btn { padding: 6px 14px; background: #f5f5f5; border: none; border-radius: 20px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.25s ease; color: #666; }
.tab-btn.active { background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%); color: white; }
.btn-add { display: inline-flex; align-items: center; gap: 5px; padding: 8px 16px; background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%); color: white; border: none; border-radius: 20px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 8px rgba(139, 92, 246, 0.25); }
.btn-add:hover { transform: translateY(-1px); }
.express-list { display: flex; flex-direction: column; gap: 12px; }
.express-item { padding: 16px 20px; background: #f8f9fa; border-radius: 12px; border: 1px solid #eee; transition: all 0.3s ease; }
.express-item:hover { border-color: #8b5cf6; transform: translateX(4px); }
.express-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.express-company { font-weight: 600; color: #1a1a2e; }
.express-status { font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 12px; }
.status-0 { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
.status-1 { background: rgba(139, 92, 246, 0.15); color: #8b5cf6; }
.status-2 { background: rgba(16, 185, 129, 0.15); color: #10b981; }
.express-code { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.express-code .label { font-size: 13px; color: #6b7280; }
.express-code .value { font-size: 18px; font-weight: 700; color: #8b5cf6; letter-spacing: 2px; }
.express-info { display: flex; gap: 16px; font-size: 13px; color: #6b7280; margin-bottom: 8px; }
.express-info span { display: flex; align-items: center; gap: 4px; }
.express-reward { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.reward-label { font-size: 13px; color: #6b7280; }
.reward-value { font-size: 16px; font-weight: 700; color: #ef4444; }
.express-helper { font-size: 13px; color: #8b5cf6; margin-bottom: 8px; }
.express-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; padding-top: 10px; border-top: 1px dashed #e5e7eb; }
.express-time { font-size: 12px; color: #9ca3af; }
.express-actions { display: flex; gap: 8px; }
.btn-accept, .btn-complete, .btn-delete, .btn-done { padding: 6px 14px; border: none; border-radius: 12px; font-size: 12px; font-weight: 500; cursor: pointer; transition: all 0.25s ease; }
.btn-accept { background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%); color: white; }
.btn-accept:hover { transform: scale(1.05); }
.btn-complete { background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; }
.btn-delete { background: rgba(239, 68, 68, 0.12); color: #ef4444; }
.btn-delete:hover { background: #ef4444; color: white; }
.btn-done { background: #e5e7eb; color: #9ca3af; cursor: not-allowed; }
.empty-state { text-align: center; padding: 48px 20px; }
.empty-icon { width: 80px; height: 80px; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #adb5bd; margin: 0 auto 20px; }
.empty-text { font-size: 18px; font-weight: 600; color: #495057; margin-bottom: 8px; }
.empty-hint { color: #6b7280; font-size: 14px; }
.back-btn { display: inline-flex; align-items: center; gap: 5px; padding: 7px 14px; background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%); border: none; border-radius: 20px; font-size: 13px; font-weight: 500; color: white; cursor: pointer; transition: all 0.25s ease; margin-bottom: 16px; box-shadow: 0 3px 12px rgba(139, 92, 246, 0.3); }
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
.form-input:focus { outline: none; border-color: #8b5cf6; box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.15); }
.form-row { display: flex; gap: 12px; }
.form-row .form-group { flex: 1; }
.modal-footer { display: flex; gap: 12px; padding: 16px 24px 24px; }
.btn-cancel, .btn-confirm { flex: 1; padding: 12px; border: none; border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.25s ease; }
.btn-cancel { background: #f5f5f5; color: #666; }
.btn-confirm { background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%); color: white; }
.btn-confirm:hover { transform: translateY(-2px); }
</style>
