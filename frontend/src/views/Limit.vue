<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="专业/年级/课程/备注" clearable style="width:240px" @keyup.enter.native="search" />
      <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
      <el-button type="success" icon="el-icon-plus" @click="openEdit()">新增限制</el-button>
    </div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="courseName" label="限制课程" min-width="140">
        <template slot-scope="{ row }">{{ row.courseName || '全局' }}</template>
      </el-table-column>
      <el-table-column prop="major" label="限选专业" width="140" />
      <el-table-column prop="grade" label="限选年级" width="100" />
      <el-table-column prop="minCredit" label="最小学分" width="90" />
      <el-table-column prop="maxCredit" label="最大学分" width="90" />
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="endTime" label="结束时间" width="160" />
      <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="mini">{{ row.status === 1 ? '生效' : '失效' }}</el-tag>
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

    <el-dialog :title="form.id ? '修改选课限制' : '新增选课限制'" :visible.sync="visible" width="620px">
      <el-form :model="form" ref="form" label-width="100px">
        <el-form-item label="限制课程">
          <el-select v-model="form.courseId" clearable filterable placeholder="留空表示全局" style="width:100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseNo + ' - ' + c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="限选专业"><el-input v-model="form.major" /></el-form-item>
        <el-form-item label="限选年级"><el-input v-model="form.grade" /></el-form-item>
        <el-form-item label="最小学分"><el-input-number v-model="form.minCredit" :min="0" :step="0.5" /></el-form-item>
        <el-form-item label="最大学分"><el-input-number v-model="form.maxCredit" :min="0" :step="0.5" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
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
const MODULE = 'limit'
export default {
  name: 'Limit',
  data() {
    return {
      loading: false, list: [], total: 0, page: 1, size: 10, keyword: '',
      visible: false, form: {}, courses: []
    }
  },
  created() { this.load(); this.loadOptions() },
  methods: {
    async loadOptions() {
      const c = await listApi('course')
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
      this.form = row ? { ...row } : { status: 1 }
      this.visible = true
    },
    async save() {
      if (this.form.id) await updateApi(MODULE, this.form)
      else await addApi(MODULE, this.form)
      this.$message.success('保存成功')
      this.visible = false
      this.load()
    },
    remove(row) {
      this.$confirm('确认删除该限制吗？', '提示', { type: 'warning' }).then(async () => {
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
