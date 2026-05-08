<template>
	<div class="page-container">
		<div class="header-actions">
			<el-input
				v-model="query.memberId"
				placeholder="会员 ID"
				class="search-input"
				clearable
				@keyup.enter="onSearch"
				@clear="onSearch"
			>
				<template #prefix>
					<el-icon><Search /></el-icon>
				</template>
			</el-input>
			<el-date-picker
				v-model="query.dateFrom"
				type="date"
				value-format="YYYY-MM-DD"
				placeholder="起始日期"
				style="width: 140px"
				@change="onSearch"
			/>
			<el-date-picker
				v-model="query.dateTo"
				type="date"
				value-format="YYYY-MM-DD"
				placeholder="结束日期"
				style="width: 140px"
				@change="onSearch"
			/>
			<el-button type="primary" @click="onSearch">查询</el-button>
			<el-button @click="resetQuery">重置</el-button>
			<el-button type="success" @click="openCheckin">手动签到</el-button>
		</div>

		<el-table :data="items" v-loading="loading" border stripe class="w-full">
			<el-table-column type="index" label="序号" width="60" align="center" />
			<el-table-column prop="memberId" label="会员 ID" width="100" align="center">
				<template #default="{ row }">
					<el-tag size="small">ID: {{ row.memberId }}</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="memberName" label="姓名" min-width="120" />
			<el-table-column prop="checkinTime" label="签到时间" min-width="200" />
			<el-table-column prop="remark" label="备注" show-overflow-tooltip />
		</el-table>

		<div class="pagination-container">
			<el-pagination
				background
				layout="total, prev, pager, next, sizes"
				:total="total"
				v-model:current-page="query.page"
				v-model:page-size="query.pageSize"
				@current-change="onPageChange"
				@size-change="onPageSizeChange"
			/>
		</div>

		<el-dialog v-model="dialogVisible" title="手动签到" width="400px">
			<el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
				<el-form-item label="会员" prop="memberId">
					<el-select
						v-model="form.memberId"
						filterable
						remote
						reserve-keyword
						placeholder="输入 ID / 姓名 / 手机尾号搜索"
						:remote-method="fetchMemberOptions"
						:loading="memberLoading"
						style="width: 100%"
					>
						<el-option
							v-for="item in memberOptions"
							:key="item.id"
							:label="`${item.id} - ${item.name} (${item.phone.slice(-4)})`"
							:value="item.id"
						/>
					</el-select>
				</el-form-item>
				<el-form-item label="备注">
					<el-input v-model="form.remark" type="textarea" :rows="2" />
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="saving" @click="onSave">提交</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import http from '../api/http'

const loading = ref(false)
const items = ref([])
const total = ref(0)

const query = reactive({
	page: 1,
	pageSize: 10,
	memberId: '',
	dateFrom: '',
	dateTo: '',
})

const dialogVisible = ref(false)
const saving = ref(false)
const form = reactive({
	memberId: null,
	remark: '',
})

const memberOptions = ref([])
const memberLoading = ref(false)

const formRef = ref(null)

const rules = {
	memberId: [{ required: true, message: '请选择会员', trigger: 'change' }],
}

function onSearch() {
	query.page = 1
	fetchList()
}

function onPageChange(page) {
	query.page = page
	fetchList()
}

function onPageSizeChange(size) {
	query.pageSize = size
	query.page = 1
	fetchList()
}

async function fetchList() {
	loading.value = true
	try {
		const params = {
			page: query.page,
			pageSize: query.pageSize,
		}
		if (query.memberId && String(query.memberId).trim()) params.memberId = String(query.memberId).trim()
		if (query.dateFrom) params.dateFrom = query.dateFrom
		if (query.dateTo) params.dateTo = query.dateTo
		const data = await http.get('/checkins', { params })
		items.value = data.items || []
		total.value = data.total || 0
	} catch (e) {
		ElMessage.error(e?.message || '加载失败')
	} finally {
		loading.value = false
	}
}

function openCheckin() {
	form.memberId = null
	form.remark = ''
	memberOptions.value = []
	setTimeout(() => formRef.value?.clearValidate(), 0)
	dialogVisible.value = true
}

async function fetchMemberOptions(queryStr) {
	if (!queryStr) {
		memberOptions.value = []
		return
	}
	memberLoading.value = true
	try {
		// Mock search logic using list API, backend search enhancement is ideal but list filter works for small scale
		const params = { page: 1, pageSize: 20, keyword: queryStr }
		const data = await http.get('/members', { params })
		memberOptions.value = data.items || []
	} catch (e) {
		// ignore
	} finally {
		memberLoading.value = false
	}
}

async function onSave() {
	if (!formRef.value) return
	await formRef.value.validate(async (valid) => {
		if (valid) {
			saving.value = true
			try {
				await http.post('/checkins', {
					memberId: parseInt(form.memberId),
					remark: form.remark,
				})
				ElMessage.success('签到成功')
				dialogVisible.value = false
				fetchList()
			} catch (e) {
				ElMessage.error(e?.message || '签到失败')
			} finally {
				saving.value = false
			}
		}
	})
}

function resetQuery() {
	query.page = 1
	query.pageSize = 10
	query.memberId = ''
	query.dateFrom = ''
	query.dateTo = ''
	fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
:deep(.el-table) {
	margin-top: 4px;
}
</style>
