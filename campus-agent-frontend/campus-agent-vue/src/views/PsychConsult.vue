<template>
  <div class="psych-page">
    <!-- 背景装饰 -->
    <div class="bg-deco">
      <div class="deco-blob b1"></div>
      <div class="deco-blob b2"></div>
      <div class="deco-blob b3"></div>
    </div>

    <!-- 顶部栏 -->
    <div class="top-bar">
      <button class="back-btn" @click="$router.push('/home')">
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        返回
      </button>
      <div class="top-title">
        <div class="title-icon">💗</div>
        <span>AI 心理导师</span>
        <span class="new-badge">NEW</span>
      </div>
      <div style="width:70px"></div>
    </div>

    <!-- 主区域 -->
    <div class="main-wrap">

      <!-- ① 功能选项卡 -->
      <div class="tab-row">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-btn"
          :class="{ active: activeTab === tab.key }"
          @click="switchTab(tab.key)"
        >
          <span class="tab-emoji">{{ tab.emoji }}</span>
          <span class="tab-label">{{ tab.label }}</span>
        </button>
      </div>

      <!-- ━━━━━━━━━ 情绪识别 ━━━━━━━━━ -->
      <div v-if="activeTab === 'detect'" class="panel">
        <div class="panel-header pink">
          <span class="panel-emoji">🔍</span>
          <div>
            <h3>情绪识别</h3>
            <p>输入你现在的心情描述，AI 帮你识别情绪类型</p>
          </div>
        </div>
        <div class="panel-body">
          <textarea
            v-model="detectText"
            class="big-textarea"
            placeholder="描述你现在的感受，例如：最近总是很疲惫，对什么事情都提不起兴趣……"
            rows="4"
          ></textarea>
          <button class="action-btn pink-btn" :disabled="detectLoading" @click="runDetect">
            <span v-if="detectLoading" class="spin">⏳</span>
            <span v-else>🔍</span>
            {{ detectLoading ? '识别中…' : '开始识别' }}
          </button>

          <!-- 识别结果 -->
          <transition name="fade-up">
            <div v-if="detectResult" class="result-card">
              <div class="result-header">
                <span class="result-emoji">{{ detectResult.emoji }}</span>
                <div>
                  <div class="result-emotion">{{ detectResult.emotion }}</div>
                  <div class="result-level" :class="detectResult.levelClass">{{ detectResult.levelText }}</div>
                </div>
              </div>
              <div class="result-desc">{{ detectResult.desc }}</div>
              <div class="emotion-tags">
                <span v-for="tag in detectResult.tags" :key="tag" class="e-tag">{{ tag }}</span>
              </div>
            </div>
          </transition>
        </div>
      </div>

      <!-- ━━━━━━━━━ 智能开导 ━━━━━━━━━ -->
      <div v-if="activeTab === 'counsel'" class="panel">
        <div class="panel-header purple">
          <span class="panel-emoji">💬</span>
          <div>
            <h3>智能开导</h3>
            <p>倾诉你的烦恼，AI 给予温暖的支持与引导</p>
          </div>
        </div>
        <div class="panel-body">
          <!-- 聊天记录 -->
          <div class="chat-box" ref="chatBox">
            <div v-if="chatMessages.length === 0" class="chat-welcome">
              <div class="welcome-icon">🌸</div>
              <p>你好，我是你的 AI 心理导师。<br>有什么想倾诉的，都可以告诉我 💙</p>
            </div>
            <div
              v-for="(msg, i) in chatMessages"
              :key="i"
              class="chat-msg"
              :class="msg.role === 'user' ? 'msg-user' : 'msg-ai'"
            >
              <div v-if="msg.role === 'ai'" class="ai-avatar">💗</div>
              <div class="bubble" :class="msg.role === 'user' ? 'bubble-user' : 'bubble-ai'">
                <div class="bubble-text" v-html="msg.content"></div>
                <div class="bubble-time">{{ msg.time }}</div>
              </div>
            </div>
            <div v-if="counselLoading" class="chat-msg msg-ai">
              <div class="ai-avatar">💗</div>
              <div class="bubble bubble-ai typing">
                <span class="dot"></span><span class="dot"></span><span class="dot"></span>
              </div>
            </div>
          </div>
          <div class="chat-input-row">
            <textarea
              v-model="counselInput"
              class="chat-input"
              placeholder="输入你想说的话…"
              rows="2"
              @keydown.enter.exact.prevent="sendCounsel"
            ></textarea>
            <button class="send-btn" :disabled="counselLoading" @click="sendCounsel">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
                <path d="M15.964.686a.5.5 0 0 0-.65-.65L.767 5.855H.766l-.452.18a.5.5 0 0 0-.082.887l.41.26.001.002 4.995 3.178 3.178 4.995.002.002.26.41a.5.5 0 0 0 .886-.083l6-15Zm-1.833 1.89L6.637 10.07l-.215-.338a.5.5 0 0 0-.154-.154l-.338-.215 7.494-7.494 1.178-.471-.47 1.178Z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- ━━━━━━━━━ 情绪趋势分析 ━━━━━━━━━ -->
      <div v-if="activeTab === 'trend'" class="panel">
        <div class="panel-header teal">
          <span class="panel-emoji">📈</span>
          <div>
            <h3>情绪趋势分析</h3>
            <p>记录每日情绪，查看你的情绪变化趋势</p>
          </div>
        </div>
        <div class="panel-body">
          <!-- 今日打卡 -->
          <div class="checkin-section">
            <div class="checkin-title">今日情绪打卡</div>
            <div class="mood-selector">
              <button
                v-for="mood in moodOptions"
                :key="mood.value"
                class="mood-opt"
                :class="{ selected: todayMood === mood.value }"
                @click="todayMood = mood.value"
              >
                <span class="mood-emoji">{{ mood.emoji }}</span>
                <span class="mood-name">{{ mood.label }}</span>
              </button>
            </div>
            <div class="checkin-note-row">
              <input v-model="todayNote" class="checkin-note" placeholder="今天发生了什么？（可选）" />
              <button class="checkin-btn" @click="doCheckin">打卡</button>
            </div>
          </div>

          <!-- 趋势图 -->
          <div class="trend-chart-wrap">
            <div class="trend-title">近 7 天情绪趋势</div>
            <div class="trend-chart">
              <div class="chart-y-labels">
                <span>😄</span><span>🙂</span><span>😐</span><span>😟</span><span>😢</span>
              </div>
              <div class="chart-bars">
                <div
                  v-for="(d, i) in trendData"
                  :key="i"
                  class="chart-col"
                >
                  <div class="bar-wrap">
                    <div
                      class="bar"
                      :style="{ height: d.score * 20 + '%', background: moodColor(d.score) }"
                      :title="d.label + '：' + moodLabel(d.score)"
                    ></div>
                  </div>
                  <div class="bar-day">{{ d.label }}</div>
                  <div class="bar-emoji">{{ moodEmoji(d.score) }}</div>
                </div>
              </div>
            </div>
            <div class="trend-summary">
              <div class="ts-item">
                <span class="ts-val">{{ trendAvg }}</span>
                <span class="ts-key">平均情绪</span>
              </div>
              <div class="ts-item">
                <span class="ts-val">{{ trendBest }}</span>
                <span class="ts-key">最佳状态</span>
              </div>
              <div class="ts-item">
                <span class="ts-val">{{ checkinDays }}</span>
                <span class="ts-key">打卡天数</span>
              </div>
            </div>
          </div>

          <!-- 历史记录 -->
          <div class="history-section">
            <div class="history-title">情绪日记</div>
            <div v-if="trendHistory.length === 0" class="empty-hint">暂无记录，快去打卡吧～</div>
            <div v-else class="history-list">
              <div v-for="(rec, i) in trendHistory" :key="i" class="history-item">
                <div class="hi-left">
                  <span class="hi-emoji">{{ moodEmoji(rec.score) }}</span>
                  <div>
                    <div class="hi-date">{{ rec.date }}</div>
                    <div class="hi-note">{{ rec.note || '没有备注' }}</div>
                  </div>
                </div>
                <div class="hi-label" :style="{ color: moodColor(rec.score) }">{{ moodLabel(rec.score) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ━━━━━━━━━ 情绪检测 ━━━━━━━━━ -->
      <div v-if="activeTab === 'test'" class="panel">
        <div class="panel-header orange">
          <span class="panel-emoji">🧪</span>
          <div>
            <h3>情绪检测</h3>
            <p>回答几个问题，全面了解你的心理状态</p>
          </div>
        </div>
        <div class="panel-body">
          <!-- 未开始 -->
          <div v-if="testPhase === 'idle'" class="test-intro">
            <div class="intro-icon">🧠</div>
            <h4>心理状态自测</h4>
            <p>本测试共 {{ testQuestions.length }} 题，约需 2 分钟<br>请根据<strong>最近一周</strong>的状态如实作答</p>
            <button class="action-btn orange-btn" @click="startTest">开始测试</button>
          </div>

          <!-- 答题中 -->
          <div v-if="testPhase === 'answering'" class="test-answering">
            <div class="test-progress">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: testProgress + '%' }"></div>
              </div>
              <span class="progress-text">{{ testCurrent + 1 }} / {{ testQuestions.length }}</span>
            </div>
            <div class="question-card">
              <div class="q-num">Q{{ testCurrent + 1 }}</div>
              <div class="q-text">{{ testQuestions[testCurrent].q }}</div>
              <div class="q-options">
                <button
                  v-for="opt in testOptions"
                  :key="opt.value"
                  class="q-opt"
                  :class="{ selected: testAnswers[testCurrent] === opt.value }"
                  @click="selectAnswer(opt.value)"
                >
                  {{ opt.label }}
                </button>
              </div>
            </div>
            <div class="test-nav">
              <button class="nav-btn" :disabled="testCurrent === 0" @click="testCurrent--">上一题</button>
              <button
                v-if="testCurrent < testQuestions.length - 1"
                class="nav-btn next"
                :disabled="testAnswers[testCurrent] === undefined"
                @click="testCurrent++"
              >下一题</button>
              <button
                v-else
                class="nav-btn submit"
                :disabled="!testComplete"
                @click="submitTest"
              >提交</button>
            </div>
          </div>

          <!-- 结果 -->
          <div v-if="testPhase === 'result'" class="test-result">
            <div class="result-score-ring" :style="{ '--c': testResultColor }">
              <div class="ring-inner">
                <div class="ring-score">{{ testScore }}</div>
                <div class="ring-max">/ {{ testMaxScore }}</div>
              </div>
            </div>
            <div class="result-level-tag" :style="{ background: testResultColor }">{{ testResultLevel }}</div>
            <p class="result-summary">{{ testResultDesc }}</p>
            <div class="result-suggestions">
              <div class="sug-title">💡 建议</div>
              <div v-for="(s, i) in testResultSuggestions" :key="i" class="sug-item">
                <span class="sug-dot">•</span> {{ s }}
              </div>
            </div>
            <button class="action-btn" style="margin-top:20px" @click="resetTest">重新测试</button>
          </div>
        </div>
      </div>

      <!-- ━━━━━━━━━ 情绪预警 ━━━━━━━━━ -->
      <div v-if="activeTab === 'alert'" class="panel">
        <div class="panel-header orange">
          <span class="panel-emoji">⚠️</span>
          <div>
            <h3>情绪预警</h3>
            <p>AI 智能助手检测到的负面情绪记录</p>
          </div>
          <button class="refresh-btn" @click="fetchAlerts" :class="{ spinning: alertLoading }">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 24 24">
              <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
            </svg>
          </button>
        </div>
        <div class="panel-body">
          <!-- 加载状态 -->
          <div v-if="alertLoading" class="empty-tip">加载中…</div>

          <!-- 空状态 -->
          <div v-else-if="!emotionAlerts.length" class="empty-tip">
            <div class="empty-icon">🌿</div>
            <div>暂无情绪预警记录</div>
            <div class="empty-sub">在 AI 智能助手聊天时，若检测到负面情绪<br>会自动记录到这里</div>
          </div>

          <!-- 预警列表 -->
          <div v-else class="alert-list">
            <div v-for="alert in emotionAlerts" :key="alert.id" class="alert-card" :class="alertLevelClass(alert.emotionLevel)">
              <div class="alert-header">
                <span class="alert-emoji">{{ emotionEmoji(alert.emotion) }}</span>
                <div class="alert-info">
                  <div class="alert-emotion">{{ alert.emotion }}</div>
                  <div class="alert-time">{{ formatTime(alert.createTime) }}</div>
                </div>
                <div class="alert-level-badge" :class="alertLevelClass(alert.emotionLevel)">
                  {{ alertLevelText(alert.emotionLevel) }}
                </div>
              </div>
              <div class="alert-content">"{{ alert.content }}"</div>
              <div v-if="alert.emotionTags" class="alert-tags">
                <span
                  v-for="tag in parseTags(alert.emotionTags)"
                  :key="tag"
                  class="e-tag"
                >{{ tag }}</span>
              </div>
              <div v-if="alert.aiReply" class="alert-reply">
                <span class="reply-label">💬 AI 回复：</span>{{ alert.aiReply }}
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- 打卡成功提示 -->
    <transition name="toast">
      <div v-if="toastMsg" class="toast">{{ toastMsg }}</div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'PsychConsult',
  data() {
    return {
      activeTab: 'detect',
      tabs: [
        { key: 'detect',  emoji: '🔍', label: '情绪识别' },
        { key: 'counsel', emoji: '💬', label: '智能开导' },
        { key: 'trend',   emoji: '📈', label: '情绪趋势' },
        { key: 'test',    emoji: '🧪', label: '情绪检测' },
        { key: 'alert',   emoji: '⚠️', label: '情绪预警' }
      ],

      /* 情绪识别 */
      detectText: '',
      detectLoading: false,
      detectResult: null,

      /* 智能开导 */
      chatMessages: [],
      counselInput: '',
      counselLoading: false,

      /* 情绪趋势 */
      todayMood: null,
      todayNote: '',
      moodOptions: [
        { value: 5, emoji: '😄', label: '很好' },
        { value: 4, emoji: '🙂', label: '还好' },
        { value: 3, emoji: '😐', label: '一般' },
        { value: 2, emoji: '😟', label: '不好' },
        { value: 1, emoji: '😢', label: '很差' }
      ],
      trendHistory: [],

      /* 情绪检测 */
      testPhase: 'idle',
      testCurrent: 0,
      testAnswers: [],
      testScore: 0,
      testMaxScore: 0,
      testResultLevel: '',
      testResultColor: '',
      testResultDesc: '',
      testResultSuggestions: [],
      testQuestions: [
        { q: '你是否经常感到情绪低落，对以前喜欢的事情失去兴趣？', weight: 3 },
        { q: '你是否容易感到焦虑或担忧，难以放松？', weight: 3 },
        { q: '你的睡眠质量如何？是否有入睡困难或早醒问题？', weight: 2 },
        { q: '你是否感到精力不足，容易疲惫？', weight: 2 },
        { q: '你是否在人际交往中感到困难，想要回避社交？', weight: 2 },
        { q: '你是否对未来感到迷茫或没有目标感？', weight: 2 },
        { q: '你是否有注意力不集中、记忆力下降的情况？', weight: 2 },
        { q: '你是否能够有效地处理日常压力？', weight: 2, reverse: true },
        { q: '你是否有规律的生活习惯（饮食、运动、睡眠）？', weight: 1, reverse: true },
        { q: '你是否有可以倾诉心事的好友或亲人？', weight: 1, reverse: true }
      ],
      testOptions: [
        { value: 0, label: '从不' },
        { value: 1, label: '偶尔' },
        { value: 2, label: '有时' },
        { value: 3, label: '经常' },
        { value: 4, label: '总是' }
      ],

      toastMsg: '',

      /* 情绪预警 */
      emotionAlerts: [],
      alertLoading: false
    }
  },
  computed: {
    trendData() {
      const days = ['周一','周二','周三','周四','周五','周六','周日']
      const today = new Date().getDay()
      const result = []
      for (let i = 6; i >= 0; i--) {
        const dayIdx = (today - i + 7) % 7
        const label = i === 0 ? '今天' : days[dayIdx === 0 ? 6 : dayIdx - 1]
        const rec = this.trendHistory.find(r => r.dayOffset === i)
        result.push({ label, score: rec ? rec.score : 0 })
      }
      return result
    },
    trendAvg() {
      const valid = this.trendHistory.filter(r => r.score > 0)
      if (!valid.length) return '--'
      const avg = valid.reduce((s, r) => s + r.score, 0) / valid.length
      return this.moodEmoji(Math.round(avg))
    },
    trendBest() {
      if (!this.trendHistory.length) return '--'
      const best = Math.max(...this.trendHistory.map(r => r.score))
      return this.moodEmoji(best)
    },
    checkinDays() {
      return this.trendHistory.length + ' 天'
    },
    testProgress() {
      return ((this.testCurrent + 1) / this.testQuestions.length) * 100
    },
    testComplete() {
      return this.testAnswers.filter(a => a !== undefined).length === this.testQuestions.length
    }
  },
  mounted() {
    this.loadTrendHistory()
  },
  methods: {
    switchTab(key) {
      this.activeTab = key
      if (key === 'alert') {
        this.fetchAlerts()
      }
    },

    /* ── 情绪预警 ── */
    async fetchAlerts() {
      const userId = this.$cookies.get('userId')
      if (!userId) return
      this.alertLoading = true
      try {
        const res = await fetch(`/emotion/alerts?userId=${userId}`)
        const json = await res.json()
        if (json.code == 200 && json.data) {
          this.emotionAlerts = json.data
        }
      } catch (e) {
        console.error('加载情绪预警失败', e)
      } finally {
        this.alertLoading = false
      }
    },

    emotionEmoji(emotion) {
      const map = {
        '焦虑': '😰', '压力大': '😫', '悲伤': '😢', '孤独': '😔',
        '愤怒': '😠', '倦怠': '😩', '迷茫': '🌫️', '失眠': '🌙',
        '自卑': '😞', '失落': '🌀'
      }
      return map[emotion] || '💭'
    },

    alertLevelClass(level) {
      if (level >= 3) return 'level-high'
      if (level >= 2) return 'level-mid'
      return 'level-low'
    },

    alertLevelText(level) {
      if (level >= 3) return '⚠️ 严重'
      if (level >= 2) return '⚡ 中等'
      return '○ 轻微'
    },

    parseTags(tags) {
      if (!tags) return []
      try { return JSON.parse(tags) } catch { return [] }
    },

    formatTime(timeStr) {
      if (!timeStr) return ''
      const d = new Date(timeStr)
      return d.getFullYear() + '年' + (d.getMonth() + 1) + '月' + d.getDate() + '日'
    },

    /* ── 情绪识别 ── */
    async runDetect() {
      if (!this.detectText.trim()) return
      this.detectLoading = true
      this.detectResult = null
      // 模拟 AI 分析（实际可接后端接口）
      await this.sleep(1200)
      this.detectResult = this.analyzeEmotion(this.detectText)
      this.detectLoading = false
    },
    analyzeEmotion(text) {
      const t = text.toLowerCase()
      const keywords = {
        happy:   ['开心','高兴','快乐','喜悦','兴奋','棒','好玩','幸福','满足','愉快'],
        sad:     ['难过','伤心','悲伤','哭','失落','绝望','痛苦','委屈','失望'],
        anxious: ['焦虑','担心','担忧','紧张','害怕','恐惧','慌','不安','压力'],
        angry:   ['生气','愤怒','火大','烦','恼火','讨厌','气死'],
        tired:   ['累','疲惫','疲倦','精力不足','没动力','无聊','提不起兴趣'],
        lonely:  ['孤独','寂寞','孤单','没人','独自']
      }
      const scores = {}
      for (const [type, words] of Object.entries(keywords)) {
        scores[type] = words.filter(w => t.includes(w)).length
      }
      const top = Object.entries(scores).sort((a, b) => b[1] - a[1])[0]
      const maps = {
        happy:   { emotion: '积极情绪 · 愉悦', emoji: '😄', levelClass: 'lv-good', levelText: '状态良好', desc: '你目前状态不错，保持这种积极的心态，继续享受生活中的美好吧！', tags: ['愉悦','满足','积极'] },
        sad:     { emotion: '负面情绪 · 悲伤', emoji: '😢', levelClass: 'lv-low',  levelText: '需要关注', desc: '感受到了你的情绪低落，这很正常。试着找个信任的人倾诉，或者做些让自己开心的事情。', tags: ['悲伤','低落','需关注'] },
        anxious: { emotion: '负面情绪 · 焦虑', emoji: '😰', levelClass: 'lv-warn', levelText: '适度警惕', desc: '焦虑是一种很常见的情绪，深呼吸、规律作息可以帮助缓解。如果持续焦虑，建议寻求专业帮助。', tags: ['焦虑','紧张','压力'] },
        angry:   { emotion: '负面情绪 · 愤怒', emoji: '😤', levelClass: 'lv-warn', levelText: '需要疏导', desc: '愤怒是正常的情绪反应，找到合适的方式发泄，比如运动、写日记，有助于平复心情。', tags: ['愤怒','烦躁','需疏导'] },
        tired:   { emotion: '负面情绪 · 倦怠', emoji: '😔', levelClass: 'lv-low',  levelText: '需要休息', desc: '你可能处于疲倦或倦怠状态，适当休息、减少不必要的任务，照顾好自己的身体是第一位的。', tags: ['倦怠','疲惫','需休息'] },
        lonely:  { emotion: '负面情绪 · 孤独', emoji: '🥺', levelClass: 'lv-low',  levelText: '需要陪伴', desc: '孤独的感受有时候比什么都难受。试着主动联系一个朋友，哪怕只是聊聊天，也能让心里暖很多。', tags: ['孤独','寂寞','需陪伴'] }
      }
      return (top[1] > 0 ? maps[top[0]] : null) || {
        emotion: '情绪平稳 · 中性',
        emoji: '😌',
        levelClass: 'lv-neutral',
        levelText: '状态平稳',
        desc: '你的情绪描述比较中性，整体状态平稳。保持觉察，关注自己的内心感受是很好的习惯。',
        tags: ['平稳','中性','自我觉察']
      }
    },

    /* ── 智能开导 ── */
    async sendCounsel() {
      const text = this.counselInput.trim()
      if (!text || this.counselLoading) return
      const now = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      this.chatMessages.push({ role: 'user', content: text, time: now })
      this.counselInput = ''
      this.counselLoading = true
      await this.$nextTick()
      this.scrollChat()
      // 模拟AI回复（实际可接后端 /agent/chat 接口）
      await this.sleep(1400)
      const reply = this.genCounselReply(text)
      this.counselLoading = false
      const nowR = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      this.chatMessages.push({ role: 'ai', content: reply, time: nowR })
      await this.$nextTick()
      this.scrollChat()
    },
    genCounselReply(text) {
      const t = text.toLowerCase()
      const pool = [
        { kw: ['压力','累','疲惫','忙'], reply: '听起来你最近承受了很多，我能感受到你的疲惫。🌿<br><br>压力是成长的一部分，但别忘了给自己一些喘息的空间。试着今晚做一件让自己放松的事，哪怕只是泡个澡或者听一首喜欢的歌，好不好？' },
        { kw: ['失眠','睡不着','睡眠'], reply: '睡眠不好真的很难受，整个人都会变得很敏感。😴<br><br>睡前可以试试这个方法：关掉手机，做5次深呼吸，让每次呼气都长一些。如果脑子里有很多思绪，试着把它们写下来，清空大脑再去睡。' },
        { kw: ['难过','伤心','哭','悲伤','失落'], reply: '感谢你愿意告诉我。💙<br><br>难过的时候，不需要强迫自己"振作"，先允许自己好好感受这种情绪。眼泪是没关系的，它是你在照顾自己内心的方式。<br><br>你愿意和我分享更多吗？' },
        { kw: ['焦虑','担心','害怕','紧张'], reply: '焦虑的感觉真的很不好受，就像心上压着一块大石头。😮‍💨<br><br>试试这个简单的方法：先深吸一口气（4秒），屏住（7秒），慢慢呼出（8秒）。重复3次，你会发现身体会慢慢放松下来。<br><br>你担心的具体是什么呢？' },
        { kw: ['孤独','寂寞','没朋友','没人'], reply: '孤独的感觉有时候比什么都难。🫂<br><br>你知道吗，很多人都有和你一样的感受，只是大家都不太说出口。我在这里陪着你。<br><br>有没有什么以前喜欢做的事情，最近没有再做了？有时候重拾一个小爱好，能帮我们找到与世界的连接。' },
        { kw: ['开心','高兴','快乐','好消息'], reply: '太棒了！听到你开心我也感到很温暖 🌟<br><br>能和我分享一下是什么让你这么开心吗？好的情绪值得被好好记录下来，它会在你低落的时候给你力量。' }
      ]
      for (const item of pool) {
        if (item.kw.some(k => t.includes(k))) return item.reply
      }
      const defaults = [
        '谢谢你愿意和我分享 💙 我在认真倾听你说的每一句话。<br><br>能多告诉我一些吗？比如这种感受是最近才有的，还是已经持续了一段时间了？',
        '我理解你现在的感受。有时候把内心的困惑说出来，本身就已经是一种释放了。🌿<br><br>你觉得是什么最让你困扰呢？',
        '每个人的感受都是真实且有意义的，你的感受值得被重视。🌸<br><br>你愿意多说一说吗？我会一直在这里陪你。'
      ]
      return defaults[Math.floor(Math.random() * defaults.length)]
    },
    scrollChat() {
      const box = this.$refs.chatBox
      if (box) box.scrollTop = box.scrollHeight
    },

    /* ── 情绪趋势 ── */
    loadTrendHistory() {
      try {
        const raw = localStorage.getItem('psych-trend-' + (this.$cookies.get('userId') || 'guest'))
        if (raw) this.trendHistory = JSON.parse(raw)
      } catch (e) { /* ignore */ }
    },
    saveTrendHistory() {
      localStorage.setItem('psych-trend-' + (this.$cookies.get('userId') || 'guest'), JSON.stringify(this.trendHistory))
    },
    doCheckin() {
      if (!this.todayMood) { this.showToast('请先选择今天的情绪 😊'); return }
      const today = new Date()
      const dateStr = `${today.getMonth() + 1}月${today.getDate()}日`
      // 检查今天是否已打卡
      const exists = this.trendHistory.find(r => r.dayOffset === 0)
      if (exists) {
        exists.score = this.todayMood
        exists.note = this.todayNote
      } else {
        // 把旧记录 dayOffset +1
        this.trendHistory = this.trendHistory
          .map(r => ({ ...r, dayOffset: r.dayOffset + 1 }))
          .filter(r => r.dayOffset <= 30)
        this.trendHistory.unshift({ dayOffset: 0, score: this.todayMood, note: this.todayNote, date: dateStr })
      }
      this.saveTrendHistory()
      this.todayMood = null
      this.todayNote = ''
      this.showToast('打卡成功！今日情绪已记录 ✅')
    },
    moodEmoji(score) {
      return ['', '😢', '😟', '😐', '🙂', '😄'][score] || '⬜'
    },
    moodLabel(score) {
      return ['', '很差', '不好', '一般', '还好', '很好'][score] || '--'
    },
    moodColor(score) {
      return ['', '#f87171', '#fb923c', '#facc15', '#4ade80', '#34d399'][score] || '#e5e7eb'
    },

    /* ── 情绪检测 ── */
    startTest() {
      this.testCurrent = 0
      this.testAnswers = new Array(this.testQuestions.length).fill(undefined)
      this.testPhase = 'answering'
    },
    selectAnswer(value) {
      this.$set(this.testAnswers, this.testCurrent, value)
      // 自动前进
      if (this.testCurrent < this.testQuestions.length - 1) {
        setTimeout(() => { this.testCurrent++ }, 300)
      }
    },
    submitTest() {
      let score = 0
      let max = 0
      this.testQuestions.forEach((q, i) => {
        const ans = this.testAnswers[i] ?? 0
        const weight = q.weight || 1
        if (q.reverse) {
          score += (4 - ans) * weight
        } else {
          score += ans * weight
        }
        max += 4 * weight
      })
      this.testScore = score
      this.testMaxScore = max
      const ratio = score / max
      if (ratio <= 0.25) {
        this.testResultLevel = '心理状态良好'
        this.testResultColor = '#34d399'
        this.testResultDesc = '你的心理状态非常健康！你有很强的情绪调节能力和积极的生活态度，继续保持！'
        this.testResultSuggestions = ['保持规律的运动和睡眠习惯', '珍惜和亲友的连接', '定期做情绪自查，关注心理健康']
      } else if (ratio <= 0.5) {
        this.testResultLevel = '轻度困扰'
        this.testResultColor = '#facc15'
        this.testResultDesc = '你目前有一些轻度的心理困扰，这很正常。通过自我调节和积极的生活方式，大多数人都能有效改善。'
        this.testResultSuggestions = ['尝试正念冥想或深呼吸练习', '保证充足的睡眠（7-8小时）', '适当运动，每周至少3次', '与信任的朋友多交流']
      } else if (ratio <= 0.75) {
        this.testResultLevel = '中度压力'
        this.testResultColor = '#fb923c'
        this.testResultDesc = '你正在承受较大的心理压力，建议积极寻求支持和帮助，不要独自扛着这一切。'
        this.testResultSuggestions = ['主动寻求心理咨询师或辅导员的帮助', '减少不必要的压力源', '建立规律的作息和放松习惯', '学习认知重构，挑战负面思维']
      } else {
        this.testResultLevel = '需要关注'
        this.testResultColor = '#f87171'
        this.testResultDesc = '你的心理状态需要关注，强烈建议你寻求专业的心理援助。记住，寻求帮助是勇气的表现，不是软弱。'
        this.testResultSuggestions = ['请尽快联系学校心理健康中心', '向辅导员或老师倾诉你的困境', '危机时可拨打心理援助热线：400-161-9995', '告知你信任的家人或朋友']
      }
      this.testPhase = 'result'
    },
    resetTest() {
      this.testPhase = 'idle'
      this.testAnswers = []
      this.testCurrent = 0
    },

    /* ── 工具 ── */
    sleep(ms) {
      return new Promise(r => setTimeout(r, ms))
    },
    showToast(msg) {
      this.toastMsg = msg
      setTimeout(() => { this.toastMsg = '' }, 2500)
    }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }

.psych-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fdf2f8 0%, #fce7f3 40%, #ede9fe 100%);
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰 */
.bg-deco { position: fixed; inset: 0; pointer-events: none; z-index: 0; }
.deco-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.35;
  animation: blobFloat 18s ease-in-out infinite;
}
.b1 { width: 380px; height: 380px; background: #f9a8d4; top: -120px; left: -100px; }
.b2 { width: 300px; height: 300px; background: #c4b5fd; bottom: -80px; right: -80px; animation-delay: -6s; }
.b3 { width: 200px; height: 200px; background: #93c5fd; top: 40%; left: 60%; animation-delay: -12s; }
@keyframes blobFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(15px, -20px) scale(1.06); }
}

/* 顶部栏 */
.top-bar {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px 8px;
}
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 8px 16px;
  background: white;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: #7c3aed;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(124,58,237,0.15);
  transition: all 0.25s;
}
.back-btn:hover { transform: translateX(-3px); box-shadow: 0 4px 14px rgba(124,58,237,0.25); }
.top-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 800;
  color: #5b21b6;
}
.title-icon { font-size: 22px; }
.new-badge {
  font-size: 10px;
  font-weight: 700;
  background: linear-gradient(135deg, #f472b6, #ec4899);
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
}

/* 主区域 */
.main-wrap {
  position: relative;
  z-index: 1;
  max-width: 680px;
  margin: 0 auto;
  padding: 0 16px 60px;
}

/* 标签行 */
.tab-row {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin: 12px 0 20px;
  overflow-x: auto;
  padding-bottom: 4px;
  -webkit-overflow-scrolling: touch;
}
.tab-row::-webkit-scrollbar { display: none; }
.tab-btn {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 9px 16px;
  border: 2px solid transparent;
  border-radius: 24px;
  background: white;
  color: #6b7280;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.tab-btn:hover { border-color: #e879f9; color: #7c3aed; }
.tab-btn.active {
  background: linear-gradient(135deg, #f472b6, #c084fc);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(192,132,252,0.35);
}
.tab-emoji { font-size: 16px; }

/* 面板 */
.panel {
  background: white;
  border-radius: 22px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(124,58,237,0.08);
  animation: panelIn 0.3s ease-out;
}
@keyframes panelIn {
  from { opacity: 0; transform: translateY(12px); }
  to   { opacity: 1; transform: translateY(0); }
}
.panel-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 24px;
  color: white;
}
.panel-header.pink   { background: linear-gradient(135deg, #f472b6, #ec4899); }
.panel-header.purple { background: linear-gradient(135deg, #a78bfa, #7c3aed); }
.panel-header.teal   { background: linear-gradient(135deg, #2dd4bf, #0891b2); }
.panel-header.orange { background: linear-gradient(135deg, #fb923c, #f97316); }
.panel-emoji { font-size: 28px; }
.panel-header h3 { margin: 0 0 4px; font-size: 17px; font-weight: 700; }
.panel-header p  { margin: 0; font-size: 13px; opacity: 0.85; }
.panel-body { padding: 20px 20px 24px; }

/* 大文本框 */
.big-textarea {
  width: 100%;
  padding: 14px 16px;
  border: 1.5px solid #e9d5ff;
  border-radius: 14px;
  font-size: 14px;
  resize: none;
  outline: none;
  font-family: inherit;
  color: #374151;
  transition: border-color 0.25s;
  line-height: 1.6;
}
.big-textarea:focus { border-color: #c084fc; box-shadow: 0 0 0 3px rgba(192,132,252,0.15); }

/* 操作按钮 */
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-top: 14px;
  padding: 12px 28px;
  border: none;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.25s;
  background: linear-gradient(135deg, #a78bfa, #7c3aed);
  color: white;
  box-shadow: 0 4px 14px rgba(124,58,237,0.3);
}
.action-btn:disabled { opacity: 0.6; cursor: not-allowed; transform: none !important; }
.action-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(124,58,237,0.4); }
.pink-btn   { background: linear-gradient(135deg, #f472b6, #ec4899); box-shadow: 0 4px 14px rgba(236,72,153,0.3); }
.pink-btn:hover:not(:disabled) { box-shadow: 0 6px 20px rgba(236,72,153,0.4); }
.orange-btn { background: linear-gradient(135deg, #fb923c, #f97316); box-shadow: 0 4px 14px rgba(249,115,22,0.3); }
.orange-btn:hover:not(:disabled) { box-shadow: 0 6px 20px rgba(249,115,22,0.4); }

/* 识别结果卡片 */
.result-card {
  margin-top: 16px;
  background: #fdf4ff;
  border: 1.5px solid #e9d5ff;
  border-radius: 16px;
  padding: 18px;
}
.result-header { display: flex; align-items: center; gap: 14px; margin-bottom: 10px; }
.result-emoji  { font-size: 36px; }
.result-emotion { font-size: 15px; font-weight: 700; color: #5b21b6; }
.result-level  { font-size: 12px; font-weight: 600; padding: 2px 10px; border-radius: 10px; margin-top: 4px; display: inline-block; }
.lv-good    { background: #d1fae5; color: #065f46; }
.lv-neutral { background: #e0f2fe; color: #075985; }
.lv-warn    { background: #fef3c7; color: #92400e; }
.lv-low     { background: #fee2e2; color: #991b1b; }
.result-desc   { font-size: 14px; color: #4b5563; line-height: 1.7; margin-bottom: 12px; }
.emotion-tags  { display: flex; flex-wrap: wrap; gap: 6px; }
.e-tag {
  background: linear-gradient(135deg, #ede9fe, #fce7f3);
  color: #7c3aed;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 10px;
}

/* 聊天区 */
.chat-box {
  height: 320px;
  overflow-y: auto;
  padding: 12px 4px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  scrollbar-width: thin;
  scrollbar-color: #e9d5ff transparent;
}
.chat-box::-webkit-scrollbar { width: 4px; }
.chat-box::-webkit-scrollbar-thumb { background: #e9d5ff; border-radius: 4px; }
.chat-welcome {
  text-align: center;
  padding: 32px 20px;
  color: #9ca3af;
  font-size: 14px;
  line-height: 1.8;
}
.welcome-icon { font-size: 40px; margin-bottom: 12px; }
.chat-msg { display: flex; align-items: flex-end; gap: 8px; }
.msg-user { flex-direction: row-reverse; }
.ai-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f472b6, #a78bfa);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}
.bubble {
  max-width: 72%;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.65;
  position: relative;
}
.bubble-user {
  background: linear-gradient(135deg, #a78bfa, #7c3aed);
  color: white;
  border-bottom-right-radius: 4px;
}
.bubble-ai {
  background: #fdf4ff;
  color: #374151;
  border: 1px solid #e9d5ff;
  border-bottom-left-radius: 4px;
}
.bubble-text { word-break: break-word; }
.bubble-time { font-size: 11px; opacity: 0.6; margin-top: 4px; text-align: right; }
/* 打字指示 */
.typing { padding: 14px 20px !important; }
.dot {
  display: inline-block;
  width: 7px; height: 7px;
  border-radius: 50%;
  background: #c084fc;
  margin: 0 2px;
  animation: dotBounce 1.2s ease-in-out infinite;
}
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes dotBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

/* 聊天输入 */
.chat-input-row {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  align-items: center;
}
.chat-input {
  flex: 1;
  min-height: 36px;
  max-height: 120px;
  padding: 18px 14px 4px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  font-size: 13px;
  resize: none;
  outline: none;
  font-family: inherit;
  transition: all 0.2s ease;
  color: #374151;
  background: white;
  line-height: 14px;
  overflow-y: auto;
  box-sizing: border-box;
}
.chat-input::placeholder {
  color: #9ca3af;
}
.chat-input:focus {
  border-color: #8B5CF6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.15);
}
.send-btn {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #A78BFA, #7C3AED);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
  font-size: 16px;
  align-self: center;
}
.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(124, 58, 237, 0.45);
}
.send-btn:active:not(:disabled) {
  transform: scale(0.95);
}
.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 情绪趋势 */
.checkin-section { margin-bottom: 20px; }
.checkin-title { font-size: 14px; font-weight: 700; color: #0e7490; margin-bottom: 12px; }
.mood-selector { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 12px; }
.mood-opt {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 10px 14px;
  border: 2px solid #e0f2fe;
  border-radius: 14px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  color: #6b7280;
}
.mood-opt:hover { border-color: #67e8f9; background: #ecfeff; }
.mood-opt.selected { border-color: #0891b2; background: #e0f2fe; color: #0e7490; font-weight: 700; }
.mood-emoji { font-size: 22px; }
.mood-name  { font-size: 12px; font-weight: 600; }
.checkin-note-row { display: flex; gap: 10px; }
.checkin-note {
  flex: 1;
  padding: 10px 14px;
  border: 1.5px solid #a5f3fc;
  border-radius: 12px;
  font-size: 13px;
  outline: none;
  color: #374151;
  transition: border-color 0.25s;
}
.checkin-note:focus { border-color: #0891b2; }
.checkin-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #2dd4bf, #0891b2);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.25s;
  white-space: nowrap;
}
.checkin-btn:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(8,145,178,0.3); }

/* 趋势图 */
.trend-chart-wrap { margin-bottom: 20px; }
.trend-title { font-size: 14px; font-weight: 700; color: #0e7490; margin-bottom: 12px; }
.trend-chart {
  display: flex;
  gap: 10px;
  padding: 12px;
  background: #f0fdfa;
  border-radius: 16px;
  border: 1px solid #a5f3fc;
  margin-bottom: 14px;
}
.chart-y-labels {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  font-size: 14px;
  padding: 4px 0;
}
.chart-bars {
  display: flex;
  flex: 1;
  gap: 6px;
  align-items: flex-end;
  height: 120px;
}
.chart-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  height: 100%;
}
.bar-wrap {
  flex: 1;
  width: 100%;
  display: flex;
  align-items: flex-end;
}
.bar {
  width: 100%;
  border-radius: 6px 6px 0 0;
  min-height: 4px;
  transition: height 0.6s ease;
}
.bar-day  { font-size: 11px; color: #6b7280; font-weight: 600; }
.bar-emoji { font-size: 13px; }

.trend-summary {
  display: flex;
  gap: 16px;
  justify-content: center;
}
.ts-item { text-align: center; }
.ts-val  { display: block; font-size: 22px; }
.ts-key  { display: block; font-size: 11px; color: #6b7280; font-weight: 600; margin-top: 2px; }

/* 历史记录 */
.history-section { margin-top: 4px; }
.history-title { font-size: 14px; font-weight: 700; color: #0e7490; margin-bottom: 12px; }
.empty-hint { color: #9ca3af; font-size: 13px; text-align: center; padding: 20px 0; }
.history-list { display: flex; flex-direction: column; gap: 8px; }
.history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f0fdfa;
  border-radius: 12px;
  border: 1px solid #a5f3fc;
  gap: 12px;
}
.hi-left { display: flex; align-items: center; gap: 12px; }
.hi-emoji { font-size: 22px; }
.hi-date  { font-size: 13px; font-weight: 700; color: #374151; }
.hi-note  { font-size: 12px; color: #9ca3af; margin-top: 2px; }
.hi-label { font-size: 12px; font-weight: 700; flex-shrink: 0; }

/* 情绪检测 */
.test-intro {
  text-align: center;
  padding: 20px 0 10px;
}
.intro-icon { font-size: 48px; margin-bottom: 14px; }
.test-intro h4 { font-size: 18px; font-weight: 800; color: #374151; margin: 0 0 10px; }
.test-intro p  { font-size: 14px; color: #6b7280; line-height: 1.7; margin: 0 0 20px; }

.test-progress { margin-bottom: 20px; }
.progress-bar {
  height: 8px;
  background: #fed7aa;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 6px;
}
.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #fb923c, #f97316);
  border-radius: 4px;
  transition: width 0.4s ease;
}
.progress-text { font-size: 12px; color: #9ca3af; font-weight: 600; }

.question-card {
  background: #fff7ed;
  border: 1.5px solid #fed7aa;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 16px;
}
.q-num  { font-size: 12px; font-weight: 700; color: #f97316; margin-bottom: 8px; }
.q-text { font-size: 15px; font-weight: 600; color: #374151; line-height: 1.6; margin-bottom: 16px; }
.q-options { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; }
.q-opt {
  padding: 10px 6px;
  border: 2px solid #fed7aa;
  border-radius: 12px;
  background: white;
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
}
.q-opt:hover   { border-color: #fb923c; color: #ea580c; background: #fff7ed; }
.q-opt.selected { border-color: #f97316; background: linear-gradient(135deg, #fb923c, #f97316); color: white; }

.test-nav { display: flex; gap: 10px; justify-content: flex-end; }
.nav-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s;
  background: #f3f4f6;
  color: #6b7280;
}
.nav-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.nav-btn:hover:not(:disabled) { background: #e5e7eb; }
.nav-btn.next, .nav-btn.submit {
  background: linear-gradient(135deg, #fb923c, #f97316);
  color: white;
  box-shadow: 0 3px 10px rgba(249,115,22,0.3);
}
.nav-btn.next:hover:not(:disabled),
.nav-btn.submit:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 5px 14px rgba(249,115,22,0.4); }

/* 测试结果 */
.test-result { text-align: center; padding: 10px 0; }
.result-score-ring {
  --c: #34d399;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background: conic-gradient(var(--c) 0% 100%, #e5e7eb 0%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
}
.ring-inner {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 2px;
}
.ring-score { font-size: 24px; font-weight: 800; color: #374151; line-height: 1; }
.ring-max   { font-size: 11px; color: #9ca3af; font-weight: 600; }
.result-level-tag {
  display: inline-block;
  color: white;
  font-size: 14px;
  font-weight: 700;
  padding: 6px 20px;
  border-radius: 24px;
  margin-bottom: 16px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.15);
}
.result-summary {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.7;
  margin-bottom: 20px;
  text-align: left;
  background: #f9fafb;
  border-radius: 12px;
  padding: 14px 16px;
}
.result-suggestions { text-align: left; }
.sug-title { font-size: 14px; font-weight: 700; color: #374151; margin-bottom: 8px; }
.sug-item  { font-size: 13px; color: #4b5563; line-height: 1.8; display: flex; gap: 6px; }
.sug-dot   { color: #f97316; font-weight: 800; flex-shrink: 0; }

/* Toast */
.toast {
  position: fixed;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  background: #5b21b6;
  color: white;
  padding: 12px 24px;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 6px 20px rgba(91,33,182,0.35);
  z-index: 999;
  white-space: nowrap;
}
.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter, .toast-leave-to { opacity: 0; transform: translateX(-50%) translateY(10px); }

/* 过渡 */
.fade-up-enter-active { transition: all 0.35s ease; }
.fade-up-enter { opacity: 0; transform: translateY(10px); }

/* 旋转 */
.spin { display: inline-block; animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

/* 响应式 */
@media (max-width: 480px) {
  .q-options { grid-template-columns: repeat(3, 1fr); }
  .mood-selector { gap: 6px; }
  .mood-opt { padding: 8px 10px; }
}

/* ── 情绪预警 ── */
.refresh-btn {
  margin-left: auto;
  background: rgba(255,255,255,0.15);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 8px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255,255,255,0.7);
  transition: all 0.2s;
  flex-shrink: 0;
}
.refresh-btn:hover { background: rgba(255,255,255,0.25); color: white; }
.refresh-btn.spinning svg { animation: spin 0.8s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

.empty-tip {
  text-align: center;
  padding: 40px 20px;
  color: rgba(255,255,255,0.55);
  font-size: 14px;
}
.empty-icon { font-size: 36px; margin-bottom: 12px; }
.empty-sub { margin-top: 8px; font-size: 12px; color: rgba(255,255,255,0.4); line-height: 1.6; }

.alert-list { display: flex; flex-direction: column; gap: 14px; }
.alert-card {
  border-radius: 14px;
  padding: 14px 16px;
  background: rgba(255,255,255,0.07);
  border: 1px solid rgba(255,255,255,0.1);
  transition: all 0.2s;
}
.alert-card:hover { background: rgba(255,255,255,0.1); }
.alert-card.level-high {
  border-color: rgba(239,68,68,0.4);
  background: rgba(239,68,68,0.08);
}
.alert-card.level-mid {
  border-color: rgba(251,146,60,0.4);
  background: rgba(251,146,60,0.06);
}
.alert-card.level-low {
  border-color: rgba(234,179,8,0.3);
  background: rgba(234,179,8,0.05);
}

.alert-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.alert-emoji { font-size: 24px; }
.alert-info { flex: 1; }
.alert-emotion { font-size: 15px; font-weight: 600; color: white; }
.alert-time { font-size: 11px; color: rgba(255,255,255,0.45); margin-top: 2px; }

.alert-level-badge {
  padding: 3px 8px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  flex-shrink: 0;
}
.alert-level-badge.level-high { background: rgba(239,68,68,0.25); color: #fca5a5; }
.alert-level-badge.level-mid  { background: rgba(251,146,60,0.2); color: #fdba74; }
.alert-level-badge.level-low  { background: rgba(234,179,8,0.15); color: #fde047; }

.alert-content {
  font-size: 13px;
  color: rgba(255,255,255,0.7);
  line-height: 1.6;
  margin-bottom: 8px;
  padding: 8px 10px;
  background: rgba(255,255,255,0.05);
  border-radius: 8px;
  font-style: italic;
}
.alert-tags { display: flex; flex-wrap: wrap; gap: 5px; margin-bottom: 6px; }
.alert-reply {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
  line-height: 1.5;
  border-top: 1px solid rgba(255,255,255,0.06);
  padding-top: 6px;
  margin-top: 4px;
}
.reply-label { color: rgba(255,255,255,0.65); font-style: normal; }
</style>
