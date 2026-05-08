<template>
	<el-container class="admin-layout">
		<el-aside width="240px" class="sidebar">
			<div class="logo-container">
				<el-icon :size="24" color="#409eff"><Management /></el-icon>
				<span class="logo-text">健身房管理系统</span>
			</div>
			<el-menu
				:default-active="route.path"
				router
				class="side-menu"
				background-color="#001529"
				text-color="rgba(255, 255, 255, 0.65)"
				active-text-color="#fff"
			>
				<el-menu-item index="/members">
					<el-icon><User /></el-icon>
					<span>会员管理</span>
				</el-menu-item>
				<el-menu-item index="/coaches">
					<el-icon><Avatar /></el-icon>
					<span>教练管理</span>
				</el-menu-item>
				<el-menu-item index="/courses">
					<el-icon><Reading /></el-icon>
					<span>课程安排</span>
				</el-menu-item>
				<el-menu-item index="/checkins">
					<el-icon><Checked /></el-icon>
					<span>签到记录</span>
				</el-menu-item>
				<el-menu-item index="/equipments">
					<el-icon><Setting /></el-icon>
					<span>器材管理</span>
				</el-menu-item>
			</el-menu>
		</el-aside>

		<el-container class="main-container">
			<el-header class="header">
				<div class="header-left">
					<h2 class="page-title">{{ pageTitle }}</h2>
				</div>
				<div class="header-right">
					<el-dropdown trigger="click">
						<div class="user-info">
							<el-avatar :size="32" icon="UserFilled" />
							<span class="username">管理员</span>
						</div>
						<template #dropdown>
							<el-dropdown-menu>
								<el-dropdown-item @click="logout">退出登录</el-dropdown-item>
							</el-dropdown-menu>
						</template>
					</el-dropdown>
				</div>
			</el-header>
			<el-main class="content-main">
				<div class="content-wrapper">
					<router-view />
				</div>
			</el-main>
		</el-container>
	</el-container>
</template>

<style scoped>
.admin-layout {
	height: 100vh;
	background-color: #f0f2f5;
}

.sidebar {
	background-color: #001529;
	box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
	z-index: 10;
	display: flex;
	flex-direction: column;
}

.logo-container {
	height: 64px;
	display: flex;
	align-items: center;
	padding: 0 24px;
	gap: 12px;
	border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-text {
	color: white;
	font-size: 18px;
	font-weight: 700;
	letter-spacing: 0.5px;
}

.side-menu {
	border-right: none;
	flex: 1;
}

.side-menu :deep(.el-menu-item) {
	height: 50px;
	line-height: 50px;
	margin: 4px 12px;
	border-radius: 8px;
}

.side-menu :deep(.el-menu-item.is-active) {
	background-color: var(--primary-color) !important;
}

.main-container {
	height: 100vh;
	display: flex;
	flex-direction: column;
}

.header {
	height: 64px;
	background-color: white;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 24px;
	box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
	z-index: 9;
}

.page-title {
	margin: 0;
	font-size: 18px;
	font-weight: 500;
	color: #1f1f1f;
}

.user-info {
	display: flex;
	align-items: center;
	gap: 10px;
	cursor: pointer;
	padding: 4px 8px;
	border-radius: 6px;
	transition: background 0.3s;
}

.user-info:hover {
	background-color: #f5f5f5;
}

.username {
	font-size: 14px;
	color: #595959;
}

.content-main {
	padding: 24px;
	overflow-y: auto;
}

.content-wrapper {
	background-color: white;
	padding: 24px;
	border-radius: 8px;
	min-height: calc(100vh - 64px - 48px - 48px);
	box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}
</style>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Management, User, Avatar, Reading, Checked, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const pageTitle = computed(() => {
	const titles = {
		'/members': '会员管理',
		'/coaches': '教练管理',
		'/courses': '课程管理',
		'/checkins': '签到记录',
		'/equipments': '器材管理',
	}
	return titles[route.path] || '系统管理'
})

function logout() {
	localStorage.removeItem('token')
	router.push('/login')
}
</script>
