<template>
  <div class="auth-page">
    <div class="auth-panel">
      <section class="brand-side">
        <div class="brand-top">
          <div class="brand-logo">
            <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8 0c-.75 2.503-2.254 4.176-3.568 5.638C3.122 7.096 2 8.345 2 10a6 6 0 0 0 12 0c0-1.655-1.122-2.904-2.432-4.362C10.254 4.176 8.75 2.503 8 0Z"/>
            </svg>
          </div>
          <div>
            <strong>校园助手</strong>
            <span>Campus Agent</span>
          </div>
        </div>

        <div class="brand-main">
          <p class="eyebrow">CREATE ACCOUNT</p>
          <h1>注册后即可解锁完整校园服务体验</h1>
          <p class="intro">新账号可以直接使用首页概览、AI 对话、反馈、提醒以及校园服务入口。</p>

          <div class="feature-list">
            <div class="feature-row"><span class="feature-dot"></span><span>快速接入校园常用功能</span></div>
            <div class="feature-row"><span class="feature-dot"></span><span>后续支持记住账号与主题偏好</span></div>
            <div class="feature-row"><span class="feature-dot"></span><span>注册后自动跳转进入主界面</span></div>
          </div>
        </div>

        <div class="weather-box">
          <div class="weather-emoji">🎓</div>
          <div>
            <strong>准备开始</strong>
            <p>完成注册后即可进入校园助手</p>
          </div>
        </div>
      </section>

      <section class="form-side">
        <div class="form-card">
          <h2>创建账号</h2>
          <p class="form-subtitle">填写基本信息完成注册</p>

          <form class="auth-form" @submit.prevent="handleRegister">
            <label class="field">
              <span>学号</span>
              <div class="input-shell">
                <input
                  v-model.trim="username"
                  type="text"
                  placeholder="请输入学号"
                  autocomplete="username"
                />
              </div>
            </label>

            <label class="field">
              <span>昵称</span>
              <div class="input-shell">
                <input
                  v-model.trim="nickname"
                  type="text"
                  placeholder="请输入昵称（可选）"
                  autocomplete="nickname"
                />
              </div>
            </label>

            <label class="field">
              <span>密码</span>
              <div class="input-shell">
                <input
                  v-model.trim="password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="至少 6 位密码"
                  autocomplete="new-password"
                />
                <button type="button" class="ghost-btn" @click="showPassword = !showPassword">
                  {{ showPassword ? '隐藏' : '显示' }}
                </button>
              </div>
            </label>

            <label class="field">
              <span>确认密码</span>
              <div class="input-shell">
                <input
                  v-model.trim="confirmPassword"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="请再次输入密码"
                  autocomplete="new-password"
                />
              </div>
            </label>

            <p v-if="errorMsg" class="form-message error">{{ errorMsg }}</p>
            <p v-if="successMsg" class="form-message success">{{ successMsg }}</p>

            <button type="submit" class="submit-btn" :disabled="loading">
              <span v-if="loading" class="spinner"></span>
              <span v-else>立即注册</span>
            </button>
          </form>

          <p class="switch-text">
            已有账号？
            <router-link to="/login">立即登录</router-link>
          </p>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { saveAuthSession } from '../utils/auth'

export default {
  name: 'Register',
  data() {
    return {
      username: '',
      nickname: '',
      password: '',
      confirmPassword: '',
      showPassword: false,
      loading: false,
      errorMsg: '',
      successMsg: ''
    }
  },
  methods: {
    async handleRegister() {
      this.errorMsg = ''
      this.successMsg = ''

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

      if (this.password !== this.confirmPassword) {
        this.errorMsg = '两次输入的密码不一致'
        return
      }

      this.loading = true

      try {
        const res = await this.$axios.post('/user/register', null, {
          params: {
            username: this.username,
            password: this.password
          }
        })

        if (res.data.code !== 200 || !res.data.data) {
          this.errorMsg = res.data.message || '注册失败，请稍后重试'
          return
        }

        const { userId, username, nickname, role = 'user' } = res.data.data
        saveAuthSession({
          userId,
          username,
          nickname: this.nickname || nickname || username,
          role
        }, {
          persist: true
        })

        this.successMsg = '注册成功，正在进入校园助手...'

        window.setTimeout(() => {
          this.$router.push('/chat')
        }, 900)
      } catch (error) {
        this.errorMsg = error.response?.data?.message || '注册失败，请稍后重试'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(111, 145, 255, 0.18), transparent 28%),
    linear-gradient(180deg, #0c1220, #121827);
}

.auth-panel {
  width: min(1120px, 100%);
  min-height: 700px;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  border-radius: 32px;
  overflow: hidden;
  background: rgba(13, 19, 32, 0.84);
  box-shadow: 0 30px 90px rgba(0, 0, 0, 0.36);
  backdrop-filter: blur(20px);
}

.brand-side {
  padding: 40px;
  display: flex;
  flex-direction: column;
  color: white;
  background:
    radial-gradient(circle at top right, rgba(135, 102, 255, 0.3), transparent 34%),
    linear-gradient(180deg, rgba(25, 34, 58, 0.92), rgba(16, 23, 40, 0.98));
}

.brand-top {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-top span {
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
}

.brand-logo {
  width: 52px;
  height: 52px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #5f7dff, #9167ff);
}

.brand-main {
  margin: auto 0;
}

.eyebrow {
  margin: 0 0 14px;
  color: #8ea5ff;
  letter-spacing: 0.18em;
  font-size: 12px;
  font-weight: 700;
}

.brand-main h1 {
  margin: 0;
  font-size: clamp(34px, 4vw, 48px);
  line-height: 1.16;
}

.intro {
  margin: 18px 0 0;
  max-width: 520px;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.72);
}

.feature-list {
  margin-top: 28px;
  display: grid;
  gap: 14px;
}

.feature-row {
  display: flex;
  align-items: center;
  gap: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.feature-dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: linear-gradient(135deg, #54d38b, #4dd2ff);
  box-shadow: 0 0 0 6px rgba(84, 211, 139, 0.12);
}

.weather-box {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.08);
}

.weather-box p {
  margin: 4px 0 0;
  color: rgba(255, 255, 255, 0.64);
}

.weather-emoji {
  font-size: 34px;
}

.form-side {
  display: grid;
  place-items: center;
  padding: 40px 28px;
  background: rgba(249, 250, 255, 0.98);
}

.form-card {
  width: min(420px, 100%);
}

.form-card h2 {
  margin: 0;
  font-size: 30px;
  color: #18213a;
}

.form-subtitle {
  margin: 10px 0 0;
  color: #6d7895;
}

.auth-form {
  margin-top: 28px;
  display: grid;
  gap: 18px;
}

.field {
  display: grid;
  gap: 8px;
}

.field span {
  font-size: 14px;
  font-weight: 600;
  color: #38435f;
}

.input-shell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 56px;
  padding: 0 16px;
  border: 1px solid #d7deee;
  border-radius: 18px;
  background: white;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.input-shell:focus-within {
  border-color: #6b7cff;
  box-shadow: 0 0 0 4px rgba(107, 124, 255, 0.12);
}

.input-shell input {
  flex: 1;
  border: 0;
  outline: 0;
  font-size: 15px;
  color: #18213a;
  background: transparent;
}

.ghost-btn {
  border: 0;
  background: transparent;
  cursor: pointer;
  color: #5f7dff;
  font-weight: 600;
}

.form-message {
  margin: 0;
  padding: 12px 14px;
  border-radius: 14px;
  font-size: 14px;
}

.form-message.error {
  color: #b42318;
  background: #fef3f2;
}

.form-message.success {
  color: #0f766e;
  background: #ecfeff;
}

.submit-btn {
  min-height: 56px;
  border: 0;
  border-radius: 18px;
  cursor: pointer;
  color: white;
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(135deg, #5f7dff, #9167ff);
  box-shadow: 0 16px 34px rgba(95, 125, 255, 0.28);
}

.submit-btn:disabled {
  opacity: 0.72;
  cursor: not-allowed;
}

.spinner {
  width: 18px;
  height: 18px;
  display: inline-block;
  border: 2px solid rgba(255, 255, 255, 0.45);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.switch-text {
  margin: 22px 0 0;
  text-align: center;
  color: #6d7895;
}

.switch-text a {
  color: #5f7dff;
  font-weight: 700;
  text-decoration: none;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 980px) {
  .auth-panel {
    grid-template-columns: 1fr;
  }

  .brand-side {
    display: none;
  }
}

@media (max-width: 560px) {
  .auth-page {
    padding: 0;
  }

  .auth-panel {
    min-height: 100vh;
    border-radius: 0;
  }

  .form-side {
    padding: 28px 18px;
  }
}
</style>
