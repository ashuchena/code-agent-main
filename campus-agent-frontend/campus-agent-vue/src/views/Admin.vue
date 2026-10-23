<template>
  <div class="admin-page">
    <header class="admin-header">
      <div class="header-mark">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M12 2 5 5v6c0 4.4 2.8 8.5 7 10 4.2-1.5 7-5.6 7-10V5l-7-3Z" />
        </svg>
      </div>
      <div>
        <h1>管理员后台</h1>
        <p>校园助手 · 系统配置中心</p>
      </div>
    </header>

    <nav class="tab-bar">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-button', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        <span class="tab-icon" v-html="tab.icon"></span>
        <span>{{ tab.label }}</span>
      </button>
    </nav>

    <section class="panel-head">
      <h2>{{ currentTitle }}</h2>
      <p>{{ currentDescription }}</p>
    </section>

    <section v-if="activeTab === 'model'" class="card model-panel">
      <div class="model-hero">
        <div class="model-hero-copy">
          <span class="model-eyebrow">模型配置</span>
          <h3>新增模型</h3>
          <p>维护可用模型列表，并切换当前后台正在使用的激活模型。</p>
        </div>
      </div>

      <div class="model-form-shell">
        <div class="form-grid model-form-grid">
          <input v-model.trim="newModel.name" placeholder="模型名称">
          <input v-model.trim="newModel.modelKey" placeholder="模型标识，如 qwen-max">
          <input v-model.trim="newModel.description" placeholder="模型说明">
        </div>
        <div class="section-actions model-actions">
          <button class="primary model-primary-btn" @click="addModel">
            <span class="model-primary-icon">+</span>
            <span>新增模型</span>
          </button>
        </div>
      </div>

      <div class="model-list-head">
        <h3 class="section-title">模型列表</h3>
        <span class="model-count">{{ modelList.length }} 个模型</span>
      </div>
      <div v-if="loadingModel" class="empty model-empty">加载中...</div>
      <div v-else-if="modelList.length === 0" class="empty model-empty">暂无模型</div>
      <div v-else class="table-wrap model-table-wrap">
        <table class="table model-table">
          <thead>
            <tr>
              <th>名称</th>
              <th>标识</th>
              <th>说明</th>
              <th class="model-status-head">状态</th>
              <th class="model-actions-head">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in modelList" :key="item.id">
              <td class="model-name-cell">
                <strong>{{ item.name || '-' }}</strong>
              </td>
              <td class="model-key-cell">
                <code class="model-key-chip">{{ item.modelKey || '-' }}</code>
              </td>
              <td class="model-desc-cell">{{ item.description || '-' }}</td>
              <td class="model-status-cell">
                <div class="model-status-wrap">
                  <span v-if="item.active === 1" class="tag success model-status-tag">当前启用</span>
                  <span v-else-if="item.enabled === 0" class="tag muted model-status-tag">已禁用</span>
                  <span v-else class="tag info model-status-tag">可用</span>
                </div>
              </td>
              <td class="model-actions-cell">
                <div class="actions model-row-actions">
                <button class="ghost model-action-btn model-action-secondary" @click="activateModel(item.id)">切换</button>
                <button class="ghost model-action-btn model-action-secondary" @click="toggleModel(item.id, item.enabled === 0 ? 1 : 0)">
                  {{ item.enabled === 0 ? '启用' : '禁用' }}
                </button>
                <button class="danger model-action-btn model-action-danger" @click="deleteModel(item.id)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section v-if="activeTab === 'phone'" class="card phone-panel">
      <h3>{{ editingPhone.id ? '编辑热线' : '新增热线' }}</h3>
      <div class="form-grid compact-grid">
        <input v-model.trim="editingPhone.name" placeholder="部门名称">
        <input v-model.trim="editingPhone.number" placeholder="电话号码">
        <input v-model.trim="editingPhone.description" placeholder="描述说明">
        <input v-model.number="editingPhone.sortOrder" class="sort-input" type="number" placeholder="排序">
      </div>
      <div class="section-actions phone-actions">
        <button class="primary phone-primary-btn" @click="savePhone">{{ editingPhone.id ? '保存' : '新增' }}</button>
        <button v-if="editingPhone.id" class="ghost phone-secondary-btn" @click="resetPhone">取消</button>
      </div>

      <h3 class="section-title">热线列表</h3>
      <div v-if="loadingPhone" class="empty">加载中...</div>
      <div v-else-if="phoneList.length === 0" class="empty">暂无热线</div>
      <div v-else class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>名称</th>
              <th>号码</th>
              <th>说明</th>
              <th>排序</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in phoneList" :key="item.id">
              <td class="phone-name-cell">{{ item.name || '-' }}</td>
              <td class="phone-number-cell">{{ item.number || '-' }}</td>
              <td class="phone-desc-cell">{{ item.description || '-' }}</td>
              <td class="phone-sort-cell">{{ item.sortOrder || 0 }}</td>
              <td class="actions phone-row-actions">
                <button class="ghost phone-table-btn phone-table-secondary" @click="startEditPhone(item)">编辑</button>
                <button class="danger phone-table-btn phone-table-danger" @click="deletePhone(item.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section v-if="activeTab === 'announce'" class="card">
      <div class="section-block notice-editor">
        <div class="section-headline">
          <div>
            <h3>{{ editingAnnounce.id ? '编辑公告' : '发布公告' }}</h3>
            <p>填写标题、正文和公告属性后即可发布到校园公告区。</p>
          </div>
        </div>
        <div class="notice-form">
          <input v-model.trim="editingAnnounce.title" placeholder="公告标题">
          <textarea v-model.trim="editingAnnounce.content" rows="6" placeholder="公告内容"></textarea>
          <div class="form-grid three announce-meta-grid">
            <select v-model="editingAnnounce.type">
              <option value="notice">通知</option>
              <option value="urgent">紧急</option>
              <option value="activity">活动</option>
            </select>
            <select v-model.number="editingAnnounce.isTop">
              <option :value="0">不置顶</option>
              <option :value="1">置顶</option>
            </select>
            <select v-model.number="editingAnnounce.enabled">
              <option :value="1">显示</option>
              <option :value="0">隐藏</option>
            </select>
          </div>
          <div class="section-actions">
            <button class="primary" @click="saveAnnounce">{{ editingAnnounce.id ? '保存公告' : '发布公告' }}</button>
            <button v-if="editingAnnounce.id" class="ghost" @click="resetAnnounce">取消</button>
          </div>
        </div>
      </div>

      <div class="section-block notice-list-block">
        <div class="section-headline">
          <div>
            <h3 class="section-title">公告列表</h3>
            <p>已发布的公告会按置顶和发布时间排序展示在这里。</p>
          </div>
        </div>
        <div v-if="loadingAnnounce" class="empty">加载中...</div>
        <div v-else-if="announceList.length === 0" class="empty">暂无公告</div>
        <div v-else class="list notice-list">
          <article v-for="item in announceList" :key="item.id" class="list-item notice-item">
            <div class="notice-main">
              <div class="notice-tags">
                <span class="tag info">{{ announceTypeLabel(item.type) }}</span>
                <span v-if="item.isTop === 1" class="tag success">置顶</span>
                <span v-if="item.enabled === 0" class="tag muted">隐藏</span>
              </div>
              <h4>{{ item.title || '-' }}</h4>
              <p>{{ item.content || '-' }}</p>
            </div>
            <div class="notice-side">
              <div class="notice-meta">
                <span>{{ formatTime(item.createTime) }}</span>
              </div>
              <div class="actions notice-actions">
                <button class="ghost notice-table-btn notice-table-secondary" @click="startEditAnnounce(item)">编辑</button>
                <button class="danger notice-table-btn notice-table-danger" @click="deleteAnnounce(item.id)">删除</button>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section v-if="activeTab === 'maintenance'" class="card maintenance-panel">
      <div class="filters maintenance-filters">
        <button :class="['filter', 'maintenance-filter', { active: maintenanceFilter === -1 }]" @click="maintenanceFilter = -1">全部</button>
        <button :class="['filter', 'maintenance-filter', { active: maintenanceFilter === 0 }]" @click="maintenanceFilter = 0">待处理</button>
        <button :class="['filter', 'maintenance-filter', { active: maintenanceFilter === 1 }]" @click="maintenanceFilter = 1">处理中</button>
        <button :class="['filter', 'maintenance-filter', { active: maintenanceFilter === 2 }]" @click="maintenanceFilter = 2">已完成</button>
      </div>
      <div v-if="loadingMaintenance" class="empty">加载中...</div>
      <div v-else-if="filteredMaintenance.length === 0" class="empty">暂无工单</div>
      <admin-table v-else :columns="maintenanceColumns" :rows="filteredMaintenance">
        <template #row="{ item }">
          <td>#{{ item.id }}</td>
          <td>{{ item.userName || '-' }}<br><small>{{ item.userPhone || '-' }}</small></td>
          <td>{{ maintenanceCategoryLabel(item.category) }}</td>
          <td>{{ item.location || '-' }}</td>
          <td>{{ item.description || '-' }}</td>
          <td><span :class="[statusClass(item.status), 'maintenance-status-tag']">{{ maintenanceStatusLabel(item.status) }}</span></td>
          <td>{{ formatTime(item.createTime) }}</td>
          <td class="actions maintenance-actions">
            <button v-if="Number(item.status) === 0" class="maintenance-btn maintenance-btn-start" @click="updateMaintenanceStatus(item.id, 1)">开始处理</button>
            <button v-if="Number(item.status) === 1" class="maintenance-btn maintenance-btn-complete" @click="updateMaintenanceStatus(item.id, 2)">标记完成</button>
            <button v-if="Number(item.status) === 2" class="maintenance-btn maintenance-btn-reopen" @click="updateMaintenanceStatus(item.id, 0)">重新开启</button>
          </td>
        </template>
      </admin-table>
    </section>

    <section v-if="activeTab === 'feedback'" class="card feedback-panel">
      <div v-if="loadingFeedback" class="empty">加载中...</div>
      <div v-else-if="feedbackList.length === 0" class="empty">暂无反馈</div>
      <admin-table v-else :columns="feedbackColumns" :rows="feedbackList">
        <template #row="{ item }">
          <td class="leave-id-cell">#{{ item.id }}</td>
          <td>{{ item.userName || '-' }}</td>
          <td>{{ feedbackTypeLabel(item.type) }}</td>
          <td>{{ item.title || '-' }}</td>
          <td>{{ item.content || '-' }}</td>
          <td><span :class="[statusClass(item.status), 'feedback-status-tag']">{{ feedbackStatusLabel(item.status) }}</span></td>
          <td>{{ item.reply || '-' }}</td>
          <td><button class="primary feedback-reply-btn" @click="openReply(item)">回复</button></td>
        </template>
      </admin-table>
    </section>

    <section v-if="activeTab === 'volunteer'" class="card volunteer-panel">
      <div class="volunteer-hero">
        <div class="volunteer-hero-copy">
          <span class="volunteer-eyebrow">审批中心</span>
          <h3>志愿活动申请</h3>
          <p>集中处理活动发布申请，快速查看当前待审核和历史审批状态。</p>
        </div>
        <div class="volunteer-stats">
          <div class="volunteer-stat volunteer-stat-all">
            <strong>{{ volunteerStats.total }}</strong>
            <span>全部申请</span>
          </div>
          <div class="volunteer-stat volunteer-stat-pending">
            <strong>{{ volunteerStats.pending }}</strong>
            <span>待审核</span>
          </div>
          <div class="volunteer-stat volunteer-stat-approved">
            <strong>{{ volunteerStats.approved }}</strong>
            <span>已通过</span>
          </div>
          <div class="volunteer-stat volunteer-stat-rejected">
            <strong>{{ volunteerStats.rejected }}</strong>
            <span>已拒绝</span>
          </div>
        </div>
      </div>

      <div class="filters volunteer-filters">
        <button :class="['filter', 'volunteer-filter', { active: volunteerFilter === -1 }]" @click="volunteerFilter = -1">全部</button>
        <button :class="['filter', 'volunteer-filter', { active: volunteerFilter === 0 }]" @click="volunteerFilter = 0">待审核</button>
        <button :class="['filter', 'volunteer-filter', { active: volunteerFilter === 1 }]" @click="volunteerFilter = 1">已通过</button>
        <button :class="['filter', 'volunteer-filter', { active: volunteerFilter === 2 }]" @click="volunteerFilter = 2">已拒绝</button>
      </div>

      <div v-if="loadingVolunteer" class="empty">加载中...</div>
      <div v-else-if="filteredVolunteer.length === 0" class="empty volunteer-empty">鏆傛棤娲诲姩</div>
      <div v-else class="volunteer-list">
        <article v-for="item in filteredVolunteer" :key="item.id" class="volunteer-item">
          <div class="volunteer-item-main">
            <div class="volunteer-item-top">
              <div class="volunteer-item-copy">
                <span class="volunteer-item-id">ID #{{ item.id }}</span>
                <h4>{{ item.title || item.name || '-' }}</h4>
              </div>
            </div>

            <div class="volunteer-item-bottom">
              <div class="volunteer-meta">
                <div class="volunteer-meta-item">
                  <span class="volunteer-meta-label">地点</span>
                  <strong>{{ item.location || '-' }}</strong>
                </div>
                <div class="volunteer-meta-item">
                  <span class="volunteer-meta-label">时间</span>
                  <strong>{{ formatTime(item.startTime) }}</strong>
                </div>
              </div>

              <div class="volunteer-actions">
                <button v-if="Number(item.approvalStatus) === 0" class="volunteer-action-btn volunteer-approve-btn" @click="approveVolunteer(item.id, 1)">通过</button>
                <button v-if="Number(item.approvalStatus) === 0" class="volunteer-action-btn volunteer-reject-btn" @click="approveVolunteer(item.id, 2)">拒绝</button>
                <span v-if="Number(item.approvalStatus) !== 0" :class="['volunteer-action-note', Number(item.approvalStatus) === 1 ? 'success' : 'danger']">
                  {{ volunteerStatusLabel(item.approvalStatus) }}
                </span>
                <button v-if="Number(item.approvalStatus) !== 0" class="danger volunteer-action-btn volunteer-delete-btn" @click="deleteVolunteer(item.id)">删除</button>
              </div>
            </div>

          </div>
        </article>
      </div>
    </section>

    <section v-if="activeTab === 'leave'" class="card leave-panel">
      <div class="filters leave-filters">
        <button :class="['filter', 'leave-filter', { active: leaveFilter === -1 }]" @click="leaveFilter = -1">全部</button>
        <button :class="['filter', 'leave-filter', { active: leaveFilter === 0 }]" @click="leaveFilter = 0">待审批</button>
        <button :class="['filter', 'leave-filter', { active: leaveFilter === 1 }]" @click="leaveFilter = 1">已批准</button>
        <button :class="['filter', 'leave-filter', { active: leaveFilter === 2 }]" @click="leaveFilter = 2">已拒绝</button>
      </div>
      <div v-if="loadingLeave" class="empty leave-empty">加载中...</div>
      <div v-else-if="filteredLeave.length === 0" class="empty leave-empty">暂无请假记录</div>
      <admin-table v-else class="leave-table-shell" :columns="leaveColumns" :rows="filteredLeave">
        <template #row="{ item }">
          <td class="leave-id-cell">#{{ item.id }}</td>
          <td class="leave-user-cell">{{ item.username || item.studentName || item.userName || '-' }}</td>
          <td class="leave-reason-cell">{{ item.reason || '-' }}</td>
          <td class="leave-time-cell">{{ formatTime(item.startTime) }} 至 {{ formatTime(item.endTime) }}</td>
          <td class="leave-status-cell">
            <div class="leave-status-wrap">
              <span :class="[statusClass(item.status), 'leave-status-tag']">{{ leaveStatusLabel(item.status) }}</span>
            </div>
          </td>
          <td class="leave-actions-cell">
            <div class="actions leave-actions">
              <button class="primary leave-action-btn leave-approve-btn" :disabled="Number(item.status) !== 0" @click="approveLeave(item.id, 1)">批准</button>
              <button class="danger leave-action-btn leave-reject-btn" :disabled="Number(item.status) !== 0" @click="approveLeave(item.id, 2)">拒绝</button>
              <button class="ghost leave-action-btn leave-delete-btn" @click="deleteLeave(item.id)">删除</button>
            </div>
          </td>
        </template>
      </admin-table>
    </section>

    <section v-if="activeTab === 'studentService'" class="card student-service-panel">
      <div class="filters student-service-filters">
        <button :class="['filter', 'student-service-filter', { active: studentServiceTab === 'express' }]" @click="studentServiceTab = 'express'">快递代取</button>
        <button :class="['filter', 'student-service-filter', { active: studentServiceTab === 'parttime' }]" @click="studentServiceTab = 'parttime'">兼职信息</button>
        <button :class="['filter', 'student-service-filter', { active: studentServiceTab === 'lostfound' }]" @click="studentServiceTab = 'lostfound'">失物招领</button>
      </div>

      <div v-if="studentServiceTab === 'express'" class="student-service-table-shell express-audit-shell">
        <div v-if="loadingExpress" class="empty">加载中...</div>
        <div v-else-if="expressList.length === 0" class="empty">暂无记录</div>
        <admin-table v-else class="express-audit-table-shell" :columns="expressColumns" :rows="expressList">
          <template #row="{ item }">
            <td>#{{ item.id }}</td>
            <td>{{ item.username || item.userName || '-' }}</td>
            <td>{{ item.expressCompany || '-' }}</td>
            <td>{{ item.pickupCode || '-' }}</td>
            <td><span :class="statusClass(item.approvalStatus)">{{ approvalStatusLabel(item.approvalStatus) }}</span></td>
            <td class="student-service-actions-cell express-actions-cell">
              <div class="actions student-service-actions express-actions">
                <button v-if="Number(item.approvalStatus) === 0" class="primary express-action-btn express-approve-btn" @click="approveExpress(item.id, 1)">通过</button>
                <button v-if="Number(item.approvalStatus) === 0" class="danger express-action-btn express-reject-btn" @click="approveExpress(item.id, 2)">拒绝</button>
                <button class="ghost express-action-btn express-delete-btn" @click="deleteExpress(item.id)">删除</button>
              </div>
            </td>
          </template>
        </admin-table>
      </div>

      <div v-if="studentServiceTab === 'parttime'" class="student-service-table-shell parttime-plain-shell">
        <div v-if="loadingPartTime" class="empty parttime-empty">加载中...</div>
        <div v-else-if="partTimeList.length === 0" class="empty parttime-empty">暂无记录</div>
        <admin-table v-else class="parttime-plain-table-shell" :columns="partTimeColumns" :rows="partTimeList">
          <template #row="{ item }">
            <td>#{{ item.id }}</td>
            <td>{{ item.title || '-' }}</td>
            <td>{{ item.publisherName || '-' }}</td>
            <td>{{ item.salary || '-' }}</td>
            <td><span :class="statusClass(item.approvalStatus)">{{ approvalStatusLabel(item.approvalStatus) }}</span></td>
            <td class="student-service-actions-cell parttime-actions-cell">
              <div class="actions student-service-actions parttime-actions">
                <button class="primary parttime-action-btn parttime-approve-btn" @click="approvePartTime(item.id, 1)">通过</button>
                <button class="danger parttime-action-btn parttime-reject-btn" @click="approvePartTime(item.id, 2)">拒绝</button>
                <button class="ghost parttime-action-btn parttime-delete-btn" @click="deletePartTime(item.id)">删除</button>
              </div>
            </td>
          </template>
        </admin-table>
      </div>

      <div v-if="studentServiceTab === 'lostfound'" class="student-service-table-shell lostfound-audit-shell">
        <div v-if="loadingLostFound" class="empty">加载中...</div>
        <div v-else-if="lostFoundList.length === 0" class="empty">暂无记录</div>
        <admin-table v-else class="lostfound-audit-table-shell" :columns="lostFoundColumns" :rows="lostFoundList">
          <template #row="{ item }">
            <td>#{{ item.id }}</td>
            <td>{{ item.title || '-' }}</td>
            <td>{{ item.type || '-' }}</td>
            <td>{{ item.location || '-' }}</td>
            <td><span :class="statusClass(item.approvalStatus)">{{ approvalStatusLabel(item.approvalStatus) }}</span></td>
            <td class="student-service-actions-cell lostfound-actions-cell">
              <div class="actions student-service-actions lostfound-actions">
                <button v-if="Number(item.approvalStatus) === 0" class="primary lostfound-action-btn lostfound-approve-btn" @click="approveLostFound(item.id, 1)">通过</button>
                <button v-if="Number(item.approvalStatus) === 0" class="danger lostfound-action-btn lostfound-reject-btn" @click="approveLostFound(item.id, 2)">拒绝</button>
                <button class="ghost lostfound-action-btn lostfound-delete-btn" @click="deleteLostFound(item.id)">删除</button>
              </div>
            </td>
          </template>
        </admin-table>
      </div>
    </section>

    <div v-if="showReplyModal" class="modal-mask" @click.self="closeReply">
      <div class="modal-card">
        <h3>回复反馈</h3>
        <p class="modal-subtitle">反馈 #{{ replyingFeedback.id }}</p>
        <input v-model.trim="replyingFeedback.replierName" placeholder="回复人姓名">
        <textarea v-model.trim="replyingFeedback.reply" rows="5" placeholder="请输入回复内容"></textarea>
        <div class="modal-actions">
          <button class="ghost" @click="closeReply">取消</button>
          <button class="primary" @click="submitReply">提交回复</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { getAuthUserId, readAuthSession } from '../utils/auth'

const AdminTable = {
  functional: true,
  props: {
    columns: { type: Array, required: true },
    rows: { type: Array, required: true }
  },
  render(h, context) {
    const columns = context.props.columns
    const rows = context.props.rows
    const incomingClass = context.data.class
    const incomingStaticClass = context.data.staticClass
    return h('div', {
      class: ['table-wrap', incomingClass],
      staticClass: incomingStaticClass
    }, [
      h('table', { class: 'table' }, [
        h('thead', [
          h('tr', columns.map(column => h('th', column)))
        ]),
        h('tbody', rows.map(item => h('tr', { key: item.id }, context.scopedSlots.row({ item }))))
      ])
    ])
  }
}

export default {
  name: 'Admin',
  components: { AdminTable },
  data() {
    return {
      activeTab: 'model',
      authUserId: null,
      tabs: [
        { key: 'model', label: 'AI 模型', icon: '<svg viewBox="0 0 24 24"><path d="M4 5.5A2.5 2.5 0 0 1 6.5 3h11A2.5 2.5 0 0 1 20 5.5v8A2.5 2.5 0 0 1 17.5 16H9l-5 4V5.5Z"/></svg>' },
        { key: 'phone', label: '服务热线', icon: '<svg viewBox="0 0 24 24"><path d="M7.6 10.8c1.4 2.7 2.9 4.2 5.6 5.6l1.9-1.9c.3-.3.7-.4 1.1-.2 1.2.4 2.4.6 3.8.6.6 0 1 .4 1 1V20c0 .6-.4 1-1 1C10.6 21 3 13.4 3 4c0-.6.4-1 1-1h4.1c.6 0 1 .4 1 1 0 1.3.2 2.6.6 3.8.1.4 0 .8-.2 1.1l-1.9 1.9Z"/></svg>' },
        { key: 'announce', label: '公告管理', icon: '<svg viewBox="0 0 24 24"><path d="M4 4h16v12H7l-3 4V4Zm4 4v2h8V8H8Zm0 4v2h6v-2H8Z"/></svg>' },
        { key: 'maintenance', label: '统一报修', icon: '<svg viewBox="0 0 24 24"><path d="m21 18.6-6.8-6.8c.6-1.8.2-3.8-1.2-5.2a5.5 5.5 0 0 0-5.8-1.3l3.4 3.4-2.9 2.9-3.4-3.4A5.5 5.5 0 0 0 5.6 14c1.4 1.4 3.4 1.8 5.2 1.2l6.8 6.8 3.4-3.4Z"/></svg>' },
        { key: 'feedback', label: '问题反馈', icon: '<svg viewBox="0 0 24 24"><path d="M12 3a9 9 0 1 0 0 18 9 9 0 0 0 0-18Zm0 14.2a1.2 1.2 0 1 1 0-2.4 1.2 1.2 0 0 1 0 2.4Zm1-4.2h-2V7h2v6Z"/></svg>' },
        { key: 'volunteer', label: '志愿活动审批', icon: '<svg viewBox="0 0 24 24"><path d="m9.2 16.6-4.3-4.3-1.7 1.7 6 6L21 8.2l-1.7-1.7-10.1 10.1Z"/></svg>' },
        { key: 'leave', label: '请假审批', icon: '<svg viewBox="0 0 24 24"><path d="M8 2h8v3h3a2 2 0 0 1 2 2v13a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h3V2Zm2 3h4V4h-4v1Z"/></svg>' },
        { key: 'studentService', label: '学生服务审核', icon: '<svg viewBox="0 0 24 24"><path d="M9 3h6a2 2 0 0 1 2 2v2h3a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h3V5a2 2 0 0 1 2-2Zm0 4h6V5H9v2Z"/></svg>' }
      ],
      descriptions: {
        model: ['AI 模型管理', '管理可用模型并切换当前激活模型。'],
        phone: ['服务热线', '管理用户侧展示的服务热线。'],
        announce: ['公告管理', '发布和维护校园公告内容。'],
        maintenance: ['统一报修工单', '集中处理当前后台的报修工单。'],
        feedback: ['问题反馈', '查看并回复用户提交的反馈。'],
        volunteer: ['志愿活动审批', '审核志愿活动发布申请。'],
        leave: ['请假审批', '审核学生请假申请。'],
        studentService: ['学生服务审核', '审核快递代取、兼职信息和失物招领。']
      },
      studentServiceTab: 'express',
      loadingModel: false,
      modelList: [],
      newModel: { name: '', modelKey: '', description: '' },
      loadingPhone: false,
      phoneList: [],
      editingPhone: { id: null, name: '', number: '', description: '', sortOrder: 0 },
      loadingAnnounce: false,
      announceList: [],
      editingAnnounce: { id: null, title: '', content: '', type: 'notice', isTop: 0, enabled: 1 },
      loadingMaintenance: false,
      maintenanceList: [],
      maintenanceFilter: -1,
      loadingFeedback: false,
      feedbackList: [],
      showReplyModal: false,
      replyingFeedback: { id: null, reply: '', replierName: '' },
      loadingVolunteer: false,
      volunteerList: [],
      volunteerFilter: -1,
      loadingLeave: false,
      leaveList: [],
      leaveFilter: -1,
      loadingExpress: false,
      expressList: [],
      loadingPartTime: false,
      partTimeList: [],
      loadingLostFound: false,
      lostFoundList: [],
      maintenanceColumns: ['ID', '报修人', '类别', '位置', '描述', '状态', '时间', '操作'],
      feedbackColumns: ['ID', '用户', '类型', '标题', '内容', '状态', '回复', '操作'],
      volunteerColumns: ['ID', '活动', '地点', '时间', '状态', '操作'],
      leaveColumns: ['ID', '用户', '原因', '时间', '状态', '操作'],
      expressColumns: ['ID', '用户', '快递公司', '取件码', '状态', '操作'],
      partTimeColumns: ['ID', '标题', '发布人', '薪资', '状态', '操作'],
      lostFoundColumns: ['ID', '标题', '类型', '地点', '状态', '操作']
    }
  },
  computed: {
    currentTitle() {
      return this.descriptions[this.activeTab][0]
    },
    currentDescription() {
      return this.descriptions[this.activeTab][1]
    },
    filteredMaintenance() {
      return this.maintenanceFilter === -1
        ? this.maintenanceList
        : this.maintenanceList.filter(item => Number(item.status) === this.maintenanceFilter)
    },
    filteredVolunteer() {
      return this.volunteerFilter === -1
        ? this.volunteerList
        : this.volunteerList.filter(item => Number(item.approvalStatus) === this.volunteerFilter)
    },
    volunteerStats() {
      return this.volunteerList.reduce((stats, item) => {
        const status = Number(item.approvalStatus)
        stats.total += 1
        if (status === 0) stats.pending += 1
        if (status === 1) stats.approved += 1
        if (status === 2) stats.rejected += 1
        return stats
      }, { total: 0, pending: 0, approved: 0, rejected: 0 })
    },
    filteredLeave() {
      return this.leaveFilter === -1
        ? this.leaveList
        : this.leaveList.filter(item => Number(item.status) === this.leaveFilter)
    }
  },
  watch: {
    activeTab(tab) {
      if (this.authUserId) this.loadActiveTab(tab)
    },
    studentServiceTab() {
      if (this.authUserId) this.loadStudentServiceCurrent()
    }
  },
  created() {
    this.refreshAuthUserId()
    if (this.authUserId) this.loadActiveTab(this.activeTab)
  },
  mounted() {
    window.addEventListener('campus-auth-change', this.handleAuthChange)
  },
  beforeDestroy() {
    window.removeEventListener('campus-auth-change', this.handleAuthChange)
  },
  methods: {
    normalizeUserId(value) {
      const text = value == null ? '' : String(value).trim()
      return /^\d+$/.test(text) ? text : null
    },
    refreshAuthUserId() {
      const auth = readAuthSession() || {}
      const cookieUserId = this.$cookies && this.$cookies.get ? this.$cookies.get('userId') : null
      const id = [auth.userId, getAuthUserId(), cookieUserId].map(this.normalizeUserId).find(Boolean)
      this.authUserId = id || (auth.username === 'admin' || auth.role === 'admin' ? '48659' : null)
    },
    handleAuthChange() {
      this.refreshAuthUserId()
      if (this.authUserId) this.loadActiveTab(this.activeTab)
    },
    loadActiveTab(tab) {
      const loaders = {
        model: this.loadModels,
        phone: this.loadPhones,
        announce: this.loadAnnounces,
        maintenance: this.loadMaintenance,
        feedback: this.loadFeedback,
        volunteer: this.loadVolunteers,
        leave: this.loadLeaves,
        studentService: this.loadStudentServiceCurrent
      }
      if (loaders[tab]) loaders[tab]()
    },
    toast(message, type = 'success') {
      if (this.$message && this.$message[type]) this.$message[type](message)
      else window.alert(message)
    },
    formatTime(value) {
      if (!value) return '-'
      return String(value).replace('T', ' ').slice(0, 16)
    },
    announceTypeLabel(type) {
      return { notice: '通知', urgent: '紧急', activity: '活动' }[type] || type || '-'
    },
    maintenanceCategoryLabel(type) {
      return { water: '水电', furniture: '家具', network: '网络', door: '门锁', other: '其他' }[type] || type || '-'
    },
    maintenanceStatusLabel(status) {
      return ['待处理', '处理中', '已完成'][Number(status)] || '未知'
    },
    feedbackTypeLabel(type) {
      return { ask: '提问', suggest: '建议', dining: '餐饮', dorm: '宿舍', course: '课程', safety: '安全' }[type] || type || '-'
    },
    feedbackStatusLabel(status) {
      return ['待处理', '处理中', '已回复', '已完成'][Number(status)] || '未知'
    },
    volunteerStatusLabel(status) {
      return { 0: '待审核', 1: '已通过', 2: '已拒绝' }[Number(status)] || '未知'
    },
    leaveStatusLabel(status) {
      return ['待审批', '已批准', '已拒绝'][Number(status)] || '未知'
    },
    approvalStatusLabel(status) {
      return { 0: '待审核', 1: '已通过', 2: '已拒绝' }[Number(status)] || '未知'
    },
    statusClass(status) {
      return { 0: 'tag warn', 1: 'tag info', 2: 'tag success', 3: 'tag success' }[Number(status)] || 'tag muted'
    },
    async loadModels() {
      this.loadingModel = true
      try {
        const res = await axios.get('/admin/models', { params: { userId: this.authUserId } })
        if (res.data.code === 200) this.modelList = res.data.data || []
      } catch (error) {
        console.warn('加载模型失败', error)
      } finally {
        this.loadingModel = false
      }
    },
    async addModel() {
      if (!this.newModel.name || !this.newModel.modelKey) return this.toast('请填写模型名称和标识', 'error')
      try {
        const res = await axios.post('/admin/models/add', null, { params: { userId: this.authUserId, ...this.newModel } })
        if (res.data.code === 200) {
          this.newModel = { name: '', modelKey: '', description: '' }
          this.toast('模型新增成功')
          this.loadModels()
        } else this.toast(res.data.message || '新增失败', 'error')
      } catch (error) {
        this.toast('新增模型失败', 'error')
      }
    },
    async activateModel(modelId) {
      try {
        const res = await axios.post('/admin/models/activate', null, { params: { userId: this.authUserId, modelId } })
        if (res.data.code === 200) {
          this.toast('模型已切换')
          this.loadModels()
        } else this.toast(res.data.message || '切换失败', 'error')
      } catch (error) {
        this.toast('切换模型失败', 'error')
      }
    },
    async toggleModel(modelId, enabled) {
      try {
        const res = await axios.post('/admin/models/toggle', null, { params: { userId: this.authUserId, modelId, enabled } })
        if (res.data.code === 200) {
          this.toast(enabled ? '模型已启用' : '模型已禁用')
          this.loadModels()
        } else this.toast(res.data.message || '操作失败', 'error')
      } catch (error) {
        this.toast('更新模型状态失败', 'error')
      }
    },
    async deleteModel(modelId) {
      if (!window.confirm('确定删除这个模型吗？')) return
      try {
        const res = await axios.post('/admin/models/delete', null, { params: { userId: this.authUserId, modelId } })
        if (res.data.code === 200) {
          this.toast('模型已删除')
          this.loadModels()
        } else this.toast(res.data.message || '删除失败', 'error')
      } catch (error) {
        this.toast('删除模型失败', 'error')
      }
    },
    async loadPhones() {
      this.loadingPhone = true
      try {
        const res = await axios.get('/admin/phones', { params: { userId: this.authUserId } })
        if (res.data.code === 200) this.phoneList = res.data.data || []
      } catch (error) {
        console.warn('加载热线失败', error)
      } finally {
        this.loadingPhone = false
      }
    },
    startEditPhone(item) {
      this.editingPhone = { ...item }
    },
    resetPhone() {
      this.editingPhone = { id: null, name: '', number: '', description: '', sortOrder: 0 }
    },
    async savePhone() {
      try {
        const res = await axios.post('/admin/phones/save', this.editingPhone, { params: { userId: this.authUserId } })
        if (res.data.code === 200) {
          this.toast(this.editingPhone.id ? '热线已更新' : '热线已新增')
          this.resetPhone()
          this.loadPhones()
        } else this.toast(res.data.message || '保存失败', 'error')
      } catch (error) {
        this.toast('保存热线失败', 'error')
      }
    },
    async deletePhone(phoneId) {
      if (!window.confirm('确定删除这条热线吗？')) return
      try {
        const res = await axios.post('/admin/phones/delete', null, { params: { userId: this.authUserId, phoneId } })
        if (res.data.code === 200) {
          this.toast('热线已删除')
          this.loadPhones()
        } else this.toast(res.data.message || '删除失败', 'error')
      } catch (error) {
        this.toast('删除热线失败', 'error')
      }
    },
    async loadAnnounces() {
      this.loadingAnnounce = true
      try {
        const res = await axios.get('/admin/announcements', { params: { userId: this.authUserId } })
        if (res.data.code === 200) this.announceList = res.data.data || []
      } catch (error) {
        this.toast('加载公告失败', 'error')
      } finally {
        this.loadingAnnounce = false
      }
    },
    startEditAnnounce(item) {
      this.editingAnnounce = { ...item }
    },
    resetAnnounce() {
      this.editingAnnounce = { id: null, title: '', content: '', type: 'notice', isTop: 0, enabled: 1 }
    },
    async saveAnnounce() {
      try {
        const res = await axios.post('/admin/announcements/save', this.editingAnnounce, { params: { userId: this.authUserId } })
        if (res.data.code === 200) {
          this.toast(this.editingAnnounce.id ? '公告已更新' : '公告已发布')
          this.resetAnnounce()
          this.loadAnnounces()
        } else this.toast(res.data.message || '保存失败', 'error')
      } catch (error) {
        this.toast('保存公告失败', 'error')
      }
    },
    async deleteAnnounce(id) {
      if (!window.confirm('确定删除这条公告吗？')) return
      try {
        const res = await axios.post('/admin/announcements/delete', null, { params: { userId: this.authUserId, announcementId: id } })
        if (res.data.code === 200) {
          this.toast('公告已删除')
          this.loadAnnounces()
        } else this.toast(res.data.message || '删除失败', 'error')
      } catch (error) {
        this.toast('删除公告失败', 'error')
      }
    },
    async loadMaintenance() {
      this.loadingMaintenance = true
      try {
        const res = await axios.get('/admin/maintenance/list', { params: { userId: this.authUserId } })
        if (res.data.code === 200) this.maintenanceList = res.data.data || []
      } catch (error) {
        this.toast('加载工单失败', 'error')
      } finally {
        this.loadingMaintenance = false
      }
    },
    async updateMaintenanceStatus(id, status) {
      try {
        const res = await axios.post('/admin/maintenance/status', null, { params: { userId: this.authUserId, maintenanceId: id, status } })
        if (res.data.code === 200) {
          this.toast('工单状态已更新')
          this.loadMaintenance()
        } else this.toast(res.data.message || '更新失败', 'error')
      } catch (error) {
        this.toast('更新工单失败', 'error')
      }
    },
    async loadFeedback() {
      this.loadingFeedback = true
      try {
        const res = await axios.get('/admin/feedback/list', { params: { userId: this.authUserId } })
        if (res.data.code === 200) this.feedbackList = res.data.data || []
      } catch (error) {
        this.toast('加载反馈失败', 'error')
      } finally {
        this.loadingFeedback = false
      }
    },
    openReply(item) {
      this.replyingFeedback = {
        id: item.id,
        reply: item.reply || '',
        replierName: item.replierName || 'admin'
      }
      this.showReplyModal = true
    },
    closeReply() {
      this.showReplyModal = false
      this.replyingFeedback = { id: null, reply: '', replierName: '' }
    },
    async submitReply() {
      if (!this.replyingFeedback.reply || !this.replyingFeedback.replierName) {
        this.toast('请填写完整回复信息', 'error')
        return
      }
      try {
        const res = await axios.post('/admin/feedback/reply', null, {
          params: {
            userId: this.authUserId,
            feedbackId: this.replyingFeedback.id,
            reply: this.replyingFeedback.reply,
            replierName: this.replyingFeedback.replierName
          }
        })
        if (res.data.code === 200) {
          this.toast('回复成功')
          this.closeReply()
          this.loadFeedback()
        } else {
          this.toast(res.data.message || '回复失败', 'error')
        }
      } catch (error) {
        this.toast('回复反馈失败', 'error')
      }
    },
    async loadVolunteers() {
      this.loadingVolunteer = true
      try {
        const res = await axios.get('/admin/volunteer/list', { params: { userId: this.authUserId } })
        if (res.data.code === 200) this.volunteerList = res.data.data || []
      } catch (error) {
        this.toast('加载志愿活动失败', 'error')
      } finally {
        this.loadingVolunteer = false
      }
    },
    async approveVolunteer(id, status) {
      try {
        const res = await axios.post('/admin/volunteer/approve', null, { params: { userId: this.authUserId, activityId: id, approvalStatus: status, approvalRemark: '' } })
        if (res.data.code === 200) {
          this.toast(status === 1 ? '已通过' : '已拒绝')
          this.loadVolunteers()
        } else this.toast(res.data.message || '审核失败', 'error')
      } catch (error) {
        this.toast('审核志愿活动失败', 'error')
      }
    },
    async deleteVolunteer(id) {
      if (!window.confirm('确定删除这条志愿活动吗？')) return
      try {
        const res = await axios.delete('/volunteer/delete', { params: { id } })
        if (res.data.code === 200) {
          this.toast('志愿活动已删除')
          this.loadVolunteers()
        } else {
          this.toast(res.data.message || '删除失败', 'error')
        }
      } catch (error) {
        this.toast('删除志愿活动失败', 'error')
      }
    },
    async loadLeaves() {
      this.loadingLeave = true
      try {
        const res = await axios.get('/leave/all')
        if (res.data.code === 200) this.leaveList = res.data.data || []
      } catch (error) {
        this.toast('加载请假记录失败', 'error')
      } finally {
        this.loadingLeave = false
      }
    },
    async approveLeave(id, status) {
      try {
        const res = await axios.put('/leave/approve', null, { params: { id, status, remark: '' } })
        if (res.data.code === 200) {
          this.toast(status === 1 ? '已批准' : '已拒绝')
          this.loadLeaves()
        } else this.toast(res.data.message || '审批失败', 'error')
      } catch (error) {
        this.toast('审批请假失败', 'error')
      }
    },
    async deleteLeave(id) {
      if (!window.confirm('确定删除这条请假记录吗？')) return
      try {
        const res = await axios.delete('/leave/delete', { params: { id } })
        if (res.data.code === 200) {
          this.toast('记录已删除')
          this.loadLeaves()
        } else this.toast(res.data.message || '删除失败', 'error')
      } catch (error) {
        this.toast('删除请假记录失败', 'error')
      }
    },
    loadStudentServiceCurrent() {
      if (this.studentServiceTab === 'express') this.loadExpress()
      if (this.studentServiceTab === 'parttime') this.loadPartTime()
      if (this.studentServiceTab === 'lostfound') this.loadLostFound()
    },
    async loadExpress() {
      this.loadingExpress = true
      try {
        const res = await axios.get('/admin/express/list', { params: { userId: this.authUserId } })
        if (res.data.code === 200) this.expressList = res.data.data || []
      } catch (error) {
        this.toast('加载快递代取失败', 'error')
      } finally {
        this.loadingExpress = false
      }
    },
    async approveExpress(id, status) {
      try {
        const res = await axios.post('/admin/express/approve', null, { params: { userId: this.authUserId, id, approvalStatus: status, approvalRemark: '' } })
        if (res.data.code === 200) {
          this.toast(status === 1 ? '已通过' : '已拒绝')
          this.loadExpress()
        } else this.toast(res.data.message || '审核失败', 'error')
      } catch (error) {
        this.toast('审核快递代取失败', 'error')
      }
    },
    async deleteExpress(id) {
      if (!window.confirm('确定删除这条快递代取记录吗？')) return
      try {
        const res = await axios.post('/admin/express/delete', null, { params: { userId: this.authUserId, id } })
        if (res.data.code === 200) {
          this.toast('记录已删除')
          this.loadExpress()
        } else this.toast(res.data.message || '删除失败', 'error')
      } catch (error) {
        this.toast('删除快递代取失败', 'error')
      }
    },
    async loadPartTime() {
      this.loadingPartTime = true
      try {
        const res = await axios.get('/admin/parttime/list', { params: { userId: this.authUserId } })
        if (res.data.code === 200) this.partTimeList = res.data.data || []
      } catch (error) {
        this.toast('加载兼职信息失败', 'error')
      } finally {
        this.loadingPartTime = false
      }
    },
    async approvePartTime(id, status) {
      try {
        const res = await axios.post('/admin/parttime/approve', null, { params: { userId: this.authUserId, id, approvalStatus: status, approvalRemark: '' } })
        if (res.data.code === 200) {
          this.toast(status === 1 ? '已通过' : '已拒绝')
          this.loadPartTime()
        } else this.toast(res.data.message || '审核失败', 'error')
      } catch (error) {
        this.toast('审核兼职信息失败', 'error')
      }
    },
    async deletePartTime(id) {
      if (!window.confirm('确定删除这条兼职信息吗？')) return
      try {
        const res = await axios.post('/admin/parttime/delete', null, { params: { userId: this.authUserId, id } })
        if (res.data.code === 200) {
          this.toast('记录已删除')
          this.loadPartTime()
        } else this.toast(res.data.message || '删除失败', 'error')
      } catch (error) {
        this.toast('删除兼职信息失败', 'error')
      }
    },
    async loadLostFound() {
      this.loadingLostFound = true
      try {
        const res = await axios.get('/admin/lostfound/list', { params: { userId: this.authUserId } })
        if (res.data.code === 200) this.lostFoundList = res.data.data || []
      } catch (error) {
        this.toast('加载失物招领失败', 'error')
      } finally {
        this.loadingLostFound = false
      }
    },
    async approveLostFound(id, status) {
      try {
        const res = await axios.post('/admin/lostfound/approve', null, { params: { userId: this.authUserId, id, approvalStatus: status, approvalRemark: '' } })
        if (res.data.code === 200) {
          this.toast(status === 1 ? '已通过' : '已拒绝')
          this.loadLostFound()
        } else this.toast(res.data.message || '审核失败', 'error')
      } catch (error) {
        this.toast('审核失物招领失败', 'error')
      }
    },
    async deleteLostFound(id) {
      if (!window.confirm('确定删除这条失物招领记录吗？')) return
      try {
        const res = await axios.post('/admin/lostfound/delete', null, { params: { userId: this.authUserId, id } })
        if (res.data.code === 200) {
          this.toast('记录已删除')
          this.loadLostFound()
        } else this.toast(res.data.message || '删除失败', 'error')
      } catch (error) {
        this.toast('删除失物招领失败', 'error')
      }
    }
  }
}
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  max-width: 1680px;
  margin: 0 auto;
  padding: 24px 28px 44px;
  background: #f2f7fb;
  color: #24313b;
  font-size: 14px;
}

.admin-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 18px;
}

.header-mark {
  width: 58px;
  height: 58px;
  display: grid;
  place-items: center;
  border-radius: 18px;
  color: #fff;
  background: linear-gradient(135deg, #5b72f2, #7656d9);
  box-shadow: 0 14px 28px rgba(91, 114, 242, 0.18);
  font-weight: 700;
}

.header-mark svg {
  width: 30px;
  height: 30px;
  fill: currentColor;
}

.admin-header h1 {
  margin: 0;
  font-size: 22px;
  line-height: 1.2;
}

.admin-header p,
.panel-head p,
.modal-subtitle {
  margin: 4px 0 0;
  color: #728392;
}

.tab-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  overflow: visible;
  padding: 16px;
  margin-bottom: 28px;
  border-radius: 28px;
  background: #fff;
  box-shadow: 0 16px 40px rgba(26, 48, 72, 0.08);
}

.tab-button {
  border: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex: 0 0 auto;
  min-height: 54px;
  padding: 12px 18px;
  border-radius: 16px;
  background: transparent;
  color: #607287;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
}

.tab-button.active {
  color: #fff;
  background: #6757df;
  box-shadow: 0 10px 22px rgba(103, 87, 223, 0.24);
}

.tab-icon {
  width: 18px;
  height: 18px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: currentColor;
}

.tab-icon ::v-deep svg {
  width: 18px;
  height: 18px;
  display: block;
  fill: currentColor;
}

.panel-head {
  margin-bottom: 18px;
}

.panel-head h2 {
  margin: 0;
  font-size: 26px;
  line-height: 1.2;
}

.card {
  border: 1px solid #dce6ef;
  border-radius: 26px;
  padding: 28px 30px;
  margin-bottom: 20px;
  background: #fff;
  box-shadow: 0 16px 34px rgba(27, 45, 65, 0.07);
}

.card h3,
.section-title {
  margin: 0 0 16px;
  font-size: 20px;
}

.section-title {
  margin-top: 28px;
}

.section-block + .section-block {
  margin-top: 26px;
  padding-top: 26px;
  border-top: 1px solid #ebf1f6;
}

.section-headline {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.section-headline p {
  margin: 6px 0 0;
  color: #728392;
}

.notice-form {
  display: grid;
  gap: 16px;
}

.announce-meta-grid {
  margin-bottom: 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.form-grid.three {
  grid-template-columns: repeat(3, minmax(160px, 1fr));
}

.compact-grid {
  grid-template-columns: minmax(220px, 1.1fr) minmax(220px, 1fr) minmax(240px, 1.2fr) minmax(120px, 140px);
  align-items: center;
}

input,
textarea,
select {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #dce6ef;
  border-radius: 12px;
  padding: 11px 13px;
  color: #263543;
  background: #fbfdff;
  font: inherit;
}

textarea {
  margin: 10px 0;
  resize: vertical;
}

button {
  border: 0;
  border-radius: 12px;
  padding: 10px 16px;
  font: inherit;
  font-weight: 700;
  cursor: pointer;
}

.primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 132px;
  color: #fff;
  background: #6757df;
  box-shadow: 0 10px 24px rgba(103, 87, 223, 0.2);
}

.ghost {
  background: #edf3f8;
  color: #4d6175;
}

.danger {
  color: #fff;
  background: #ef4444;
}

.actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.section-actions {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.maintenance-actions {
  justify-content: flex-start;
}

.maintenance-panel {
  background:
    radial-gradient(circle at top right, rgba(103, 87, 223, 0.06), transparent 24%),
    linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.maintenance-filters {
  margin-bottom: 18px;
}

.maintenance-filter {
  min-width: 96px;
  min-height: 42px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 600;
}

.maintenance-status-tag {
  min-width: 72px;
  justify-content: center;
  padding: 6px 12px;
  font-size: 13px;
}

.maintenance-btn {
  min-width: 108px;
  min-height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 400;
  line-height: 1;
  white-space: nowrap;
  box-shadow: none;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

.maintenance-btn:hover {
  transform: none;
}

.maintenance-btn-start {
  color: #7a4b00;
  background: #fff3cf;
}

.maintenance-btn-complete {
  color: #ffffff;
  background: #6a58eb;
}

.maintenance-btn-reopen {
  color: #607286;
  background: #f1f6fb;
  box-shadow: inset 0 0 0 1px rgba(223, 232, 241, 0.95);
}

.sort-input {
  text-align: center;
}

.filters {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.filter {
  border: 1px solid #dce6ef;
  background: #fff;
  color: #53677c;
}

.filter.active {
  border-color: #6757df;
  color: #fff;
  background: #6757df;
}

.model-panel {
  background:
    radial-gradient(circle at top right, rgba(103, 87, 223, 0.08), transparent 24%),
    linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.model-hero {
  margin-bottom: 14px;
  padding: 16px 20px;
  border: 1px solid #e6edf6;
  border-radius: 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f9fbff 100%);
}

.model-hero-copy {
  max-width: 760px;
}

.model-eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 5px 10px;
  margin-bottom: 10px;
  border-radius: 999px;
  color: #6757df;
  background: rgba(103, 87, 223, 0.1);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.model-hero-copy h3 {
  margin: 0 0 6px;
  font-size: 24px;
  color: #213547;
}

.model-hero-copy p {
  margin: 0;
  color: #6c8298;
  font-size: 14px;
  line-height: 1.6;
}

.model-form-shell {
  margin-bottom: 22px;
  padding: 18px 18px 16px;
  border: 1px solid #e7eef6;
  border-radius: 20px;
  background: #fff;
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.04);
}

.model-form-grid {
  margin-bottom: 12px;
}

.model-form-grid input {
  min-height: 46px;
  border-radius: 14px;
  background: linear-gradient(180deg, #fcfdff 0%, #f6f9fd 100%);
  font-size: 14px;
}

.model-form-grid input::placeholder {
  color: #93a3b3;
}

.model-actions {
  margin-bottom: 0;
  justify-content: flex-end;
}

.model-primary-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 112px;
  min-height: 34px;
  gap: 6px;
  padding: 0 12px;
  border-radius: 999px;
  background: linear-gradient(135deg, #6a58eb 0%, #5c4bd9 100%);
  box-shadow: 0 6px 14px rgba(103, 87, 223, 0.1);
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
}

.model-primary-icon {
  width: 16px;
  height: 16px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #5f4edc;
  background: rgba(255, 255, 255, 0.92);
  font-size: 12px;
  line-height: 1;
  flex: 0 0 auto;
}

.model-list-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 10px;
}

.model-list-head .section-title {
  margin: 0;
}

.model-count {
  display: inline-flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 999px;
  color: #6b7f94;
  background: #eff4f9;
  font-size: 13px;
  font-weight: 700;
}

.model-table-wrap {
  border: 1px solid #e8eef5;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.04);
}

.model-table {
  min-width: 900px;
  table-layout: fixed;
}

.model-table th,
.model-table td {
  padding: 20px 18px;
  vertical-align: middle;
}

.model-table th:nth-child(1),
.model-table td:nth-child(1) {
  width: 26%;
  text-align: center;
}

.model-table th:nth-child(2),
.model-table td:nth-child(2) {
  width: 24%;
  text-align: center;
}

.model-table th:nth-child(3),
.model-table td:nth-child(3) {
  width: 24%;
  text-align: center;
}

.model-table tbody tr {
  transition: background-color 0.2s ease;
}

.model-table tbody tr:hover {
  background: rgba(246, 249, 253, 0.9);
}

.model-key-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 108px;
  padding: 8px 14px;
  border-radius: 999px;
  color: #49627b;
  background: #f2f6fb;
  font-size: 13px;
  font-weight: 500;
}

.model-desc-cell {
  color: #52677c;
  line-height: 1.5;
}

.model-status-tag {
  min-width: 72px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  padding: 0;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  line-height: 1;
}

.model-status-cell {
  width: 144px;
  padding-left: 0 !important;
  padding-right: 0 !important;
  text-align: center;
  vertical-align: middle;
  white-space: nowrap;
}

.model-status-wrap {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.model-status-head {
  width: 144px;
  text-align: center !important;
  padding-left: 0 !important;
  padding-right: 0 !important;
}

.model-actions-head {
  width: 280px;
  text-align: center !important;
}

.model-actions-cell {
  width: 280px;
  padding-left: 20px !important;
  padding-right: 20px !important;
  text-align: center;
  vertical-align: middle;
}

.model-row-actions {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  flex-wrap: nowrap;
  margin: 0 auto;
}

.model-action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 68px;
  height: 32px;
  min-height: 32px;
  padding: 0 14px;
  box-sizing: border-box;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 500;
  line-height: 1;
  white-space: nowrap;
}

.model-action-secondary {
  color: #607286;
  background: #f1f6fb;
  box-shadow: inset 0 0 0 1px rgba(223, 232, 241, 0.95);
}

.model-action-danger {
  background: #f85757;
  box-shadow: none;
}

.model-empty {
  min-height: 180px;
  border: 1px dashed #dce5ef;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.72);
}

.phone-actions {
  gap: 8px;
}

.phone-primary-btn {
  min-width: 96px;
  min-height: 32px;
  padding: 0 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 500;
  box-shadow: 0 6px 14px rgba(103, 87, 223, 0.1);
}

.phone-secondary-btn {
  min-width: 72px;
  min-height: 32px;
  padding: 0 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 400;
}

.phone-row-actions {
  gap: 6px;
}

.phone-table-btn {
  width: 56px;
  height: 28px;
  min-height: 28px;
  padding: 0;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 400;
  line-height: 1;
}

.phone-table-secondary {
  color: #607286;
  background: #f1f6fb;
  box-shadow: inset 0 0 0 1px rgba(223, 232, 241, 0.95);
}

.phone-table-danger {
  background: #f85757;
  box-shadow: none;
}

.phone-panel .table th:nth-child(1),
.phone-panel .table td:nth-child(1),
.phone-name-cell {
  text-align: center;
}

.phone-panel .table th:nth-child(2),
.phone-panel .table td:nth-child(2),
.phone-number-cell {
  text-align: center;
}

.phone-panel .table th:nth-child(3),
.phone-panel .table td:nth-child(3),
.phone-desc-cell {
  text-align: center;
}

.phone-panel .table th:nth-child(4),
.phone-panel .table td:nth-child(4),
.phone-sort-cell {
  text-align: center;
}

.phone-panel .table th:nth-child(5),
.phone-panel .table td:nth-child(5) {
  text-align: center;
}

.phone-row-actions {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  margin: 0 auto;
}

.volunteer-panel {
  padding: 24px;
  background:
    radial-gradient(circle at top right, rgba(103, 87, 223, 0.09), transparent 28%),
    linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.volunteer-hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 22px 24px;
  margin-bottom: 18px;
  border: 1px solid #e5ecf5;
  border-radius: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f4f8ff 100%);
}

.volunteer-hero-copy {
  max-width: 440px;
}

.volunteer-eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  margin-bottom: 12px;
  border-radius: 999px;
  color: #6757df;
  background: rgba(103, 87, 223, 0.1);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.volunteer-hero-copy h3 {
  margin: 0 0 8px;
  font-size: 28px;
  color: #213547;
}

.volunteer-hero-copy p {
  margin: 0;
  color: #698095;
  line-height: 1.7;
}

.volunteer-stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(120px, 1fr));
  gap: 12px;
  min-width: 320px;
}

.volunteer-stat {
  padding: 16px 18px;
  border-radius: 20px;
  color: #31465b;
  background: #fff;
  box-shadow: inset 0 0 0 1px rgba(221, 230, 240, 0.9);
}

.volunteer-stat strong {
  display: block;
  margin-bottom: 6px;
  font-size: 28px;
  line-height: 1;
}

.volunteer-stat span {
  color: #6d8195;
  font-size: 13px;
  font-weight: 600;
}

.volunteer-stat-all strong {
  color: #334155;
}

.volunteer-stat-pending strong {
  color: #c27a00;
}

.volunteer-stat-approved strong {
  color: #2563eb;
}

.volunteer-stat-rejected strong {
  color: #dc2626;
}

.volunteer-filters {
  margin-bottom: 14px;
  gap: 6px;
}

.volunteer-filter {
  min-width: 82px;
  min-height: 36px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.03);
}

.volunteer-list {
  display: grid;
  gap: 12px;
}

.volunteer-item {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 16px 18px;
  border: 1px solid #e5ecf5;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.045);
}

.volunteer-item-main {
  flex: 1;
  min-width: 0;
}

.volunteer-item-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 12px;
}

.volunteer-item-id {
  display: inline-block;
  margin-bottom: 6px;
  color: #7a8da1;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.volunteer-item h4 {
  margin: 0;
  color: #203447;
  font-size: 18px;
}

.volunteer-item-bottom {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.volunteer-item-copy {
  flex: 1;
  min-width: 0;
}

.volunteer-meta {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, minmax(150px, 1fr));
  gap: 10px;
}

.volunteer-meta-item {
  padding: 10px 12px;
  border-radius: 14px;
  background: #f6f9fc;
}

.volunteer-meta-label {
  display: block;
  margin-bottom: 6px;
  color: #7c8da0;
  font-size: 11px;
  font-weight: 700;
}

.volunteer-meta-item strong {
  color: #31465b;
  font-size: 14px;
}

.volunteer-actions {
  width: 120px;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  gap: 0;
  margin-right: 0;
}

.volunteer-actions > * + * {
  margin-top: 12px;
}

.volunteer-action-btn {
  width: 104px;
  height: 32px;
  min-height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 10px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
  text-align: center;
  box-shadow: none;
}

.volunteer-approve-btn {
  color: #ffffff;
  background: #6a58eb;
}

.volunteer-reject-btn {
  color: #ffffff;
  background: #f85757;
}

.volunteer-action-note {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 104px;
  height: 32px;
  min-height: 32px;
  padding: 0 10px;
  border-radius: 16px;
  color: #64748b;
  background: #eef3f8;
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
  text-align: center;
}

.volunteer-action-note.success {
  color: #0f8a58;
  background: #d6f5df;
  margin-bottom: 0;
}

.volunteer-action-note.danger {
  color: #dc2626;
  background: #fee2e2;
  margin-bottom: 0;
}

.volunteer-empty {
  min-height: 220px;
  border: 1px dashed #d9e4ef;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.75);
}

.empty {
  min-height: 110px;
  display: grid;
  place-items: center;
  color: #728392;
}

.table-wrap {
  overflow-x: auto;
}

.table {
  width: 100%;
  border-collapse: collapse;
  min-width: 860px;
}

.table th,
.table td {
  border-bottom: 1px solid #e6eef5;
  padding: 13px 10px;
  text-align: left;
  vertical-align: top;
}

.table td.actions,
.table td[class*='actions-cell'],
.table td[class*='status-cell'] {
  vertical-align: middle;
}

.table td > button,
.table td .actions > button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
  box-sizing: border-box;
}

.table td.actions,
.table td[class*='actions-cell'] {
  text-align: center;
}

.table td.actions .actions,
.table td[class*='actions-cell'] .actions {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
  margin: 0 auto;
}

.student-service-actions-cell {
  vertical-align: middle !important;
  text-align: left;
}

.student-service-actions {
  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
  margin: 0;
}

.table th {
  color: #63778c;
  font-weight: 700;
}

.tag {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 4px 10px;
  color: #4d6175;
  background: #edf3f8;
  font-size: 12px;
  font-weight: 700;
}

.tag.warn {
  color: #b45309;
  background: #fff1c6;
}

.tag.info {
  color: #2563eb;
  background: #dbeafe;
}

.tag.success {
  color: #15803d;
  background: #d7f8df;
}

.tag.muted {
  color: #64748b;
  background: #eef2f7;
}

.leave-panel {
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.leave-filters {
  margin-bottom: 20px;
}

.leave-filter {
  min-width: 104px;
  min-height: 44px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 700;
}

.leave-empty {
  min-height: 180px;
  border: 1px dashed #dce6ef;
  border-radius: 22px;
  background: #fcfdff;
}

.leave-table-shell ::v-deep .table-wrap {
  overflow-x: auto;
  border: 1px solid #e7eef5;
  border-radius: 24px;
  background: #ffffff;
  box-shadow: 0 14px 34px rgba(20, 35, 52, 0.05);
}

.leave-table-shell ::v-deep .table {
  min-width: 980px;
  table-layout: fixed;
}

.leave-table-shell ::v-deep .table th,
.leave-table-shell ::v-deep .table td {
  padding: 12px 8px;
  font-size: 14px;
  line-height: 1.4;
  vertical-align: middle;
}

.leave-table-shell ::v-deep .table thead th {
  color: #5f7590;
  font-size: 15px;
  font-weight: 800;
  background: #ffffff;
  white-space: nowrap;
}

.leave-table-shell ::v-deep .table thead th:nth-child(5),
.leave-table-shell ::v-deep .table thead th:nth-child(5) {
  text-align: center;
}

.leave-table-shell ::v-deep .table thead th:nth-child(6) {
  text-align: center;
}

.leave-table-shell ::v-deep .table th:nth-child(5),
.leave-table-shell ::v-deep .table td:nth-child(5) {
  width: 88px;
  min-width: 88px;
  max-width: 88px;
  text-align: center;
  padding-left: 0;
  padding-right: 0;
}

.leave-table-shell ::v-deep .table th:nth-child(6),
.leave-table-shell ::v-deep .table td:nth-child(6) {
  width: 198px;
  min-width: 198px;
  max-width: 198px;
  text-align: center;
  padding-left: 0;
  padding-right: 0;
}

.leave-table-shell ::v-deep .table tbody tr {
  height: 52px;
}

.leave-id-cell {
  width: 84px;
  font-weight: 700;
  white-space: nowrap;
}

.leave-user-cell {
  width: 140px;
  font-weight: 500;
}

.leave-reason-cell {
  min-width: 150px;
  color: #2f4357;
}

.leave-time-cell {
  width: 196px;
  color: #4e6479;
  white-space: nowrap;
}

.leave-status-cell {
  text-align: center;
  vertical-align: middle;
}

.leave-status-wrap {
  width: max-content;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.leave-status-tag {
  width: 82px;
  height: 30px;
  box-sizing: border-box;
  display: inline-flex;
  padding: 0;
  margin: 0 auto;
  border-radius: 999px;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  line-height: 1;
  text-align: center;
}

.leave-actions-cell {
  text-align: center;
  vertical-align: middle;
}

.leave-actions {
  width: max-content;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
  flex-wrap: nowrap;
  margin: 0 auto;
}

.leave-action-btn {
  width: 66px;
  height: 30px;
  min-width: 66px;
  min-height: 30px;
  padding: 0;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 700;
  line-height: 1;
  text-align: center;
  box-shadow: none;
}

.leave-approve-btn {
  background: #6757df;
  color: #ffffff;
}

.leave-reject-btn {
  background: #ef4444;
  color: #ffffff;
}

.leave-delete-btn {
  background: #edf2f7;
  color: #5f6f82;
}

.leave-action-btn:disabled {
  opacity: 0.42;
  cursor: not-allowed;
}

.leave-filters {
  gap: 6px;
  margin-bottom: 14px;
}

.leave-filter {
  min-width: 82px;
  min-height: 36px;
  padding: 0 16px;
  border-radius: 12px;
  font-size: 13px;
}

.student-service-panel {
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.student-service-filters {
  margin-bottom: 18px;
  gap: 8px;
}

.student-service-filter {
  min-width: 116px;
  min-height: 44px;
  padding: 0 18px;
  border-radius: 16px;
  font-size: 15px;
  font-weight: 700;
}

.express-audit-shell .empty,
.lostfound-audit-shell .empty {
  min-height: 220px;
  border: 1px dashed #dce6ef;
  border-radius: 24px;
  background: #fcfdff;
}

.express-audit-table-shell ::v-deep .table-wrap,
.lostfound-audit-table-shell ::v-deep .table-wrap {
  overflow-x: auto;
  border: 1px solid #e7eef5;
  border-radius: 24px;
  background: #ffffff;
  box-shadow: 0 14px 34px rgba(20, 35, 52, 0.05);
}

.express-audit-table-shell ::v-deep .table,
.lostfound-audit-table-shell ::v-deep .table {
  min-width: 1010px;
  table-layout: fixed;
}

.express-audit-table-shell ::v-deep .table th,
.express-audit-table-shell ::v-deep .table td,
.lostfound-audit-table-shell ::v-deep .table th,
.lostfound-audit-table-shell ::v-deep .table td {
  padding: 18px 12px;
  font-size: 14px;
  line-height: 1.4;
  vertical-align: middle;
}

.express-audit-table-shell ::v-deep .table thead th,
.lostfound-audit-table-shell ::v-deep .table thead th {
  color: #5f7590;
  font-size: 15px;
  font-weight: 800;
  background: #ffffff;
  white-space: nowrap;
  text-align: center;
}

.express-audit-table-shell ::v-deep .table th:nth-child(1),
.express-audit-table-shell ::v-deep .table td:nth-child(1),
.lostfound-audit-table-shell ::v-deep .table th:nth-child(1),
.lostfound-audit-table-shell ::v-deep .table td:nth-child(1) {
  width: 96px;
  text-align: center;
}

.express-audit-table-shell ::v-deep .table th:nth-child(2),
.express-audit-table-shell ::v-deep .table td:nth-child(2) {
  width: 160px;
  text-align: center;
}

.express-audit-table-shell ::v-deep .table th:nth-child(3),
.express-audit-table-shell ::v-deep .table td:nth-child(3) {
  width: 160px;
  text-align: center;
}

.express-audit-table-shell ::v-deep .table th:nth-child(4),
.express-audit-table-shell ::v-deep .table td:nth-child(4) {
  width: 136px;
  text-align: center;
}

.express-audit-table-shell ::v-deep .table th:nth-child(5),
.express-audit-table-shell ::v-deep .table td:nth-child(5),
.lostfound-audit-table-shell ::v-deep .table th:nth-child(5),
.lostfound-audit-table-shell ::v-deep .table td:nth-child(5) {
  width: 132px;
  text-align: center;
}

.express-audit-table-shell ::v-deep .table th:nth-child(6),
.express-audit-table-shell ::v-deep .table td:nth-child(6),
.lostfound-audit-table-shell ::v-deep .table th:nth-child(6),
.lostfound-audit-table-shell ::v-deep .table td:nth-child(6) {
  width: 252px;
  min-width: 252px;
  max-width: 252px;
  text-align: center;
  padding-left: 0;
  padding-right: 0;
}

.lostfound-audit-table-shell ::v-deep .table th:nth-child(2),
.lostfound-audit-table-shell ::v-deep .table td:nth-child(2) {
  width: 184px;
  text-align: center;
}

.lostfound-audit-table-shell ::v-deep .table th:nth-child(3),
.lostfound-audit-table-shell ::v-deep .table td:nth-child(3) {
  width: 112px;
  text-align: center;
}

.lostfound-audit-table-shell ::v-deep .table th:nth-child(4),
.lostfound-audit-table-shell ::v-deep .table td:nth-child(4) {
  width: 184px;
  text-align: center;
}

.express-audit-table-shell ::v-deep .table tbody tr,
.lostfound-audit-table-shell ::v-deep .table tbody tr {
  height: 74px;
}

.express-actions-cell {
  text-align: center !important;
}

.express-actions {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
}

.express-action-btn {
  width: 76px;
  height: 32px;
  min-width: 76px;
  min-height: 32px;
  padding: 0 6px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
  text-align: center;
  box-shadow: none;
  white-space: nowrap;
}

.express-approve-btn {
  background: #6757df;
  color: #ffffff;
}

.express-reject-btn {
  background: #ef4444;
  color: #ffffff;
}

.express-delete-btn {
  background: #edf2f7;
  color: #5f6f82;
}

.lostfound-actions-cell {
  text-align: center !important;
}

.lostfound-actions {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
}

.lostfound-action-btn {
  width: 76px;
  height: 32px;
  min-width: 76px;
  min-height: 32px;
  padding: 0 6px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
  text-align: center;
  box-shadow: none;
  white-space: nowrap;
}

.lostfound-approve-btn {
  background: #6757df;
  color: #ffffff;
}

.lostfound-reject-btn {
  background: #ef4444;
  color: #ffffff;
}

.lostfound-delete-btn {
  background: #edf2f7;
  color: #5f6f82;
}

.parttime-empty {
  min-height: 220px;
  border: 1px dashed #dce6ef;
  border-radius: 24px;
  background: #fcfdff;
}

.parttime-plain-shell .empty {
  min-height: 220px;
  border: 1px dashed #dce6ef;
  border-radius: 24px;
  background: #fcfdff;
}

.parttime-plain-table-shell ::v-deep .table-wrap {
  overflow-x: auto;
  border: 1px solid #e7eef5;
  border-radius: 24px;
  background: #ffffff;
  box-shadow: 0 14px 34px rgba(20, 35, 52, 0.05);
}

.parttime-plain-table-shell ::v-deep .table {
  min-width: 1010px;
  table-layout: fixed;
}

.parttime-plain-table-shell ::v-deep .table th,
.parttime-plain-table-shell ::v-deep .table td {
  padding: 18px 12px;
  font-size: 14px;
  line-height: 1.4;
  vertical-align: middle;
}

.parttime-plain-table-shell ::v-deep .table thead th {
  color: #5f7590;
  font-size: 15px;
  font-weight: 800;
  background: #ffffff;
  white-space: nowrap;
  text-align: center;
}

.parttime-plain-table-shell ::v-deep .table th:nth-child(1),
.parttime-plain-table-shell ::v-deep .table td:nth-child(1) {
  width: 96px;
  text-align: center;
}

.parttime-plain-table-shell ::v-deep .table th:nth-child(2),
.parttime-plain-table-shell ::v-deep .table td:nth-child(2) {
  width: 184px;
  text-align: center;
}

.parttime-plain-table-shell ::v-deep .table th:nth-child(3),
.parttime-plain-table-shell ::v-deep .table td:nth-child(3) {
  width: 160px;
  text-align: center;
}

.parttime-plain-table-shell ::v-deep .table th:nth-child(4),
.parttime-plain-table-shell ::v-deep .table td:nth-child(4) {
  width: 136px;
  text-align: center;
}

.parttime-plain-table-shell ::v-deep .table th:nth-child(5),
.parttime-plain-table-shell ::v-deep .table td:nth-child(5) {
  width: 132px;
  text-align: center;
}

.parttime-plain-table-shell ::v-deep .table th:nth-child(6),
.parttime-plain-table-shell ::v-deep .table td:nth-child(6) {
  width: 252px;
  min-width: 252px;
  max-width: 252px;
  text-align: center;
  padding-left: 0;
  padding-right: 0;
}

.parttime-plain-table-shell ::v-deep .table tbody tr {
  height: 74px;
}

.parttime-actions-cell {
  text-align: center !important;
}

.parttime-actions {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
}

.parttime-action-btn {
  width: 76px;
  height: 32px;
  min-width: 76px;
  min-height: 32px;
  padding: 0 6px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
  text-align: center;
  box-shadow: none;
  white-space: nowrap;
}

.parttime-approve-btn {
  background: #6757df;
  color: #ffffff;
}

.parttime-reject-btn {
  background: #ef4444;
  color: #ffffff;
}

.parttime-delete-btn {
  background: #edf2f7;
  color: #5f6f82;
}

.list {
  display: grid;
  gap: 12px;
}

.list-item {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  border: 1px solid #e6eef5;
  border-radius: 16px;
  padding: 14px;
}

.notice-list {
  gap: 14px;
}

.notice-item {
  align-items: stretch;
  padding: 18px 20px;
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.notice-main {
  flex: 1;
  min-width: 0;
}

.notice-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.notice-side {
  min-width: 148px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
}

.notice-meta {
  color: #7b8b99;
  font-size: 12px;
  white-space: nowrap;
}

.notice-actions {
  gap: 6px;
  justify-content: flex-end;
}

.notice-table-btn {
  width: 56px;
  height: 28px;
  min-height: 28px;
  padding: 0;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 400;
  line-height: 1;
}

.notice-table-secondary {
  color: #607286;
  background: #f1f6fb;
  box-shadow: inset 0 0 0 1px rgba(223, 232, 241, 0.95);
}

.notice-table-danger {
  background: #f85757;
  box-shadow: none;
}

.feedback-panel {
  background:
    radial-gradient(circle at top right, rgba(103, 87, 223, 0.05), transparent 24%),
    linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.feedback-status-tag {
  min-width: 72px;
  justify-content: center;
  padding: 6px 12px;
  font-size: 13px;
}

.feedback-reply-btn {
  min-width: 0;
  width: 60px;
  height: 32px;
  min-height: 32px;
  padding: 0;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 400;
  line-height: 1;
  background: #6a58eb;
  box-shadow: none;
}

.list-item h4 {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 500;
}

.list-item p {
  margin: 0 0 8px;
  color: #4b5f73;
  font-size: 14px;
  line-height: 1.6;
}

.modal-mask {
  position: fixed;
  inset: 0;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(15, 23, 42, 0.38);
  z-index: 1000;
}

.modal-card {
  width: min(520px, 100%);
  border-radius: 18px;
  padding: 20px;
  background: #fff;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.24);
}

.modal-card h3 {
  margin: 0;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 12px;
}

@media (max-width: 720px) {
  .admin-page {
    padding: 14px;
  }

  .tab-bar {
    padding: 12px;
    border-radius: 22px;
  }

  .tab-button {
    width: 100%;
    justify-content: flex-start;
  }

  .card {
    padding: 20px 18px;
    border-radius: 20px;
  }

  .form-grid,
  .form-grid.three {
    grid-template-columns: 1fr;
  }

  .compact-grid {
    grid-template-columns: 1fr;
  }

  .list-item {
    flex-direction: column;
  }

  .section-headline {
    flex-direction: column;
  }

  .notice-side {
    min-width: 0;
    width: 100%;
    align-items: flex-start;
  }

  .notice-actions {
    justify-content: flex-start;
  }

  .maintenance-btn {
    min-width: 96px;
    min-height: 32px;
  }

  .volunteer-panel {
    padding: 18px;
  }

  .model-hero,
  .model-form-shell {
    padding: 18px;
  }

  .model-list-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .model-form-grid input {
    min-height: 54px;
  }

  .model-actions {
    justify-content: flex-start;
  }

  .model-row-actions {
    min-width: 0;
    flex-wrap: wrap;
  }

  .volunteer-hero {
    flex-direction: column;
    padding: 18px;
  }

  .volunteer-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    min-width: 0;
  }

  .volunteer-filter {
    flex: 1 1 calc(50% - 10px);
    min-width: 0;
  }

  .volunteer-item {
    flex-direction: column;
    padding: 18px;
  }

  .volunteer-item-top {
    flex-direction: column;
  }

  .volunteer-item-bottom {
    flex-direction: column;
  }

  .volunteer-meta {
    grid-template-columns: 1fr;
  }

  .volunteer-actions {
    width: 100%;
  }
}
</style>
