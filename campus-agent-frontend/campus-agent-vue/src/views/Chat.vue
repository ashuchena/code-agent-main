<template>
  <div class="chat-page">
    <!-- 背景装饰 -->
    <div class="bg-blob blob1"></div>
    <div class="bg-blob blob2"></div>
    <div class="bg-blob blob3"></div>

    <!-- 顶部标题栏（简化版，因为侧边栏已有导航） -->
    <header class="chat-header">
      <div class="header-center">
        <div class="ai-avatar">
          <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
            <circle cx="9" cy="12" r="0.5" fill="currentColor" stroke="none"/>
            <circle cx="12" cy="12" r="0.5" fill="currentColor" stroke="none"/>
            <circle cx="15" cy="12" r="0.5" fill="currentColor" stroke="none"/>
          </svg>
        </div>
        <div class="header-copy">
          <div class="header-eyebrow">校园生活小管家</div>
          <h2 class="header-title">AI 对话</h2>
          <p class="header-status">
            <span class="status-dot"></span>{{ timeGreeting }}，随时为你服务
          </p>
        </div>
      </div>
      <div class="header-actions">
        <button class="action-btn archive-btn" @click="archiveHistory" title="archive chat history">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
            <path d="M4 3h16a1 1 0 0 1 1 1v4a2 2 0 0 1-2 2v9a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-9a2 2 0 0 1-2-2V4a1 1 0 0 1 1-1zm2 7v9h12v-9H6zm0-5v3h12V5H6zm3 8h6v2H9v-2z"/>
          </svg>
          <span class="btn-text">归档</span>
        </button>
        <button class="action-btn archive-btn" @click="openArchiveModal" title="view chat archives">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
            <path d="M3 5a2 2 0 0 1 2-2h6l2 2h6a2 2 0 0 1 2 2v2H3V5zm0 4h18v10a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V9zm4 3v2h10v-2H7z"/>
          </svg>
          <span class="btn-text">归档列表</span>
        </button>
        <button class="action-btn export-btn" @click="exportHistory" title="导出聊天记录">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
            <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z"/>
          </svg>
          <span class="btn-text">导出</span>
        </button>
        <button class="action-btn clear-btn" @click="clearHistory" title="清空聊天记录">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
            <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
          </svg>
          <span class="btn-text">清空</span>
        </button>
      </div>
    </header>

    <!-- 消息区域 -->
    <div class="chat-body" ref="chatBody">
      <!-- 欢迎消息 -->
      <div v-if="messages.length === 0" class="welcome-area">
        <div class="welcome-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0L12 2.69z"/>
          </svg>
        </div>
        <h3 class="welcome-title">{{ timeGreeting }}，{{ username }} 👋</h3>
        <p class="welcome-desc">我是校园生活小管家，有什么可以帮到你的吗？试试问我这些问题：</p>
        <div class="quick-questions">
          <button
            v-for="q in quickQuestions"
            :key="q.text"
            class="quick-q-btn"
            @click="sendQuickQuestion(q.text)"
          >{{ q.text }}</button>
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="message-list">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message-item', msg.role === 'user' ? 'user-msg' : 'ai-msg']"
        >
          <div v-if="msg.role === 'assistant'" class="msg-avatar ai-avatar-sm">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0L12 2.69z"/>
            </svg>
          </div>
          <div v-else class="msg-avatar user-avatar-sm">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
            </svg>
          </div>
          <div class="msg-content">
            <div class="msg-bubble" v-html="formatMessage(msg.content)"></div>
          </div>
        </div>

        <!-- 加载中 -->
        <div v-if="loading" class="message-item ai-msg">
          <div class="msg-avatar ai-avatar-sm">
            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0L12 2.69z"/>
            </svg>
          </div>
          <div class="msg-content">
            <div class="msg-bubble loading-bubble">
              <span class="loading-dot"></span>
              <span class="loading-dot"></span>
              <span class="loading-dot"></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入区 -->
    <div class="chat-footer">
      <!-- 已选文件预览 -->
      <div v-if="selectedFiles.length > 0" class="selected-files-preview">
        <div v-for="(file, index) in selectedFiles" :key="index" class="file-preview-item">
          <div v-if="file.type.startsWith('image/')" class="image-preview">
            <img :src="file.preview" alt="preview" />
            <button class="remove-file" @click="removeFile(index)">×</button>
          </div>
          <div v-else class="doc-preview">
            <span class="doc-icon">{{ getFileExt(file.name) }}</span>
            <span class="doc-name">{{ file.name }}</span>
            <button class="remove-file" @click="removeFile(index)">×</button>
          </div>
        </div>
      </div>
      <div class="input-wrapper">
        <!-- 上传按钮 -->
        <button class="upload-btn" @click="triggerUpload" title="上传图片或文档">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
            <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
          </svg>
        </button>
        <input ref="fileInput" type="file" multiple accept="image/*,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.pdf,.txt" style="display:none" @change="handleFileSelect" />
        <textarea
          v-model="inputText"
          class="chat-input"
          placeholder="输入你的问题..."
          rows="1"
          @keydown.enter.exact.prevent="sendMessage"
          @input="autoResize"
          ref="inputArea"
        ></textarea>
        <button
          class="send-btn"
          :class="{ active: inputText.trim() || selectedFiles.length > 0 }"
          @click="sendMessage"
          :disabled="(!inputText.trim() && selectedFiles.length === 0) || loading"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- 请假弹窗 -->
    <div v-if="showArchiveModal" class="modal-overlay" @click.self="showArchiveModal = false">
      <div class="modal-content archive-modal">
        <div class="modal-header">
          <h3>聊天归档列表</h3>
          <button class="modal-close" @click="showArchiveModal = false" aria-label="关闭">×</button>
        </div>
        <div class="modal-body archive-body">
          <div v-if="chatArchives.length === 0" class="archive-empty">暂无归档记录</div>
          <div v-else class="archive-list">
            <div v-for="archive in chatArchives" :key="archive.id" class="archive-item">
              <div class="archive-main">
                <strong class="archive-title">{{ archive.title }}</strong>
                <div class="archive-meta">{{ archive.createdAt }} · {{ archive.messageCount }} 条消息</div>
              </div>
              <div class="archive-actions">
                <button class="archive-action restore" @click="restoreArchive(archive)">恢复</button>
                <button class="archive-action delete" @click="deleteArchive(archive.id)">删除</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showLeaveModal" class="modal-overlay" @click.self="showLeaveModal = false">
      <div class="modal-content leave-modal">
        <div class="modal-header">
          <h3>提交请假申请</h3>
          <button class="modal-close" @click="showLeaveModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>请假类型</label>
            <div class="radio-group">
              <label class="radio-label"><input type="radio" v-model="newLeave.leaveType" value="事假"> 事假</label>
              <label class="radio-label"><input type="radio" v-model="newLeave.leaveType" value="病假"> 病假</label>
              <label class="radio-label"><input type="radio" v-model="newLeave.leaveType" value="公假"> 公假</label>
            </div>
          </div>
          <div class="form-group">
            <label>开始日期</label>
            <input v-model="newLeave.startDate" type="date" class="form-input">
          </div>
          <div class="form-group">
            <label>结束日期</label>
            <input v-model="newLeave.endDate" type="date" class="form-input">
          </div>
          <div class="form-group">
            <label>请假原因</label>
            <textarea v-model="newLeave.reason" class="form-input" rows="3" placeholder="请输入请假原因"></textarea>
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input v-model="newLeave.contactPhone" type="text" class="form-input" placeholder="请输入联系电话">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showLeaveModal = false">取消</button>
          <button class="btn-confirm" @click="submitLeave">提交申请</button>
        </div>
      </div>
    </div>

    <!-- 报修弹窗 -->
    <div v-if="showRepairModal" class="modal-overlay" @click.self="showRepairModal = false">
      <div class="modal-content repair-modal">
        <div class="modal-header">
          <h3>提交报修申请</h3>
          <button class="modal-close" @click="showRepairModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>报修类别</label>
            <div class="category-grid">
              <div
                v-for="cat in repairCategories"
                :key="cat.value"
                class="category-item"
                :class="{ active: newRepair.category === cat.value }"
                @click="newRepair.category = cat.value"
              >
                <span class="category-icon">{{ cat.icon }}</span>
                <span class="category-name">{{ cat.label }}</span>
              </div>
            </div>
          </div>
          <div class="form-group">
            <label>报修地点</label>
            <input v-model="newRepair.location" type="text" class="form-input" placeholder="如：学生宿舍3号楼201室">
          </div>
          <div class="form-group">
            <label>问题描述</label>
            <textarea v-model="newRepair.description" class="form-input" rows="3" placeholder="请详细描述报修问题"></textarea>
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input v-model="newRepair.userPhone" type="tel" class="form-input" placeholder="方便维修人员联系你">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showRepairModal = false">取消</button>
          <button class="btn-confirm" @click="submitRepair">提交报修</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Chat',
    data() {
      return {
        inputText: '',
        messages: [],
        loading: false,
        selectedFiles: [],
        username: this.$cookies.get('username') || '用户',
        timeGreeting: '你好',
        showArchiveModal: false,
        chatArchives: [],
        showLeaveModal: false,
        newLeave: { leaveType: '事假', startDate: '', endDate: '', reason: '', contactPhone: '' },
        showRepairModal: false,
        newRepair: { category: 'water', location: '', description: '', userPhone: '' },
        repairCategories: [
          { value: 'water', label: '水电维修', icon: '💧' },
          { value: 'furniture', label: '家具维修', icon: '🛋️' },
          { value: 'network', label: '网络报修', icon: '📶' },
          { value: 'door', label: '门窗维修', icon: '🚪' },
          { value: 'other', label: '其他', icon: '🔧' }
        ],
        /* ==================== 【优化】更丰富的快捷问题 ==================== */
        quickQuestions: [
          { text: '今天有什么好吃的食堂推荐？', category: 'dining' },
          { text: '我今天有哪些课？', category: 'schedule' },
          { text: '帮我报修：宿舍门锁坏了', category: 'repair' },
          { text: '图书馆开放时间是多少？', category: 'info' }
        ]
      }
    },
  mounted() {
    this.restoreDraft()
    this.loadHistory()
    this.updateGreeting()
  },
  beforeDestroy() {
    this.persistDraft()
  },
  methods: {
    getDraftStorageKey() {
      const userId = this.$cookies.get('userId')
      return userId ? `campus-chat-draft:${userId}` : ''
    },
    persistDraft() {
      const key = this.getDraftStorageKey()
      if (!key) return
      localStorage.setItem(key, JSON.stringify({
        inputText: this.inputText,
        messages: this.messages
      }))
    },
    restoreDraft() {
      const key = this.getDraftStorageKey()
      if (!key) return
      try {
        const raw = localStorage.getItem(key)
        if (!raw) return
        const draft = JSON.parse(raw)
        this.inputText = draft.inputText || ''
        if (Array.isArray(draft.messages) && draft.messages.length) {
          this.messages = draft.messages
          this.$nextTick(() => this.scrollToBottom())
        }
      } catch (e) {
        localStorage.removeItem(key)
      }
    },
    getArchiveStorageKey() {
      const userId = this.$cookies.get('userId')
      return userId ? `campus-chat-archives:${userId}` : ''
    },
    readArchives() {
      const key = this.getArchiveStorageKey()
      if (!key) return []
      try {
        const raw = localStorage.getItem(key)
        const archives = raw ? JSON.parse(raw) : []
        return Array.isArray(archives) ? archives : []
      } catch (error) {
        localStorage.removeItem(key)
        return []
      }
    },
    writeArchives(archives) {
      const key = this.getArchiveStorageKey()
      if (!key) return
      localStorage.setItem(key, JSON.stringify(archives))
    },
    openArchiveModal() {
      this.chatArchives = this.readArchives()
      this.showArchiveModal = true
    },
    archiveHistory() {
      if (!this.messages.length) {
        alert('当前没有可归档的聊天记录')
        return
      }
      const title = window.prompt('请输入归档标题', `聊天归档 ${new Date().toLocaleString('zh-CN', { hour12: false })}`)
      if (title === null) return

      const archive = {
        id: `${Date.now()}-${Math.random().toString(16).slice(2, 8)}`,
        title: title.trim() || `聊天归档 ${new Date().toLocaleString('zh-CN', { hour12: false })}`,
        createdAt: new Date().toLocaleString('zh-CN', { hour12: false }),
        messageCount: this.messages.length,
        messages: this.messages.map(msg => ({
          role: msg.role,
          content: msg.content
        }))
      }

      const archives = [archive, ...this.readArchives()].slice(0, 50)
      this.writeArchives(archives)
      this.chatArchives = archives
      this.showArchiveModal = true
      alert('聊天记录已归档')
    },
    updateGreeting() {
      const hour = new Date().getHours()
      if (hour < 6) this.timeGreeting = '夜深了'
      else if (hour < 9) this.timeGreeting = '早上好'
      else if (hour < 12) this.timeGreeting = '上午好'
      else if (hour < 14) this.timeGreeting = '中午好'
      else if (hour < 18) this.timeGreeting = '下午好'
      else if (hour < 22) this.timeGreeting = '晚上好'
      else this.timeGreeting = '夜深了'
    },
    async loadHistory() {
      const userId = this.$cookies.get('userId')
      if (!userId) return
      try {
        const res = await fetch(`/agent/history?userId=${userId}`)
        // 即使请求失败也继续，不要阻塞用户
        if (!res.ok) {
          console.warn('历史加载失败（服务可能未启动），继续使用...')
          return
        }
        const json = await res.json()
        if (json.code == 200 && json.data) {
          this.messages = json.data.map(m => ({
            role: m.role === 'user' ? 'user' : 'assistant',
            content: m.content
          }))
          this.$nextTick(() => this.scrollToBottom())
        }
      } catch (e) {
        // 静默处理错误，不影响用户使用
        console.warn('历史加载失败，继续使用...', e.message)
      }
    },
    async sendMessage() {
      const text = this.inputText.trim()
      const hasFiles = this.selectedFiles.length > 0
      if ((!text && !hasFiles) || this.loading) return
      const userId = this.$cookies.get('userId')
      if (!userId) return

      // 检测请假意图
      const leaveKeywords = ['请假', '休假', '要请', '想请', '需要请假']
      const hasLeaveIntent = leaveKeywords.some(kw => text.includes(kw))
      if (hasLeaveIntent && !hasFiles) {
        this.inputText = ''
        this.showLeaveModal = true
        return
      }

      // 检测报修意图 - 自动解析并提交
      // 【增强】更全面的报修意图检测
      const repairIntentKeywords = ['报修', '维修', '报障', '报故', '报修申请', '申请维修', '维修申请', '需要修理', '需要维修']
      const hasExplicitIntent = repairIntentKeywords.some(kw => text.includes(kw))

      // 检测问题描述关键词（通常是描述某物出问题了）
      const problemKeywords = ['坏了', '漏水', '故障', '损坏', '裂了', '堵了', '不通', '断网', '没网', '打不开', '关不上', '掉了', '松了', '出问题', '不能用了', '故障了']
      const hasProblem = problemKeywords.some(kw => text.includes(kw))

      // 检测地点+维修类别组合（如：水管、水龙头、门锁、灯、空调等）
      const repairItems = ['水管', '水龙头', '马桶', '淋浴', '门锁', '锁', '灯', '空调', '网络', '网线', '床', '桌子', '椅子', '柜子', '门', '窗', '插座', '下水道', '花洒', '热水器', '水表', '窗户', '椅子', '桌子', '床']
      const hasRepairItem = repairItems.some(item => text.includes(item))

      // 【新增】检测报修场景描述
      const repairScenarios = ['需要报修', '帮我报修', '要报修', '想报修', '申请报修', '登记报修', '提交报修']
      const hasRepairScenario = repairScenarios.some(s => text.includes(s))

      const hasRepairIntent = hasExplicitIntent || hasRepairScenario || (hasProblem && hasRepairItem)
      if (hasRepairIntent && !hasFiles) {
        // 先添加到消息列表，显示用户的问题
        this.messages.push({ role: 'user', content: text })
        this.inputText = ''
        this.loading = true
        this.$nextTick(() => this.scrollToBottom())

        // 智能解析报修信息
        const parsed = this.parseRepairInfo(text)
        console.log('解析报修信息:', parsed)

        // 自动提交报修
        try {
          const res = await this.$axios.post('/maintenance/add', {
            userId: parseInt(userId),
            category: parsed.category,
            location: parsed.location,
            description: parsed.description,
            userPhone: this.$cookies.get('userPhone') || ''
          })
          if (res.data.code === 200) {
            const reply = `✅ 报修已登记！\n\n📍 地点：${parsed.location}\n📝 描述：${parsed.description}\n📋 类别：${parsed.categoryName}\n\n维修人员会尽快处理，你可以在"我的报修记录"中查看进度。`
            this.messages.push({
              role: 'assistant',
              content: reply
            })
            // 【修复】保存消息到数据库，这样刷新后不会丢失
            try {
              await this.$axios.post('/agent/save-message', null, {
                params: { userId: userId, role: 'user', content: text }
              })
              await this.$axios.post('/agent/save-message', null, {
                params: { userId: userId, role: 'assistant', content: reply }
              })
            } catch (e) {
              console.warn('保存历史记录失败', e.message)
            }
          } else {
            this.messages.push({
              role: 'assistant',
              content: '❌ 报修登记失败：' + (res.data.message || '请稍后重试')
            })
          }
        } catch (err) {
          console.error('报修提交失败:', err)
          this.messages.push({
            role: 'assistant',
            content: '❌ 报修登记失败，请稍后重试或前往报修页面手动提交。'
          })
        }
        this.loading = false
        this.$nextTick(() => this.scrollToBottom())
        return
      }

      let sendContent = text
      if (hasFiles) {
        const fileNames = this.selectedFiles.map(f => `[附件: ${f.name}]`).join(' ')
        sendContent = sendContent ? `${text}\n${fileNames}` : fileNames
      }

      this.messages.push({ role: 'user', content: sendContent })
      const sentFiles = [...this.selectedFiles]
      this.inputText = ''
      this.selectedFiles = []
      this.loading = true
      this.$nextTick(() => {
        this.scrollToBottom()
        if (this.$refs.inputArea) this.$refs.inputArea.style.height = 'auto'
      })

      try {
        const formData = new FormData()
        formData.append('content', sendContent)
        formData.append('userId', userId)
        sentFiles.forEach((f) => {
          formData.append('file', f.file)
        })
        const res = await fetch('/agent/chat', { method: 'POST', body: formData })
        const json = await res.json()
        if (json.code == 200) {
          const reply = json.data?.reply || '抱歉，我没有收到回复，请稍后再试。'
          this.messages.push({ role: 'assistant', content: reply })
        } else {
          this.messages.push({ role: 'assistant', content: '抱歉，服务出错了：' + (json.message || '未知错误') })
        }
      } catch (e) {
        this.messages.push({ role: 'assistant', content: '网络错误，请检查连接后重试。' })
      } finally {
        this.loading = false
        this.$nextTick(() => this.scrollToBottom())
      }
    },
    sendQuickQuestion(q) {
      this.inputText = q
      this.sendMessage()
    },
    triggerUpload() {
      this.$refs.fileInput.click()
    },
    handleFileSelect(event) {
      const files = Array.from(event.target.files)
      files.forEach(file => {
        if (this.selectedFiles.length >= 4) {
          alert('最多只能选择4个文件')
          return
        }
        if (file.size > 10 * 1024 * 1024) {
          alert('文件大小不能超过10MB')
          return
        }
        const fileObj = {
          name: file.name,
          type: file.type,
          size: file.size,
          file: file
        }
        if (file.type.startsWith('image/')) {
          const reader = new FileReader()
          reader.onload = (e) => {
            this.$set(fileObj, 'preview', e.target.result)
          }
          reader.readAsDataURL(file)
        }
        this.selectedFiles.push(fileObj)
      })
      event.target.value = ''
    },
    removeFile(index) {
      this.selectedFiles.splice(index, 1)
    },
    getFileExt(filename) {
      return filename.split('.').pop().toUpperCase().substring(0, 4)
    },
    async clearHistory() {
      const userId = this.$cookies.get('userId')
      if (!userId) return
      if (!confirm('确定清除所有对话记录？')) return
      try {
        await fetch('/agent/clear-history', {
          method: 'POST',
          body: new URLSearchParams({ userId })
        })
        this.messages = []
      } catch (e) {
        console.error('清除失败', e)
      }
    },
    async exportHistory() {
      const userId = this.$cookies.get('userId')
      if (!userId) return
      try {
        const res = await fetch(`/agent/export?userId=${userId}&format=md`)
        const json = await res.json()
        if (json.code == 200 && json.data) {
          const blob = new Blob([json.data.content], { type: 'text/markdown' })
          const url = URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = json.data.filename || '聊天记录.md'
          a.click()
          URL.revokeObjectURL(url)
        } else {
          alert('导出失败：' + (json.message || '未知错误'))
        }
      } catch (e) {
        console.error('导出失败', e)
        alert('导出失败，请重试')
      }
    },
    async restoreArchive(archive) {
      const userId = this.$cookies.get('userId')
      if (!userId || !archive || !Array.isArray(archive.messages)) return
      if (!confirm(`确定恢复归档“${archive.title}”吗？当前聊天记录会被替换。`)) return
      try {
        await fetch('/agent/clear-history', {
          method: 'POST',
          body: new URLSearchParams({ userId })
        })
        for (const message of archive.messages) {
          await this.$axios.post('/agent/save-message', null, {
            params: {
              userId,
              role: message.role === 'user' ? 'user' : 'assistant',
              content: message.content
            }
          })
        }
        this.messages = archive.messages.map(message => ({
          role: message.role === 'user' ? 'user' : 'assistant',
          content: message.content
        }))
        this.showArchiveModal = false
        this.$nextTick(() => this.scrollToBottom())
        alert('归档已恢复')
      } catch (error) {
        console.error('恢复归档失败', error)
        alert('恢复归档失败，请稍后重试')
      }
    },
    deleteArchive(archiveId) {
      if (!confirm('确定删除这条归档记录吗？')) return
      const archives = this.readArchives().filter(item => item.id !== archiveId)
      this.writeArchives(archives)
      this.chatArchives = archives
    },
    scrollToBottom() {
      if (this.$refs.chatBody) {
        this.$refs.chatBody.scrollTop = this.$refs.chatBody.scrollHeight
      }
    },
    autoResize(e) {
      e.target.style.height = 'auto'
      e.target.style.height = Math.min(e.target.scrollHeight, 120) + 'px'
    },
    formatMessage(text) {
      if (!text) return ''
      let html = text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')

      const hasMarkdown = /(^|\n)(#{1,4}\s|[-*]\s|>\s)|```|`[^`]+`|\*\*[^*]+\*\*/.test(text)
      if (!hasMarkdown) {
        return html.replace(/\n/g, '<br>')
      }

      html = html
        .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
        .replace(/`([^`]+)`/g, '<code>$1</code>')
        .replace(/^#### (.+)$/gm, '<h4>$1</h4>')
        .replace(/^### (.+)$/gm, '<h3>$1</h3>')
        .replace(/^## (.+)$/gm, '<h2>$1</h2>')
        .replace(/^# (.+)$/gm, '<h1>$1</h1>')
        .replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>')
        .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
        .replace(/^&gt; (.+)$/gm, '<blockquote>$1</blockquote>')
        .replace(/^[-*] (.+)$/gm, '<li>$1</li>')
        .replace(/(<li>.*<\/li>\s*)+/g, '<ul>$&</ul>')
        .replace(/\n{2,}/g, '</p><p>')
        .replace(/\n/g, '<br>')

      return `<div class="md-content"><p>${html}</p></div>`
    },
    async submitLeave() {
      if (!this.newLeave.startDate || !this.newLeave.endDate || !this.newLeave.reason) {
        alert('请填写完整信息')
        return
      }
      const userId = this.$cookies.get('userId')
      if (!userId) {
        alert('请先登录')
        return
      }
      try {
        const res = await this.$axios.post('/leave/add', {
          userId: userId,
          ...this.newLeave
        })
        if (res.data.code === 200) {
          this.showLeaveModal = false
          this.newLeave = { leaveType: '事假', startDate: '', endDate: '', reason: '', contactPhone: '' }
          this.messages.push({ role: 'assistant', content: '✅ 请假申请已提交，请耐心等待审批结果！' })
          this.$nextTick(() => this.scrollToBottom())
        } else {
          alert('提交失败：' + (res.data.msg || '未知错误'))
        }
      } catch (err) {
        alert('提交失败，请重试')
      }
    },
    async submitRepair() {
      if (!this.newRepair.category) {
        alert('请选择报修类别')
        return
      }
      if (!this.newRepair.location) {
        alert('请填写报修地点')
        return
      }
      if (!this.newRepair.description) {
        alert('请填写问题描述')
        return
      }
      if (!this.newRepair.userPhone) {
        alert('请填写联系电话')
        return
      }
      const userId = this.$cookies.get('userId')
      if (!userId) {
        alert('请先登录')
        return
      }
      try {
        const res = await this.$axios.post('/maintenance/add', {
          userId: parseInt(userId),
          category: this.newRepair.category,
          location: this.newRepair.location,
          description: this.newRepair.description,
          userPhone: this.newRepair.userPhone
        })
        if (res.data.code === 200) {
          this.showRepairModal = false
          this.newRepair = { category: 'water', location: '', description: '', userPhone: '' }
          this.messages.push({ role: 'assistant', content: '✅ 报修已提交，我们会尽快处理！你可以在"我的报修记录"中查看进度。' })
          this.$nextTick(() => this.scrollToBottom())
        } else {
          alert('提交失败：' + (res.data.message || '未知错误'))
        }
      } catch (err) {
        alert('提交失败，请重试')
      }
    },
    // 智能解析报修信息
    parseRepairInfo(text) {
      let category = 'other'
      let categoryName = '其他'
      let location = '学生宿舍'
      let description = text

      // 【增强】解析类别 - 使用更精确的匹配逻辑
      const categoryPatterns = [
        { keywords: ['水龙头', '水管', '漏水', '停水', '马桶', '淋浴', '下水道', '厕所', '水表', '阀门'], value: 'water', name: '水电维修' },
        { keywords: ['门锁', '锁', '门', '窗户', '窗'], value: 'door', name: '门窗维修' },
        { keywords: ['网络', '网', 'wifi', 'WiFi', '断网', '上网', '网线', '宽带'], value: 'network', name: '网络报修' },
        { keywords: ['床', '桌子', '椅子', '柜子', '家具', '书架', '电脑', '笔记本'], value: 'furniture', name: '家具维修' }
      ]

      for (const p of categoryPatterns) {
        if (p.keywords.some(kw => text.includes(kw))) {
          category = p.value
          categoryName = p.name
          break
        }
      }

      // 【增强】解析地点 - 支持更多模式
      let matched = false
      
      // 模式1：完整地点（栋号+建筑类型+房间号）
      const fullPatterns = [
        /(\d+栋.*?(教学楼|宿舍|公寓).*)/i,
        /(\d+号楼.*)/i,
        /(\d+公寓.*)/i
      ]
      
      for (const pattern of fullPatterns) {
        const match = text.match(pattern)
        if (match) {
          location = match[1] || match[0]
          matched = true
          break
        }
      }

      // 模式2：简单地点关键词
      if (!matched) {
        const locationPatterns = [
          /(宿舍\d+楼)/i,
          /(\d+室)/i,
          /(\d+宿舍)/i,
          /(学生公寓|宿舍|教学楼|图书馆|食堂|操场|实验室|办公室)/
        ]
        
        for (const pattern of locationPatterns) {
          const match = text.match(pattern)
          if (match) {
            location = match[0]
            matched = true
            break
          }
        }
      }

      // 清理描述，移除地点信息（避免重复）
      if (matched) {
        description = text.replace(location, '').trim()
        // 清理多余的描述词
        description = description.replace(/^(我的|我们|那个|这个|就是)/, '').trim()
        // 【修复】如果清理后描述太短或无意义，保留原始文本
        if (description.length < 2 || /^\d+$/.test(description)) {
          description = text  // 保留完整原始内容作为描述
        }
      }

      return { category, categoryName, location, description }
    }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }

.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  /* ==================== 【优化】更柔和的渐变背景 ==================== */
  background: linear-gradient(145deg, #6f86e8 0%, #7a67d8 52%, #8a6fe0 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-blob {
  position: absolute;
  border-radius: 50%;
  background: rgba(255,255,255,0.04);
  pointer-events: none;
  display: none;
}
.blob1 { width: 500px; height: 500px; top: -150px; right: -100px; }
.blob2 { width: 300px; height: 300px; top: 200px; left: -80px; }
.blob3 { width: 250px; height: 250px; bottom: 100px; right: 150px; }

/* 顶部导航 */
.chat-header {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: rgba(255,255,255,0.08);
  border-bottom: 1px solid rgba(255,255,255,0.12);
}

.header-center {
  display: flex;
  align-items: center;
  gap: 14px;
}
.ai-avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #a78bfa, #818cf8);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 3px 10px rgba(139, 92, 246, 0.24);
}
.header-title {
  font-size: 20px;
  font-weight: 700;
  color: white;
  margin: 0 0 2px;
}
.header-status {
  font-size: 13px;
  color: rgba(255,255,255,0.75);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}
.status-dot {
  width: 8px;
  height: 8px;
  background: #4ade80;
  border-radius: 50%;
  display: inline-block;
  box-shadow: 0 0 4px #4ade80;
}

.header-actions { display: flex; gap: 10px; }
.action-btn {
  height: 36px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 0 14px;
  transition: background-color 0.18s ease, border-color 0.18s ease, color 0.18s ease;
  font-size: 13px;
  font-weight: 500;
}
.export-btn {
  background: rgba(255,255,255,0.12);
  color: rgba(255,255,255,0.9);
  border: 1px solid rgba(255,255,255,0.15);
}
.export-btn:hover {
  background: rgba(102,126,234,0.4);
  color: white;
  border-color: rgba(102,126,234,0.5);
}
.archive-btn {
  background: rgba(255,255,255,0.12);
  color: rgba(255,255,255,0.9);
  border: 1px solid rgba(255,255,255,0.15);
}
.archive-btn:hover {
  background: rgba(16,185,129,0.28);
  color: white;
  border-color: rgba(16,185,129,0.38);
}
.clear-btn {
  background: rgba(255,255,255,0.12);
  color: rgba(255,255,255,0.9);
  border: 1px solid rgba(255,255,255,0.15);
}
.clear-btn:hover {
  background: rgba(239,68,68,0.35);
  color: white;
  border-color: rgba(239,68,68,0.4);
}
.btn-text {
  line-height: 1;
}

/* 消息区域 */
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 欢迎区 */
.welcome-area {
  text-align: center;
  padding: 40px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.welcome-icon {
  width: 88px;
  height: 88px;
  background: rgba(255,255,255,0.15);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 24px;
  box-shadow: 0 4px 18px rgba(0,0,0,0.1);
}
.welcome-title {
  font-size: 26px;
  font-weight: 700;
  color: white;
  margin: 0 0 12px;
}
.welcome-desc {
  font-size: 15px;
  color: rgba(255,255,255,0.75);
  max-width: 480px;
  line-height: 1.6;
  margin: 0 0 28px;
}
.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  max-width: 640px;
}
.quick-q-btn {
  background: rgba(255,255,255,0.12);
  border: 1px solid rgba(255,255,255,0.2);
  color: white;
  padding: 10px 20px;
  border-radius: 24px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease, border-color 0.2s ease, transform 0.2s ease;
}
.quick-q-btn:hover {
  background: rgba(255,255,255,0.22);
  border-color: rgba(255,255,255,0.4);
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 4px 14px rgba(0,0,0,0.1);
}

/* 消息列表 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

.user-msg { flex-direction: row-reverse; }

.msg-avatar {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}
.ai-avatar-sm {
  background: linear-gradient(135deg, #a78bfa, #818cf8);
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.22);
}
.user-avatar-sm {
  background: linear-gradient(135deg, #f472b6, #fb7185);
  box-shadow: 0 2px 8px rgba(244, 114, 182, 0.2);
}

.msg-content {
  max-width: 70%;
}
.msg-bubble {
  padding: 14px 18px;
  border-radius: 18px;
  font-size: 15px;
  line-height: 1.6;
  word-break: break-word;
}

.user-msg .msg-bubble {
  background: white;
  color: #1e293b;
  border-bottom-right-radius: 6px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.06);
}
.ai-msg .msg-bubble {
  background: rgba(255,255,255,0.15);
  color: white;
  border: 1px solid rgba(255,255,255,0.15);
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.06);
}
.msg-bubble :deep(.md-content) {
  line-height: 1.7;
}
.msg-bubble :deep(.md-content p) {
  margin: 0;
}
.msg-bubble :deep(.md-content p + p) {
  margin-top: 10px;
}
.msg-bubble :deep(.md-content h1),
.msg-bubble :deep(.md-content h2),
.msg-bubble :deep(.md-content h3),
.msg-bubble :deep(.md-content h4) {
  margin: 0 0 8px;
  line-height: 1.35;
  font-weight: 800;
}
.msg-bubble :deep(.md-content h1) { font-size: 22px; }
.msg-bubble :deep(.md-content h2) { font-size: 20px; }
.msg-bubble :deep(.md-content h3) { font-size: 18px; }
.msg-bubble :deep(.md-content h4) { font-size: 16px; }
.msg-bubble :deep(.md-content ul) {
  margin: 8px 0;
  padding-left: 20px;
}
.msg-bubble :deep(.md-content li) {
  margin: 4px 0;
}
.msg-bubble :deep(.md-content blockquote) {
  margin: 10px 0;
  padding: 8px 12px;
  border-left: 3px solid rgba(255,255,255,0.45);
  background: rgba(255,255,255,0.08);
  border-radius: 8px;
}
.msg-bubble :deep(.md-content code) {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 6px;
  background: rgba(15, 23, 42, 0.12);
  font-size: 13px;
  font-family: Consolas, 'Courier New', monospace;
}
.msg-bubble :deep(.md-content pre) {
  margin: 10px 0;
  padding: 12px 14px;
  overflow-x: auto;
  border-radius: 12px;
  background: rgba(15, 23, 42, 0.16);
}
.msg-bubble :deep(.md-content pre code) {
  display: block;
  padding: 0;
  background: transparent;
}

/* 加载动画 */
.loading-bubble {
  display: flex;
  gap: 6px;
  align-items: center;
  padding: 16px 22px;
}
.loading-dot {
  width: 8px;
  height: 8px;
  background: rgba(255,255,255,0.8);
  border-radius: 50%;
  animation: bounce 1.2s infinite;
}
.loading-dot:nth-child(2) { animation-delay: 0.2s; }
.loading-dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

/* 底部输入区 */
.chat-footer {
  position: relative;
  z-index: 10;
  padding: 16px 24px 24px;
  background: rgba(255,255,255,0.06);
  border-top: 1px solid rgba(255,255,255,0.08);
}
.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background: rgba(255,255,255,0.12);
  border: 1px solid rgba(255,255,255,0.18);
  border-radius: 20px;
  padding: 8px 8px 8px 12px;
  transition: background-color 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}
.input-wrapper:focus-within {
  background: rgba(255,255,255,0.18);
  border-color: rgba(255,255,255,0.35);
  box-shadow: 0 0 0 3px rgba(255,255,255,0.1);
}
.chat-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: white;
  font-size: 15px;
  resize: none;
  line-height: 44px;
  min-height: 44px;
  max-height: 120px;
  font-family: inherit;
  padding: 0 4px;
}
.chat-input::placeholder {
  color: rgba(255,255,255,0.55);
  line-height: 44px;
}

/* 上传按钮 */
.upload-btn {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.5), rgba(167, 139, 250, 0.4));
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 14px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.18s ease, background-color 0.18s ease, box-shadow 0.18s ease;
  flex-shrink: 0;
}
.upload-btn:hover {
  background: linear-gradient(135deg, #667eea, #764ba2);
  transform: scale(1.03);
  box-shadow: 0 4px 12px rgba(102,126,234,0.28);
}

/* 文件预览 */
.selected-files-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 8px 0 14px;
}
.file-preview-item {
  position: relative;
}
.image-preview {
  width: 64px;
  height: 64px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  border: 2px solid rgba(255,255,255,0.2);
}
.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.remove-file {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 22px;
  height: 22px;
  background: #ef4444;
  border: 2px solid white;
  border-radius: 50%;
  color: white;
  font-size: 14px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.doc-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255,255,255,0.15);
  padding: 8px 12px;
  border-radius: 10px;
  max-width: 160px;
}
.doc-icon {
  background: #667eea;
  color: white;
  font-size: 10px;
  font-weight: bold;
  padding: 4px 6px;
  border-radius: 4px;
}
.doc-name {
  color: white;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 90px;
}

.send-btn {
  width: 48px;
  height: 48px;
  background: rgba(255,255,255,0.1);
  border: 1.5px solid rgba(255,255,255,0.2);
  border-radius: 14px;
  color: rgba(255,255,255,0.4);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.18s ease, background-color 0.18s ease, border-color 0.18s ease, box-shadow 0.18s ease;
  flex-shrink: 0;
}
.send-btn.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-color: transparent;
  color: white;
  transform: scale(1);
  box-shadow: 0 4px 14px rgba(102,126,234,0.34);
}
.send-btn.active:hover {
  transform: scale(1.04);
  box-shadow: 0 5px 18px rgba(102,126,234,0.42);
}
.send-btn.active:active {
  transform: scale(0.95);
}
.send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: scale(1);
  box-shadow: none;
}

/* 滚动条 */
.chat-body::-webkit-scrollbar { width: 6px; }
.chat-body::-webkit-scrollbar-track { background: transparent; }
.chat-body::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.25); border-radius: 3px; }

/* 移动端 */
@media (max-width: 768px) {
  .chat-header { padding: 12px 16px; }
  .header-title { font-size: 16px; }
  .btn-text { display: none; }
  .action-btn { padding: 0 10px; }
  .chat-body { padding: 16px; }
  .msg-content { max-width: 85%; }
  .quick-questions { padding: 0; }
  .quick-q-btn { padding: 8px 14px; font-size: 13px; }
  .welcome-title { font-size: 22px; }
  .welcome-icon { width: 72px; height: 72px; }
}

/* 请假弹窗 */
.modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.2);
  animation: slideUp 0.3s ease;
}
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}
.modal-header h3 { margin: 0; font-size: 18px; font-weight: 600; color: #1a1a2e; }
.modal-close {
  width: 32px; height: 32px;
  background: #f5f5f5;
  border: none;
  border-radius: 50%;
  font-size: 20px;
  color: #666;
  cursor: pointer;
  transition: all 0.25s ease;
}
.modal-close:hover { background: #e0e0e0; }

.modal-body { padding: 24px; }
.form-group { margin-bottom: 16px; }
.form-group:last-child { margin-bottom: 0; }
.form-group label { display: block; margin-bottom: 6px; font-size: 14px; font-weight: 500; color: #333; }
.form-input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.25s ease;
  box-sizing: border-box;
}
.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102,126,234,0.15);
}
.radio-group { display: flex; gap: 16px; padding-top: 4px; }
.radio-label { font-size: 14px; color: #333; cursor: pointer; display: flex; align-items: center; gap: 6px; }

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px 24px 24px;
}
.btn-cancel,
.btn-confirm {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}
.btn-cancel { background: #f5f5f5; color: #666; }
.btn-cancel:hover { background: #e0e0e0; }
.btn-confirm { background: linear-gradient(135deg, #667eea, #764ba2); color: white; }
.btn-confirm:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(102,126,234,0.35); }
.archive-body {
  max-height: 420px;
  overflow-y: auto;
}
.archive-empty {
  padding: 32px 12px;
  text-align: center;
  color: #64748b;
  font-size: 14px;
}
.archive-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.archive-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
}
.archive-main {
  min-width: 0;
  flex: 1;
}
.archive-title {
  display: block;
  color: #1e293b;
  font-size: 14px;
  line-height: 1.4;
}
.archive-meta {
  margin-top: 4px;
  color: #64748b;
  font-size: 12px;
}
.archive-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
.archive-action {
  border: none;
  border-radius: 10px;
  padding: 8px 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}
.archive-action.restore {
  background: #dbeafe;
  color: #1d4ed8;
}
.archive-action.restore:hover {
  background: #bfdbfe;
}
.archive-action.delete {
  background: #fee2e2;
  color: #dc2626;
}
.archive-action.delete:hover {
  background: #fecaca;
}
/* 报修弹窗类别网格 */
.category-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.repair-modal .category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 10px 4px;
  background: #f8fafc;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 12px;
}

.repair-modal .category-item:hover {
  border-color: #667eea;
  background: #f0f4ff;
}

.repair-modal .category-item.active {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.repair-modal .category-item.active .category-name {
  color: white;
}

.repair-modal .category-icon {
  font-size: 20px;
}

.repair-modal .category-name {
  font-size: 11px;
  font-weight: 600;
  color: #374151;
}

@media (max-width: 768px) {
  .repair-modal .category-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 480px) {
  .repair-modal .category-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* ===== 深色模式适配 ===== */
body.dark-mode .chat-page {
  background: #1a1a2e;
}

body.dark-mode .chat-header {
  background: rgba(26, 26, 46, 0.8);
  border-bottom-color: rgba(255, 255, 255, 0.1);
}

body.dark-mode .chat-body {
  background: #1a1a2e;
}

body.dark-mode .chat-footer {
  background: rgba(26, 26, 46, 0.8);
  border-top-color: rgba(255, 255, 255, 0.1);
}

body.dark-mode .welcome-title {
  color: #e4e4e7;
}

body.dark-mode .welcome-desc {
  color: #a1a1aa;
}

body.dark-mode .welcome-icon {
  background: rgba(102, 126, 234, 0.2);
  color: #a78bfa;
}

body.dark-mode .quick-q-btn {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
  color: #e4e4e7;
}

body.dark-mode .quick-q-btn:hover {
  background: rgba(102, 126, 234, 0.35);
  border-color: rgba(102, 126, 234, 0.5);
}

body.dark-mode .user-msg .msg-bubble {
  background: #252542;
  color: #e4e4e7;
  border-color: rgba(255, 255, 255, 0.1);
}

body.dark-mode .ai-msg .msg-bubble {
  background: rgba(102, 126, 234, 0.25);
  color: #e4e4e7;
  border-color: rgba(102, 126, 234, 0.3);
}

body.dark-mode .chat-input {
  color: #e4e4e7;
}

body.dark-mode .chat-input::placeholder {
  color: #71717a;
}

body.dark-mode .input-wrapper {
  background: rgba(37, 37, 66, 0.8);
  border-color: rgba(255, 255, 255, 0.15);
}

body.dark-mode .input-wrapper:focus-within {
  background: rgba(37, 37, 66, 1);
  border-color: rgba(102, 126, 234, 0.5);
}

body.dark-mode .modal-content {
  background: #252542;
  color: #e4e4e7;
}

body.dark-mode .modal-header {
  border-bottom-color: rgba(255, 255, 255, 0.1);
}

body.dark-mode .modal-header h3 {
  color: #e4e4e7;
}

body.dark-mode .modal-close {
  background: rgba(255, 255, 255, 0.1);
  color: #a1a1aa;
}

body.dark-mode .modal-close:hover {
  background: rgba(255, 255, 255, 0.2);
}

body.dark-mode .form-group label {
  color: #a1a1aa;
}

body.dark-mode .form-input {
  background: #1a1a2e;
  border-color: rgba(255, 255, 255, 0.2);
  color: #e4e4e7;
}

body.dark-mode .btn-cancel {
  background: rgba(255, 255, 255, 0.1);
  color: #a1a1aa;
}

body.dark-mode .btn-cancel:hover {
  background: rgba(255, 255, 255, 0.15);
}

body.dark-mode .repair-modal .category-item {
  background: #1a1a2e;
  border-color: rgba(255, 255, 255, 0.15);
}

body.dark-mode .repair-modal .category-item:hover {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.5);
}

body.dark-mode .repair-modal .category-name {
  color: #a1a1aa;
}

body.dark-mode .doc-preview {
  background: rgba(255, 255, 255, 0.1);
}

body.dark-mode .doc-name {
  color: #e4e4e7;
}

body.dark-mode .scrollbar {
  background: rgba(255, 255, 255, 0.1);
}
</style>
<style>
.chat-page .chat-header {
  min-height: 72px !important;
  padding: 12px 28px !important;
  background: rgba(255, 255, 255, 0.92) !important;
  border-bottom: 1px solid #dfe8ef !important;
  box-shadow: 0 8px 24px rgba(58, 83, 103, 0.04) !important;
}

.chat-page .header-center {
  min-width: 0 !important;
  gap: 12px !important;
}

.chat-page .header-copy {
  min-width: 0 !important;
  display: grid !important;
  gap: 2px !important;
}

.chat-page .header-eyebrow {
  color: #7c8c99 !important;
  font-size: 12px !important;
  font-weight: 700 !important;
  line-height: 1 !important;
}

.chat-page .header-title {
  margin: 0 !important;
  color: #24313b !important;
  font-size: 22px !important;
  font-weight: 800 !important;
  line-height: 1.12 !important;
  letter-spacing: 0 !important;
}

.chat-page .header-status {
  margin: 0 !important;
  color: #758595 !important;
  font-size: 13px !important;
  line-height: 1.2 !important;
}

.chat-page .ai-avatar {
  width: 48px !important;
  height: 48px !important;
  border-radius: 16px !important;
  box-shadow: 0 10px 22px rgba(25, 182, 210, 0.18) !important;
}

.chat-page .ai-avatar svg {
  width: 22px !important;
  height: 22px !important;
}

.chat-page .header-actions {
  display: flex !important;
  align-items: center !important;
  gap: 8px !important;
}

.chat-page .action-btn {
  min-width: 72px !important;
  height: 38px !important;
  padding: 0 13px !important;
  border-radius: 12px !important;
  background: #ffffff !important;
  border: 1px solid #dfe8ef !important;
  color: #667684 !important;
  box-shadow: none !important;
  font-size: 13px !important;
  font-weight: 700 !important;
}

.chat-page .action-btn:hover {
  transform: translateY(-1px) !important;
}

.chat-page .export-btn:hover {
  background: #eef8fc !important;
  color: #1598b2 !important;
  border-color: #bde6f0 !important;
}

.chat-page .clear-btn:hover {
  background: #fff4f4 !important;
  color: #d85d5d !important;
  border-color: #f0caca !important;
}

body.dark-mode .chat-page .chat-header {
  background: rgba(24, 33, 48, 0.92) !important;
  border-bottom-color: rgba(255, 255, 255, 0.08) !important;
  box-shadow: none !important;
}

body.dark-mode .chat-page .header-eyebrow,
body.dark-mode .chat-page .header-status {
  color: #9aa8b8 !important;
}

body.dark-mode .chat-page .header-title {
  color: #edf3ff !important;
}

body.dark-mode .chat-page .action-btn {
  background: rgba(255, 255, 255, 0.04) !important;
  border-color: rgba(255, 255, 255, 0.08) !important;
  color: #aab7c5 !important;
}

@media (max-width: 768px) {
  .chat-page .chat-header {
    min-height: 64px !important;
    padding: 10px 14px !important;
  }

  .chat-page .ai-avatar {
    width: 42px !important;
    height: 42px !important;
    border-radius: 14px !important;
  }

  .chat-page .header-eyebrow {
    display: none !important;
  }

  .chat-page .header-title {
    font-size: 18px !important;
  }

  .chat-page .header-status {
    font-size: 12px !important;
  }

  .chat-page .action-btn {
    min-width: 40px !important;
    width: 40px !important;
    height: 36px !important;
    padding: 0 !important;
  }

  .chat-page .action-btn .btn-text {
    display: none !important;
  }
}
</style>
<style>
.chat-page .chat-header {
  padding: 10px 22px !important;
}

.chat-page .header-center {
  gap: 12px !important;
}

.chat-page .ai-avatar {
  width: 44px !important;
  height: 44px !important;
  border-radius: 13px !important;
}

.chat-page .ai-avatar svg {
  width: 20px !important;
  height: 20px !important;
}

.chat-page .header-title {
  font-size: 18px !important;
  line-height: 1.2 !important;
  font-weight: 700 !important;
  margin: 0 0 2px !important;
}

.chat-page .header-status {
  font-size: 12px !important;
  margin: 0 !important;
}

.chat-page .header-actions {
  gap: 8px !important;
}

.chat-page .action-btn {
  min-width: 56px !important;
  height: 30px !important;
  padding: 0 10px !important;
  border-radius: 10px !important;
  font-size: 12px !important;
  font-weight: 600 !important;
  background: #f8fbfd !important;
  color: #6f7f8e !important;
  border: 1px solid #dce7ef !important;
  box-shadow: none !important;
}

.chat-page .action-btn svg {
  width: 13px !important;
  height: 13px !important;
}

.chat-page .export-btn:hover {
  background: #edf8fc !important;
  color: #1598b2 !important;
  border-color: #bde6f0 !important;
}

.chat-page .clear-btn:hover {
  background: #fff3f3 !important;
  color: #d85d5d !important;
  border-color: #f0caca !important;
}
</style>
<style scoped>
.chat-page {
  background: linear-gradient(180deg, #f7f9fc 0%, #eef3f7 100%);
}

.bg-blob {
  display: none;
}

.chat-header {
  padding: 16px 28px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #dde7ef;
}

.header-center {
  gap: 16px;
}

.ai-avatar {
  background: linear-gradient(135deg, #1cb7d4, #14a5c0);
  box-shadow: 0 8px 20px rgba(25, 182, 210, 0.24);
}

.header-title {
  color: #24313b;
  font-size: 22px;
  font-weight: 700;
  line-height: 1.2;
}

.header-status {
  color: #6f8291;
  font-size: 13px;
}

.status-dot {
  background: #72d77b;
  box-shadow: 0 0 8px rgba(114, 215, 123, 0.4);
}

.header-actions {
  gap: 10px;
}

.action-btn {
  height: 38px;
  padding: 0 14px;
  border: 1px solid #dfe8ef;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  gap: 6px;
  box-shadow: 0 6px 14px rgba(58, 83, 103, 0.04);
}

.action-btn svg {
  width: 14px;
  height: 14px;
}

.export-btn,
.clear-btn {
  background: #f9fcfe;
  color: #697987;
}

.export-btn:hover {
  background: #edf8fc;
  color: #1598b2;
  border-color: #bde6f0;
}

.clear-btn:hover {
  background: #fff3f3;
  color: #d85d5d;
  border-color: #f0caca;
}

.chat-body {
  padding: 26px 28px;
  background: linear-gradient(180deg, #f7f9fc 0%, #eef3f7 100%);
}

.welcome-icon {
  background: linear-gradient(135deg, #1cb7d4, #14a5c0);
  box-shadow: 0 14px 32px rgba(25, 182, 210, 0.18);
}

.welcome-title {
  color: #23313c;
}

.welcome-desc {
  color: #748593;
}

.quick-q-btn {
  background: #ffffff;
  border: 1px solid #dbe7f1;
  color: #44525e;
  box-shadow: 0 8px 20px rgba(72, 96, 116, 0.06);
}

.quick-q-btn:hover {
  background: #f5fbfe;
  border-color: #c7eaf4;
  box-shadow: 0 12px 24px rgba(72, 96, 116, 0.1);
}

.ai-avatar-sm {
  background: linear-gradient(135deg, #1cb7d4, #14a5c0);
  box-shadow: 0 8px 18px rgba(25, 182, 210, 0.18);
}

.user-avatar-sm {
  background: linear-gradient(135deg, #31c85b, #21b64c);
  box-shadow: 0 8px 18px rgba(49, 200, 91, 0.18);
}

.msg-content {
  max-width: 74%;
}

.msg-bubble {
  border-radius: 22px;
  line-height: 1.65;
}

.user-msg .msg-bubble {
  background: linear-gradient(135deg, #1bb7d3, #0ea7c3);
  color: white;
  border-bottom-right-radius: 10px;
  box-shadow: 0 10px 24px rgba(25, 182, 210, 0.2);
}

.ai-msg .msg-bubble {
  background: #ffffff;
  color: #33414c;
  border: 1px solid #dfe8ef;
  border-bottom-left-radius: 10px;
  box-shadow: 0 12px 28px rgba(72, 96, 116, 0.08);
}

.loading-dot {
  background: #95a8b6;
}

.chat-footer {
  padding: 18px 28px 24px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(10px);
  border-top: 1px solid #dde7ef;
}

.input-wrapper {
  background: #ffffff;
  border: 1px solid #dde7ef;
  border-radius: 30px;
  padding: 10px 12px 10px 14px;
  box-shadow: 0 12px 26px rgba(72, 96, 116, 0.06);
}

.input-wrapper:focus-within {
  background: #ffffff;
  border-color: #cde4ee;
  box-shadow: 0 0 0 3px rgba(25, 182, 210, 0.08);
}

.chat-input {
  color: #34424d;
}

.chat-input::placeholder {
  color: #9ba9b4;
}

.upload-btn {
  background: #f7f9fc;
  border: 1px solid #dfe8ef;
  border-radius: 18px;
  color: #6c7b87;
}

.upload-btn:hover {
  background: #eef8fc;
  color: #199fba;
  box-shadow: none;
  transform: scale(1.04);
}

.image-preview {
  border-color: #e0e8ef;
}

.doc-preview {
  background: #f6f9fc;
}

.doc-icon {
  background: #1cb7d4;
}

.doc-name {
  color: #52616d;
}

.send-btn {
  background: #f8fafc;
  border: 1px solid #dfe8ef;
  border-radius: 18px;
  color: #bcc8d1;
}

.send-btn.active {
  background: linear-gradient(135deg, #1bb7d3, #0ea7c3);
  box-shadow: 0 10px 24px rgba(25, 182, 210, 0.25);
}

.send-btn.active:hover {
  transform: scale(1.05);
  box-shadow: 0 14px 28px rgba(25, 182, 210, 0.3);
}

.chat-body::-webkit-scrollbar {
  width: 8px;
}

.chat-body::-webkit-scrollbar-thumb {
  background: #d7e4ec;
  border-radius: 999px;
}

.form-input:focus {
  border-color: #19b6d2;
  box-shadow: 0 0 0 3px rgba(25, 182, 210, 0.12);
}

.btn-confirm {
  background: linear-gradient(135deg, #1bb7d3, #0ea7c3);
}

.btn-confirm:hover {
  box-shadow: 0 8px 18px rgba(25, 182, 210, 0.22);
}

.repair-modal .category-item:hover {
  border-color: #19b6d2;
  background: #eef8fc;
}

.repair-modal .category-item.active {
  border-color: #19b6d2;
  background: linear-gradient(135deg, #1bb7d3, #0ea7c3);
}

@media (max-width: 768px) {
  .chat-header {
    padding: 14px 16px;
  }

  .action-btn {
    height: 42px;
    padding: 0 12px;
    border-radius: 12px;
  }

  .chat-body {
    padding: 16px;
  }

  .chat-footer {
    padding: 14px 16px 18px;
  }
}
</style>
