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
                <path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6zm-1-5.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z"/>
                <path d="M10.5 10.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z"/>
              </svg>
            </div>
            <h2 class="header-title">失物招领</h2>
            <p class="header-subtitle">寻物启事 & 失物招领，帮助找回遗失物品</p>
          </div>
          
          <div class="content-card">
            <div class="card-header-custom">
              <div class="d-flex align-items-center">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16" class="me-2">
                  <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
                </svg>
                全部信息
              </div>
              <span class="count-badge">{{ itemList.length }} 条</span>
            </div>
            <div class="card-body-custom">
              <div class="list-header">
                <div class="tab-group">
                  <button class="tab-btn" :class="{ active: currentTab === 'all' }" @click="currentTab = 'all'">全部</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'found' }" @click="currentTab = 'found'">我捡到的</button>
                  <button class="tab-btn" :class="{ active: currentTab === 'lost' }" @click="currentTab = 'lost'">我丢失的</button>
                </div>
                <button class="btn-add" @click="showAddModal = true">
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
                  </svg>
                  发布
                </button>
              </div>
              
              <div v-if="filteredList.length > 0" class="item-list">
                <div v-for="item in filteredList" :key="item.id" class="item-card">
                  <div class="item-header">
                    <span class="item-type" :class="item.type">{{ item.type === 'found' ? '招领' : '寻物' }}</span>
                    <span class="item-status" :class="item.status === 0 ? 'ongoing' : 'resolved'">
                      {{ item.status === 0 ? '进行中' : '已解决' }}
                    </span>
                  </div>
                  <div class="item-content">
                    <div class="item-title">{{ item.title }}</div>
                    <div class="item-desc">{{ item.description }}</div>
                    <div class="item-info">
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z"/></svg> {{ item.location }}</span>
                      <span><svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2zm.995-14.901a1 1 0 1 0-1.99 0A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-.2-6-.2-6a5.002 5.002 0 0 0-2.005-2.098zM8 5c0 1.098.5 6 2 7h8c1.5-1-.2-6-.2-6a5.002 5.002 0 0 0-2.005-2.098A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7H6c1.5-1 2-5.902 2-7z"/></svg> {{ item.contactPhone }}</span>
                    </div>
                  </div>
                  <div class="item-footer">
                    <span class="item-time">发布于 {{ formatTime(item.createTime) }}</span>
                    <div class="item-actions">
                      <button v-if="item.status === 0" class="btn-resolve" @click="resolveItem(item)">已找到</button>
                      <button class="btn-delete" @click="deleteItem(item)">删除</button>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-else class="empty-state">
                <div class="empty-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6zm-1-5.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z"/>
                    <path d="M10.5 10.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0z"/>
                  </svg>
                </div>
                <div class="empty-text">暂无信息</div>
                <div class="empty-hint">点击右上角「发布」分享信息</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 发布弹窗 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>发布信息</h3>
          <button class="modal-close" @click="showAddModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>信息类型</label>
            <div class="type-selector">
              <button class="type-btn" :class="{ active: newItem.type === 'lost' }" @click="newItem.type = 'lost'">寻物启事</button>
              <button class="type-btn" :class="{ active: newItem.type === 'found' }" @click="newItem.type = 'found'">失物招领</button>
            </div>
          </div>
          <div class="form-group">
            <label>标题</label>
            <input v-model="newItem.title" type="text" placeholder="请输入标题" class="form-input">
          </div>
          <div class="form-group">
            <label>物品描述</label>
            <textarea v-model="newItem.description" placeholder="请详细描述物品特征" class="form-input form-textarea"></textarea>
          </div>
          <div class="form-group">
            <label>地点</label>
            <input v-model="newItem.location" type="text" placeholder="丢失/捡到地点" class="form-input">
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input v-model="newItem.contactPhone" type="tel" placeholder="请输入联系方式" class="form-input">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showAddModal = false">取消</button>
          <button class="btn-confirm" @click="submitItem">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LostFound',
  data() {
    return {
      itemList: [],
      currentTab: 'all',
      userId: this.$cookies.get('userId') || 1,
      showAddModal: false,
      newItem: {
        type: 'lost',
        title: '',
        description: '',
        location: '',
        contactPhone: ''
      }
    }
  },
  computed: {
    filteredList() {
      if (this.currentTab === 'all') return this.itemList
      if (this.currentTab === 'found') return this.itemList.filter(i => i.type === 'found')
      if (this.currentTab === 'lost') return this.itemList.filter(i => i.type === 'lost')
      return this.itemList
    }
  },
  mounted() {
    this.getItemList()
  },
  methods: {
    async getItemList() {
      try {
        const res = await this.$axios.get('/lostfound/list', { params: { userId: this.userId } })
        if (res.data.code === 200) {
          this.itemList = res.data.data
        }
      } catch (err) {
        console.error(err)
      }
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.getFullYear() + '年' + (date.getMonth() + 1) + '月' + date.getDate() + '日'
    },
    async submitItem() {
      if (!this.newItem.title || !this.newItem.description || !this.newItem.location) {
        alert('请填写完整信息')
        return
      }
      try {
        const res = await this.$axios.post('/lostfound/add', {
          userId: this.userId,
          ...this.newItem
        })
        if (res.data.code === 200) {
          this.showAddModal = false
          this.newItem = { type: 'lost', title: '', description: '', location: '', contactPhone: '' }
          this.getItemList()
          alert('发布成功！')
        }
      } catch (err) {
        alert('发布失败')
      }
    },
    async resolveItem(item) {
      try {
        const res = await this.$axios.put('/lostfound/resolve', null, { params: { id: item.id } })
        if (res.data.code === 200) {
          item.status = 1
        }
      } catch (err) {
        alert('操作失败')
      }
    },
    async deleteItem(item) {
      if (!confirm('确定删除吗？')) return
      try {
        const res = await this.$axios.delete('/lostfound/delete', { params: { id: item.id } })
        if (res.data.code === 200) {
          this.getItemList()
        }
      } catch (err) {
        alert('删除失败')
      }
    }
  }
}
</script>

<style scoped>
.page-container { min-height: 100vh; position: relative; overflow: hidden; background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 50%, #f0f2f5 100%); }
.bg-decoration { position: fixed; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: 0; }
.circle { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.5; animation: float 20s ease-in-out infinite; }
.circle-1 { width: 350px; height: 350px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); top: -80px; left: -80px; }
.circle-2 { width: 300px; height: 300px; background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%); bottom: -60px; right: -60px; animation-delay: -5s; }
@keyframes float { 0%, 100% { transform: translate(0, 0) scale(1); } 50% { transform: translate(20px, -20px) scale(1.05); } }
.main-content { position: relative; z-index: 1; padding: 30px 20px 50px; }
.page-header { animation: slideUp 0.6s ease-out; }
.header-icon { width: 64px; height: 64px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); border-radius: 18px; display: flex; align-items: center; justify-content: center; color: white; margin: 0 auto 16px; box-shadow: 0 8px 24px rgba(16, 185, 129, 0.3); }
.header-title { font-size: 1.75rem; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.header-subtitle { color: #6b7280; font-size: 15px; }
.content-card { background: white; border-radius: 20px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08); overflow: hidden; animation: slideUp 0.6s ease-out 0.1s both; }
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
.card-header-custom { display: flex; align-items: center; justify-content: space-between; padding: 18px 24px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; font-weight: 600; font-size: 16px; }
.count-badge { background: rgba(255, 255, 255, 0.2); padding: 4px 12px; border-radius: 20px; font-size: 13px; font-weight: 500; }
.card-body-custom { padding: 20px; }
.list-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #eee; gap: 12px; flex-wrap: wrap; }
.tab-group { display: flex; gap: 8px; }
.tab-btn { padding: 6px 14px; background: #f5f5f5; border: none; border-radius: 20px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.25s ease; color: #666; }
.tab-btn.active { background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; }
.btn-add { display: inline-flex; align-items: center; gap: 5px; padding: 8px 16px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; border: none; border-radius: 20px; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 8px rgba(16, 185, 129, 0.25); }
.btn-add:hover { transform: translateY(-1px); }
.item-list { display: flex; flex-direction: column; gap: 12px; }
.item-card { padding: 16px 20px; background: #f8f9fa; border-radius: 12px; border: 1px solid #eee; transition: all 0.3s ease; }
.item-card:hover { border-color: #10b981; transform: translateX(4px); }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.item-type { padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; color: white; }
.item-type.招领 { background: linear-gradient(135deg, #10b981, #059669); }
.item-type.寻物 { background: linear-gradient(135deg, #ef4444, #dc2626); }
.item-status { font-size: 12px; font-weight: 600; padding: 4px 10px; border-radius: 12px; }
.item-status.ongoing { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
.item-status.resolved { background: rgba(16, 185, 129, 0.15); color: #10b981; }
.item-title { font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 6px; }
.item-desc { font-size: 14px; color: #4b5563; margin-bottom: 8px; line-height: 1.5; }
.item-info { display: flex; gap: 16px; font-size: 13px; color: #6b7280; }
.item-info span { display: flex; align-items: center; gap: 4px; }
.item-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; padding-top: 10px; border-top: 1px dashed #e5e7eb; }
.item-time { font-size: 12px; color: #9ca3af; }
.item-actions { display: flex; gap: 8px; }
.btn-resolve { padding: 4px 12px; background: rgba(16, 185, 129, 0.15); color: #10b981; border: none; border-radius: 12px; font-size: 12px; font-weight: 500; cursor: pointer; }
.btn-resolve:hover { background: #10b981; color: white; }
.btn-delete { padding: 4px 12px; background: rgba(239, 68, 68, 0.12); color: #ef4444; border: none; border-radius: 12px; font-size: 12px; font-weight: 500; cursor: pointer; }
.btn-delete:hover { background: #ef4444; color: white; }
.empty-state { text-align: center; padding: 48px 20px; }
.empty-icon { width: 80px; height: 80px; background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%); border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #adb5bd; margin: 0 auto 20px; }
.empty-text { font-size: 18px; font-weight: 600; color: #495057; margin-bottom: 8px; }
.empty-hint { color: #6b7280; font-size: 14px; }
.back-btn { display: inline-flex; align-items: center; gap: 5px; padding: 7px 14px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); border: none; border-radius: 20px; font-size: 13px; font-weight: 500; color: white; cursor: pointer; transition: all 0.25s ease; margin-bottom: 16px; box-shadow: 0 3px 12px rgba(16, 185, 129, 0.3); }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; animation: fadeIn 0.2s ease; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.modal-content { background: white; border-radius: 16px; width: 90%; max-width: 420px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2); animation: slideUp 0.3s ease; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px; border-bottom: 1px solid #eee; }
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 600; color: #1a1a2e; }
.modal-close { width: 32px; height: 32px; background: #f5f5f5; border: none; border-radius: 50%; font-size: 20px; color: #666; cursor: pointer; }
.modal-close:hover { background: #e0e0e0; }
.modal-body { padding: 24px; }
.form-group { margin-bottom: 16px; }
.form-group:last-child { margin-bottom: 0; }
.form-group label { display: block; margin-bottom: 6px; font-size: 14px; font-weight: 500; color: #333; }
.form-input { width: 100%; padding: 12px 14px; border: 1px solid #e0e0e0; border-radius: 10px; font-size: 14px; transition: all 0.25s ease; box-sizing: border-box; }
.form-input:focus { outline: none; border-color: #10b981; box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15); }
.form-textarea { min-height: 80px; resize: vertical; }
.type-selector { display: flex; gap: 8px; }
.type-btn { flex: 1; padding: 10px; border: 2px solid #e0e0e0; border-radius: 10px; background: white; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.25s ease; }
.type-btn.active { border-color: #10b981; background: rgba(16, 185, 129, 0.1); color: #10b981; }
.modal-footer { display: flex; gap: 12px; padding: 16px 24px 24px; }
.btn-cancel, .btn-confirm { flex: 1; padding: 12px; border: none; border-radius: 10px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.25s ease; }
.btn-cancel { background: #f5f5f5; color: #666; }
.btn-cancel:hover { background: #e0e0e0; }
.btn-confirm { background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; }
.btn-confirm:hover { transform: translateY(-2px); }
</style>
