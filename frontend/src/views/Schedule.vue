<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="课程/教师/教室" clearable style="width:240px" @keyup.enter.native="search" />
      <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
      <el-button type="success" icon="el-icon-plus" @click="openEdit()">新增排课</el-button>
    </div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="courseNo" label="课程编号" width="100" />
      <el-table-column prop="courseName" label="课程名称" min-width="130" />
      <el-table-column prop="teacherName" label="授课教师" width="100" />
      <el-table-column prop="semester" label="学期" width="120" />
      <el-table-column label="上课时间" width="140">
        <template slot-scope="{ row }">周{{ row.weekday }} 第{{ row.sectionStart }}-{{ row.sectionEnd }}节</template>
      </el-table-column>
      <el-table-column prop="classroom" label="教室" width="120" />
      <el-table-column label="容量" width="100">
        <template slot-scope="{ row }">{{ row.selectedCount }}/{{ row.capacity }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="mini">{{ row.status === 1 ? '开课' : '停开' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" @click="openEdit(row)">修改</el-button>
          <el-button type="text" style="color:#F56C6C" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="size" :current-page.sync="page" @current-change="load" />

    <el-dialog :title="form.id ? '修改排课' : '新增排课'" :visible.sync="visible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="90px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" filterable style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseNo + ' - ' + c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" prop="teacherId">
          <el-select v-model="form.teacherId" filterable style="width:100%">
            <el-option v-for="t in teachers" :key="t.id" :label="t.teacherNo + ' - ' + t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="semester"><el-input v-model="form.semester" placeholder="如 2025-2026-2" /></el-form-item>
        <el-form-item label="星期" prop="weekday">
          <el-select v-model="form.weekday">
            <el-option v-for="d in 7" :key="d" :label="'周'+d" :value="d" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始节次" prop="sectionStart"><el-input-number v-model="form.sectionStart" :min="1" :max="12" /></el-form-item>
        <el-form-item label="结束节次" prop="sectionEnd"><el-input-number v-model="form.sectionEnd" :min="1" :max="12" /></el-form-item>
        <el-form-item label="教室"><el-input v-model="form.classroom" /></el-form-item>
        <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="1" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
import { pageApi, addApi, updateApi, deleteApi, listApi } from '../api'
const MODULE = 'schedule'
export default {
  name: 'Schedule',
  data() {
    return {
      loading: false, list: [], total: 0, page: 1, size: 10, keyword: '',
      visible: false, form: {}, courses: [], teachers: [],
      rules: {
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
        teacherId: [{ required: true, message: '请选择教师', trigger: 'change' }],
        semester: [{ required: true, message: '请输入学期', trigger: 'blur' }],
        weekday: [{ required: true, message: '请选择星期', trigger: 'change' }],
        sectionStart: [{ required: true, message: '请输入开始节次', trigger: 'blur' }],
        sectionEnd: [{ required: true, message: '请输入结束节次', trigger: 'blur' }]
      }
    }
  },
  created() { this.load(); this.loadOptions() },
  methods: {
    async loadOptions() {
      const [c, t] = await Promise.all([listApi('course'), listApi('teacher')])
      this.courses = c.data || []
      this.teachers = t.data || []
    },
    async load() {
      this.loading = true
      try {
        const res = await pageApi(MODULE, { page: this.page, size: this.size, keyword: this.keyword })
        this.list = res.data.records
        this.total = res.data.total
      } finally { this.loading = false }
    },
    search() { this.page = 1; this.load() },
    openEdit(row) {
      this.form = row ? { ...row } : { weekday: 1, sectionStart: 1, sectionEnd: 2, capacity: 50, status: 1, semester: '2025-2026-2' }
      this.visible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    save() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (this.form.id) await updateApi(MODULE, this.form)
        else await addApi(MODULE, this.form)
        this.$message.success('保存成功')
        this.visible = false
        this.load()
      })
    },
    remove(row) {
      this.$confirm('确认删除该排课吗？', '提示', { type: 'warning' }).then(async () => {
        await deleteApi(MODULE, row.id)
        this.$message.success('删除成功')
        this.load()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.toolbar { margin-bottom: 12px; display: flex; gap: 8px; }
.pager { margin-top: 12px; text-align: right; }
</style>
