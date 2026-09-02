<template>
  <div class="login-page">
    <div class="login-card">
      <h2>学生选课管理系统</h2>
      <p class="sub">SpringBoot + Vue + Element UI</p>
      <el-form ref="form" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" prefix-icon="el-icon-user" placeholder="用户名" @keyup.enter.native="handleLogin" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" prefix-icon="el-icon-lock" type="password" placeholder="密码" show-password @keyup.enter.native="handleLogin" />
        </el-form-item>
        <el-button type="primary" style="width:100%" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form>
      <div class="tip">默认账号：admin / admin123</div>
    </div>
  </div>
</template>

<script>
import { login } from '../api'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      form: { username: 'admin', password: 'admin123' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await login(this.form)
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('username', res.data.username)
          localStorage.setItem('realName', res.data.realName || '')
          this.$message.success('登录成功')
          this.$router.push('/')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f2d3d 0%, #3a6073 50%, #16222a 100%);
}
.login-card {
  width: 380px;
  background: #fff;
  border-radius: 8px;
  padding: 36px 32px 28px;
  box-shadow: 0 12px 40px rgba(0,0,0,.25);
}
.login-card h2 {
  margin: 0;
  text-align: center;
  color: #1f2d3d;
}
.sub {
  text-align: center;
  color: #909399;
  margin: 8px 0 28px;
  font-size: 13px;
}
.tip {
  margin-top: 16px;
  text-align: center;
  color: #909399;
  font-size: 12px;
}
</style>
