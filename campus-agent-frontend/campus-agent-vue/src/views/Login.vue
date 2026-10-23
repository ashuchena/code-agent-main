<template>
  <div class="login-page">
    <main class="login-shell">
      <section class="brand-panel">
        <div class="brand-heading">
          <div class="brand-mark">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8 0c-.75 2.503-2.254 4.176-3.568 5.638C3.122 7.096 2 8.345 2 10a6 6 0 0 0 12 0c0-1.655-1.122-2.904-2.432-4.362C10.254 4.176 8.75 2.503 8 0Z" />
            </svg>
          </div>
          <div>
            <strong>校园助手</strong>
            <p>Campus Agent</p>
          </div>
        </div>

        <div class="hero-copy">
          <span class="hero-pill">统一校园入口</span>
          <h1>登录后继续使用 AI 对话、提醒事项和校园服务。</h1>
          <p>一个入口串起课表、问答、反馈与常用服务，让校园生活更顺手，也让你的登录状态自动延续。</p>
        </div>

        <div class="feature-grid">
          <article v-for="item in features" :key="item.title" class="feature-card">
            <span class="feature-icon">{{ item.icon }}</span>
            <div class="feature-copy">
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
            </div>
          </article>
        </div>

        <div class="weather-card">
          <span class="weather-emoji">{{ weatherEmoji }}</span>
          <div class="weather-copy">
            <strong>{{ greetingText }}，欢迎回来</strong>
            <p>{{ weatherText }} · {{ temperature }}</p>
          </div>
          <span class="live-pill">LIVE</span>
        </div>
      </section>

      <section class="form-panel">
        <div class="form-card">
          <div class="form-header">
            <h2>账号登录</h2>
            <p>输入学号与密码，继续进入校园助手。</p>
          </div>

          <form class="login-form" @submit.prevent="handleLogin">
            <label class="field">
              <span>学号</span>
              <div class="input-wrap">
                <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" width="17" height="17" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6Zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0Zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4Z" />
                </svg>
                <input
                  v-model.trim="username"
                  type="text"
                  placeholder="请输入学号"
                  autocomplete="username"
                />
              </div>
            </label>

            <label class="field">
              <span>密码</span>
              <div class="input-wrap">
                <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" width="17" height="17" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2Zm3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2Z" />
                </svg>
                <input
                  v-model.trim="password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="请输入密码"
                  autocomplete="current-password"
                />
                <button
                  type="button"
                  class="icon-action"
                  :title="showPassword ? '隐藏密码' : '显示密码'"
                  @click="showPassword = !showPassword"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="17" height="17" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8Zm-8 4.5c-2.12 0-3.88-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8a13.134 13.134 0 0 1-1.66 2.043C11.879 11.332 10.119 12.5 8 12.5Z" />
                    <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5Z" />
                  </svg>
                </button>
              </div>
            </label>

            <div class="form-tools">
              <label class="remember-me">
                <input v-model="rememberMe" type="checkbox" />
                <span>记住我</span>
              </label>
              <button type="button" class="link-button" @click="handleForgot">忘记密码？</button>
            </div>

            <p v-if="errorMsg" class="error-message">{{ errorMsg }}</p>

            <button type="submit" class="submit-button" :disabled="loading">
              <span v-if="loading" class="spinner"></span>
              <span v-else>登录</span>
            </button>
          </form>

          <p class="register-text">
            还没有账号？
            <router-link to="/register">立即注册</router-link>
          </p>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
import { getAuthUserId, saveAuthSession } from '../utils/auth'

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      rememberMe: false,
      showPassword: false,
      loading: false,
      errorMsg: '',
      weatherEmoji: '☀️',
      weatherText: '天气加载中',
      temperature: '--',
      features: [
        { icon: '课', title: '智能课表', desc: '快速查看课程安排与提醒。' },
        { icon: '聊', title: '纯文本对话', desc: 'AI 回复清晰直接，阅读更轻松。' },
        { icon: '服', title: '校园服务', desc: '报修、反馈、公告入口集中管理。' },
        { icon: '稳', title: '状态持久化', desc: '登录状态与主题偏好自动恢复。' }
      ]
    }
  },
  computed: {
    greetingText() {
      const hour = new Date().getHours()
      if (hour < 6) return '夜深了'
      if (hour < 9) return '早上好'
      if (hour < 12) return '上午好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      return '晚上好'
    }
  },
  mounted() {
    const userId = getAuthUserId()
    if (userId) {
      this.$router.replace(this.$route.query.redirect || '/chat')
      return
    }

    const savedUsername = this.$cookies.get('rememberedUsername')
    if (savedUsername) {
      this.username = savedUsername
      this.rememberMe = true
    }

    this.fetchWeather()
  },
  methods: {
    async fetchWeather() {
      try {
        const res = await this.$axios.get('/weather/current')
        if (res.data.code === 200 && res.data.data) {
          const weather = res.data.data
          this.weatherEmoji = weather.icon || '☀️'
          this.weatherText = weather.text || '天气未知'
          this.temperature = weather.temp || '--'
        }
      } catch (error) {
        this.weatherText = '天气暂不可用'
      }
    },
    handleForgot() {
      this.$message.info('请联系管理员或辅导员重置密码。')
    },
    async handleLogin() {
      this.errorMsg = ''

      if (!this.username) {
        this.errorMsg = '请输入学号'
        return
      }

      if (!this.password) {
        this.errorMsg = '请输入密码'
        return
      }

      if (this.password.length < 6) {
        this.errorMsg = '密码至少需要 6 位'
        return
      }

      this.loading = true

      try {
        const res = await this.$axios.post('/user/login', null, {
          params: {
            username: this.username,
            password: this.password
          }
        })

        if (res.data.code !== 200 || !res.data.data) {
          this.errorMsg = res.data.message || '登录失败，请稍后重试'
          return
        }

        const { userId, username, nickname, role = 'user' } = res.data.data
        saveAuthSession({
          userId,
          username,
          nickname: nickname || username,
          role
        }, {
          persist: this.rememberMe
        })

        if (this.rememberMe) {
          this.$cookies.set('rememberedUsername', this.username, '30d', '/')
        } else {
          this.$cookies.remove('rememberedUsername', '/')
        }

        this.$router.replace(this.$route.query.redirect || '/chat')
      } catch (error) {
        this.errorMsg = error.response?.data?.message || '登录失败，请检查网络或账号密码'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  padding: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eef4fb;
}

.login-shell {
  width: min(1080px, calc(100vw - 64px));
  height: min(620px, calc(100vh - 64px));
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(340px, 0.85fr);
  overflow: hidden;
  border-radius: 28px;
  background: #ffffff;
  border: 1px solid rgba(219, 231, 241, 0.9);
  box-shadow: 0 18px 44px rgba(46, 73, 97, 0.1);
}

.brand-panel,
.form-panel {
  min-width: 0;
}

.brand-panel {
  padding: 34px 36px 32px;
  display: flex;
  flex-direction: column;
  gap: 22px;
  background: #ffffff;
}

.brand-heading {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-heading strong {
  display: block;
  font-size: 17px;
  color: var(--text-main);
}

.brand-heading p {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--text-soft);
}

.brand-mark {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  color: #fff;
  background: linear-gradient(135deg, #1eb6d2, #29a9d5);
  box-shadow: 0 10px 20px rgba(30, 182, 210, 0.2);
}

.hero-copy {
  max-width: 560px;
}

.hero-pill {
  display: inline-flex;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(28, 183, 212, 0.1);
  color: #1097b5;
  font-size: 12px;
  font-weight: 700;
}

.hero-copy h1 {
  margin: 14px 0 14px;
  font-size: 32px;
  line-height: 1.26;
  letter-spacing: -0.03em;
  color: var(--text-main);
}

.hero-copy p {
  margin: 0;
  color: var(--text-soft);
  font-size: 13px;
  line-height: 1.75;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.feature-card {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 16px;
  border-radius: 18px;
  background: #ffffff;
  border: 1px solid rgba(219, 231, 241, 0.92);
  box-shadow: 0 8px 18px rgba(83, 110, 132, 0.04);
}

.feature-icon {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-size: 15px;
  font-weight: 700;
  color: #169fbc;
  background: rgba(28, 183, 212, 0.1);
  flex-shrink: 0;
}

.feature-copy strong {
  display: block;
  color: var(--text-main);
  font-size: 14px;
}

.feature-copy p {
  margin: 6px 0 0;
  color: var(--text-soft);
  font-size: 12px;
  line-height: 1.65;
}

.weather-card {
  margin-top: auto;
  min-height: 78px;
  padding: 16px 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(28, 183, 212, 0.1), rgba(91, 132, 255, 0.08));
  border: 1px solid rgba(205, 228, 238, 0.92);
}

.weather-emoji {
  font-size: 26px;
  line-height: 1;
}

.weather-copy {
  flex: 1;
}

.weather-copy strong {
  display: block;
  color: var(--text-main);
  font-size: 14px;
}

.weather-copy p {
  margin: 6px 0 0;
  color: var(--text-soft);
  font-size: 12px;
}

.live-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 6px 10px;
  border-radius: 999px;
  background: #18b6d1;
  color: #fff;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.04em;
}

.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 34px 30px;
  background: #ffffff;
  border-left: 1px solid rgba(219, 231, 241, 0.88);
}

.form-card {
  width: min(400px, 100%);
  padding: 8px 4px;
}

.form-header h2 {
  margin: 0;
  font-size: 28px;
  line-height: 1.12;
  color: var(--text-main);
}

.form-header p {
  margin: 10px 0 0;
  color: var(--text-soft);
  font-size: 13px;
  line-height: 1.7;
}

.login-form {
  margin-top: 28px;
  display: grid;
  gap: 18px;
}

.field {
  display: grid;
  gap: 10px;
}

.field > span {
  color: #738696;
  font-size: 13px;
  font-weight: 700;
}

.input-wrap {
  min-height: 52px;
  padding: 0 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 16px;
  border: 1px solid rgba(213, 225, 235, 0.98);
  background: rgba(248, 251, 253, 0.98);
  color: #728392;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.input-wrap:focus-within {
  border-color: rgba(28, 183, 212, 0.56);
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 0 0 4px rgba(28, 183, 212, 0.08);
}

.input-icon {
  flex: 0 0 auto;
  color: #8596a3;
}

.input-wrap input {
  flex: 1;
  min-width: 0;
  padding: 0;
  border: 0;
  outline: 0;
  background: transparent;
  box-shadow: none;
  color: var(--text-main);
  font-size: 14px;
}

.input-wrap input::placeholder {
  color: #9aabb7;
}

.icon-action,
.link-button {
  border: 0;
  padding: 0;
  background: transparent;
  cursor: pointer;
}

.icon-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #7e909e;
}

.link-button {
  color: #169fbc;
  font-size: 13px;
  font-weight: 700;
}

.form-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.remember-me {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--text-soft);
  font-size: 13px;
}

.remember-me input {
  width: 16px;
  height: 16px;
  margin: 0;
  accent-color: #18b6d1;
}

.error-message {
  margin: 0;
  padding: 12px 14px;
  border-radius: 14px;
  color: #cc5b5b;
  background: rgba(255, 106, 106, 0.1);
  font-size: 13px;
}

.submit-button {
  min-height: 54px;
  border: 0;
  border-radius: 16px;
  color: #fff;
  cursor: pointer;
  font-size: 16px;
  font-weight: 800;
  background: linear-gradient(135deg, #1cb7d4, #0ea7c3);
  box-shadow: 0 14px 28px rgba(25, 182, 210, 0.16);
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}

.submit-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 20px 38px rgba(25, 182, 210, 0.24);
}

.submit-button:disabled {
  opacity: 0.72;
  cursor: not-allowed;
}

.spinner {
  width: 18px;
  height: 18px;
  display: inline-block;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.register-text {
  margin: 22px 0 0;
  text-align: center;
  color: var(--text-soft);
  font-size: 13px;
}

.register-text a {
  color: #169fbc;
  font-weight: 800;
  text-decoration: none;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

:global([data-theme='dark'] .login-shell) {
  background: rgba(13, 22, 36, 0.84);
  border-color: rgba(255, 255, 255, 0.08);
  box-shadow: 0 30px 76px rgba(0, 0, 0, 0.34);
}

:global([data-theme='dark'] .brand-panel) {
  background: linear-gradient(160deg, rgba(18, 34, 56, 0.76), rgba(13, 22, 36, 0.42));
}

:global([data-theme='dark'] .form-panel) {
  background: rgba(10, 17, 28, 0.82);
  border-left-color: rgba(255, 255, 255, 0.08);
}

:global([data-theme='dark'] .feature-card),
:global([data-theme='dark'] .weather-card) {
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(255, 255, 255, 0.08);
  box-shadow: none;
}

:global([data-theme='dark'] .hero-pill) {
  background: rgba(30, 182, 210, 0.16);
  color: #7ee0f4;
}

:global([data-theme='dark'] .feature-icon) {
  background: rgba(30, 182, 210, 0.14);
  color: #7ee0f4;
}

:global([data-theme='dark'] .input-wrap) {
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(255, 255, 255, 0.08);
  color: #8ca1b5;
}

:global([data-theme='dark'] .input-wrap:focus-within) {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(30, 182, 210, 0.38);
  box-shadow: 0 0 0 4px rgba(30, 182, 210, 0.12);
}

:global([data-theme='dark'] .input-wrap input::placeholder) {
  color: #6e7f91;
}

@media (max-width: 1100px) {
  .login-shell {
    width: min(980px, calc(100vw - 48px));
    grid-template-columns: minmax(0, 1fr) minmax(320px, 0.9fr);
  }

  .hero-copy h1 {
    font-size: 28px;
  }
}

@media (max-width: 960px) {
  .login-page {
    padding: 16px;
    height: auto;
    min-height: 100vh;
  }

  .login-shell {
    width: min(100%, 760px);
    height: auto;
    min-height: auto;
    border-radius: 24px;
    grid-template-columns: 1fr;
  }

  .brand-panel,
  .form-panel {
    padding: 28px 20px;
  }

  .brand-panel {
    gap: 24px;
  }

  .hero-copy h1 {
    font-size: 28px;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .form-card {
    width: 100%;
    padding: 8px 0 0;
  }

  .form-header h2 {
    font-size: 30px;
  }
}

@media (max-width: 600px) {
  .weather-card {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .live-pill {
    margin-left: 44px;
  }

  .form-tools {
    flex-wrap: wrap;
  }
}
</style>
