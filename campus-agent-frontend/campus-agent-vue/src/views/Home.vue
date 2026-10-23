<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-decor decor-one"></div>
      <div class="hero-decor decor-two"></div>
      <div class="hero-content">
        <div class="hero-copy">
          <span class="hero-badge">校园智能体平台</span>
          <h1>
            <span class="hero-greeting">{{ timeGreeting }}</span>
            <span class="hero-name">{{ username }}</span>
          </h1>
          <p class="hero-date">{{ currentDate }}</p>
          <p class="hero-desc">
            把校园里的高频事务、智能问答和服务入口集中到一个平台里，让查询、办理和获取信息更省步骤。
          </p>
        </div>

        <button class="weather-card" @click="showWeatherDetail">
          <div class="weather-main">
            <span class="weather-icon">{{ weatherData.icon }}</span>
            <div class="weather-copy">
              <strong>{{ weatherData.temp }}</strong>
              <span>{{ weatherData.hint }}</span>
            </div>
          </div>
          <p>{{ weatherData.tip }}</p>
        </button>
      </div>
    </section>

    <section class="home-main">
      <div class="section-head">
        <div>
          <h2>核心功能</h2>
          <p>点击卡片即可进入对应服务，优先展示项目已完成的核心能力。</p>
        </div>
      </div>

      <div class="feature-grid">
        <article
          v-for="item in functions"
          :key="item.path"
          class="feature-card"
          @click="goToPage(item.path)"
        >
          <div class="feature-icon" :style="{ background: `linear-gradient(135deg, ${item.colorStart}, ${item.colorEnd})` }">
            <span v-html="item.icon"></span>
          </div>
          <div class="feature-copy">
            <div class="feature-title-row">
              <h3>{{ item.title }}</h3>
              <span v-if="item.tag" class="feature-tag">{{ item.tag }}</span>
            </div>
            <p>{{ item.desc }}</p>
          </div>
          <span class="feature-arrow">→</span>
        </article>
      </div>

      <div class="section-head secondary-head">
        <div>
          <h2>常用服务</h2>
          <p>适合日常高频使用的快捷入口。</p>
        </div>
      </div>

      <div class="quick-grid">
        <button
          v-for="item in quickActions"
          :key="item.path"
          class="quick-card"
          @click="goToPage(item.path)"
        >
          <span class="quick-emoji" :style="{ background: item.bg }">{{ item.emoji }}</span>
          <span class="quick-label">{{ item.name }}</span>
        </button>
      </div>

      <div class="section-head secondary-head">
        <div>
          <h2>扩展设计</h2>
          <p>这是本项目预留的统一服务接入方向，用于展示平台的可扩展能力。</p>
        </div>
      </div>

      <div class="access-grid">
        <article
          v-for="item in accessCards"
          :key="item.path"
          class="access-card"
          @click="goToPage(item.path)"
        >
          <div class="access-top">
            <span class="access-icon">{{ item.icon }}</span>
            <span class="access-badge">{{ item.badge }}</span>
          </div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      username: this.$cookies.get('nickname') || this.$cookies.get('username') || '校园用户',
      currentDate: '',
      timeGreeting: '',
      weatherData: {
        icon: '☀️',
        temp: '26°C',
        hint: '晴',
        tip: '紫外线较强，外出记得做好防晒。'
      },
      functions: [
        {
          path: '/chat',
          title: 'AI 智能助手',
          desc: '支持校园问答、图文理解和多轮对话，是整个平台的统一智能入口。',
          colorStart: '#18b7d1',
          colorEnd: '#0f9fb8',
          tag: 'AI',
          icon: '<svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="currentColor" viewBox="0 0 24 24"><path d="M4 5.5A2.5 2.5 0 0 1 6.5 3h11A2.5 2.5 0 0 1 20 5.5v7a2.5 2.5 0 0 1-2.5 2.5H10L5.5 19v-4H6.5A2.5 2.5 0 0 1 4 12.5v-7Z"/><path d="M8 8h8v2H8zm0 4h5v2H8z"/></svg>'
        },
        {
          path: '/student-service',
          title: '学生服务',
          desc: '整合课表、请假、失物招领、兼职、快递等校园高频事务。',
          colorStart: '#ff9b4a',
          colorEnd: '#ff6f61',
          tag: '',
          icon: '<svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="currentColor" viewBox="0 0 24 24"><path d="m12 3 9 4.5-9 4.5L3 7.5 12 3Zm-7.5 8.2L12 15l7.5-3.8V16L12 20l-7.5-4V11.2Z"/></svg>'
        },
        {
          path: '/psych-consult',
          title: '心理陪伴',
          desc: '提供情绪陪伴与校园心理支持建议，扩展校园服务温度。',
          colorStart: '#ff7eb6',
          colorEnd: '#ff5e98',
          tag: '',
          icon: '<svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="currentColor" viewBox="0 0 24 24"><path d="M12 21s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 11c0 5.6-7 10-7 10Z"/></svg>'
        },
        {
          path: '/feedback',
          title: '问题反馈',
          desc: '收集校园报障、建议与问题反馈，形成服务优化闭环。',
          colorStart: '#3b82f6',
          colorEnd: '#1d4ed8',
          tag: '',
          icon: '<svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="currentColor" viewBox="0 0 24 24"><path d="M4 5.5A2.5 2.5 0 0 1 6.5 3h11A2.5 2.5 0 0 1 20 5.5v9a2.5 2.5 0 0 1-2.5 2.5H9L4 19v-4.5Zm4 1.5h8v2H8Zm0 4h8v2H8Z"/></svg>'
        }
      ],
      quickActions: [
        { path: '/schedule-list', name: '今日课表', emoji: '📚', bg: 'linear-gradient(135deg, #6b7cff, #8e64ff)' },
        { path: '/dining', name: '食堂推荐', emoji: '🍜', bg: 'linear-gradient(135deg, #ff9b4a, #ff6f61)' },
        { path: '/remind', name: '我的提醒', emoji: '⏰', bg: 'linear-gradient(135deg, #22c55e, #0ea5a4)' },
        { path: '/maintenance', name: '报修登记', emoji: '🛠', bg: 'linear-gradient(135deg, #3b82f6, #2563eb)' },
        { path: '/repair-phone', name: '服务热线', emoji: '☎️', bg: 'linear-gradient(135deg, #8b5cf6, #7c3aed)' },
        { path: '/lostfound', name: '失物招领', emoji: '🎒', bg: 'linear-gradient(135deg, #f472b6, #ec4899)' },
        { path: '/express', name: '快递代取', emoji: '📦', bg: 'linear-gradient(135deg, #06b6d4, #0891b2)' },
        { path: '/leave', name: '请假申请', emoji: '📝', bg: 'linear-gradient(135deg, #84cc16, #65a30d)' }
      ],
      accessCards: [
        {
          path: '/service-access',
          title: '统一服务接入',
          desc: '展示系统预留的统一服务接口层，可用于接入图书馆、体育馆、教务、一卡通等校园系统。',
          icon: '🔗',
          badge: '扩展设计'
        },
        {
          path: '/service-access?tab=library',
          title: '图书馆检索',
          desc: '轻量演示入口，说明后续可对接图书馆馆藏检索、电子书资源和借阅信息。',
          icon: '📖',
          badge: '演示入口'
        },
        {
          path: '/service-access?tab=venue',
          title: '场馆预约',
          desc: '轻量演示入口，说明后续可接入体育馆预约、时间段查询和预约状态回显。',
          icon: '🏟️',
          badge: '演示入口'
        }
      ]
    }
  },
  created() {
    this.updateDateTime()
    this.initWeather()
  },
  methods: {
    updateDateTime() {
      const now = new Date()
      const weekdays = ['日', '一', '二', '三', '四', '五', '六']

      this.currentDate = `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 星期${weekdays[now.getDay()]}`

      const hour = now.getHours()
      if (hour < 6) this.timeGreeting = '夜深了，'
      else if (hour < 9) this.timeGreeting = '早上好，'
      else if (hour < 12) this.timeGreeting = '上午好，'
      else if (hour < 14) this.timeGreeting = '中午好，'
      else if (hour < 18) this.timeGreeting = '下午好，'
      else if (hour < 22) this.timeGreeting = '晚上好，'
      else this.timeGreeting = '夜深了，'
    },
    initWeather() {
      const hour = new Date().getHours()

      if (hour >= 6 && hour < 12) {
        this.weatherData = {
          icon: '🌤️',
          temp: '18°C',
          hint: '多云转晴',
          tip: '上午气温舒适，适合上课或出行。'
        }
      } else if (hour >= 12 && hour < 18) {
        this.weatherData = {
          icon: '☀️',
          temp: '26°C',
          hint: '晴',
          tip: '午后偏热，外出请注意补水。'
        }
      } else if (hour >= 18 && hour < 22) {
        this.weatherData = {
          icon: '🌙',
          temp: '22°C',
          hint: '晴转多云',
          tip: '晚间体感舒适，适合散步或自习。'
        }
      } else {
        this.weatherData = {
          icon: '🌌',
          temp: '16°C',
          hint: '夜间微凉',
          tip: '昼夜温差较大，记得早点休息。'
        }
      }
    },
    showWeatherDetail() {
      this.$message.info(`${this.weatherData.hint} ${this.weatherData.temp}，${this.weatherData.tip}`)
    },
    goToPage(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  color: var(--text-main);
  border-radius: 28px;
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.72), rgba(255, 255, 255, 0.92)),
    var(--panel-bg);
  box-shadow: 0 28px 60px rgba(44, 76, 165, 0.08);
}

.hero-section {
  position: relative;
  overflow: hidden;
  padding: 18px 28px 12px;
  background: linear-gradient(135deg, rgba(24, 183, 209, 0.96), rgba(58, 113, 227, 0.94));
}

.hero-decor {
  position: absolute;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
}

.decor-one {
  width: 300px;
  height: 300px;
  right: -70px;
  top: -110px;
}

.decor-two {
  width: 180px;
  height: 180px;
  left: -48px;
  bottom: -80px;
}

.hero-content {
  position: relative;
  max-width: 100%;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 240px;
  gap: 14px;
  align-items: center;
}

.hero-copy {
  color: white;
  max-width: 520px;
  display: grid;
  align-content: center;
  justify-self: start;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  justify-self: start;
  padding: 5px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 8px 20px rgba(33, 43, 116, 0.12);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.05em;
}

.hero-copy h1 {
  margin: 12px 0 0;
  display: flex;
  align-items: baseline;
  flex-wrap: wrap;
  gap: 10px 14px;
  font-size: clamp(26px, 2.8vw, 38px);
  line-height: 1.12;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.hero-greeting {
  font-weight: 700;
}

.hero-name {
  font-weight: 800;
  color: rgba(255, 255, 255, 0.98);
}

.hero-date,
.hero-desc {
  margin: 0;
}

.hero-date {
  margin-top: 8px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.01em;
  color: rgba(255, 255, 255, 0.72);
}

.hero-desc {
  margin-top: 10px;
  max-width: 420px;
  font-size: 13px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.88);
}

.weather-card {
  width: 100%;
  max-width: 220px;
  justify-self: end;
  padding: 12px 14px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 18px;
  color: white;
  text-align: left;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(14px);
  transition: transform 0.2s ease, background 0.2s ease;
}

.weather-card:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.18);
}

.weather-main {
  display: flex;
  align-items: center;
  gap: 8px;
}

.weather-icon {
  font-size: 24px;
}

.weather-copy {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.weather-copy strong {
  font-size: 18px;
}

.weather-copy span,
.weather-card p {
  color: rgba(255, 255, 255, 0.8);
}

.weather-card p {
  margin: 8px 0 0;
  font-size: 11px;
  line-height: 1.45;
}

.home-main {
  max-width: 1040px;
  margin: 0 auto;
  padding: 20px 32px 28px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.section-head h2 {
  margin: 0;
  font-size: 20px;
  line-height: 1.2;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.section-head p {
  margin: 4px 0 0;
  font-size: 14px;
  line-height: 1.45;
  color: var(--text-soft);
}

.secondary-head {
  margin-top: 24px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  max-width: 920px;
  margin: 0 auto;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 92px;
  padding: 10px 12px;
  border-radius: 20px;
  border: 1px solid var(--card-border);
  background: var(--card-bg);
  box-shadow: 0 18px 40px rgba(59, 86, 173, 0.08);
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 24px 50px rgba(59, 86, 173, 0.14);
}

.feature-icon {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  color: white;
  flex-shrink: 0;
}

.feature-copy {
  flex: 1;
  min-width: 0;
}

.feature-title-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 4px;
}

.feature-title-row h3 {
  margin: 0;
  font-size: 15px;
}

.feature-tag {
  padding: 4px 8px;
  border-radius: 999px;
  background: linear-gradient(135deg, #18b7d1, #3a71e3);
  color: white;
  font-size: 11px;
  font-weight: 700;
}

.feature-copy p {
  margin: 0;
  font-size: 13px;
  line-height: 1.55;
  color: var(--text-soft);
}

.feature-arrow {
  font-size: 20px;
  color: #8ea0d5;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  max-width: 920px;
  margin: 0 auto;
}

.quick-card {
  min-height: 126px;
  padding: 16px 14px;
  border: 1px solid var(--card-border);
  border-radius: 20px;
  background: var(--card-bg);
  box-shadow: 0 14px 34px rgba(59, 86, 173, 0.06);
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
  text-align: left;
}

.quick-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(59, 86, 173, 0.12);
}

.quick-emoji {
  width: 44px;
  height: 44px;
  margin-bottom: 14px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  font-size: 20px;
  color: white;
}

.quick-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.access-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.access-card {
  padding: 18px;
  border-radius: 22px;
  border: 1px solid rgba(24, 183, 209, 0.14);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(245, 251, 255, 0.98));
  box-shadow: 0 18px 40px rgba(34, 72, 104, 0.08);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.access-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 24px 48px rgba(34, 72, 104, 0.12);
}

.access-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.access-icon {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  font-size: 22px;
  background: linear-gradient(135deg, #18b7d1, #3a71e3);
  color: #fff;
  box-shadow: 0 10px 24px rgba(24, 183, 209, 0.2);
}

.access-badge {
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(24, 183, 209, 0.12);
  color: #1188a4;
  font-size: 12px;
  font-weight: 700;
}

.access-card h3 {
  margin: 0 0 8px;
  font-size: 17px;
  color: var(--text-main);
}

.access-card p {
  margin: 0;
  color: var(--text-soft);
  font-size: 13px;
  line-height: 1.65;
}

@media (max-width: 960px) {
  .home-page {
    border-radius: 0;
    box-shadow: none;
  }

  .hero-section {
    padding: 24px 18px 20px;
  }

  .hero-content {
    grid-template-columns: 1fr;
  }

  .weather-card {
    max-width: none;
    justify-self: stretch;
  }

  .home-main {
    padding: 18px 18px 24px;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .quick-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .access-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .hero-copy h1 {
    font-size: 26px;
    gap: 6px 10px;
  }

  .hero-section {
    padding: 20px 16px 18px;
  }

  .home-main {
    padding: 16px 16px 20px;
  }

  .quick-grid {
    grid-template-columns: 1fr 1fr;
  }

  .feature-card {
    padding: 18px;
  }
}
</style>
