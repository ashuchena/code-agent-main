<template>
  <div class="page-container">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>

    <div class="main-content">
      <div class="row justify-content-center">
        <div class="col-lg-8 col-xl-7">
          <button class="back-btn" @click="$router.push('/home')">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            返回
          </button>

          <!-- 反馈类型选择 -->
          <div v-if="!selectedType" class="type-selection">
            <div class="page-header text-center mb-4">
              <div class="header-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M20 2H4c-1.1 0-1.99.9-1.99 2L2 22l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-7 12h-2v-2h2v2zm0-4h-2V6h2v4z"/>
                </svg>
              </div>
              <h2 class="header-title">问题反馈</h2>
              <p class="header-subtitle">请选择反馈类型</p>
            </div>

            <div class="feedback-types-grid">
              <div class="feedback-type-card" @click="selectType('ask')">
                <div class="type-icon" style="background: linear-gradient(135deg, #3b82f6, #1d4ed8);">
                  <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2-1.09V17h2V9L12 3zm6.82 6L12 12.72 5.18 9 12 5.28 18.82 9zM17 15.99l-5 2.73-5-2.73v-3.72L12 15l5-2.73v3.72z"/>
                  </svg>
                </div>
                <h3 class="type-title">📚 向老师提问</h3>
                <p class="type-desc">学习问题、课程咨询、学业规划</p>
              </div>

              <div class="feedback-type-card" @click="selectType('suggest')">
                <div class="type-icon" style="background: linear-gradient(135deg, #f59e0b, #d97706);">
                  <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M9 21c0 .5.4 1 1 1h4c.6 0 1-.5 1-1v-1H9v1zm3-19C8.1 2 5 5.1 5 9c0 2.4 1.2 4.5 3 5.7V17c0 .5.4 1 1 1h6c.6 0 1-.5 1-1v-2.3c1.8-1.3 3-3.4 3-5.7 0-3.9-3.1-7-7-7z"/>
                  </svg>
                </div>
                <h3 class="type-title">💡 投诉与建议</h3>
                <p class="type-desc">校园服务、管理建议、投诉反馈</p>
              </div>

              <div class="feedback-type-card" @click="selectType('dining')">
                <div class="type-icon" style="background: linear-gradient(135deg, #ec4899, #be185d);">
                  <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M11 9H9V2H7v7H5V2H3v7c0 2.12 1.66 3.84 3.75 3.97V22h2.5v-9.03C11.34 12.84 13 11.12 13 9V2h-2v7zm5-3v8h2.5v8H21V2c-2.76 0-5 2.24-5 4z"/>
                  </svg>
                </div>
                <h3 class="type-title">🍽️ 食堂餐饮反馈</h3>
                <p class="type-desc">菜品质量、卫生问题、价格建议</p>
              </div>

              <div class="feedback-type-card" @click="selectType('dorm')">
                <div class="type-icon" style="background: linear-gradient(135deg, #06b6d4, #0891b2);">
                  <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
                  </svg>
                </div>
                <h3 class="type-title">🏠 宿舍管理反馈</h3>
                <p class="type-desc">住宿环境、设施维修、管理服务</p>
              </div>

              <div class="feedback-type-card" @click="selectType('course')">
                <div class="type-icon" style="background: linear-gradient(135deg, #10b981, #059669);">
                  <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z"/>
                  </svg>
                </div>
                <h3 class="type-title">📖 课程教学反馈</h3>
                <p class="type-desc">教学质量、课程安排、教师评价</p>
              </div>

              <div class="feedback-type-card" @click="selectType('safety')">
                <div class="type-icon" style="background: linear-gradient(135deg, #ef4444, #dc2626);">
                  <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-1 6h2v2h-2V7zm0 4h2v6h-2v-6z"/>
                  </svg>
                </div>
                <h3 class="type-title">⚠️ 校园安全隐患</h3>
                <p class="type-desc">安全问题、紧急事件、风险报告</p>
              </div>
            </div>

            <!-- 查看反馈记录按钮 -->
            <div class="view-records-section">
              <button class="view-records-btn" @click="showRecords = true">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
                </svg>
                查看我的反馈记录
              </button>
            </div>
          </div>

          <!-- 反馈表单 -->
          <div v-else-if="selectedType && !showRecords" class="form-section">
            <div class="form-header">
              <button class="back-btn-small" @click="selectedType = null">
                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                  <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
                </svg>
                返回选择
              </button>
              <h2 class="form-title">{{ getTypeName(selectedType) }}</h2>
            </div>

            <!-- 向老师提问 -->
            <template v-if="selectedType === 'ask'">
              <div class="form-group">
                <label>课程名称 <span class="required">*</span></label>
                <input v-model="formData.courseName" type="text" placeholder="如：高等数学、大学英语" />
              </div>
              <div class="form-group">
                <label>问题类型</label>
                <select v-model="formData.questionType">
                  <option value="">请选择</option>
                  <option value="homework">作业问题</option>
                  <option value="exam">考试相关</option>
                  <option value="courseware">课件资料</option>
                  <option value="other">其他</option>
                </select>
              </div>
              <div class="form-group">
                <label>详细描述 <span class="required">*</span></label>
                <textarea v-model="formData.description" placeholder="请详细描述你的问题..." rows="4"></textarea>
              </div>
            </template>

            <!-- 投诉与建议 -->
            <template v-else-if="selectedType === 'suggest'">
              <div class="form-group">
                <label>反馈地点 <span class="required">*</span></label>
                <input v-model="formData.location" type="text" placeholder="如：图书馆、食堂二楼" />
              </div>
              <div class="form-group">
                <label>问题类型</label>
                <select v-model="formData.suggestType">
                  <option value="">请选择</option>
                  <option value="service">服务质量</option>
                  <option value="facility">设施问题</option>
                  <option value="management">管理建议</option>
                  <option value="other">其他</option>
                </select>
              </div>
              <div class="form-group">
                <label>详细描述 <span class="required">*</span></label>
                <textarea v-model="formData.description" placeholder="请详细描述您的问题或建议..." rows="4"></textarea>
              </div>
            </template>

            <!-- 食堂餐饮反馈 -->
            <template v-else-if="selectedType === 'dining'">
              <div class="form-group">
                <label>食堂名称 <span class="required">*</span></label>
                <select v-model="formData.canteenName">
                  <option value="">请选择食堂</option>
                  <option value="一食堂">第一食堂</option>
                  <option value="二食堂">第二食堂</option>
                  <option value="三食堂">第三食堂</option>
                  <option value="四食堂">第四食堂</option>
                  <option value="其他">其他</option>
                </select>
              </div>
              <div class="form-group">
                <label>档口位置</label>
                <input v-model="formData.stallLocation" type="text" placeholder="如：一楼西侧、二楼川菜窗口" />
              </div>
              <div class="form-group">
                <label>反馈类型</label>
                <select v-model="formData.diningType">
                  <option value="">请选择</option>
                  <option value="quality">菜品质量问题</option>
                  <option value="hygiene">卫生问题</option>
                  <option value="price">价格问题</option>
                  <option value="service">服务态度</option>
                  <option value="suggest">改进建议</option>
                </select>
              </div>
              <div class="form-group">
                <label>详细描述 <span class="required">*</span></label>
                <textarea v-model="formData.description" placeholder="请描述具体问题..." rows="4"></textarea>
              </div>
            </template>

            <!-- 宿舍管理反馈 -->
            <template v-else-if="selectedType === 'dorm'">
              <div class="form-group">
                <label>宿舍楼号 <span class="required">*</span></label>
                <input v-model="formData.buildingNo" type="text" placeholder="如：3号楼、5号楼" />
              </div>
              <div class="form-group">
                <label>房间号 <span class="required">*</span></label>
                <input v-model="formData.roomNo" type="text" placeholder="如：201、301" />
              </div>
              <div class="form-group">
                <label>问题类型</label>
                <select v-model="formData.dormType">
                  <option value="">请选择</option>
                  <option value="water">水电问题</option>
                  <option value="furniture">家具维修</option>
                  <option value="network">网络问题</option>
                  <option value="door">门窗问题</option>
                  <option value="clean">卫生问题</option>
                  <option value="other">其他</option>
                </select>
              </div>
              <div class="form-group">
                <label>详细描述 <span class="required">*</span></label>
                <textarea v-model="formData.description" placeholder="请描述具体问题..." rows="4"></textarea>
              </div>
            </template>

            <!-- 课程教学反馈 -->
            <template v-else-if="selectedType === 'course'">
              <div class="form-group">
                <label>课程名称 <span class="required">*</span></label>
                <input v-model="formData.courseName" type="text" placeholder="请输入课程名称" />
              </div>
              <div class="form-group">
                <label>授课教师</label>
                <input v-model="formData.teacherName" type="text" placeholder="请输入教师姓名" />
              </div>
              <div class="form-group">
                <label>反馈类型</label>
                <select v-model="formData.courseType">
                  <option value="">请选择</option>
                  <option value="quality">教学质量</option>
                  <option value="arrange">课程安排</option>
                  <option value="material">教材课件</option>
                  <option value="practice">实践环节</option>
                  <option value="suggest">改进建议</option>
                </select>
              </div>
              <div class="form-group">
                <label>详细描述 <span class="required">*</span></label>
                <textarea v-model="formData.description" placeholder="请详细描述您的反馈..." rows="4"></textarea>
              </div>
            </template>

            <!-- 校园安全隐患 -->
            <template v-else-if="selectedType === 'safety'">
              <div class="form-group">
                <label>隐患地点 <span class="required">*</span></label>
                <input v-model="formData.location" type="text" placeholder="请描述具体位置" />
              </div>
              <div class="form-group">
                <label>紧急程度 <span class="required">*</span></label>
                <select v-model="formData.urgencyLevel">
                  <option value="">请选择</option>
                  <option value="low">一般</option>
                  <option value="medium">较紧急</option>
                  <option value="high" class="urgent">紧急</option>
                  <option value="critical" class="critical">非常紧急</option>
                </select>
              </div>
              <div class="form-group">
                <label>隐患类型</label>
                <select v-model="formData.safetyType">
                  <option value="">请选择</option>
                  <option value="fire">消防安全</option>
                  <option value="electric">用电安全</option>
                  <option value="building">建筑安全</option>
                  <option value="equipment">设施安全</option>
                  <option value="other">其他</option>
                </select>
              </div>
              <div class="form-group">
                <label>详细描述 <span class="required">*</span></label>
                <textarea v-model="formData.description" placeholder="请详细描述安全隐患情况..." rows="4"></textarea>
              </div>
            </template>

            <!-- 通用字段：联系方式 -->
            <div class="form-group">
              <label>联系方式 <span class="optional">（选填）</span></label>
              <input v-model="formData.contact" type="text" placeholder="手机号或QQ，方便我们联系你" />
            </div>
            <button class="submit-btn" @click="submitRepair">提交反馈</button>
          </div>

          <!-- 反馈记录 -->
          <div v-if="showRecords" class="records-section">
            <div class="form-header">
              <button class="back-btn-small" @click="showRecords = false; selectedType = null">
                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                  <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
                </svg>
                返回
              </button>
              <h2 class="form-title">我的反馈记录</h2>
              <button class="refresh-btn" @click="loadRepairList">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
                </svg>
                刷新
              </button>
            </div>

            <!-- 显示已完成开关 -->
            <div class="filter-section">
              <label class="toggle-label">
                <input type="checkbox" v-model="showCompleted" class="toggle-input" />
                <span class="toggle-switch"></span>
                <span class="toggle-text">显示已完成</span>
              </label>
            </div>

            <div v-if="loading" class="loading-state">
              <div class="spinner"></div>
              <p>加载中...</p>
            </div>

            <div v-else-if="repairList.length === 0" class="empty-state">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="#cbd5e1" viewBox="0 0 24 24">
                <path d="M19 5v14H5V5h14m0-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z"/>
              </svg>
              <p>暂无反馈记录</p>
            </div>

            <div v-else class="repair-list">
              <div v-for="item in filteredRepairList" :key="item.id" class="repair-item">
                <div class="repair-item-header">
                  <span class="type-tag" :class="item.type">{{ getTypeName(item.type) }}</span>
                  <div class="header-right">
                    <span class="status-tag" :class="'status-' + item.status">{{ getStatusName(item.status) }}</span>
                    <button 
                      v-if="item.status !== 2" 
                      class="complete-btn"
                      @click.stop="markAsComplete(item.id)"
                    >
                      完成
                    </button>
                  </div>
                </div>
                <div class="repair-item-body">
                  <p class="repair-location">📍 {{ item.location || '—' }}</p>
                  <p class="repair-desc">{{ item.description }}</p>
                </div>
                <div class="repair-item-footer">
                  <span class="repair-time">{{ formatTime(item.createTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showToast" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script>
export default {
  name: 'Repair',
  data() {
    return {
      selectedType: null, // 选中的反馈类型
      showRecords: false, // 是否显示记录
      showCompleted: false, // 是否显示已完成记录
      formData: {
        userId: null,
        type: '',
        // 通用字段
        location: '',
        description: '',
        contact: '',
        // 向老师提问
        courseName: '',
        questionType: '',
        // 投诉与建议
        suggestType: '',
        // 食堂餐饮
        canteenName: '',
        stallLocation: '',
        diningType: '',
        // 宿舍管理
        buildingNo: '',
        roomNo: '',
        dormType: '',
        // 课程教学
        teacherName: '',
        courseType: '',
        // 校园安全
        urgencyLevel: '',
        safetyType: ''
      },
      showToast: false,
      toastMessage: '',
      repairList: [],
      loading: false
    }
  },
  mounted() {
    // 获取当前用户ID
    const userId = this.$cookies.get('userId')
    console.log('[mounted] 获取到 userId cookie:', userId)
    if (userId) {
      this.formData.userId = parseInt(userId)
      console.log('[mounted] formData.userId 已设置为:', this.formData.userId)
    } else {
      console.warn('[mounted] userId cookie 为空!')
    }
  },
  watch: {
    // 监听 showRecords 变化，自动加载数据
    showRecords(newVal, oldVal) {
      console.log('[watch showRecords] 从', oldVal, '变为', newVal)
      console.log('[watch showRecords] formData.userId:', this.formData.userId)
      if (newVal) {
        this.loadRepairList()
      }
    }
  },
  computed: {
    // 显示报修记录
    filteredRepairList() {
      const validTypes = ['ask', 'suggest', 'dining', 'dorm', 'course', 'safety', 'water', 'furniture', 'network', 'door', 'other']
      const filtered = this.repairList.filter(item => {
        if (!validTypes.includes(item.type)) return false
        if (this.showCompleted) {
          return item.status === 2  // 只显示已完成
        } else {
          return item.status !== 2  // 只显示未完成
        }
      })
      console.log('[filteredRepairList] showCompleted:', this.showCompleted, '结果条数:', filtered.length, '总条数:', this.repairList.length)
      return filtered
    }
  },
  methods: {
    selectType(type) {
      this.selectedType = type
      this.formData.type = type
      this.showRecords = false
      // 重置表单数据（保留userId）
      this.formData = {
        userId: this.formData.userId,
        type: type,
        location: '',
        description: '',
        contact: '',
        courseName: '',
        questionType: '',
        suggestType: '',
        canteenName: '',
        stallLocation: '',
        diningType: '',
        buildingNo: '',
        roomNo: '',
        dormType: '',
        teacherName: '',
        courseType: '',
        urgencyLevel: '',
        safetyType: ''
      }
    },
    showToastMessage(msg) {
      this.toastMessage = msg
      this.showToast = true
      setTimeout(() => { this.showToast = false }, 2000)
    },
    loadRepairList() {
      const userId = this.formData.userId
      console.log('[loadRepairList] userId:', userId, '(type:', typeof userId, ')')
      if (!userId) {
        console.warn('[loadRepairList] userId 为空，无法加载列表')
        this.repairList = []
        return
      }
      this.loading = true
      // 添加时间戳防止缓存
      const ts = Date.now()
      this.$axios.get(`/repair/list?userId=${userId}&_t=${ts}`)
        .then(res => {
          console.log('[loadRepairList] 接口返回:', res.data)
          if (res.data.code === 200) {
            this.repairList = res.data.data || []
            console.log('[loadRepairList] 已赋值 repairList, 条数:', this.repairList.length)
          }
        })
        .catch(err => {
          console.error('[loadRepairList] 加载报修列表失败:', err)
        })
        .finally(() => {
          this.loading = false
        })
    },
    async markAsComplete(id) {
      try {
        const res = await this.$axios.put(`/repair/update?id=${id}&status=2`)
        if (res.data.code === 200) {
          this.showToastMessage('已标记为完成')
          // 从列表中移除该记录
          this.repairList = this.repairList.filter(r => r.id !== id)
        } else {
          this.showToastMessage(res.data.message || '操作失败')
        }
      } catch (error) {
        console.error('标记完成失败:', error)
        this.showToastMessage('操作失败，请稍后重试')
      }
    },
    async submitRepair() {
      // 验证必填字段
      if (!this.formData.description) {
        this.showToastMessage('请填写详细描述')
        return
      }

      // 根据类型验证特定字段
      const type = this.selectedType
      if (type === 'ask' && !this.formData.courseName) {
        this.showToastMessage('请填写课程名称')
        return
      }
      if (type === 'dining' && !this.formData.canteenName) {
        this.showToastMessage('请选择食堂')
        return
      }
      if (type === 'dorm' && (!this.formData.buildingNo || !this.formData.roomNo)) {
        this.showToastMessage('请填写宿舍楼号和房间号')
        return
      }
      if (type === 'course' && !this.formData.courseName) {
        this.showToastMessage('请填写课程名称')
        return
      }
      if (type === 'safety' && (!this.formData.location || !this.formData.urgencyLevel)) {
        this.showToastMessage('请填写隐患地点和紧急程度')
        return
      }
      if (!this.formData.userId) {
        this.showToastMessage('请先登录')
        return
      }
      try {
        const res = await this.$axios.post('/repair/add', this.formData)
        if (res.data.code === 200) {
          this.showToastMessage('反馈已提交，我们会尽快处理')
          // 完全重置表单
          this.formData = {
            userId: this.formData.userId,
            type: '',
            location: '',
            description: '',
            contact: '',
            courseName: '',
            questionType: '',
            suggestType: '',
            canteenName: '',
            stallLocation: '',
            diningType: '',
            buildingNo: '',
            roomNo: '',
            dormType: '',
            teacherName: '',
            courseType: '',
            urgencyLevel: '',
            safetyType: ''
          }
          this.selectedType = null
        } else {
          this.showToastMessage(res.data.message || '提交失败，请稍后重试')
        }
      } catch (error) {
        console.error('提交失败:', error)
        this.showToastMessage('提交失败，请检查网络连接')
      }
    },
    getTypeName(type) {
      const typeMap = {
        ask: '📚 向老师提问',
        suggest: '💡 投诉与建议',
        dining: '🍽️ 食堂餐饮反馈',
        dorm: '🏠 宿舍管理反馈',
        course: '📖 课程教学反馈',
        safety: '⚠️ 校园安全隐患',
        // AI报修类型
        water: '💧 水电报修',
        furniture: '🪑 家具报修',
        network: '📶 网络报修',
        door: '🚪 门窗报修',
        other: '📦 其他报修'
      }
      return typeMap[type] || type
    },
    getStatusName(status) {
      const statusMap = {
        0: '待处理',
        1: '处理中',
        2: '已完成'
      }
      return statusMap[status] || '未知'
    },
    formatTime(time) {
      if (!time) return '-'
      const date = new Date(time)
      return date.getFullYear() + '年' + (date.getMonth() + 1) + '月' + date.getDate() + '日'
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 50%, #f0f2f5 100%);
}

.bg-decoration {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  pointer-events: none;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 20s ease-in-out infinite;
}

.circle-1 {
  width: 350px; height: 350px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  top: -80px; right: -80px;
}

.circle-2 {
  width: 300px; height: 300px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  bottom: -60px; left: -60px;
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, -20px) scale(1.05); }
}

.main-content {
  position: relative;
  z-index: 1;
  padding: 30px 20px 50px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 7px 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: white;
  cursor: pointer;
  transition: all 0.25s ease;
  margin-bottom: 16px;
  box-shadow: 0 3px 12px rgba(102, 126, 234, 0.3);
}

.back-btn:hover {
  transform: translateX(-3px) scale(1.03);
  box-shadow: 0 6px 18px rgba(102, 126, 234, 0.4);
}

.page-header {
  animation: slideUp 0.6s ease-out;
}

.header-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 16px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.header-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.header-subtitle {
  color: #6b7280;
  font-size: 15px;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 表单区域 */
.form-section {
  background: white;
  border-radius: 20px;
  padding: 24px;
  animation: slideUp 0.6s ease-out 0.1s both;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;
}

.form-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.form-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.back-btn-small {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: #f1f5f9;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.25s ease;
}

.back-btn-small:hover {
  background: #e2e8f0;
  color: #475569;
}

/* 反馈类型选择 */
.type-selection {
  animation: slideUp 0.6s ease-out;
}

.feedback-types-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.feedback-type-card {
  background: white;
  border-radius: 16px;
  padding: 24px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border: 2px solid transparent;
}

.feedback-type-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
}

.type-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.type-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px;
}

.type-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
  line-height: 1.4;
}

/* 查看记录按钮 */
.view-records-section {
  text-align: center;
}

.view-records-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: all 0.25s ease;
}

.view-records-btn:hover {
  border-color: #667eea;
  color: #667eea;
  background: #f8faff;
}

/* 记录列表 */
.records-section {
  animation: slideUp 0.6s ease-out;
}

.repair-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.repair-item {
  background: white;
  border-radius: 16px;
  padding: 16px 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.repair-item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.repair-item-body {
  margin-bottom: 12px;
}

.repair-location {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 8px;
}

.repair-desc {
  font-size: 14px;
  color: #374151;
  margin: 0;
  line-height: 1.5;
}

.repair-item-footer {
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.repair-time {
  font-size: 12px;
  color: #9ca3af;
}

.complete-btn {
  padding: 4px 12px;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.complete-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.optional {
  color: #9ca3af;
  font-weight: 400;
}

.required {
  color: #ef4444;
  font-weight: 600;
}

select option.urgent {
  background-color: #fef3c7;
  color: #b45309;
}

select option.critical {
  background-color: #fee2e2;
  color: #dc2626;
  font-weight: 600;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 14px 16px;
  border: 1.5px solid #e5e7eb;
  border-radius: 12px;
  font-size: 14px;
  color: #1a1a2e;
  transition: all 0.25s ease;
  font-family: inherit;
  background: #fafbfc;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: white;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #b0b8c4;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.35);
  margin-top: 8px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.45);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 报修进度区域 */
.progress-section {
  animation: slideUp 0.6s ease-out 0.2s both;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: white;
  border: 1.5px solid #e5e7eb;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.refresh-btn svg {
  transition: transform 0.3s ease;
}

.refresh-btn:hover svg {
  transform: rotate(180deg);
}

/* 加载和空状态 */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.loading-state p,
.empty-state p {
  margin-top: 12px;
  color: #6b7280;
  font-size: 14px;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 报修表格 */
.repair-table {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.repair-table table {
  width: 100%;
  border-collapse: collapse;
}

.repair-table th,
.repair-table td {
  padding: 14px 16px;
  text-align: left;
  font-size: 13px;
}

.repair-table th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
}

.repair-table th:first-child {
  border-radius: 16px 0 0 0;
}

.repair-table th:last-child {
  border-radius: 0 16px 0 0;
}

.repair-table tr:not(:last-child) td {
  border-bottom: 1px solid #f3f4f6;
}

.repair-table tr:hover td {
  background: #fafbfc;
}

.repair-table td {
  color: #374151;
}

.desc-cell {
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 类型标签 */
.type-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.type-tag.ask {
  background: #dbeafe;
  color: #1d4ed8;
}

.type-tag.suggest {
  background: #fef3c7;
  color: #b45309;
}

.type-tag.dining {
  background: #fce7f3;
  color: #be185d;
}

.type-tag.dorm {
  background: #e0f2fe;
  color: #0369a1;
}

.type-tag.course {
  background: #d1fae5;
  color: #047857;
}

.type-tag.safety {
  background: #fee2e2;
  color: #dc2626;
}

/* 旧类型兼容 */
.type-tag.water {
  background: #e0f2fe;
  color: #0369a1;
}

.type-tag.furniture {
  background: #fef3c7;
  color: #b45309;
}

.type-tag.network {
  background: #dbeafe;
  color: #1d4ed8;
}

.type-tag.door {
  background: #fee2e2;
  color: #dc2626;
}

.type-tag.other {
  background: #f3f4f6;
  color: #64748b;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.status-tag.status-0 {
  background: #fef3c7;
  color: #b45309;
}

.status-tag.status-1 {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-tag.status-2 {
  background: #d1fae5;
  color: #047857;
}

/* 显示已完成开关 */
.filter-section {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-bottom: 16px;
  padding: 8px 0;
}

.toggle-label {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.toggle-input {
  display: none;
}

.toggle-switch {
  position: relative;
  width: 44px;
  height: 24px;
  background: #cbd5e1;
  border-radius: 12px;
  transition: all 0.3s ease;
  margin-right: 8px;
}

.toggle-switch::after {
  content: '';
  position: absolute;
  width: 18px;
  height: 18px;
  background: white;
  border-radius: 50%;
  top: 3px;
  left: 3px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.toggle-input:checked + .toggle-switch {
  background: linear-gradient(135deg, #10b981, #059669);
}

.toggle-input:checked + .toggle-switch::after {
  left: 23px;
}

.toggle-text {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
}

.toggle-input:checked ~ .toggle-text {
  color: #059669;
}

/* Toast 提示 */
.toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  z-index: 3000;
  animation: toastIn 0.3s ease;
}

@keyframes toastIn {
  from { opacity: 0; transform: translate(-50%, -50%) scale(0.9); }
  to { opacity: 1; transform: translate(-50%, -50%) scale(1); }
}

@media (max-width: 768px) {
  .main-content { padding: 20px 16px 40px; }
  .form-section { padding: 20px 16px; }
  .repair-table {
    overflow-x: auto;
  }
  .repair-table table {
    min-width: 600px;
  }
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .feedback-types-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  .feedback-type-card {
    padding: 16px 12px;
  }
  .type-icon {
    width: 48px;
    height: 48px;
    margin-bottom: 12px;
  }
  .type-icon svg {
    width: 24px;
    height: 24px;
  }
  .type-title {
    font-size: 14px;
  }
  .type-desc {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .feedback-types-grid {
    grid-template-columns: 1fr;
  }
}
</style>
