<template>
  <div class="dashboard" v-loading="loading">
    <div class="welcome">
      <div>
        <h2>欢迎回来，{{ displayName }}</h2>
        <p>数据来自当前数据库实时统计，点击卡片可进入对应模块。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" plain @click="load">刷新数据</el-button>
    </div>

    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="8" :md="6" v-for="item in cards" :key="item.key">
        <div class="stat-card" :style="{ borderTopColor: item.color }" @click="go(item.path)">
          <div class="stat-top">
            <span class="stat-label">{{ item.label }}</span>
            <i :class="item.icon" :style="{ color: item.color }"></i>
          </div>
          <div class="stat-num">{{ displayCount(item) }}</div>
          <div class="stat-extra">{{ item.extra }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="panel-row">
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel">
          <div slot="header" class="panel-header">
            <span>最新公告</span>
            <el-button type="text" @click="go('/announcement')">全部</el-button>
          </div>
          <div v-if="!recentAnnouncements.length" class="empty">暂无公告</div>
          <div
            v-for="item in recentAnnouncements"
            :key="'a'+item.id"
            class="list-item"
            @click="go('/announcement')"
          >
            <div class="list-title">
              <el-tag size="mini" :type="item.status === 1 ? 'success' : 'info'">
                {{ item.status === 1 ? '发布' : '下架' }}
              </el-tag>
              <span>{{ item.title }}</span>
            </div>
            <div class="list-meta">{{ item.publisher || '管理员' }} · {{ item.createTime || '-' }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel">
          <div slot="header" class="panel-header">
            <span>最近选课</span>
            <el-button type="text" @click="go('/selection')">全部</el-button>
          </div>
          <div v-if="!recentSelections.length" class="empty">暂无选课记录</div>
          <div
            v-for="item in recentSelections"
            :key="'s'+item.id"
            class="list-item"
            @click="go('/selection')"
          >
            <div class="list-title">
              <span>{{ item.studentName || '未知学生' }}</span>
              <span class="muted">选了</span>
              <span>{{ item.courseName || '未知课程' }}</span>
            </div>
            <div class="list-meta">
              {{ item.teacherName || '-' }} · {{ item.semester || '-' }} ·
              {{ item.status === 1 ? '已选' : '已退' }}
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel">
          <div slot="header" class="panel-header">
            <span>近期排课</span>
            <el-button type="text" @click="go('/schedule')">全部</el-button>
          </div>
          <div v-if="!recentSchedules.length" class="empty">暂无排课</div>
          <div
            v-for="item in recentSchedules"
            :key="'c'+item.id"
            class="list-item"
            @click="go('/schedule')"
          >
            <div class="list-title">
              <span>{{ item.courseName || '未知课程' }}</span>
              <el-tag size="mini" :type="item.status === 1 ? 'success' : 'info'">
                {{ item.status === 1 ? '开课' : '停开' }}
              </el-tag>
            </div>
            <div class="list-meta">
              {{ item.teacherName || '-' }} · 周{{ item.weekday }} 第{{ item.sectionStart }}-{{ item.sectionEnd }}节 ·
              {{ item.selectedCount || 0 }}/{{ item.capacity || 0 }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="quick-panel">
      <div slot="header">快捷入口</div>
      <div class="quick-list">
        <el-button
          v-for="item in shortcuts"
          :key="item.path"
          plain
          :icon="item.icon"
          @click="go(item.path)"
        >{{ item.label }}</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getOverview } from '../api'

export default {
  name: 'Dashboard',
  data() {
    return {
      loading: false,
      stats: {},
      recentAnnouncements: [],
      recentSelections: [],
      recentSchedules: [],
      cards: [
        { key: 'studentCount', label: '学生', path: '/student', icon: 'el-icon-user', color: '#2f6fed', extra: '进入学生管理' },
        { key: 'teacherCount', label: '教师', path: '/teacher', icon: 'el-icon-s-custom', color: '#0f9d8a', extra: '进入教师管理' },
        { key: 'courseCount', label: '课程', path: '/course', icon: 'el-icon-reading', color: '#d9891c', extra: '进入课程管理' },
        { key: 'scheduleCount', label: '排课', path: '/schedule', icon: 'el-icon-date', color: '#6b5ce7', extraKey: 'openScheduleCount', extraPrefix: '开课中 ' },
        { key: 'selectionCount', label: '有效选课', path: '/selection', icon: 'el-icon-tickets', color: '#e35d6a', extra: '仅统计已选状态' },
        { key: 'announcementCount', label: '公告', path: '/announcement', icon: 'el-icon-bell', color: '#3aa0ff', extraKey: 'publishedAnnouncementCount', extraPrefix: '已发布 ' },
        { key: 'gradeCount', label: '成绩记录', path: '/grade', icon: 'el-icon-document', color: '#5b8c5a', extra: '进入成绩管理' },
        { key: 'limitCount', label: '生效限制', path: '/limit', icon: 'el-icon-lock', color: '#8a6d3b', extra: '当前生效规则' }
      ],
      shortcuts: [
        { label: '新增学生', path: '/student', icon: 'el-icon-plus' },
        { label: '新增课程', path: '/course', icon: 'el-icon-plus' },
        { label: '安排排课', path: '/schedule', icon: 'el-icon-date' },
        { label: '办理选课', path: '/selection', icon: 'el-icon-tickets' },
        { label: '录入成绩', path: '/grade', icon: 'el-icon-edit' },
        { label: '发布公告', path: '/announcement', icon: 'el-icon-bell' }
      ]
    }
  },
  computed: {
    displayName() {
      return localStorage.getItem('realName') || localStorage.getItem('username') || '管理员'
    }
  },
  created() {
    this.load()
  },
  methods: {
    displayCount(item) {
      const n = this.stats[item.key]
      return n == null ? 0 : n
    },
    async load() {
      this.loading = true
      try {
        const res = await getOverview()
        const data = res.data || {}
        this.stats = data.stats || {}
        this.recentAnnouncements = data.recentAnnouncements || []
        this.recentSelections = data.recentSelections || []
        this.recentSchedules = data.recentSchedules || []
        this.cards = this.cards.map(card => {
          if (card.extraKey) {
            return {
              ...card,
              extra: (card.extraPrefix || '') + (this.stats[card.extraKey] == null ? 0 : this.stats[card.extraKey])
            }
          }
          return card
        })
      } finally {
        this.loading = false
      }
    },
    go(path) {
      if (path) this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.dashboard { min-height: 100%; }
.welcome {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 18px 20px;
  background: linear-gradient(120deg, #f7f9fc 0%, #eef4ff 100%);
  border: 1px solid #e4ebf5;
  border-radius: 8px;
}
.welcome h2 {
  margin: 0 0 6px;
  font-size: 20px;
  color: #1f2d3d;
}
.welcome p {
  margin: 0;
  color: #7a8699;
  font-size: 13px;
}
.stat-row { margin-bottom: 8px; }
.stat-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-top: 3px solid #409EFF;
  border-radius: 8px;
  padding: 14px 16px 12px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform .15s ease, box-shadow .15s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(31, 45, 61, .08);
}
.stat-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #606266;
  font-size: 13px;
}
.stat-top i { font-size: 18px; }
.stat-num {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  color: #1f2d3d;
  line-height: 1.1;
}
.stat-extra {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
.panel-row { margin-bottom: 8px; }
.panel {
  margin-bottom: 16px;
  border: 1px solid #ebeef5;
  min-height: 280px;
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.list-item {
  padding: 10px 0;
  border-bottom: 1px dashed #eef0f4;
  cursor: pointer;
}
.list-item:last-child { border-bottom: none; }
.list-item:hover .list-title { color: #2f6fed; }
.list-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
  font-size: 14px;
}
.list-meta {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
  padding-left: 2px;
}
.muted { color: #909399; }
.empty {
  color: #c0c4cc;
  text-align: center;
  padding: 40px 0;
}
.quick-panel { border: 1px solid #ebeef5; }
.quick-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>
