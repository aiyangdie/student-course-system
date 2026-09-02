<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="brand">学生选课管理系统</div>
      <el-menu :default-active="$route.path" router background-color="#1f2d3d" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/dashboard"><i class="el-icon-s-home"></i><span>首页概览</span></el-menu-item>
        <el-menu-item index="/student"><i class="el-icon-user"></i><span>学生管理</span></el-menu-item>
        <el-menu-item index="/teacher"><i class="el-icon-s-custom"></i><span>教师管理</span></el-menu-item>
        <el-menu-item index="/course"><i class="el-icon-reading"></i><span>课程信息管理</span></el-menu-item>
        <el-menu-item index="/announcement"><i class="el-icon-bell"></i><span>公告信息管理</span></el-menu-item>
        <el-menu-item index="/schedule"><i class="el-icon-date"></i><span>排课信息管理</span></el-menu-item>
        <el-menu-item index="/selection"><i class="el-icon-tickets"></i><span>选课信息管理</span></el-menu-item>
        <el-menu-item index="/limit"><i class="el-icon-lock"></i><span>选课限制管理</span></el-menu-item>
        <el-menu-item index="/grade"><i class="el-icon-document"></i><span>学生成绩管理</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="title">{{ $route.meta.title || '管理后台' }}</span>
        <div class="user">
          <span>{{ realName || username }}</span>
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { logout } from '../api'

export default {
  name: 'MainLayout',
  data() {
    return {
      username: localStorage.getItem('username') || '管理员',
      realName: localStorage.getItem('realName') || ''
    }
  },
  methods: {
    async handleLogout() {
      try { await logout() } catch (e) { /* ignore */ }
      localStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.layout { height: 100%; }
.aside { background: #1f2d3d; min-height: 100vh; }
.brand {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #2d3a4b;
  padding: 0 10px;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
}
.title { font-size: 16px; font-weight: 600; color: #303133; }
.user { display: flex; align-items: center; gap: 12px; color: #606266; }
.main { padding: 16px; }
.el-menu { border-right: none; }
</style>
