<template>
  <div class="page-container">
    <div class="main-content">
      <div class="page-wrapper">
        <div class="page-header">
          <button class="back-home-btn" type="button" @click="$router.push('/home')">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
              <path d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0Z"/>
            </svg>
            <span>返回</span>
          </button>
          <div class="header-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2zm.995-14.901a1 1 0 1 0-1.99 0A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-.2-6-.05-7m-8.5 8a1 1 0 1 1 0-2 1 1 0 0 1 0 2zm0 3a1 1 0 1 1 0-2 1 1 0 0 1 0 2zm0 3a1 1 0 1 1 0-2 1 1 0 0 1 0 2z"/>
            </svg>
          </div>
          <h2 class="header-title">公告通知</h2>
          <p class="header-subtitle">校园最新动态一手掌握</p>
        </div>

        <div v-if="loading" class="empty-state">
          <p>加载中...</p>
        </div>

        <div class="announcement-list" v-else-if="announcements.length > 0">
          <div class="announcement-card" v-for="item in announcements" :key="item.id" @click="viewDetail(item)">
            <div class="announcement-badge" :class="item.type">
              {{ item.typeText }}
            </div>
            <h3 class="announcement-title">{{ item.title }}</h3>
            <p class="announcement-content">{{ item.summary }}</p>
            <div class="announcement-meta">
              <span class="meta-time">{{ item.time }}</span>
              <span class="meta-views">阅读</span>
            </div>
          </div>
        </div>

        <div class="empty-state" v-else>
          <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" fill="currentColor" viewBox="0 0 16 16">
            <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2zm.995-14.901a1 1 0 1 0-1.99 0A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-.2-6-.05-7m-8.5 8a1 1 0 1 1 0-2 1 1 0 0 1 0 2zm0 3a1 1 0 1 1 0-2 1 1 0 0 1 0 2zm0 3a1 1 0 1 1 0-2 1 1 0 0 1 0 2z"/>
          </svg>
          <p>暂无公告通知</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Announcement',
  data() {
    return {
      loading: false,
      announcements: []
    }
  },
  mounted() {
    this.loadAnnouncements()
  },
  methods: {
    async loadAnnouncements() {
      this.loading = true
      try {
        const res = await this.$axios.get('/admin/announcements/public')
        if (res.data.code === 200) {
          this.announcements = (res.data.data || []).map(item => ({
            id: item.id,
            title: item.title || '-',
            summary: item.content || '-',
            time: this.formatTime(item.createTime),
            type: item.type || 'notice',
            typeText: this.typeText(item.type)
          }))
        }
      } catch (error) {
        this.$message.error('加载公告失败')
      } finally {
        this.loading = false
      }
    },
    typeText(type) {
      return {
        notice: '通知',
        urgent: '紧急',
        activity: '活动'
      }[type] || '通知'
    },
    formatTime(value) {
      if (!value) return '-'
      return String(value).replace('T', ' ').slice(0, 16)
    },
    viewDetail(item) {
      this.$message.info(`公告详情：${item.title}`)
    }
  }
}
</script>

<style scoped>
.page-container { min-height: 100vh; background: #f8fafc; }
.main-content { padding: 24px 16px 48px; }
.page-wrapper { position: relative; max-width: 520px; margin: 0 auto; }
.page-topbar { display: none; }
.page-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: 24px;
}
.back-home-btn {
  position: absolute;
  left: -118px;
  top: 8px;
  border: 0;
  border-radius: 999px;
  padding: 8px 18px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: white;
  background: linear-gradient(135deg, #5f79f7 0%, #7a57d7 100%);
  box-shadow: 0 10px 24px rgba(95, 121, 247, 0.28);
  transition: transform 0.18s ease, box-shadow 0.18s ease, filter 0.18s ease;
}
.back-home-btn:hover {
  transform: translateY(-1px);
  filter: brightness(1.03);
  box-shadow: 0 14px 28px rgba(95, 121, 247, 0.34);
}
.back-home-btn span {
  font-size: 14px;
  font-weight: 700;
  line-height: 1;
}
.header-icon { width: 56px; height: 56px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 16px; display: flex; align-items: center; justify-content: center; color: white; margin: 0 auto 12px; box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3); }
.header-title { font-size: 1.4rem; font-weight: 700; color: #1e293b; margin-bottom: 4px; }
.header-subtitle { color: #64748b; font-size: 13px; }
.announcement-list { display: flex; flex-direction: column; gap: 14px; }
.announcement-card {
  background: white;
  border-radius: 14px;
  padding: 16px;
  transition: all 0.25s ease;
  border: 1px solid rgba(0,0,0,0.05);
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  cursor: pointer;
}
.announcement-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}
.announcement-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  margin-bottom: 10px;
}
.announcement-badge.notice {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
.announcement-badge.activity {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  color: white;
}
.announcement-badge.urgent {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}
.announcement-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
  line-height: 1.4;
}
.announcement-content {
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.announcement-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #94a3b8;
}
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #94a3b8;
}
.empty-state svg {
  margin-bottom: 16px;
  opacity: 0.5;
}
.empty-state p {
  font-size: 14px;
}
@media (max-width: 560px) {
  .page-wrapper {
    max-width: 100%;
  }
  .back-home-btn {
    position: static;
    padding: 9px 18px;
    margin-bottom: 16px;
  }
  .back-home-btn span {
    font-size: 14px;
  }
}
</style>
