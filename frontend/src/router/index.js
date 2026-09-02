import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '../layout/MainLayout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '首页概览' } },
      { path: 'student', name: 'Student', component: () => import('../views/Student.vue'), meta: { title: '学生管理' } },
      { path: 'teacher', name: 'Teacher', component: () => import('../views/Teacher.vue'), meta: { title: '教师管理' } },
      { path: 'course', name: 'Course', component: () => import('../views/Course.vue'), meta: { title: '课程信息管理' } },
      { path: 'announcement', name: 'Announcement', component: () => import('../views/Announcement.vue'), meta: { title: '公告信息管理' } },
      { path: 'schedule', name: 'Schedule', component: () => import('../views/Schedule.vue'), meta: { title: '排课信息管理' } },
      { path: 'selection', name: 'Selection', component: () => import('../views/Selection.vue'), meta: { title: '选课信息管理' } },
      { path: 'limit', name: 'Limit', component: () => import('../views/Limit.vue'), meta: { title: '选课限制管理' } },
      { path: 'grade', name: 'Grade', component: () => import('../views/Grade.vue'), meta: { title: '学生成绩管理' } }
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path !== '/login' && !localStorage.getItem('token')) {
    next('/login')
  } else if (to.path === '/login' && localStorage.getItem('token')) {
    next('/')
  } else {
    next()
  }
})

export default router
