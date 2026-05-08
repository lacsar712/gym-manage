<template>
	<div class="page-container">
		<div class="header-actions">
			<el-input
				v-model="query.keyword"
				placeholder="搜索课程标题"
				class="search-input"
				clearable
				@keyup.enter="onSearch"
				@clear="onSearch"
			>
				<template #prefix>
					<el-icon><Search /></el-icon>
				</template>
			</el-input>
			<el-select v-model="query.coachId" placeholder="按教练过滤" clearable style="width: 200px" @change="onSearch">
				<el-option v-for="c in allCoaches" :key="c.id" :label="c.name" :value="c.id" />
			</el-select>
			<el-button type="primary" @click="onSearch">查询</el-button>
			<el-button @click="resetQuery">重置</el-button>
			<div class="flex-1"></div>
			<el-button type="success" @click="openCreate">
				<el-icon><Plus /></el-icon>
				<span>新增课程</span>
			</el-button>
		</div>

		<el-table :data="items" v-loading="loading" border stripe class="w-full">
			<el-table-column type="index" label="序号" width="60" align="center" />
			<el-table-column prop="title" label="标题" min-width="150" />
			<el-table-column label="教练" width="150">
				<template #default="{ row }">
					<el-tag effect="plain" size="small">{{ coachName(row.coachId) }}</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="startTime" label="开始时间" width="170" />
			<el-table-column prop="endTime" label="结束时间" width="170" />
			<el-table-column prop="capacity" label="容量" width="80" align="center" />
			<el-table-column prop="status" label="状态" width="100" align="center">
				<template #default="{ row }">
					<el-tag :type="row.status === 'OPEN' ? 'success' : 'info'" size="small">
						{{ row.status === 'OPEN' ? '开放预约' : '已结课' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="remark" label="备注" show-overflow-tooltip />
			<el-table-column label="操作" width="150" fixed="right" align="center">
				<template #default="{ row }">
					<el-button size="small" link type="primary" @click="openEdit(row)">编辑</el-button>
					<el-button size="small" link type="danger" @click="onDelete(row)">删除</el-button>
				</template>
			</el-table-column>
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

		<el-dialog v-model="dialogVisible" :title="editingId ? '编辑课程' : '新增课程'" width="520px">
			<el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
				<el-form-item label="标题" prop="title">
					<el-input v-model="form.title" />
				</el-form-item>
				<el-form-item label="教练" prop="coachId">
					<el-select v-model="form.coachId" style="width: 100%">
						<el-option v-for="c in activeCoaches" :key="c.id" :label="c.name" :value="c.id" />
					</el-select>
				</el-form-item>
				<el-form-item label="开始时间" prop="startDateTime">
					<el-date-picker
						v-model="form.startDateTime"
						type="datetime"
						value-format="YYYY-MM-DD HH:mm:ss"
						style="width: 100%"
					/>
				</el-form-item>
				<el-form-item label="结束时间" prop="endDateTime">
					<el-date-picker
						v-model="form.endDateTime"
						type="datetime"
						value-format="YYYY-MM-DD HH:mm:ss"
						style="width: 100%"
					/>
				</el-form-item>
				<el-form-item label="最大人数" prop="maxParticipants">
					<el-input-number v-model="form.maxParticipants" :min="1" style="width: 100%" />
				</el-form-item>
				<el-form-item label="状态">
					<el-select v-model="form.status" style="width: 100%">
						<el-option label="开放预约" value="OPEN" />
						<el-option label="已结课" value="CLOSED" />
					</el-select>
				</el-form-item>
				<el-form-item label="备注">
					<el-input v-model="form.remark" type="textarea" :rows="3" />
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import http from '../api/http'

const loading = ref(false)
const saving = ref(false)
const items = ref([])
const total = ref(0)

const query = reactive({
	page: 1,
	pageSize: 10,
	keyword: '',
	coachId: null,
})

const allCoaches = ref([])
const activeCoaches = computed(() => allCoaches.value.filter((c) => (c.status || '').toUpperCase() === 'ACTIVE'))
const coachMap = computed(() => {
	const m = new Map()
	for (const c of allCoaches.value) m.set(c.id, c.name)
	return m
})

const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
	title: '',
	coachId: null,
	startDateTime: '',
	endDateTime: '',
	maxParticipants: 1,
	status: 'OPEN',
	remark: '',
})

function coachName(id) {
	return coachMap.value.get(id) || `#${id}`
}

function resetForm() {
	form.title = ''
	form.coachId = null
	form.startDateTime = ''
	form.endDateTime = ''
	form.maxParticipants = 1
	form.status = 'OPEN'
	form.remark = ''
}

function openCreate() {
	editingId.value = null
	resetForm()
	setTimeout(() => formRef.value?.clearValidate(), 0)
	dialogVisible.value = true
}

function openEdit(row) {
	editingId.value = row.id
	form.title = row.title
	form.coachId = row.coachId
	form.startDateTime = row.startTime
	form.endDateTime = row.endTime
	form.maxParticipants = row.capacity
	form.status = row.status || 'OPEN'
	form.remark = row.remark || ''
	setTimeout(() => formRef.value?.clearValidate(), 0)
	dialogVisible.value = true
}

async function fetchCoaches() {
	try {
		const data = await http.get('/coaches', { params: { page: 1, pageSize: 1000 } })
		allCoaches.value = data.items || []
	} catch (e) {
		ElMessage.error(e?.message || '加载教练失败')
	}
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
		if (query.keyword && query.keyword.trim()) params.keyword = query.keyword.trim()
		if (query.coachId) params.coachId = query.coachId
		const data = await http.get('/courses', { params })
		items.value = data.items || []
		total.value = data.total || 0
	} catch (e) {
		ElMessage.error(e?.message || '加载失败')
	} finally {
		loading.value = false
	}
}

function resetQuery() {
	query.page = 1
	query.pageSize = 10
	query.keyword = ''
	query.coachId = null
	fetchList()
}

const formRef = ref(null)

const validateEndDate = (rule, value, callback) => {
	if (!value) {
		callback(new Error('请选择结束时间'))
	} else if (form.startDateTime && new Date(value) <= new Date(form.startDateTime)) {
		callback(new Error('结束时间必须晚于开始时间'))
	} else {
		callback()
	}
}

const rules = {
	title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
	coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
	startDateTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
	endDateTime: [{ required: true, validator: validateEndDate, trigger: 'change' }],
	maxParticipants: [{ required: true, message: '请输入最大人数', trigger: 'blur' }],
}

async function onSave() {
	if (!formRef.value) return
	await formRef.value.validate(async (valid) => {
		if (valid) {
			saving.value = true
			try {
				const payload = {
					title: form.title,
					coachId: form.coachId,
					startTime: form.startDateTime,
					endTime: form.endDateTime,
					capacity: form.maxParticipants,
					status: form.status,
					remark: form.remark,
				}
				if (editingId.value) {
					await http.put(`/courses/${editingId.value}`, payload)
				} else {
					await http.post('/courses', payload)
				}
				ElMessage.success('保存成功')
				dialogVisible.value = false
				fetchList()
			} catch (e) {
				ElMessage.error(e?.message || '保存失败')
			} finally {
				saving.value = false
			}
		}
	})
}

async function onDelete(row) {
	try {
		await ElMessageBox.confirm(`确认删除课程「${row.title}」？`, '提示', { type: 'warning' })
		await http.delete(`/courses/${row.id}`)
		ElMessage.success('删除成功')
		fetchList()
	} catch {
		// ignore
	}
}

onMounted(async () => {
	await fetchCoaches()
	await fetchList()
})
</script>

<style scoped>
.pagination-container {
	display: flex;
	justify-content: flex-end;
	margin-top: 24px;
}

:deep(.el-table) {
	margin-top: 4px;
}
</style>
