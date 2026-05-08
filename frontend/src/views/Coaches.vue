<template>
	<div class="page-container">
		<div class="header-actions">
			<el-input
				v-model="query.keyword"
				placeholder="搜索姓名/手机号"
				class="search-input"
				clearable
				@keyup.enter="onSearch"
				@clear="onSearch"
			>
				<template #prefix>
					<el-icon><Search /></el-icon>
				</template>
			</el-input>
			<el-button type="primary" @click="onSearch">查询</el-button>
			<el-button @click="resetQuery">重置</el-button>
			<div class="flex-1"></div>
			<el-button type="success" @click="openCreate">
				<el-icon><Plus /></el-icon>
				<span>新增教练</span>
			</el-button>
		</div>

		<el-table :data="items" v-loading="loading" border stripe class="w-full">
			<el-table-column type="index" label="序号" width="60" align="center" />
			<el-table-column prop="name" label="姓名" min-width="120" />
			<el-table-column prop="phone" label="手机号" width="130" />
			<el-table-column prop="specialty" label="擅长" min-width="150" />
			<el-table-column prop="status" label="状态" width="100" align="center">
				<template #default="{ row }">
					<el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">
						{{ statusLabel(row.status) }}
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

		<el-dialog v-model="dialogVisible" :title="editingId ? '编辑教练' : '新增教练'" width="520px">
			<el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
				<el-form-item label="姓名" prop="name">
					<el-input v-model="form.name" />
				</el-form-item>
				<el-form-item label="手机号" prop="phone">
					<el-input v-model="form.phone" />
				</el-form-item>
				<el-form-item label="擅长" prop="specialty">
					<el-input v-model="form.specialty" />
				</el-form-item>
				<el-form-item label="状态" prop="status">
					<el-select v-model="form.status" style="width: 100%">
						<el-option label="在职" value="ACTIVE" />
						<el-option label="离职" value="INACTIVE" />
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
})

const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
	name: '',
	phone: '',
	specialty: '',
	status: 'ACTIVE',
	remark: '',
})

function statusLabel(v) {
	return v === 'INACTIVE' ? '离职' : '在职'
}

function resetForm() {
	form.name = ''
	form.phone = ''
	form.specialty = ''
	form.status = 'ACTIVE'
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
	form.phone = row.phone
	form.specialty = row.specialty || ''
	form.status = row.status || 'ACTIVE'
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
		const data = await http.get('/coaches', { params })
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
	fetchList()
}

const formRef = ref(null)

const rules = {
	name: [
		{ required: true, message: '请输入姓名', trigger: 'blur' },
		{ min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
	],
	phone: [
		{ required: true, message: '请输入手机号', trigger: 'blur' },
		{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
	],
	specialty: [{ required: true, message: '请输入擅长领域', trigger: 'blur' }],
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
					phone: form.phone,
					specialty: form.specialty,
					status: form.status,
					remark: form.remark,
				}
				if (editingId.value) {
					await http.put(`/coaches/${editingId.value}`, payload)
				} else {
					await http.post('/coaches', payload)
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
		await ElMessageBox.confirm(`确认删除教练「${row.name}」？`, '提示', { type: 'warning' })
		await http.delete(`/coaches/${row.id}`)
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
