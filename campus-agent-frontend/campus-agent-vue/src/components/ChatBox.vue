<template>
  <div class="chat-box d-flex flex-column">
    <!-- 顶部标题栏 -->
    <div class="chat-header">
      <div class="header-right">
        <span class="ai-tag">AI</span>
      </div>
    </div>

    <!-- 欢迎区域 -->
    <div class="welcome-area">
      <!-- 收起时显示的展开条 -->
      <div v-if="!welcomeVisible" class="welcome-expand-bar" @click="toggleWelcome">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
          <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
        </svg>
        <span>展开欢迎卡片</span>
      </div>

      <!-- 欢迎卡片（展开时显示） -->
      <div v-if="welcomeVisible" class="welcome-card">
        <div class="welcome-card-inner" style="position: relative;">
          <div class="welcome-header">
            <div class="welcome-avatar">
              <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
            </div>
            <div class="welcome-text">
              <h3>你好，我是校园小管家 ✨</h3>
              <p>点击快捷入口，或直接输入问题</p>
            </div>
          </div>
          <div class="quick-actions">
            <div class="quick-btn" @click="goToPage('/schedule')">
              <div class="quick-icon-wrap icon-purple">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11zM7 10h5v5H7z"/>
                </svg>
              </div>
              <span class="quick-btn-label">今日课表</span>
            </div>
            <div class="quick-btn" @click="goToPage('/dining')">
              <div class="quick-icon-wrap icon-orange">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M8.1 13.34l2.83-2.83L3.91 3.5c-1.56 1.56-1.56 4.09 0 5.66l4.19 4.18zm6.78-1.81c1.53.71 3.68.21 5.27-1.38 1.91-1.91 2.28-4.65.81-6.12-1.46-1.46-4.2-1.1-6.12.81-1.59 1.59-2.09 3.74-1.38 5.27L3.7 19.87l1.41 1.41L12 14.41l6.88 6.88 1.41-1.41L13.41 13l1.47-1.47z"/>
                </svg>
              </div>
              <span class="quick-btn-label">食堂推荐</span>
            </div>
            <div class="quick-btn" @click="goToPage('/maintenance')">
              <div class="quick-icon-wrap icon-green">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z"/>
                </svg>
              </div>
              <span class="quick-btn-label">报修电话</span>
            </div>
            <div class="quick-btn" @click="goToPage('/remind')">
              <div class="quick-icon-wrap icon-pink">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
                </svg>
              </div>
              <span class="quick-btn-label">提醒事项</span>
            </div>
          </div>
          <!-- 收起按钮 -->
          <button class="collapse-btn" type="button" @click="toggleWelcome" title="收起">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
              <path d="M19 13H5v-2h14v2z"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 对话内容区域 -->
    <div class="chat-content flex-grow-1 overflow-auto" id="chatContent">
      <!-- 对话消息列表 -->
      <div v-for="(msg, index) in msgList" :key="index" class="message-wrapper mb-3" :class="msg.isUser ? 'user-msg' : 'ai-msg'">
        <div v-if="!msg.isUser" class="ai-avatar-sm me-2">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm4.59-12.42L10 14.17l-2.59-2.58L6 13l4 4 8-8z"/>
          </svg>
        </div>
        <div class="message-bubble rounded-3 p-3" :class="msg.isUser ? 'user-bubble' : 'ai-bubble'">
          <template v-if="msg.isUser">
            <div v-if="msg.images && msg.images.length > 0" class="mb-2 d-flex flex-wrap gap-2">
              <img v-for="(img, idx) in msg.images" :key="idx" :src="img" class="rounded msg-img" @click="previewImage(img)" />
            </div>
            <div v-if="msg.docs && msg.docs.length > 0" class="mb-2">
              <div v-for="(doc, idx) in msg.docs" :key="idx" class="msg-doc d-flex align-items-center gap-2 rounded px-3 py-2 mb-1">
                <div class="doc-icon-sm" :style="{ background: getDocColor(doc.name) }">{{ getDocExt(doc.name) }}</div>
                <span class="text-truncate" style="max-width: 150px;">{{ doc.name }}</span>
              </div>
            </div>
            <div>{{ msg.content }}</div>
          </template>
          <div v-else class="ai-msg-content" v-html="renderMarkdown(msg.content)"></div>
          <ResultCard v-if="msg.data" :data="msg.data" :intent="msg.intent" />
        </div>
      </div>

      <!-- 图片预览模态框 -->
      <div v-if="previewSrc" class="preview-modal" @click.self="previewSrc = null">
        <img :src="previewSrc" class="preview-image" @click="previewSrc = null" />
        <button type="button" class="preview-close" @click="previewSrc = null">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" viewBox="0 0 24 24">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
        </button>
      </div>
    </div>

    <!-- 已选文件预览区 -->
    <div v-if="selectedImages.length > 0 || selectedDocs.length > 0" class="file-preview-bar">
      <div class="d-flex flex-wrap gap-2 align-items-center">
        <div v-for="(img, index) in selectedImages" :key="'img-'+index" class="position-relative img-preview-card">
          <img :src="img" class="rounded-2" style="width: 56px; height: 56px; object-fit: cover; cursor: pointer;" @click="previewImage(img)" />
          <button type="button" class="btn-close position-absolute top-0 end-0 translate-middle" style="font-size: 10px; background: #fff; border-radius: 50%; padding: 3px; box-shadow: 0 2px 4px rgba(0,0,0,0.2);" @click="removeImage(index)"></button>
        </div>
        <div v-for="(doc, index) in selectedDocs" :key="'doc-'+index" class="doc-preview-card d-flex align-items-center gap-2 px-2 py-1 bg-white rounded-2 shadow-sm">
          <div class="doc-icon rounded-1 d-flex align-items-center justify-content-center" :style="{ background: getDocColor(doc.name) }">
            <span class="text-white fw-bold" style="font-size: 10px;">{{ getDocExt(doc.name) }}</span>
          </div>
          <span class="text-truncate" style="max-width: 100px; font-size: 12px; color: #495057;">{{ doc.name }}</span>
          <button type="button" class="btn-close" style="font-size: 9px;" @click="removeDoc(index)"></button>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="input-area">
      <div class="input-inner">
        <!-- 上传按钮 -->
        <div class="position-relative">
          <button class="upload-btn" type="button" title="上传图片或文件" @click="triggerUpload">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 24 24">
              <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
            </svg>
          </button>
          <span v-if="totalSelectedCount > 0" class="badge-count">{{ totalSelectedCount }}</span>
        </div>
        <input ref="uploadInput" type="file" multiple accept="image/*,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.pdf" style="display: none;" @change="handleFileUpload" />
        <!-- 输入框 -->
        <input v-model="inputContent" type="text" class="chat-input flex-grow-1" placeholder="有什么想问的，尽管说…" @keyup.enter="sendMsg">
        <!-- 发送按钮 -->
        <button class="send-btn" type="button" @click="sendMsg">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 24 24">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import ResultCard from './ResultCard.vue'
export default {
  name: 'ChatBox',
  components: { ResultCard },
  data() {
    return {
      welcomeVisible: true,  // 欢迎卡片显示状态
      inputContent: '',      // 输入内容
      msgList: [],           // 消息列表
      userId: null,          // 用户ID（从cookies获取）
      selectedImages: [],    // 已选图片数组（base64，未发送）
      selectedDocs: [],      // 已选文档数组 {name, base64}
      previewSrc: null,      // 图片预览模态框
      isRecording: false,   // 语音录音状态
      mediaRecorder: null,  // 媒体录制器
      audioChunks: []       // 录音数据块
    }
  },
  computed: {
    // 已选文件总数
    totalSelectedCount() {
      return this.selectedImages.length + this.selectedDocs.length
    }
  },
  mounted() {
    // 从 cookies 获取用户ID
    this.userId = this.$cookies.get('userId')
    
    // 从 localStorage 恢复输入内容
    const savedInput = localStorage.getItem('chatInput_' + this.userId)
    if (savedInput) this.inputContent = savedInput
    // 从 localStorage 恢复欢迎卡片状态
    const savedWelcome = localStorage.getItem('welcomeVisible_' + this.userId)
    if (savedWelcome !== null) this.welcomeVisible = savedWelcome === 'true'
    // 从 localStorage 恢复消息列表
    const savedMsgs = localStorage.getItem('chatMsgs_' + this.userId)
    if (savedMsgs) {
      try {
        this.msgList = JSON.parse(savedMsgs)
      } catch (e) {
        console.error('解析历史消息失败:', e)
      }
    }
    // 加载服务端历史（仅首次加载时用，localStorage 有数据则跳过）
    if (!savedMsgs) this.loadHistory()
  },
  watch: {
    // 输入内容实时缓存
    inputContent(val) {
      localStorage.setItem('chatInput_' + this.userId, val)
    },
    // 消息列表变化时缓存
    msgList: {
      handler(val) {
        localStorage.setItem('chatMsgs_' + this.userId, JSON.stringify(val))
      },
      deep: true
    }
  },
methods: {
    // 切换欢迎卡片显示/收起
    toggleWelcome() {
      this.welcomeVisible = !this.welcomeVisible
      localStorage.setItem('welcomeVisible_' + this.userId, this.welcomeVisible)
    },
    // 加载对话历史
    async loadHistory() {
      try {
        const res = await this.$axios.get('/agent/history', {
          params: { userId: this.userId }
        })
        if (res.data.code === 200 && res.data.data) {
          // 将历史消息添加到消息列表
          res.data.data.forEach(msg => {
            this.msgList.push({
              isUser: msg.role === 'user',
              content: msg.content,
              images: [],
              docs: []
            })
          })
        }
      } catch (err) {
        console.error('加载历史失败:', err)
      }
    },
    // 触发上传（统一入口）
    triggerUpload() {
      this.$refs.uploadInput.click()
    },
    // 处理文件上传（图片+文档）
    handleFileUpload(event) {
      const files = event.target.files
      if (!files || files.length === 0) return
      
      let imageCount = this.selectedImages.length
      let docCount = this.selectedDocs.length
      
      for (let i = 0; i < files.length; i++) {
        const file = files[i]
        
        // 最多4张图片
        if (file.type.startsWith('image/') && imageCount < 4) {
          this.compressImage(file, 800, (base64) => {
            this.selectedImages.push(base64)
          })
          imageCount++
        }
        // 最多2个文档
        else if (!file.type.startsWith('image/') && docCount < 2) {
          this.convertFileToBase64(file, (base64) => {
            this.selectedDocs.push({ name: file.name, base64: base64 })
          })
          docCount++
        }
        // 超出限制时提示
        else if (file.type.startsWith('image/') && imageCount >= 4) {
          this.$message && this.$message.warning('最多只能上传4张图片')
          break
        }
        else if (docCount >= 2) {
          this.$message && this.$message.warning('最多只能上传2个文档')
          break
        }
      }
      
      // 文件大小校验（图片10MB，文档20MB）
      for (let i = 0; i < files.length; i++) {
        const file = files[i]
        if (file.type.startsWith('image/') && file.size > 10 * 1024 * 1024) {
          this.$message && this.$message.warning('图片大小不能超过10MB')
          return
        }
        if (!file.type.startsWith('image/') && file.size > 20 * 1024 * 1024) {
          this.$message && this.$message.warning('文件大小不能超过20MB')
          return
        }
      }
      
      event.target.value = ''
    },
    // 移除已选图片
    removeImage(index) {
      this.selectedImages.splice(index, 1)
    },
    // 移除已选文档
    removeDoc(index) {
      this.selectedDocs.splice(index, 1)
    },
    // 获取文档扩展名
    getDocExt(filename) {
      const ext = filename.split('.').pop()?.toUpperCase() || 'DOC'
      return ext.substring(0, 3)
    },
    // 获取文档图标颜色
    getDocColor(filename) {
      const ext = filename.split('.').pop()?.toLowerCase() || ''
      const colors = {
        pdf: '#dc3545',
        doc: '#0d6efd',
        docx: '#0d6efd',
        xls: '#198754',
        xlsx: '#198754',
        ppt: '#fd7e14',
        pptx: '#fd7e14'
      }
      return colors[ext] || '#6c757d'
    },
    // 文件转Base64
    convertFileToBase64(file, callback) {
      const reader = new FileReader()
      reader.onload = (e) => callback(e.target.result)
      reader.readAsDataURL(file)
    },
    // 压缩图片到指定宽度，返回base64
    compressImage(file, maxWidth = 800, callback) {
      const reader = new FileReader()
      reader.onload = (e) => {
        const img = new Image()
        img.onload = () => {
          const canvas = document.createElement('canvas')
          let width = img.width
          let height = img.height
          
          // 如果图片已经小于最大宽度，直接返回
          if (width <= maxWidth) {
            callback(e.target.result)
            return
          }
          
          // 按比例缩放
          const ratio = maxWidth / width
          width = maxWidth
          height = Math.round(height * ratio)
          
          canvas.width = width
          canvas.height = height
          const ctx = canvas.getContext('2d')
          ctx.drawImage(img, 0, 0, width, height)
          
          // 输出压缩后的base64（质量0.7）
          const compressed = canvas.toDataURL('image/jpeg', 0.7)
          callback(compressed)
        }
        img.src = e.target.result
      }
      reader.readAsDataURL(file)
    },
    // 语音输入（录音转文字需要后端ASR，这里仅做录音演示）
    async toggleVoice() {
      if (this.isRecording) {
        // 停止录音
        if (this.mediaRecorder) {
          this.mediaRecorder.stop()
          this.mediaRecorder.onstop = () => {
            this.audioChunks = []
            alert('语音已录制（ASR语音转文字功能待接入）')
          }
        }
        this.isRecording = false
      } else {
        // 开始录音
        try {
          const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
          this.mediaRecorder = new MediaRecorder(stream)
          this.audioChunks = []
          this.mediaRecorder.ondataavailable = (e) => {
            if (e.data.size > 0) this.audioChunks.push(e.data)
          }
          this.mediaRecorder.start()
          this.isRecording = true
        } catch (err) {
          alert('请允许麦克风权限后使用语音输入')
          console.error(err)
        }
      }
    },
    // 清除对话历史
    clearHistory() {
      if (confirm('确定要清除当前对话历史吗？')) {
        this.msgList = []
        // 调用后端清除历史
        this.$axios.post('/agent/clear-history', null, {
          params: { userId: this.userId }
        }).then(() => {
          this.$message && this.$message.success('对话历史已清除')
        }).catch(() => {
          // 后端未实现清除接口时静默失败
        })
      }
    },
    // 跳转到指定页面
    goToPage(path) {
      this.$router.push(path)
    },
    // 预览大图
    previewImage(src) {
      this.previewSrc = src
    },
    // 导出聊天记录
    async exportChat() {
      try {
        const res = await this.$axios.get('/agent/export', {
          params: { userId: this.userId, format: 'md' }
        })
        if (res.data.code === 200) {
          const { content, filename } = res.data.data
          // 创建下载
          const blob = new Blob([content], { type: 'text/markdown;charset=utf-8' })
          const url = URL.createObjectURL(blob)
          const link = document.createElement('a')
          link.href = url
          link.download = filename
          link.click()
          URL.revokeObjectURL(url)
          this.$message && this.$message.success('导出成功！')
        }
      } catch (err) {
        console.error('导出失败:', err)
        this.$message && this.$message.error('导出失败，请重试')
      }
    },
    // 清空聊天记录
    async clearChat() {
      if (!confirm('确定要清空所有聊天记录吗？此操作不可恢复。')) return
      try {
        const res = await this.$axios.post('/agent/clear-history', null, {
          params: { userId: this.userId }
        })
        if (res.data.code === 200) {
          this.msgList = []
          this.$message && this.$message.success('聊天记录已清空')
        }
      } catch (err) {
        console.error('清空失败:', err)
        this.$message && this.$message.error('清空失败，请重试')
      }
    },
    // 轻量markdown渲染（纯文本时原样输出，有markdown符号时转为HTML）
    renderMarkdown(text) {
      if (!text) return ''
      let html = text
        // 转义HTML特殊字符，防止XSS
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
      // 检查是否包含markdown标记
      const hasMd = /[#*>\-_~[!]/.test(text)
      if (!hasMd) return text  // 纯文本直接返回，不包裹html标签
      // 有markdown → 逐步转换
      html = html
        // 代码块 ```code``` → <pre><code>
        .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
        // 行内代码 `code` → <code>
        .replace(/`([^`]+)`/g, '<code>$1</code>')
        // 标题 ### → <h3> (支持h1-h4)
        .replace(/^#### (.+)$/gm, '<h4>$1</h4>')
        .replace(/^### (.+)$/gm, '<h3>$1</h3>')
        .replace(/^## (.+)$/gm, '<h2>$1</h2>')
        .replace(/^# (.+)$/gm, '<h1>$1</h1>')
        // 粗斜体 ***text*** → <strong><em>
        .replace(/\*\*\*(.+?)\*\*\*/g, '<strong><em>$1</em></strong>')
        // 粗体 **text** → <strong>
        .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
        // 斜体 *text* → <em>（排除已处理的粗体）
        .replace(/(?<!\*)\*([^*]+)\*(?!\*)/g, '<em>$1</em>')
        // 引用 > text → <blockquote>
        .replace(/^&gt; (.+)$/gm, '<blockquote>$1</blockquote>')
        // 无序列表 - item / * item
        .replace(/^[-*] (.+)$/gm, '<li>$1</li>')
        // 连续的<li>用<ul>包裹（简单处理）
        .replace(/(<li>.*<\/li>(\r?\n)?)+/g, '<ul>$&</ul>')
        // 段落分隔：两个以上换行
        .replace(/\n\n+/g, '</p><p>')
        // 单个换行 → <br>
        .replace(/\n/g, '<br>')
      return '<p>' + html + '</p>'
    },
    // 发送消息
    async sendMsg() {
      const text = this.inputContent.trim()
      const hasImages = this.selectedImages.length > 0
      const hasDocs = this.selectedDocs.length > 0
      
      if (!text && !hasImages && !hasDocs) {
        this.$message && this.$message.warning('请输入内容或选择图片/文件！')
        return
      }
      
      // 1. 添加用户消息到列表（带图片和文档预览）
      this.msgList.push({
        isUser: true,
        content: text || (hasDocs ? '(文件)' : '(图片)'),
        images: [...this.selectedImages],
        docs: [...this.selectedDocs]
      })
      
      const content = text
      const images = [...this.selectedImages]
      const docs = [...this.selectedDocs]
      
      this.inputContent = ''
      this.selectedImages = []
      this.selectedDocs = []
      
      // 2. 滚动到底部
      this.$nextTick(() => {
        const chatContent = document.getElementById('chatContent')
        chatContent.scrollTop = chatContent.scrollHeight
      })
      
      // 3. 调用后端接口（区分有图/有文档/纯文本）
      try {
        let res
        if (hasImages) {
          // 有图片：使用VL模型（支持看图对话）
          const formData = new FormData()
          formData.append('content', content)
          formData.append('userId', this.userId)
          formData.append('imageBase64', images[0])
          // 如果同时有文档，也传递文档
          if (hasDocs) {
            formData.append('docBase64', docs[0].base64)
            formData.append('docName', docs[0].name)
          }
          res = await this.$axios.post('/agent/chat-with-image', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
          })
        } else {
          // 纯文本或只有文档：使用普通对话
          if (hasDocs) {
            // 有文档：用FormData传递
            const formData = new FormData()
            formData.append('content', content || '')
            formData.append('userId', this.userId)
            formData.append('docBase64', docs[0].base64)
            formData.append('docName', docs[0].name)
            res = await this.$axios.post('/agent/chat', formData, {
              headers: { 'Content-Type': 'multipart/form-data' }
            })
          } else {
            // 纯文本
            res = await this.$axios.post('/agent/chat', null, {
              params: {
                content: content,
                userId: this.userId
              }
            })
          }
        }
        if (res.data.code === 200) {
          // 4. 添加机器人消息到列表
          this.msgList.push({
            isUser: false,
            content: res.data.data.reply,
            data: res.data.data.data,
            intent: res.data.data.intent
          })
        } else {
          this.msgList.push({
            isUser: false,
            content: '服务出错了：' + res.data.msg
          })
        }
      } catch (err) {
        console.error(err)
        this.msgList.push({
          isUser: false,
          content: '网络出错了，请重试！'
        })
      } finally {
        // 5. 滚动到底部
        this.$nextTick(() => {
          const chatContent = document.getElementById('chatContent')
          chatContent.scrollTop = chatContent.scrollHeight
        })
      }
    },
    // 退出登录
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        // 清除cookies
        this.$cookies.remove('userId')
        this.$cookies.remove('username')
        this.$cookies.remove('nickname')
        // 清除localStorage
        localStorage.removeItem('campus-agent-remembered-username')
        // 尝试使用router跳转，如果失败则使用window.location
        try {
          if (this.$router) {
            this.$router.push('/login')
          } else {
            window.location.href = '/login'
          }
        } catch (e) {
          console.error('路由跳转失败，使用window.location:', e)
          window.location.href = '/login'
        }
      }
    }
  }
}
</script>

<style scoped>
/* ===== 整体容器 ===== */
.chat-box {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
  height: 700px;
  display: flex;
  flex-direction: column;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.15),
              0 0 0 1px rgba(102, 126, 234, 0.05),
              inset 0 1px 0 rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(102, 126, 234, 0.1);
  position: relative;
}
.chat-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.3), transparent);
  z-index: 1;
}

/* ===== 顶部标题栏 ===== */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.08);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  backdrop-filter: blur(10px);
  position: relative;
}
.chat-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.2), transparent);
}
.header-right {
  display: flex;
  align-items: center;
}
.ai-tag {
  padding: 4px 12px;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: white;
  font-size: 12px;
  font-weight: 700;
  border-radius: 20px;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.3);
}

/* ===== 欢迎区域 ===== */
.welcome-area {
  padding: 20px 24px 24px;
}
.welcome-expand-bar {
  padding: 12px 18px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
  color: #667eea;
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1.5px dashed rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}
.welcome-expand-bar::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.6s ease;
}
.welcome-expand-bar:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.12) 0%, rgba(118, 75, 162, 0.12) 100%);
  border-color: #667eea;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.15);
}
.welcome-expand-bar:hover::before {
  left: 100%;
}
.welcome-card {
  border-radius: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
  animation: cardSlideIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.25);
  position: relative;
}
.welcome-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
}
@keyframes cardSlideIn {
  from { opacity: 0; transform: translateY(-20px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.welcome-card-inner {
  padding: 24px 24px 20px;
  position: relative;
}
.welcome-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}
.welcome-avatar {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}
.welcome-avatar:hover {
  transform: scale(1.05) rotate(5deg);
}
.welcome-text h3 {
  font-size: 16px;
  font-weight: 700;
  color: white;
  margin: 0 0 4px;
  letter-spacing: -0.2px;
}
.welcome-text p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0;
  font-weight: 400;
}
.collapse-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}
.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.35);
  transform: rotate(90deg) scale(1.1);
}
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 8px;
  background: rgba(255, 255, 255, 0.12);
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  color: white;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}
.quick-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}
.quick-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.15);
}
.quick-btn:hover::before {
  opacity: 1;
}
.quick-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.quick-btn:hover .quick-icon-wrap {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}
.icon-purple { background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%); }
.icon-orange { background: linear-gradient(135deg, #f472b6 0%, #fb7185 100%); }
.icon-green { background: linear-gradient(135deg, #06b6d4 0%, #22d3ee 100%); }
.icon-pink { background: linear-gradient(135deg, #22c55e 0%, #4ade80 100%); }
.quick-btn-label {
  font-size: 12px;
  font-weight: 600;
  position: relative;
  z-index: 1;
}

/* ===== 对话内容区 ===== */
.chat-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  background: linear-gradient(135deg, rgba(248, 249, 255, 0.8) 0%, rgba(245, 247, 255, 0.8) 100%);
  backdrop-filter: blur(10px);
  position: relative;
}
.chat-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.1), transparent);
}
.chat-content::-webkit-scrollbar {
  width: 6px;
}
.chat-content::-webkit-scrollbar-track {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 3px;
}
.chat-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
  transition: all 0.3s ease;
}
.chat-content::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4799 100%);
}

/* ===== 消息样式 ===== */
.message-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  margin-bottom: 20px;
  animation: msgSlide 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes msgSlide {
  from { opacity: 0; transform: translateY(20px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.user-msg { justify-content: flex-end; }
.ai-msg { justify-content: flex-start; }

.ai-avatar-sm {
  width: 36px;
  height: 36px;
  min-width: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  border: 2px solid white;
  transition: all 0.3s ease;
}
.ai-avatar-sm:hover {
  transform: scale(1.1) rotate(10deg);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}
.message-bubble {
  max-width: 70%;
  font-size: 14px;
  line-height: 1.7;
  animation: bubbleIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
}
@keyframes bubbleIn {
  from { opacity: 0; transform: translateY(10px) scale(0.9); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.user-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px 20px 4px 20px;
  padding: 12px 16px;
  font-size: 14px;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.25);
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.2);
}
.user-bubble::before {
  content: '';
  position: absolute;
  bottom: 0;
  right: -6px;
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  clip-path: polygon(0 0, 100% 0, 100% 100%);
}
.ai-bubble {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
  border: 1px solid rgba(102, 126, 234, 0.15);
  border-radius: 20px 20px 20px 4px;
  padding: 14px 18px;
  color: #1f2937;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(10px);
  position: relative;
  border-left: 4px solid #667eea;
}
.ai-bubble::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: -6px;
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
  clip-path: polygon(0 0, 100% 0, 0 100%);
  border-left: 1px solid rgba(102, 126, 234, 0.15);
  border-bottom: 1px solid rgba(102, 126, 234, 0.15);
}

/* ===== 消息图片/文档 ===== */
.msg-img {
  max-width: 120px;
  max-height: 120px;
  object-fit: cover;
  cursor: pointer;
  border-radius: 8px;
  margin-top: 6px;
}
.msg-doc {
  background: rgba(255,255,255,0.15);
  font-size: 12px;
  border-radius: 8px;
  padding: 8px 12px;
}
.ai-msg .msg-doc { background: #f3f4f6; }
.doc-icon-sm {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 9px;
  font-weight: 700;
  color: white;
}

/* ===== 图片预览 ===== */
.preview-modal {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.85);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}
.preview-image {
  max-width: 90vw;
  max-height: 90vh;
  object-fit: contain;
  border-radius: 12px;
}
.preview-close {
  position: absolute;
  top: 20px; right: 20px;
  background: rgba(255,255,255,0.15);
  border: none;
  color: white;
  width: 40px; height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

/* ===== 文件预览条 ===== */
.file-preview-bar {
  padding: 12px 24px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(10px);
  position: relative;
}
.file-preview-bar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.2), transparent);
}

/* ===== 底部输入区 ===== */
.input-area {
  padding: 16px 24px 20px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(10px);
  position: relative;
}
.input-area::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.3), transparent);
}
.input-inner {
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, rgba(248, 250, 255, 0.9) 0%, rgba(245, 247, 255, 0.9) 100%);
  border: 1.5px solid rgba(102, 126, 234, 0.2);
  border-radius: 16px;
  padding: 10px 14px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.1);
  position: relative;
  overflow: hidden;
}
.input-inner::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.1), transparent);
  transition: left 0.6s ease;
}
.input-inner:focus-within {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
  border-color: #667eea;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.2), 0 0 0 4px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}
.input-inner:focus-within::before {
  left: 100%;
}
.upload-btn {
  width: 40px;
  height: 40px;
  min-width: 40px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}
.upload-btn:active {
  transform: scale(0.95);
}
.upload-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
}
.badge-count {
  position: absolute;
  top: -6px; right: -6px;
  min-width: 20px; height: 20px;
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
  font-size: 11px;
  font-weight: 700;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(248, 113, 113, 0.3);
  border: 2px solid white;
  z-index: 10;
}
.chat-input {
  border: none;
  background: transparent;
  padding: 8px 6px;
  font-size: 15px;
  color: #1f2937;
  outline: none;
  flex: 1;
  min-width: 0;
  font-weight: 400;
  letter-spacing: -0.1px;
}
.chat-input::placeholder {
  color: rgba(102, 126, 234, 0.5);
  font-weight: 400;
}
.send-btn {
  width: 44px;
  height: 44px;
  min-width: 44px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
}
.send-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.1) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}
.send-btn:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4), 0 0 0 4px rgba(102, 126, 234, 0.1);
}
.send-btn:hover::before {
  opacity: 1;
}
.send-btn:active {
  transform: scale(0.95) rotate(0deg);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

/* ===== 文件预览卡片 ===== */
.img-preview-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
  border-radius: 12px;
  padding: 4px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}
.img-preview-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}
.img-preview-card:hover {
  transform: translateY(-2px) scale(1.05);
  border-color: #667eea;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.2);
}
.img-preview-card:hover::before {
  opacity: 1;
}
.img-preview-card img {
  border-radius: 8px;
  transition: transform 0.3s ease;
}
.img-preview-card:hover img {
  transform: scale(1.05);
}
.doc-preview-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 10px;
  padding: 8px 12px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}
.doc-preview-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}
.doc-preview-card:hover {
  transform: translateY(-2px) scale(1.05);
  border-color: #667eea;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.2);
}
.doc-preview-card:hover::before {
  opacity: 1;
}
.doc-icon {
  width: 32px; height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 11px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}
.doc-preview-card:hover .doc-icon {
  transform: scale(1.1) rotate(5deg);
}

/* ===== AI消息 Markdown ===== */
.ai-msg-content :deep(h1),
.ai-msg-content :deep(h2),
.ai-msg-content :deep(h3),
.ai-msg-content :deep(h4) {
  font-size: 1em;
  font-weight: bold;
  margin: 0.3em 0;
}
.ai-msg-content :deep(strong) { font-weight: bold; }
.ai-msg-content :deep(em) { font-style: italic; }
.ai-msg-content :deep(code) {
  background: #f0f2f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9em;
  color: #c7254e;
}
.ai-msg-content :deep(pre) {
  background: #1e293b;
  color: #e2e8f0;
  padding: 10px 14px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 6px 0;
}
.ai-msg-content :deep(blockquote) {
  border-left: 3px solid #a5b4fc;
  padding-left: 10px;
  margin: 6px 0;
  color: #6b7280;
}
.ai-msg-content :deep(ul) { padding-left: 18px; margin: 4px 0; }
.ai-msg-content :deep(li) { margin: 2px 0; }
.ai-msg-content :deep(p) { margin: 2px 0; }
</style>
