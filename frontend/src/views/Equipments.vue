<template>
	<div class="page-container">
		<div class="header-actions">
			<el-input
				v-model="query.keyword"
				placeholder="搜索名称/编号"
				class="search-input"
				clearable
				@keyup.enter="onSearch"
				@clear="onSearch"
			>
				<template #prefix>
					<el-icon><Search /></el-icon>
				</template>
			</el-input>
			<el-select v-model="query.status" placeholder="按状态过滤" clearable style="width: 200px" @change="onSearch">
				<el-option label="可用" value="AVAILABLE" />
				<el-option label="维护中" value="MAINTENANCE" />
				<el-option label="故障" value="BROKEN" />
			</el-select>
			<el-button type="primary" @click="onSearch">查询</el-button>
			<el-button @click="resetQuery">重置</el-button>
			<div class="flex-1"></div>
			<el-button type="success" @click="openCreate">
				<el-icon><Plus /></el-icon>
				<span>新增器材</span>
			</el-button>
		</div>

		<el-table :data="items" v-loading="loading" border stripe class="w-full">
			<el-table-column type="index" label="序号" width="60" align="center" />
			<el-table-column prop="name" label="名称" min-width="150" />
			<el-table-column prop="code" label="编号" width="140" />
			<el-table-column prop="location" label="位置" width="120" />
			<el-table-column prop="status" label="状态" width="120" align="center">
				<template #default="{ row }">
					<el-tag
						:type="row.status === 'AVAILABLE' ? 'success' : row.status === 'MAINTENANCE' ? 'warning' : 'danger'"
						size="small"
					>
						{{ statusLabel(row.status) }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="buyDate" label="购入日期" width="120" />
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

		<el-dialog v-model="dialogVisible" :title="editingId ? '编辑器材' : '新增器材'" width="520px">
			<el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
				<el-form-item label="名称" prop="name">
					<el-input v-model="form.name" />
				</el-form-item>
				<el-form-item label="编号" prop="code">
					<el-input v-model="form.code" placeholder="可选，若填必须唯一" />
				</el-form-item>
				<el-form-item label="位置" prop="location">
					<el-input v-model="form.location" placeholder="如A区、2楼" />
				</el-form-item>
				<el-form-item label="状态" prop="status">
					<el-select v-model="form.status" style="width: 100%">
						<el-option label="可用" value="AVAILABLE" />
						<el-option label="维护中" value="MAINTENANCE" />
						<el-option label="故障" value="BROKEN" />
					</el-select>
				</el-form-item>
				<el-form-item label="购入日期">
					<el-date-picker v-model="form.buyDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
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
import { onMounted, reactive, ref } from 'vue'
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
	status: '',
})

const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
	name: '',
	code: '',
	location: '',
	status: 'AVAILABLE',
	buyDate: '',
	remark: '',
})

function statusLabel(v) {
	if (v === 'MAINTENANCE') return '维护中'
	if (v === 'BROKEN') return '故障'
	return '可用'
}

function resetForm() {
	form.name = ''
	form.code = ''
	form.location = ''
	form.status = 'AVAILABLE'
	form.buyDate = ''
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
	form.name = row.name
	form.code = row.code
	form.location = row.location
	form.status = row.status || 'AVAILABLE'
	form.buyDate = row.buyDate || ''
	form.remark = row.remark || ''
	setTimeout(() => formRef.value?.clearValidate(), 0)
	dialogVisible.value = true
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
		if (query.status) params.status = query.status
		const data = await http.get('/equipments', { params })
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
	query.status = ''
	fetchList()
}

const formRef = ref(null)

const rules = {
	name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
	location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
	status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

async function onSave() {
	if (!formRef.value) return
	await formRef.value.validate(async (valid) => {
		if (valid) {
			saving.value = true
			try {
				const payload = {
					name: form.name,
					code: form.code,
					location: form.location,
					status: form.status,
					buyDate: form.buyDate || null,
					remark: form.remark,
				}
				if (editingId.value) {
					await http.put(`/equipments/${editingId.value}`, payload)
				} else {
					await http.post('/equipments', payload)
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
		await ElMessageBox.confirm(`确认删除器材「${row.name}」？`, '提示', { type: 'warning' })
		await http.delete(`/equipments/${row.id}`)
		ElMessage.success('删除成功')
		fetchList()
	} catch {
		// ignore
	}
}

onMounted(fetchList)
</script>

<style scoped>
:deep(.el-table) {
	margin-top: 4px;
}
</style>
