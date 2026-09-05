<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="学号/姓名/课程" clearable style="width:240px" @keyup.enter.native="search" />
      <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
      <el-button type="success" icon="el-icon-plus" @click="openEdit()">新增成绩</el-button>
    </div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="studentNo" label="学号" width="110" />
      <el-table-column prop="studentName" label="学生" width="100" />
      <el-table-column prop="courseName" label="课程" min-width="140" />
      <el-table-column prop="semester" label="学期" width="120" />
      <el-table-column prop="usualScore" label="平时成绩" width="90" />
      <el-table-column prop="examScore" label="考试成绩" width="90" />
      <el-table-column prop="totalScore" label="总评" width="90" />
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" @click="openEdit(row)">修改</el-button>
          <el-button type="text" style="color:#F56C6C" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="size" :current-page.sync="page" @current-change="load" />

    <el-dialog :title="form.id ? '修改成绩' : '新增成绩'" :visible.sync="visible" width="560px">
      <el-form :model="form" :rules="rules" ref="form" label-width="90px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" filterable style="width:100%">
            <el-option v-for="s in students" :key="s.id" :label="s.studentNo + ' - ' + s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" filterable style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseNo + ' - ' + c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期"><el-input v-model="form.semester" /></el-form-item>
        <el-form-item label="平时成绩"><el-input-number v-model="form.usualScore" :min="0" :max="100" :precision="2" /></el-form-item>
        <el-form-item label="考试成绩"><el-input-number v-model="form.examScore" :min="0" :max="100" :precision="2" /></el-form-item>
        <el-form-item label="总评成绩"><el-input-number v-model="form.totalScore" :min="0" :max="100" :precision="2" placeholder="可留空自动计算" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
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
const MODULE = 'grade'
export default {
  name: 'Grade',
  data() {
    return {
      loading: false, list: [], total: 0, page: 1, size: 10, keyword: '',
      visible: false, form: {}, students: [], courses: [],
      rules: {
        studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }]
      }
    }
  },
  created() { this.load(); this.loadOptions() },
  methods: {
    async loadOptions() {
      const [s, c] = await Promise.all([listApi('student'), listApi('course')])
      this.students = s.data || []
      this.courses = c.data || []
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
      this.form = row ? { ...row } : { semester: '2025-2026-2' }
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
      this.$confirm('确认删除该成绩吗？', '提示', { type: 'warning' }).then(async () => {
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
