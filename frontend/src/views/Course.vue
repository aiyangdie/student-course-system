<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="课程编号/名称/院系" clearable style="width:240px" @keyup.enter.native="search" />
      <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
      <el-button type="success" icon="el-icon-plus" @click="openEdit()">新增课程</el-button>
    </div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="courseNo" label="课程编号" width="110" />
      <el-table-column prop="courseName" label="课程名称" min-width="140" />
      <el-table-column prop="credit" label="学分" width="70" />
      <el-table-column prop="hours" label="学时" width="70" />
      <el-table-column prop="courseType" label="类型" width="80" />
      <el-table-column prop="department" label="开课院系" min-width="120" />
      <el-table-column prop="description" label="简介" min-width="180" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="mini">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
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

    <el-dialog :title="form.id ? '修改课程' : '新增课程'" :visible.sync="visible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="90px">
        <el-form-item label="课程编号" prop="courseNo"><el-input v-model="form.courseNo" /></el-form-item>
        <el-form-item label="课程名称" prop="courseName"><el-input v-model="form.courseName" /></el-form-item>
        <el-form-item label="学分"><el-input-number v-model="form.credit" :min="0" :step="0.5" /></el-form-item>
        <el-form-item label="学时"><el-input-number v-model="form.hours" :min="0" /></el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="form.courseType"><el-option label="必修" value="必修" /><el-option label="选修" value="选修" /></el-select>
        </el-form-item>
        <el-form-item label="开课院系"><el-input v-model="form.department" /></el-form-item>
        <el-form-item label="课程简介"><el-input type="textarea" :rows="3" v-model="form.description" /></el-form-item>
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
import { pageApi, addApi, updateApi, deleteApi } from '../api'
const MODULE = 'course'
export default {
  name: 'Course',
  data() {
    return {
      loading: false, list: [], total: 0, page: 1, size: 10, keyword: '',
      visible: false, form: {},
      rules: {
        courseNo: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
        courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
      }
    }
  },
  created() { this.load() },
  methods: {
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
      this.form = row ? { ...row } : { courseType: '必修', credit: 2, hours: 32, status: 1 }
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
      this.$confirm('确认删除该课程吗？', '提示', { type: 'warning' }).then(async () => {
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
