<template>
  <div class="service-access-page">
    <section class="access-hero">
      <div class="hero-copy">
        <span class="hero-badge">可扩展创新</span>
        <h1>统一服务接入接口</h1>
        <p>
          系统预留统一服务接入接口，可对接图书馆检索、电子书资源、体育馆预约等第三方校园数据源，实现跨系统一站式访问。
        </p>
      </div>
      <div class="hero-note">
        <strong>当前状态</strong>
        <span>概念验证 / 轻量演示入口</span>
      </div>
    </section>

    <section class="tab-section">
      <button
        v-for="item in tabs"
        :key="item.key"
        :class="['tab-btn', { active: activeTab === item.key }]"
        @click="setTab(item.key)"
      >
        {{ item.label }}
      </button>
    </section>

    <section class="content-grid">
      <article class="intro-card">
        <h2>扩展设计说明</h2>
        <p>
          我们当前已完成核心校园服务闭环，并预留统一接口层，后续可快速对接图书馆、体育馆、教务等系统。
        </p>
        <ul>
          <li>用户层：学生 / 管理员</li>
          <li>智能体层：AI 问答、意图识别、推荐</li>
          <li>服务层：课表、请假、报修、反馈、志愿、兼职</li>
          <li>接口层：图书馆、体育馆、教务、一卡通、公告系统</li>
        </ul>
      </article>

      <article v-if="activeTab === 'architecture'" class="detail-card architecture-card">
        <h2>统一接入架构图</h2>
        <div class="arch-wrap">
          <div class="arch-column">
            <span class="arch-title">用户层</span>
            <div class="arch-box">学生</div>
            <div class="arch-box">管理员</div>
          </div>
          <div class="arch-column">
            <span class="arch-title">智能体层</span>
            <div class="arch-box accent">AI 问答</div>
            <div class="arch-box accent">意图识别</div>
            <div class="arch-box accent">智能推荐</div>
          </div>
          <div class="arch-column">
            <span class="arch-title">服务层</span>
            <div class="arch-box">课表 / 提醒</div>
            <div class="arch-box">请假 / 报修</div>
            <div class="arch-box">反馈 / 志愿 / 兼职</div>
          </div>
          <div class="arch-column">
            <span class="arch-title">接口层</span>
            <div class="arch-box soft">图书馆</div>
            <div class="arch-box soft">体育馆</div>
            <div class="arch-box soft">教务 / 一卡通 / 公告</div>
          </div>
        </div>
      </article>

      <article v-if="activeTab === 'library'" class="detail-card">
        <h2>图书馆检索演示入口</h2>
        <p class="desc">
          该入口用于展示后续接入方向，不表示当前已经全面打通学校图书馆系统。
        </p>
        <div class="mock-panel">
          <div class="mock-search">
            <input v-model.trim="libraryKeyword" type="text" placeholder="输入书名、作者或关键词，例如：数据结构">
            <button @click="mockLibrarySearch">检索</button>
          </div>
          <div class="mock-result-list">
            <div v-for="item in libraryResults" :key="item.title" class="mock-result">
              <strong>{{ item.title }}</strong>
              <span>{{ item.meta }}</span>
            </div>
          </div>
        </div>
      </article>

      <article v-if="activeTab === 'venue'" class="detail-card">
        <h2>场馆预约演示入口</h2>
        <p class="desc">
          该入口用于说明后续可接入体育馆预约系统，实现空闲时段查询、预约提交和预约结果回显。
        </p>
        <div class="venue-grid">
          <div v-for="item in venueSlots" :key="item.name" class="venue-card">
            <h3>{{ item.name }}</h3>
            <p>{{ item.time }}</p>
            <span :class="['slot-tag', item.status === '可预约' ? 'free' : 'busy']">{{ item.status }}</span>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script>
export default {
  name: 'ServiceAccess',
  data() {
    return {
      tabs: [
        { key: 'architecture', label: '架构图' },
        { key: 'library', label: '图书馆检索' },
        { key: 'venue', label: '场馆预约' }
      ],
      activeTab: 'architecture',
      libraryKeyword: '',
      libraryResults: [
        { title: '数据结构（演示馆藏）', meta: '纸质馆藏 / 可借阅 / A-203' },
        { title: 'Java Web 开发基础（演示电子书）', meta: '电子资源 / 可在线阅读' },
        { title: '人工智能导论（演示资源）', meta: '纸电同步 / 支持馆藏查询' }
      ],
      venueSlots: [
        { name: '羽毛球馆 1 号场', time: '18:00 - 19:00', status: '可预约' },
        { name: '羽毛球馆 2 号场', time: '19:00 - 20:00', status: '已占用' },
        { name: '篮球馆主场地', time: '20:00 - 21:00', status: '可预约' }
      ]
    }
  },
  created() {
    const tab = this.$route.query.tab
    if (tab && this.tabs.some(item => item.key === tab)) {
      this.activeTab = tab
    }
  },
  watch: {
    '$route.query.tab'(value) {
      if (value && this.tabs.some(item => item.key === value)) {
        this.activeTab = value
      }
    }
  },
  methods: {
    setTab(tab) {
      this.activeTab = tab
      this.$router.replace({ path: this.$route.path, query: { tab } })
    },
    mockLibrarySearch() {
      const keyword = this.libraryKeyword || '校园资源'
      this.libraryResults = [
        { title: `${keyword}（演示检索结果 1）`, meta: '纸质馆藏 / 可借阅 / 图书馆二楼' },
        { title: `${keyword}（演示电子资源 2）`, meta: '电子资源 / 可在线阅读 / 已接入示意' },
        { title: `${keyword}（演示馆藏 3）`, meta: '馆藏检索 / 可预约借阅 / 扩展设计示意' }
      ]
      this.$message.success('已刷新演示检索结果')
    }
  }
}
</script>

<style scoped>
.service-access-page {
  min-height: 100vh;
  padding: 24px;
  background:
    radial-gradient(circle at top right, rgba(24, 183, 209, 0.1), transparent 22%),
    linear-gradient(180deg, #f5fbff 0%, #eef4f8 100%);
}

.access-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #18b7d1, #3a71e3);
  color: #fff;
  box-shadow: 0 22px 44px rgba(36, 77, 120, 0.18);
}

.hero-copy {
  max-width: 720px;
}

.hero-badge {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.16);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.hero-copy h1 {
  margin: 16px 0 10px;
  font-size: 34px;
  line-height: 1.1;
}

.hero-copy p {
  margin: 0;
  max-width: 680px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.92);
}

.hero-note {
  min-width: 220px;
  align-self: flex-start;
  display: grid;
  gap: 8px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(10px);
}

.hero-note strong {
  font-size: 16px;
}

.hero-note span {
  color: rgba(255, 255, 255, 0.88);
  font-size: 14px;
}

.tab-section {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.tab-btn {
  min-width: 110px;
  height: 42px;
  padding: 0 18px;
  border: 1px solid #dce9f1;
  border-radius: 999px;
  background: #fff;
  color: #51606c;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn.active {
  color: #fff;
  border-color: transparent;
  background: linear-gradient(135deg, #18b7d1, #3a71e3);
  box-shadow: 0 12px 24px rgba(24, 183, 209, 0.18);
}

.content-grid {
  display: grid;
  grid-template-columns: 1.05fr 1.35fr;
  gap: 18px;
  margin-top: 18px;
}

.intro-card,
.detail-card {
  padding: 22px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid #e2edf4;
  box-shadow: 0 18px 36px rgba(32, 52, 71, 0.08);
}

.intro-card h2,
.detail-card h2 {
  margin: 0 0 12px;
  color: #24323d;
  font-size: 22px;
}

.intro-card p,
.detail-card .desc {
  margin: 0 0 14px;
  color: #61717e;
  line-height: 1.7;
}

.intro-card ul {
  margin: 0;
  padding-left: 18px;
  color: #33424d;
  line-height: 1.9;
}

.arch-wrap {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.arch-column {
  display: grid;
  gap: 10px;
}

.arch-title {
  font-size: 13px;
  font-weight: 700;
  color: #6a7d8f;
}

.arch-box {
  min-height: 62px;
  padding: 14px 12px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  text-align: center;
  font-size: 14px;
  font-weight: 700;
  color: #32414d;
  background: #f7fbfe;
  border: 1px solid #deebf3;
}

.arch-box.accent {
  color: #0f8da7;
  background: rgba(24, 183, 209, 0.1);
  border-color: rgba(24, 183, 209, 0.2);
}

.arch-box.soft {
  color: #3463b8;
  background: rgba(58, 113, 227, 0.08);
  border-color: rgba(58, 113, 227, 0.18);
}

.mock-panel {
  display: grid;
  gap: 14px;
}

.mock-search {
  display: flex;
  gap: 10px;
}

.mock-search input {
  flex: 1;
  height: 46px;
  padding: 0 14px;
  border: 1px solid #dbe8f1;
  border-radius: 14px;
  outline: none;
  font-size: 14px;
}

.mock-search input:focus {
  border-color: #18b7d1;
  box-shadow: 0 0 0 3px rgba(24, 183, 209, 0.12);
}

.mock-search button {
  min-width: 88px;
  height: 46px;
  border: 0;
  border-radius: 14px;
  background: linear-gradient(135deg, #18b7d1, #3a71e3);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
}

.mock-result-list {
  display: grid;
  gap: 10px;
}

.mock-result {
  padding: 14px;
  border-radius: 16px;
  background: #f7fbfe;
  border: 1px solid #e1edf4;
}

.mock-result strong {
  display: block;
  margin-bottom: 6px;
  color: #283744;
}

.mock-result span {
  color: #6a7987;
  font-size: 14px;
}

.venue-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.venue-card {
  padding: 16px;
  border-radius: 18px;
  background: #f7fbfe;
  border: 1px solid #e1edf4;
}

.venue-card h3 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #24323d;
}

.venue-card p {
  margin: 0 0 12px;
  color: #667684;
}

.slot-tag {
  display: inline-flex;
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.slot-tag.free {
  color: #118556;
  background: #d8f5e6;
}

.slot-tag.busy {
  color: #cb3a3a;
  background: #fee2e2;
}

@media (max-width: 980px) {
  .access-hero {
    flex-direction: column;
  }

  .content-grid {
    grid-template-columns: 1fr;
  }

  .arch-wrap {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .venue-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .service-access-page {
    padding: 14px;
  }

  .access-hero,
  .intro-card,
  .detail-card {
    padding: 18px;
    border-radius: 20px;
  }

  .hero-copy h1 {
    font-size: 28px;
  }

  .tab-section {
    flex-wrap: wrap;
  }

  .tab-btn {
    flex: 1 1 calc(50% - 10px);
  }

  .mock-search {
    flex-direction: column;
  }

  .arch-wrap {
    grid-template-columns: 1fr;
  }
}
</style>
