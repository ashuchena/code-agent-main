<template>
  <div class="page-container">
    <div class="main-content">
      <div class="page-wrapper">
        <button class="back-btn" @click="$router.push('/home')">
          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
            <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
          </svg>
          返回
        </button>

        <div class="page-header">
          <div class="header-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="currentColor" viewBox="0 0 24 24">
              <path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/>
            </svg>
          </div>
          <h2 class="header-title">常用服务热线</h2>
          <p class="header-subtitle">一键拨打，快速联系校园各部门</p>
        </div>

        <div v-if="phoneList.length > 0" class="phone-list">
          <div class="phone-item" v-for="phone in phoneList" :key="phone.id">
            <div class="phone-icon" :style="{ background: phone.iconBg }">
              <svg v-if="phone.icon === 'restaurant'" xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24"><path d="M11 9H9V2H7v7H5V2H3v7c0 2.12 1.66 3.84 3.75 3.97V22h2.5v-9.03C11.34 12.84 13 11.12 13 9V2h-2v7zm5-3v8h2.5v8H21V2c-2.76 0-5 2.24-5 4z"/></svg>
              <svg v-else-if="phone.icon === 'medical'" xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24"><path d="M19 3H5c-1.1 0-1.99.9-1.99 2L3 19c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-1 11h-4v4h-4v-4H6v-4h4V6h4v4h4v4z"/></svg>
              <svg v-else-if="phone.icon === 'security'" xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24"><path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4z"/></svg>
              <svg v-else-if="phone.icon === 'counselor'" xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24"><path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/></svg>
              <svg v-else-if="phone.icon === 'psychology'" xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>
              <svg v-else-if="phone.icon === 'library'" xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24"><path d="M12 11.55C9.64 9.35 6.48 8 3 8v11c3.48 0 6.64 1.35 9 3.55 2.36-2.19 5.52-3.55 9-3.55V8c-3.48 0-6.64 1.35-9 3.55zM12 8c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3z"/></svg>
              <svg v-else-if="phone.icon === 'water'" xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2c-5.33 4.55-8 8.48-8 11.8 0 4.98 3.8 8.2 8 8.2s8-3.22 8-8.2c0-3.32-2.67-7.25-8-11.8zm0 18c-3.35 0-6-2.57-6-6.2 0-2.34 1.95-5.44 6-9.14 4.05 3.7 6 6.79 6 9.14 0 3.63-2.65 6.2-6 6.2z"/></svg>
              <svg v-else-if="phone.icon === 'network'" xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24"><path d="M12 18c.83 0 1.5.67 1.5 1.5S12.83 21 12 21s-1.5-.67-1.5-1.5S11.17 18 12 18zm0-3c2.21 0 4 1.79 4 4h-2a2 2 0 0 0-4 0H8c0-2.21 1.79-4 4-4zm0-4c4.42 0 8 3.58 8 8h-2c0-3.31-2.69-6-6-6s-6 2.69-6 6H4c0-4.42 3.58-8 8-8zm0-4c6.63 0 12 5.37 12 12h-2c0-5.52-4.48-10-10-10S2 13.48 2 19H0C0 8.37 5.37 3 12 3z"/></svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 24 24"><path d="M20 15.5c-1.25 0-2.45-.2-3.57-.57a1.02 1.02 0 0 0-1.02.24l-2.2 2.2a15.074 15.074 0 0 1-6.59-6.59l2.2-2.2c.28-.28.36-.67.25-1.02A11.36 11.36 0 0 1 8.5 4c0-.55-.45-1-1-1H4c-.55 0-1 .45-1 1 0 9.39 7.61 17 17 17 .55 0 1-.45 1-1v-3.5c0-.55-.45-1-1-1z"/></svg>
            </div>
            <div class="phone-info">
              <span class="phone-name">{{ phone.name }}</span>
              <span class="phone-desc">{{ phone.description || '校园服务电话' }}</span>
            </div>
            <a class="phone-call-btn" :href="'tel:' + phone.number">拨打</a>
          </div>
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
              <path d="M20 15.5c-1.25 0-2.45-.2-3.57-.57a1.02 1.02 0 0 0-1.02.24l-2.2 2.2a15.074 15.074 0 0 1-6.59-6.59l2.2-2.2c.28-.28.36-.67.25-1.02A11.36 11.36 0 0 1 8.5 4c0-.55-.45-1-1-1H4c-.55 0-1 .45-1 1 0 9.39 7.61 17 17 17 .55 0 1-.45 1-1v-3.5c0-.55-.45-1-1-1z"/>
            </svg>
          </div>
          <p>暂无服务热线</p>
        </div>

        <div class="tip-section">
          <div class="tip-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/></svg>
          </div>
          <div class="tip-text">
            <p>温馨提示：如遇紧急情况，请直接拨打校园报警电话 <strong>110</strong></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const PHONE_THEMES = [
  { keyword: '食堂', icon: 'restaurant', iconBg: 'linear-gradient(135deg, #f97316 0%, #ea580c 100%)' },
  { keyword: '医', icon: 'medical', iconBg: 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)' },
  { keyword: '保卫', icon: 'security', iconBg: 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)' },
  { keyword: '辅导员', icon: 'counselor', iconBg: 'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)' },
  { keyword: '心理', icon: 'psychology', iconBg: 'linear-gradient(135deg, #ec4899 0%, #db2777 100%)' },
  { keyword: '图书馆', icon: 'library', iconBg: 'linear-gradient(135deg, #06b6d4 0%, #0891b2 100%)' },
  { keyword: '后勤', icon: 'water', iconBg: 'linear-gradient(135deg, #10b981 0%, #059669 100%)' },
  { keyword: '维修', icon: 'water', iconBg: 'linear-gradient(135deg, #10b981 0%, #059669 100%)' },
  { keyword: '网络', icon: 'network', iconBg: 'linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)' }
]

export default {
  name: 'ServiceHotline',
  data() {
    return {
      phoneList: []
    }
  },
  mounted() {
    this.loadPhones()
  },
  methods: {
    normalizePhone(item) {
      const source = `${item.name || ''}${item.description || ''}`
      const matched = PHONE_THEMES.find(theme => source.includes(theme.keyword))
      return {
        ...item,
        description: item.description || '',
        icon: item.icon || (matched ? matched.icon : 'phone'),
        iconBg: item.iconBg || (matched ? matched.iconBg : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)')
      }
    },
    async loadPhones() {
      try {
        const res = await this.$axios.get('/admin/phones/public')
        if (res.data.code === 200) {
          this.phoneList = (res.data.data || []).map(this.normalizePhone)
        }
      } catch (error) {
        this.phoneList = []
      }
    }
  }
}
</script>

<style scoped>
.page-container { min-height: 100vh; background: #f8fafc; }
.main-content { padding: 24px 16px 48px; }
.page-wrapper { max-width: 560px; margin: 0 auto; }
.back-btn { display: inline-flex; align-items: center; gap: 4px; padding: 8px 18px; background: linear-gradient(135deg, #667eea, #764ba2); border: none; border-radius: 50px; font-size: 13px; font-weight: 600; color: white; cursor: pointer; transition: all 0.2s ease; margin-bottom: 16px; box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3); }
.back-btn:hover { transform: translateX(-3px); box-shadow: 0 5px 16px rgba(102, 126, 234, 0.4); }
.page-header { text-align: center; margin-bottom: 20px; }
.header-icon { width: 56px; height: 56px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 16px; display: flex; align-items: center; justify-content: center; color: white; margin: 0 auto 12px; box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3); }
.header-title { font-size: 1.4rem; font-weight: 700; color: #1e293b; margin-bottom: 4px; }
.header-subtitle { color: #64748b; font-size: 13px; }
.phone-list { display: flex; flex-direction: column; gap: 12px; }
.phone-item { display: flex; align-items: center; gap: 14px; background: white; border-radius: 14px; padding: 14px 16px; transition: all 0.25s ease; border: 1px solid rgba(0,0,0,0.05); box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.phone-item:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(0,0,0,0.08); }
.phone-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: white; flex-shrink: 0; box-shadow: 0 3px 10px rgba(0,0,0,0.12); }
.phone-info { flex: 1; display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.phone-name { font-size: 15px; font-weight: 600; color: #1e293b; }
.phone-desc { font-size: 12px; color: #64748b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.phone-call-btn { padding: 8px 18px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; border: none; border-radius: 20px; font-size: 13px; font-weight: 600; cursor: pointer; text-decoration: none; transition: all 0.2s ease; box-shadow: 0 3px 10px rgba(16, 185, 129, 0.3); flex-shrink: 0; }
.phone-call-btn:hover { transform: scale(1.05); box-shadow: 0 5px 14px rgba(16, 185, 129, 0.4); }
.empty-state { display: grid; place-items: center; gap: 10px; min-height: 180px; color: #64748b; background: white; border: 1px solid rgba(0,0,0,0.05); border-radius: 16px; }
.empty-icon { width: 56px; height: 56px; border-radius: 16px; display: grid; place-items: center; color: #94a3b8; background: #eef2f7; }
.tip-section { display: flex; align-items: flex-start; gap: 12px; background: #fef3c7; border-radius: 12px; padding: 14px; margin-top: 20px; }
.tip-icon { color: #f59e0b; flex-shrink: 0; margin-top: 1px; }
.tip-text { font-size: 13px; color: #92400e; line-height: 1.5; }
.tip-text strong { font-weight: 600; }
</style>
