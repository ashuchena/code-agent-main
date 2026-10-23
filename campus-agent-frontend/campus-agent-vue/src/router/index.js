import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Admin from '../views/Admin.vue'
import Schedule from '../views/Schedule.vue'
import ScheduleList from '../views/ScheduleList.vue'
import Remind from '../views/Remind.vue'
import Dining from '../views/Dining.vue'
import RepairPhone from '../views/RepairPhone.vue'
import Chat from '../views/Chat.vue'
import PsychConsult from '../views/PsychConsult.vue'
import OrderFood from '../views/OrderFood.vue'
import Leave from '../views/Leave.vue'
import LostFound from '../views/LostFound.vue'
import Express from '../views/Express.vue'
import Volunteer from '../views/Volunteer.vue'
import PartTime from '../views/PartTime.vue'
import Maintenance from '../views/Maintenance.vue'
import Feedback from '../views/Feedback.vue'
import FeedbackForm from '../views/FeedbackForm.vue'
import Announcement from '../views/Announcement.vue'
import ServiceAccess from '../views/ServiceAccess.vue'
import { getAuthUserId, hydrateAuthCookies, readAuthSession } from '../utils/auth'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录 - 校园助手', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册 - 校园助手', requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/chat'
  },
  {
    path: '/chat',
    name: 'Chat',
    component: Chat,
    meta: { title: 'AI 对话 - 校园助手', requiresAuth: true }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { title: '首页概览 - 校园助手', requiresAuth: true }
  },
  {
    path: '/student-service',
    name: 'StudentService',
    component: Schedule,
    meta: { title: '学生服务 - 校园助手', requiresAuth: true }
  },
  {
    path: '/schedule',
    name: 'Schedule',
    component: Schedule,
    meta: { title: '学生服务 - 校园助手', requiresAuth: true }
  },
  {
    path: '/schedule-list',
    name: 'ScheduleList',
    component: ScheduleList,
    meta: { title: '我的课表 - 校园助手', requiresAuth: true }
  },
  {
    path: '/remind',
    name: 'Remind',
    component: Remind,
    meta: { title: '提醒事项 - 校园助手', requiresAuth: true }
  },
  {
    path: '/dining',
    name: 'Dining',
    component: Dining,
    meta: { title: '食堂推荐 - 校园助手', requiresAuth: true }
  },
  {
    path: '/repair',
    redirect: '/maintenance'
  },
  {
    path: '/repair-phone',
    name: 'RepairPhone',
    component: RepairPhone,
    meta: { title: '服务热线 - 校园助手', requiresAuth: true }
  },
  {
    path: '/psych-consult',
    name: 'PsychConsult',
    component: PsychConsult,
    meta: { title: '心理陪伴 - 校园助手', requiresAuth: true }
  },
  {
    path: '/psych',
    redirect: '/psych-consult'
  },
  {
    path: '/order/:id',
    name: 'OrderFood',
    component: OrderFood,
    meta: { title: '点餐系统 - 校园助手', requiresAuth: true }
  },
  {
    path: '/leave',
    name: 'Leave',
    component: Leave,
    meta: { title: '请假申请 - 校园助手', requiresAuth: true }
  },
  {
    path: '/lostfound',
    name: 'LostFound',
    component: LostFound,
    meta: { title: '失物招领 - 校园助手', requiresAuth: true }
  },
  {
    path: '/express',
    name: 'Express',
    component: Express,
    meta: { title: '快递代取 - 校园助手', requiresAuth: true }
  },
  {
    path: '/volunteer',
    name: 'Volunteer',
    component: Volunteer,
    meta: { title: '志愿活动 - 校园助手', requiresAuth: true }
  },
  {
    path: '/parttime',
    name: 'PartTime',
    component: PartTime,
    meta: { title: '勤工助学 - 校园助手', requiresAuth: true }
  },
  {
    path: '/maintenance',
    name: 'Maintenance',
    component: Maintenance,
    meta: { title: '报修登记 - 校园助手', requiresAuth: true }
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: Feedback,
    meta: { title: '问题反馈 - 校园助手', requiresAuth: true }
  },
  {
    path: '/feedback/:type',
    name: 'FeedbackForm',
    component: FeedbackForm,
    meta: { title: '反馈表单 - 校园助手', requiresAuth: true }
  },
  {
    path: '/announcement',
    name: 'Announcement',
    component: Announcement,
    meta: { title: '公告通知 - 校园助手', requiresAuth: true }
  },
  {
    path: '/service-access',
    name: 'ServiceAccess',
    component: ServiceAccess,
    meta: { title: '统一服务接入 - 校园助手', requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    meta: { title: '管理员后台 - 校园助手', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '*',
    redirect: '/chat'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
  scrollBehavior() {
    return { x: 0, y: 0 }
  }
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '校园助手'

  const auth = readAuthSession()
  const userId = getAuthUserId()

  if (auth && !Vue.$cookies.get('userId')) {
    hydrateAuthCookies(auth)
  }

  if (to.meta.requiresAuth && !userId) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  if (to.meta.requiresAdmin && (!auth || auth.role !== 'admin')) {
    next('/chat')
    return
  }

  if ((to.path === '/login' || to.path === '/register') && userId) {
    next(to.query.redirect || '/chat')
    return
  }

  next()
})

export default router
