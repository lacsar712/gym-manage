<template>
	<div class="login-container">
		<div class="login-background"></div>
		<el-card class="login-card">
			<div class="login-header">
				<div class="logo-box">
					<el-icon :size="32" color="#409eff"><Management /></el-icon>
				</div>
				<h2>健身房管理系统</h2>
				<p>专业、高效、智能的健身房管理平台</p>
			</div>

			<el-form :model="form" @keyup.enter="onSubmit" label-position="top">
				<el-form-item label="账号">
					<el-input v-model="form.username" autocomplete="username" placeholder="请输入账号">
						<template #prefix>
							<el-icon><User /></el-icon>
						</template>
					</el-input>
				</el-form-item>
				<el-form-item label="密码">
					<el-input
						v-model="form.password"
						type="password"
						autocomplete="current-password"
						show-password
						placeholder="请输入密码"
					>
						<template #prefix>
							<el-icon><Lock /></el-icon>
						</template>
					</el-input>
				</el-form-item>
				<el-form-item style="margin-top: 32px">
					<el-button type="primary" :loading="loading" class="login-submit" @click="onSubmit"> 登 录 </el-button>
				</el-form-item>
				<div class="login-footer">
					<span>默认账号：admin / admin123</span>
				</div>
			</el-form>
		</el-card>
	</div>
</template>

<style scoped>
.login-container {
	height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
	position: relative;
	overflow: hidden;
}

.login-background {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
	z-index: -1;
}

.login-background::after {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-image: radial-gradient(rgba(255, 255, 255, 0.1) 1px, transparent 1px);
	background-size: 32px 32px;
}

.login-card {
	width: 400px;
	border-radius: 16px;
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
	padding: 20px;
}

.login-header {
	text-align: center;
	margin-bottom: 40px;
}

.logo-box {
	width: 64px;
	height: 64px;
	background: #ecf5ff;
	border-radius: 16px;
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 0 auto 16px;
}

.login-header h2 {
	margin: 0;
	font-size: 24px;
	color: #303133;
	letter-spacing: 1px;
}

.login-header p {
	margin: 8px 0 0;
	font-size: 14px;
	color: #909399;
}

.login-submit {
	width: 100%;
	height: 44px;
	font-size: 16px;
	font-weight: 600;
	border-radius: 8px;
	letter-spacing: 4px;
}

.login-footer {
	text-align: center;
	margin-top: 16px;
	font-size: 13px;
	color: #c0c4cc;
}

:deep(.el-form-item__label) {
	font-weight: 500;
	padding-bottom: 4px !important;
}

:deep(.el-input__wrapper) {
	padding: 4px 12px;
	border-radius: 8px;
}
</style>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { User, Lock, Management } from '@element-plus/icons-vue'
import http from '../api/http'

const router = useRouter()
const loading = ref(false)
const form = reactive({
	username: 'admin',
	password: 'admin123',
})

async function onSubmit() {
	if (!form.username || !form.password) {
		ElMessage.error('请输入账号密码')
		return
	}
	loading.value = true
	try {
		const data = await http.post('/auth/login', { username: form.username, password: form.password })
		localStorage.setItem('token', data.token)
		router.push('/members')
	} catch (e) {
		ElMessage.error(e?.message || '登录失败')
	} finally {
		loading.value = false
	}
}
</script>
