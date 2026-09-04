<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="标题/内容/发布人" clearable style="width:240px" @keyup.enter.native="search" />
      <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
      <el-button type="success" icon="el-icon-plus" @click="openEdit()">新增公告</el-button>
    </div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="content" label="内容" min-width="240" show-overflow-tooltip />
      <el-table-column prop="publisher" label="发布人" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="mini">{{ row.status === 1 ? '发布' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="160" />
      <el-table-column label="操作" width="160" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" @click="openEdit(row)">修改</el-button>
          <el-button type="text" style="color:#F56C6C" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" background layout="total, prev, pager, next" :total="total" :page-size="size" :current-page.sync="page" @current-change="load" />

    <el-dialog :title="form.id ? '修改公告' : '新增公告'" :visible.sync="visible" width="640px">
      <el-form :model="form" :rules="rules" ref="form" label-width="80px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input type="textarea" :rows="5" v-model="form.content" /></el-form-item>
        <el-form-item label="发布人"><el-input v-model="form.publisher" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="发布" inactive-text="下架" /></el-form-item>
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
const MODULE = 'announcement'
export default {
  name: 'Announcement',
  data() {
    return {
      loading: false, list: [], total: 0, page: 1, size: 10, keyword: '',
      visible: false, form: {},
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
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
      this.form = row ? { ...row } : { publisher: '管理员', status: 1 }
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
      this.$confirm('确认删除该公告吗？', '提示', { type: 'warning' }).then(async () => {
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
