<template>
  <div
    id="app"
    class="app-shell"
    :class="{
      'sidebar-collapsed': sidebarCollapsed,
      'sidebar-hidden': hideSidebar
    }"
  >
    <div v-if="!hideSidebar && mobileSidebarOpen" class="sidebar-overlay" @click="closeMobileSidebar"></div>

    <aside v-if="!hideSidebar" class="sidebar" :class="{ open: mobileSidebarOpen }">
      <div class="sidebar-header">
        <router-link class="brand-wrap" to="/chat" title="返回 AI 对话">
          <span class="brand-mark">
            <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8 0c-.75 2.503-2.254 4.176-3.568 5.638C3.122 7.096 2 8.345 2 10a6 6 0 0 0 12 0c0-1.655-1.122-2.904-2.432-4.362C10.254 4.176 8.75 2.503 8 0Z"/>
            </svg>
          </span>
          <span v-if="!sidebarCollapsed" class="brand-copy">
            <strong>校园助手</strong>
            <small>校园生活小管家</small>
          </span>
        </router-link>

      </div>

      <nav class="sidebar-nav">
        <div v-for="section in menuSections" :key="section.title" class="nav-group">
          <p v-if="!sidebarCollapsed" class="group-title">{{ section.title }}</p>
          <router-link
            v-for="item in section.items"
            :key="item.path"
            :to="item.path"
            class="side-nav-item"
            active-class="active"
            @click.native="handleNavClick"
          >
            <span class="nav-icon" v-html="item.icon"></span>
            <span v-if="!sidebarCollapsed" class="nav-text">{{ item.label }}</span>
            <span v-if="!sidebarCollapsed && item.path === '/announcement'" class="nav-badge">公告</span>
          </router-link>
        </div>
      </nav>

      <div class="sidebar-footer">
        <button class="sidebar-tool" :class="{ compact: sidebarCollapsed }" type="button" @click="toggleTheme">
          <span class="tool-icon">
            <svg v-if="!isDarkMode" xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm0-12a.5.5 0 0 1 .5.5V2a.5.5 0 0 1-1 0V.5A.5.5 0 0 1 8 0Zm0 14a.5.5 0 0 1 .5.5V16a.5.5 0 0 1-1 0v-1.5A.5.5 0 0 1 8 14Z"/>
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
              <path d="M6 .278a.77.77 0 0 1 .08.858A7.2 7.2 0 0 0 5.2 4.596c0 4.022 3.279 7.278 7.319 7.278.527 0 1.04-.055 1.533-.16a.79.79 0 0 1 .81.316.73.73 0 0 1-.031.893A8.35 8.35 0 0 1 8.344 16C3.734 16 0 12.286 0 7.71 0 4.266 2.114 1.312 5.124.06A.75.75 0 0 1 6 .278Z"/>
            </svg>
          </span>
          <span v-if="!sidebarCollapsed">{{ isDarkMode ? '浅色主题' : '深色主题' }}</span>
        </button>
        <button
          v-if="!isMobile"
          class="sidebar-tool sidebar-collapse-btn"
          :class="{ compact: sidebarCollapsed }"
          type="button"
          :title="sidebarCollapsed ? '展开侧边栏' : '收起侧边栏'"
          @click="toggleCollapse"
        >
          <span class="tool-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
              <path v-if="sidebarCollapsed" d="M6.354 3.646a.5.5 0 0 0-.708.708L9.293 8l-3.647 3.646a.5.5 0 0 0 .708.708l4-4a.5.5 0 0 0 0-.708l-4-4Z"/>
              <path v-else d="M9.646 3.646a.5.5 0 0 1 .708.708L6.707 8l3.647 3.646a.5.5 0 0 1-.708.708l-4-4a.5.5 0 0 1 0-.708l4-4Z"/>
            </svg>
          </span>
          <span v-if="!sidebarCollapsed">{{ sidebarCollapsed ? '展开侧边栏' : '收起侧边栏' }}</span>
        </button>

        <div class="user-card" :class="{ compact: sidebarCollapsed }">
          <span class="avatar-chip">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6Zm-5 6s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1H3Z"/>
            </svg>
          </span>
          <span v-if="!sidebarCollapsed" class="user-copy">
            <strong>{{ username }}</strong>
            <small>{{ isAdmin ? '管理员' : '校园用户' }}</small>
          </span>
          <button v-if="!sidebarCollapsed" class="logout-btn" type="button" @click="handleLogout">退出</button>
        </div>
      </div>
    </aside>

    <div class="app-frame">
      <header v-if="!hideSidebar" class="mobile-topbar">
        <button class="mobile-icon" type="button" @click="toggleMobileSidebar" title="打开菜单">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
            <path d="M2 3.75A.75.75 0 0 1 2.75 3h10.5a.75.75 0 0 1 0 1.5H2.75A.75.75 0 0 1 2 3.75Zm0 4.25A.75.75 0 0 1 2.75 7h10.5a.75.75 0 0 1 0 1.5H2.75A.75.75 0 0 1 2 8Zm0 4.25a.75.75 0 0 1 .75-.75h10.5a.75.75 0 0 1 0 1.5H2.75a.75.75 0 0 1-.75-.75Z"/>
          </svg>
        </button>
        <div class="mobile-brand">
          <strong>校园助手</strong>
          <span>{{ currentRouteLabel }}</span>
        </div>
        <button class="mobile-icon" type="button" @click="toggleTheme" title="切换主题">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
            <path d="M8 1a7 7 0 1 0 7 7 .5.5 0 0 0-.798-.4A5.5 5.5 0 0 1 8.5 2.798.5.5 0 0 0 8 1Z"/>
          </svg>
        </button>
      </header>

      <main class="app-main">
        <router-view />
      </main>
    </div>

    <div class="toast-stack">
      <transition-group name="toast">
        <div v-for="toast in toasts" :key="toast.id" class="toast-item" :class="toast.type">
          <span>{{ toast.text }}</span>
          <button type="button" @click="removeToast(toast.id)">×</button>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script>
import { clearAuthSession, readAuthSession } from './utils/auth'

const MENU_SECTIONS = [
  {
    title: '核心功能',
    items: [
      {
        path: '/chat',
        label: 'AI 对话',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M7 10h10M7 14h6"/><path stroke-linecap="round" stroke-linejoin="round" d="M4 6.5A2.5 2.5 0 0 1 6.5 4h11A2.5 2.5 0 0 1 20 6.5v7A2.5 2.5 0 0 1 17.5 16H10l-4.5 4v-4H6.5A2.5 2.5 0 0 1 4 13.5v-7Z"/></svg>'
      },
      {
        path: '/home',
        label: '首页概览',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="m3 11 9-8 9 8"/><path stroke-linecap="round" stroke-linejoin="round" d="M5 10v10h14V10"/></svg>'
      }
    ]
  },
  {
    title: '常用服务',
    items: [
      {
        path: '/student-service',
        label: '学生服务',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="m3 8 9-4 9 4-9 4-9-4Z"/><path stroke-linecap="round" stroke-linejoin="round" d="m6 10.5 6 2.5 6-2.5"/><path stroke-linecap="round" stroke-linejoin="round" d="M6 14l6 3 6-3"/></svg>'
      },
      {
        path: '/remind',
        label: '提醒事项',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.4-1.4A2 2 0 0 1 18 14.2V11a6 6 0 1 0-12 0v3.2a2 2 0 0 1-.6 1.4L4 17h5"/><path stroke-linecap="round" stroke-linejoin="round" d="M10 17a2 2 0 0 0 4 0"/></svg>'
      },
      {
        path: '/feedback',
        label: '问题反馈',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M8 9h8M8 13h5"/><path stroke-linecap="round" stroke-linejoin="round" d="M6 4h12a2 2 0 0 1 2 2v12l-4-3H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2Z"/></svg>'
      },
      {
        path: '/psych-consult',
        label: '心理导师',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 21s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 11c0 5.6-7 10-7 10Z"/></svg>'
      },
      {
        path: '/announcement',
        label: '公告通知',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M4 12V8a2 2 0 0 1 2-2h3l7-2v16l-7-2H6a2 2 0 0 1-2-2v-4Z"/><path stroke-linecap="round" stroke-linejoin="round" d="M6 18v2a2 2 0 0 0 2 2h1"/></svg>'
      }
    ]
  },
  {
    title: '更多服务',
    items: [
      {
        path: '/repair-phone',
        label: '服务热线',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M22 16.9v2a2 2 0 0 1-2.2 2 19.8 19.8 0 0 1-8.6-3.1 19.5 19.5 0 0 1-6-6A19.8 19.8 0 0 1 2.1 3.2 2 2 0 0 1 4.1 1h2a2 2 0 0 1 2 1.7l.4 2.8a2 2 0 0 1-.6 1.8L6.6 8.6a16 16 0 0 0 8.8 8.8l1.3-1.3a2 2 0 0 1 1.8-.6l2.8.4A2 2 0 0 1 22 16.9Z"/></svg>'
      },
      {
        path: '/maintenance',
        label: '报修登记',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="m14 7 3-3 3 3-3 3"/><path stroke-linecap="round" stroke-linejoin="round" d="m11 10-7 7v3h3l7-7"/><path stroke-linecap="round" stroke-linejoin="round" d="m13 8 3 3"/></svg>'
      },
      {
        path: '/admin',
        label: '管理后台',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M12 2 4 5v6c0 5 3.4 9.7 8 11 4.6-1.3 8-6 8-11V5l-8-3Z"/><path stroke-linecap="round" stroke-linejoin="round" d="M9.5 12.5 11 14l3.5-4"/></svg>'
      }
    ]
  }
]

export default {
  name: 'App',
  data() {
    return {
      authState: null,
      isCollapsed: false,
      isDarkMode: false,
      isMobile: false,
      mobileSidebarOpen: false,
      toasts: []
    }
  },
  computed: {
    sidebarCollapsed() {
      return !this.isMobile && this.isCollapsed
    },
    hideSidebar() {
      return ['/login', '/register'].includes(this.$route.path)
    },
    username() {
      const auth = this.authState
      return (auth && (auth.nickname || auth.username)) || '校园用户'
    },
    isAdmin() {
      const auth = this.authState
      return auth && auth.role === 'admin'
    },
    menuSections() {
      return MENU_SECTIONS.map(section => ({
        ...section,
        items: section.items.filter(item => item.path !== '/admin' || this.isAdmin)
      }))
    },
    currentRouteLabel() {
      const current = this.menuSections
        .reduce((items, section) => items.concat(section.items), [])
        .find(item => item.path === this.$route.path)
      return current ? current.label : '校园生活小管家'
    }
  },
  watch: {
    '$route.path'() {
      this.closeMobileSidebar()
      this.refreshAuthState()
    }
  },
  created() {
    const savedTheme = localStorage.getItem('campus-theme')
    const savedCollapsed = localStorage.getItem('campus-sidebar-collapsed')
    const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
    this.refreshAuthState()
    this.isDarkMode = savedTheme ? savedTheme === 'dark' : prefersDark
    this.isCollapsed = savedCollapsed === 'true'
    this.applyTheme()
  },
  mounted() {
    this.handleResize()
    window.addEventListener('resize', this.handleResize, { passive: true })
    window.addEventListener('campus-message', this.handleToastEvent)
    window.addEventListener('campus-auth-change', this.handleAuthChange)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    window.removeEventListener('campus-message', this.handleToastEvent)
    window.removeEventListener('campus-auth-change', this.handleAuthChange)
  },
  methods: {
    refreshAuthState() {
      this.authState = readAuthSession()
    },
    handleAuthChange(event) {
      this.authState = event.detail || readAuthSession()
    },
    handleResize() {
      const isMobile = window.innerWidth <= 960
      this.isMobile = isMobile
      if (isMobile) {
        this.isCollapsed = false
      } else {
        this.mobileSidebarOpen = false
        this.isCollapsed = localStorage.getItem('campus-sidebar-collapsed') === 'true'
      }
    },
    toggleTheme() {
      this.isDarkMode = !this.isDarkMode
      localStorage.setItem('campus-theme', this.isDarkMode ? 'dark' : 'light')
      this.applyTheme()
    },
    applyTheme() {
      document.documentElement.setAttribute('data-theme', this.isDarkMode ? 'dark' : 'light')
      document.body.classList.toggle('dark-mode', this.isDarkMode)
    },
    toggleCollapse() {
      if (this.isMobile) return
      this.isCollapsed = !this.isCollapsed
      localStorage.setItem('campus-sidebar-collapsed', String(this.isCollapsed))
    },
    toggleMobileSidebar() {
      this.mobileSidebarOpen = !this.mobileSidebarOpen
    },
    closeMobileSidebar() {
      this.mobileSidebarOpen = false
    },
    handleNavClick() {
      if (this.isMobile) {
        this.closeMobileSidebar()
      }
    },
    handleLogout() {
      clearAuthSession()
      this.authState = null
      this.$cookies.remove('rememberedUsername', '/')
      this.$router.replace('/login')
    },
    handleToastEvent(event) {
      const toast = event.detail
      this.toasts.push(toast)
      window.setTimeout(() => this.removeToast(toast.id), 2600)
    },
    removeToast(id) {
      this.toasts = this.toasts.filter(item => item.id !== id)
    }
  }
}
</script>

<style>
:root {
  --app-bg: #eef4ff;
  --card-bg: #ffffff;
  --card-border: rgba(219, 231, 241, 0.95);
  --text-main: #22303c;
  --text-soft: #758595;
  --sidebar-width: 252px;
  --sidebar-width-collapsed: 74px;
  --sidebar-bg: rgba(255, 255, 255, 0.94);
  --sidebar-active: #d8ebfb;
  --sidebar-accent: #1cb7d4;
  --sidebar-soft: rgba(28, 183, 212, 0.1);
}

[data-theme='dark'] {
  --app-bg: #0d1726;
  --card-bg: #132133;
  --card-border: rgba(255, 255, 255, 0.08);
  --text-main: #edf3ff;
  --text-soft: #93a4b8;
  --sidebar-bg: rgba(11, 20, 32, 0.96);
  --sidebar-active: rgba(28, 183, 212, 0.16);
  --sidebar-accent: #67dff2;
  --sidebar-soft: rgba(28, 183, 212, 0.14);
}

* {
  box-sizing: border-box;
}

html,
body,
#app {
  min-height: 100%;
}

body {
  margin: 0;
  background: var(--app-bg);
  color: var(--text-main);
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

a {
  color: inherit;
}

.app-shell {
  min-height: 100vh;
  background: var(--app-bg);
}

.sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 30;
  width: var(--sidebar-width);
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  background: var(--sidebar-bg);
  border-right: 1px solid var(--card-border);
  box-shadow: 0 18px 42px rgba(70, 95, 124, 0.12);
  backdrop-filter: blur(18px);
  transition: width 0.28s cubic-bezier(0.22, 1, 0.36, 1), transform 0.28s cubic-bezier(0.22, 1, 0.36, 1);
}

.sidebar-collapsed .sidebar {
  width: var(--sidebar-width-collapsed);
}

.sidebar-header,
.brand-wrap,
.side-nav-item,
.sidebar-tool,
.user-card,
.mobile-topbar {
  display: flex;
  align-items: center;
}

.sidebar-header {
  justify-content: space-between;
  gap: 10px;
  padding: 4px 4px 16px;
}

.brand-wrap {
  min-width: 0;
  gap: 12px;
  text-decoration: none;
}

.brand-mark,
.avatar-chip {
  width: 44px;
  height: 44px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  flex-shrink: 0;
  color: white;
  background: linear-gradient(135deg, #1cb7d4, #0ea7c3);
  box-shadow: 0 12px 24px rgba(25, 182, 210, 0.2);
}

.brand-copy {
  max-width: 140px;
  min-width: 0;
  display: grid;
  gap: 3px;
  overflow: hidden;
  transition: opacity 0.18s ease, transform 0.22s ease, max-width 0.22s ease;
}

.brand-copy strong,
.user-copy strong {
  color: var(--text-main);
  font-size: 15px;
  line-height: 1.15;
  white-space: nowrap;
}

.brand-copy small,
.user-copy small {
  color: var(--text-soft);
  font-size: 12px;
  white-space: nowrap;
}

.icon-btn,
.mobile-icon {
  width: 38px;
  height: 38px;
  border: 0;
  border-radius: 13px;
  display: grid;
  place-items: center;
  cursor: pointer;
  color: var(--text-soft);
  background: rgba(125, 151, 174, 0.12);
  transition: background 0.18s ease, color 0.18s ease, transform 0.18s ease;
}

.icon-btn:hover,
.mobile-icon:hover {
  color: var(--text-main);
  background: var(--sidebar-soft);
  transform: translateY(-1px);
}

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 6px 2px 16px;
}

.sidebar-nav::-webkit-scrollbar {
  width: 6px;
}

.sidebar-nav::-webkit-scrollbar-thumb {
  border-radius: 999px;
  background: rgba(125, 151, 174, 0.26);
}

.nav-group + .nav-group {
  margin-top: 12px;
}

.group-title {
  max-height: 24px;
  margin: 0 0 8px;
  padding: 0 14px;
  color: var(--text-soft);
  font-size: 12px;
  font-weight: 700;
  overflow: hidden;
  transition: opacity 0.18s ease, transform 0.22s ease, max-height 0.22s ease, margin 0.22s ease;
}

.side-nav-item {
  min-height: 54px;
  gap: 14px;
  margin: 6px 0;
  padding: 0 16px;
  border-radius: 18px;
  color: #607080;
  text-decoration: none;
  transition: background 0.18s ease, color 0.18s ease, transform 0.18s ease;
}

[data-theme='dark'] .side-nav-item {
  color: rgba(237, 243, 255, 0.8);
}

.side-nav-item:hover {
  background: var(--sidebar-soft);
  color: var(--text-main);
  transform: translateX(2px);
}

.side-nav-item.active {
  color: var(--text-main);
  font-weight: 700;
  background: linear-gradient(90deg, var(--sidebar-accent) 0 4px, var(--sidebar-active) 4px 100%);
}

.nav-icon {
  width: 22px;
  height: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.nav-text {
  max-width: 120px;
  min-width: 0;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  transition: opacity 0.18s ease, transform 0.22s ease, max-width 0.22s ease;
}

.nav-badge {
  max-width: 64px;
  margin-left: auto;
  padding: 4px 10px;
  border-radius: 999px;
  background: #ff5f5f;
  color: white;
  font-size: 12px;
  font-weight: 700;
  transition: opacity 0.18s ease, transform 0.22s ease, max-width 0.22s ease, margin 0.22s ease, padding 0.22s ease;
}

.sidebar-footer {
  padding-top: 12px;
  border-top: 1px solid var(--card-border);
}

.sidebar-tool {
  width: 100%;
  min-height: 42px;
  gap: 10px;
  padding: 0 12px;
  border: 1px solid var(--card-border);
  border-radius: 14px;
  cursor: pointer;
  color: var(--text-soft);
  background: rgba(125, 151, 174, 0.08);
  font-weight: 700;
  transition: width 0.24s ease, min-height 0.24s ease, padding 0.24s ease, border-radius 0.24s ease, background 0.18s ease, color 0.18s ease, transform 0.18s ease;
}

.sidebar-tool.compact {
  width: 44px;
  min-height: 44px;
  justify-content: center;
  padding: 0;
  margin-left: auto;
  margin-right: auto;
  border-radius: 14px;
}

.sidebar-collapse-btn {
  margin-top: 10px;
}

.tool-icon {
  width: 20px;
  height: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.user-card {
  min-height: 68px;
  max-height: 88px;
  gap: 12px;
  margin-top: 10px;
  padding: 12px;
  border-radius: 18px;
  border: 1px solid var(--card-border);
  background: var(--card-bg);
  overflow: hidden;
  transition: opacity 0.18s ease, transform 0.22s ease, max-height 0.22s ease, margin 0.22s ease, padding 0.22s ease, border-color 0.18s ease, background 0.18s ease;
}

.user-card.compact {
  width: 44px;
  min-height: 44px;
  justify-content: center;
  margin-top: 10px;
  padding: 0;
  border: 0;
  border-radius: 14px;
  background: transparent;
  box-shadow: none;
}

.user-copy {
  max-width: 120px;
  min-width: 0;
  flex: 1;
  display: grid;
  gap: 3px;
  overflow: hidden;
  transition: opacity 0.18s ease, transform 0.22s ease, max-width 0.22s ease;
}

.logout-btn {
  max-width: 60px;
  border: 0;
  border-radius: 12px;
  padding: 8px 12px;
  cursor: pointer;
  color: var(--text-soft);
  background: rgba(125, 151, 174, 0.12);
  transition: opacity 0.18s ease, transform 0.22s ease, max-width 0.22s ease, padding 0.22s ease;
}

.sidebar-collapsed .sidebar-header {
  justify-content: center;
}

.sidebar-collapsed .brand-wrap,
.sidebar-collapsed .side-nav-item {
  justify-content: center;
}

.sidebar-collapsed .side-nav-item {
  width: 44px;
  min-height: 44px;
  margin: 6px auto;
  padding: 0;
  border-radius: 14px;
}

.sidebar-collapsed .side-nav-item.active {
  background: var(--sidebar-active);
  box-shadow: inset 3px 0 0 var(--sidebar-accent);
}

.sidebar-collapsed .sidebar-nav {
  overflow-y: hidden;
}

.sidebar-collapsed .sidebar-nav::-webkit-scrollbar {
  width: 0;
}

.sidebar-collapsed .sidebar-footer {
  display: grid;
  gap: 10px;
  justify-items: center;
}

.sidebar-collapsed .user-card {
  opacity: 0;
  max-height: 0;
  margin-top: 0;
  padding-top: 0;
  padding-bottom: 0;
  border-color: transparent;
  background: transparent;
  transform: translateY(4px);
  pointer-events: none;
}

.sidebar-collapsed .brand-copy,
.sidebar-collapsed .group-title,
.sidebar-collapsed .nav-text,
.sidebar-collapsed .nav-badge,
.sidebar-collapsed .user-copy,
.sidebar-collapsed .logout-btn {
  opacity: 0;
  max-width: 0;
  max-height: 0;
  margin: 0;
  padding-left: 0;
  padding-right: 0;
  transform: translateX(-6px);
  pointer-events: none;
}

.app-frame {
  min-height: 100vh;
  margin-left: var(--sidebar-width);
  transition: margin-left 0.28s cubic-bezier(0.22, 1, 0.36, 1);
}

.sidebar-collapsed .app-frame {
  margin-left: var(--sidebar-width-collapsed);
}

.app-main {
  min-height: 100vh;
}

.sidebar-hidden .app-frame {
  min-height: 100vh;
  margin-left: 0;
}

.sidebar-hidden .app-main {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mobile-topbar {
  display: none;
}

.sidebar-overlay {
  position: fixed;
  inset: 0;
  z-index: 20;
  background: rgba(10, 17, 28, 0.5);
}

.toast-stack {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
}

.toast-item {
  min-width: 220px;
  max-width: 360px;
  margin-bottom: 10px;
  padding: 14px 16px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: white;
  box-shadow: 0 18px 36px rgba(19, 25, 43, 0.24);
}

.toast-item button {
  border: 0;
  background: transparent;
  color: inherit;
  font-size: 18px;
  cursor: pointer;
}

.toast-item.info {
  background: linear-gradient(135deg, #4a7dff, #6d8eff);
}

.toast-item.success {
  background: linear-gradient(135deg, #0ea56b, #20c997);
}

.toast-item.warning {
  background: linear-gradient(135deg, #f59e0b, #f97316);
}

.toast-item.error {
  background: linear-gradient(135deg, #ef4444, #fb7185);
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.2s ease;
}

.toast-enter,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (max-width: 960px) {
  .sidebar {
    width: 280px;
    transform: translateX(-100%);
  }

  .sidebar.open {
    transform: translateX(0);
  }

  .sidebar-collapsed .sidebar {
    width: 280px;
  }

  .app-frame,
  .sidebar-collapsed .app-frame {
    margin-left: 0;
  }

  .mobile-topbar {
    position: sticky;
    top: 0;
    z-index: 12;
    min-height: 56px;
    justify-content: space-between;
    gap: 12px;
    padding: 10px 14px;
    background: var(--sidebar-bg);
    border-bottom: 1px solid var(--card-border);
    backdrop-filter: blur(16px);
  }

  .mobile-brand {
    flex: 1;
    min-width: 0;
    display: grid;
    gap: 2px;
  }

  .mobile-brand strong {
    font-size: 15px;
    line-height: 1.15;
  }

  .mobile-brand span {
    color: var(--text-soft);
    font-size: 12px;
  }
}
</style>
