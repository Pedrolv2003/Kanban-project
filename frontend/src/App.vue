<template>
  <div v-if="!isAuthenticated" @click="handleGlobalClick">
    <LoginForm 
      v-if="currentPage === 'login'"
      @login-success="handleLoginSuccess"
      @switch-to-register="currentPage = 'register'"
    />
    <RegisterForm 
      v-else
      @register-success="handleRegisterSuccess"
      @back-to-login="currentPage = 'login'"
      @back-to-kanban="handleBackToKanban"
    />
  </div>
  
  <!-- Página do Calendário -->
  <CalendarPage 
    v-else-if="currentPage === 'calendar'"
    :tasks="tasks"
    :user-email="userEmail"
    :columns="dynamicColumns"
    @close="currentPage = 'kanban'"
    @refresh-tasks="loadTasks"
  />
  
  <!-- Página do Kanban -->
  <div v-else class="kanban-app">
    <header class="kanban-header">
      <div class="header-content">
        <div class="logo-area">
          <h1 class="logo">
            <span class="highlight">Mini</span> Kanban
          </h1>
          <p class="logo-subtitle">Organize suas tarefas com estilo</p>
        </div>
        <div class="user-area">
          <button @click="openNewColumnModal" class="btn-new-column">
            <i class="fas fa-plus-circle"></i> Nova Coluna
          </button>
          
          <button class="calendar-btn" @click="openCalendarPage">
            <i class="fas fa-calendar-alt"></i> Calendario
          </button>
          
          <SettingsMenu 
            :user-email="userEmail"
            @register="showRegisterPage"
            @logout="logout"
          />
        </div>
      </div>
    </header>

    <main class="kanban-main">
      <div v-if="message" class="message-toast" :class="{ error: message.includes('Erro') }">
        <i :class="message.includes('Erro') ? 'fas fa-exclamation-circle' : 'fas fa-check-circle'"></i>
        {{ message }}
      </div>

      <div class="kanban-board-wrapper">
        <div class="kanban-board">
          <KanbanColumn
            v-for="col in dynamicColumns"
            :key="col.status"
            :status="col.status"
            :title="col.title"
            :color="col.color"
            :icon="col.icon"
            :tasks="tasksByStatus[col.status]"
            :selected-id="selectedTask?.id ?? null"
            :active-filters="columnFilters[col.status] || {}"
            @select="handleSelectTask"
            @drop="moveTask"
            @open-create-modal="openCreateModal"
            @delete-column="openDeleteColumnModal"
            @open-filter="openFilterModal"
          />
        </div>
      </div>
      
      <div class="scroll-spacer"></div>
    </main>

    <FilterModal 
      :is-open="isFilterModalOpen"
      :column-title="filteringColumn?.title || ''"
      :all-tasks="tasks"
      @close="closeFilterModal"
      @apply-filter="applyFilterToColumn"
    />

    <Modal :is-open="isTaskModalOpen" :title="modalTitle" @close="closeTaskModal">
      <TaskFormModal
        :initial-data="editingTask"
        :is-editing="!!editingTask"
        @submit="handleTaskSubmit"
        @cancel="closeTaskModal"
      />
    </Modal>

    <TaskDetailModal
      :is-open="isTaskDetailModalOpen"
      :task="selectedTask"
      @close="closeTaskDetailModal"
      @edit="openEditFromDetail"
      @delete="openDeleteConfirm"
    />

    <Modal :is-open="isDeleteModalOpen" title="Eliminar Tarefa" @close="closeDeleteModal">
      <div class="delete-confirm">
        <i class="fas fa-exclamation-triangle"></i>
        <p>Tem certeza que deseja eliminar a tarefa <strong>"{{ deletingTask?.titulo }}"</strong>?</p>
        <p class="delete-warning">Esta acao nao pode ser desfeita.</p>
        <div class="delete-actions">
          <button class="btn-cancel" @click="closeDeleteModal">Cancelar</button>
          <button class="btn-confirm" @click="confirmDelete">Eliminar</button>
        </div>
      </div>
    </Modal>
    
    <Modal :is-open="isDeleteColumnModalOpen" title="Eliminar Coluna" @close="closeDeleteColumnModal">
      <div class="delete-confirm">
        <i class="fas fa-exclamation-triangle"></i>
        <p>Tem certeza que deseja eliminar a coluna <strong>"{{ deletingColumn?.title }}"</strong>?</p>
        <p v-if="tasksInDeletingColumn > 0" class="delete-warning">
          Existem {{ tasksInDeletingColumn }} tarefa{{ tasksInDeletingColumn > 1 ? 's' : '' }} nesta coluna. 
          Elas serao movidas para "To Do".
        </p>
        <p v-else class="delete-warning">Esta acao nao pode ser desfeita.</p>
        <div class="delete-actions">
          <button class="btn-cancel" @click="closeDeleteColumnModal">Cancelar</button>
          <button class="btn-confirm" @click="confirmDeleteColumn">Eliminar Coluna</button>
        </div>
      </div>
    </Modal>
    
    <NewColumnModal 
      :is-open="isNewColumnModalOpen"
      @close="closeNewColumnModal"
      @create="createNewColumn"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import KanbanColumn from './components/KanbanColumn.vue'
import LoginForm from './components/LoginForm.vue'
import RegisterForm from './components/RegisterForm.vue'
import Modal from './components/Modal.vue'
import TaskFormModal from './components/TaskFormModal.vue'
import NewColumnModal from './components/NewColumnModal.vue'
import FilterModal from './components/FilterModal.vue'
import TaskDetailModal from './components/TaskDetailModal.vue'
import SettingsMenu from './components/SettingsMenu.vue'
import CalendarPage from './components/CalendarPage.vue'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const tasks = ref([])
const selectedTask = ref(null)
const message = ref('')
const isAuthenticated = ref(false)
const userEmail = ref('')
const userRole = ref('')
const currentPage = ref('login') // 'login', 'register', 'kanban', 'calendar'
const isFilterModalOpen = ref(false)
const filteringColumn = ref(null)
const columnFilters = ref({})
const isDeleteColumnModalOpen = ref(false)
const deletingColumn = ref(null)
const tasksInDeletingColumn = ref(0)
const isTaskDetailModalOpen = ref(false)

const columns = ref([
  { 
    status: 'TO_DO', 
    title: 'To Do', 
    color: '#3b82f6', 
    icon: 'fas fa-list-ul',
    order: 0
  },
  { 
    status: 'IN_PROGRESS', 
    title: 'In Progress', 
    color: '#f59e0b', 
    icon: 'fas fa-spinner',
    order: 1
  },
  { 
    status: 'DONE', 
    title: 'Done', 
    color: '#10b981', 
    icon: 'fas fa-check-circle',
    order: 2
  }
])

const isTaskModalOpen = ref(false)
const isDeleteModalOpen = ref(false)
const isNewColumnModalOpen = ref(false)
const editingTask = ref(null)
const deletingTask = ref(null)
const creatingStatus = ref(null)

const previousSession = ref({
  token: null,
  email: null,
  role: null
})

const dynamicColumns = computed(() => {
  return [...columns.value].sort((a, b) => a.order - b.order)
})

const tasksByStatus = computed(() => {
  const groups = {}
  columns.value.forEach(col => {
    groups[col.status] = []
  })
  tasks.value.forEach(t => {
    const key = t.estado || 'TO_DO'
    if (groups[key] !== undefined) {
      groups[key].push(t)
    } else {
      if (groups['TO_DO']) groups['TO_DO'].push(t)
    }
  })
  return groups
})

const modalTitle = computed(() => {
  return editingTask.value ? 'Editar Tarefa' : 'Nova Tarefa'
})

// Navegação
const openCalendarPage = () => {
  currentPage.value = 'calendar'
}

const closeTaskDetailModal = () => {
  isTaskDetailModalOpen.value = false
}

const openEditFromDetail = (task) => {
  editingTask.value = { ...task }
  creatingStatus.value = null
  isTaskModalOpen.value = true
  closeTaskDetailModal()
}

// Carregar colunas do backend
const loadColumns = async () => {
  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/columns`)
    if (res.ok) {
      const data = await res.json()
      columns.value = data.map((col, index) => ({
        status: col.status,
        title: col.title,
        color: col.color,
        icon: col.icon,
        order: col.orderIndex !== undefined ? col.orderIndex : index
      }))
    }
  } catch (err) {
    console.error('Erro ao carregar colunas:', err)
  }
}

// Criar nova coluna no backend
const createNewColumn = async (newColumn) => {
  const newStatus = newColumn.status
  if (columns.value.some(col => col.status === newStatus)) {
    showMessage('Ja existe uma coluna com este nome', 2000)
    return
  }
  
  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/columns`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        status: newStatus,
        title: newColumn.title,
        color: newColumn.color,
        icon: newColumn.icon,
        orderIndex: columns.value.length
      })
    })
    
    if (!res.ok) {
      const error = await res.text()
      throw new Error(error)
    }
    
    const saved = await res.json()
    columns.value.push({
      status: saved.status,
      title: saved.title,
      color: saved.color,
      icon: saved.icon,
      order: saved.orderIndex
    })
    
    showMessage(`Coluna "${newColumn.title}" criada com sucesso!`, 2000)
  } catch (err) {
    showMessage(`${err.message}`, 2000)
  }
}

// Deletar coluna no backend
function openDeleteColumnModal(column) {
  const tasksCount = tasks.value.filter(t => t.estado === column.status).length
  tasksInDeletingColumn.value = tasksCount
  deletingColumn.value = column
  isDeleteColumnModalOpen.value = true
}

function closeDeleteColumnModal() {
  isDeleteColumnModalOpen.value = false
  deletingColumn.value = null
  tasksInDeletingColumn.value = 0
}

async function confirmDeleteColumn() {
  if (!deletingColumn.value) return
  
  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/columns/${deletingColumn.value.status}`, {
      method: 'DELETE'
    })
    
    if (!res.ok) {
      const error = await res.text()
      throw new Error(error)
    }
    
    columns.value = columns.value.filter(col => col.status !== deletingColumn.value.status)
    
    if (selectedTask.value && selectedTask.value.estado === deletingColumn.value.status) {
      selectedTask.value = null
    }
    
    showMessage('Coluna removida com sucesso!', 2000)
    closeDeleteColumnModal()
  } catch (err) {
    showMessage(`${err.message}`, 2000)
  }
}

const openNewColumnModal = () => {
  isNewColumnModalOpen.value = true
}

const closeNewColumnModal = () => {
  isNewColumnModalOpen.value = false
}

const openCreateModal = (status) => {
  creatingStatus.value = status
  editingTask.value = null
  isTaskModalOpen.value = true
}

const openEditModal = (task) => {
  editingTask.value = { ...task }
  creatingStatus.value = null
  isTaskModalOpen.value = true
}

const closeTaskModal = () => {
  isTaskModalOpen.value = false
  editingTask.value = null
  creatingStatus.value = null
}

const openDeleteConfirm = (task) => {
  deletingTask.value = task
  isDeleteModalOpen.value = true
}

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false
  deletingTask.value = null
}

const confirmDelete = async () => {
  if (!deletingTask.value) return
  
  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/tasks/${deletingTask.value.id}`, { 
      method: 'DELETE' 
    })
    if (!res.ok) throw new Error()
    
    if (selectedTask.value?.id === deletingTask.value.id) {
      selectedTask.value = null
    }
    
    await loadTasks()
    showMessage('Tarefa eliminada com sucesso!', 2000)
    closeDeleteModal()
  } catch {
    showMessage('Erro ao eliminar tarefa', 2000)
  }
}

const handleTaskSubmit = async (taskData) => {
  try {
    if (editingTask.value) {
      const currentTask = editingTask.value
      const res = await fetchWithAuth(`${API_BASE_URL}/api/tasks/${currentTask.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          titulo: taskData.titulo,
          descricao: taskData.descricao,
          responsavel: taskData.responsavel,
          estado: currentTask.estado,
          dataInicio: taskData.dataInicio,
          dataFim: taskData.dataFim
        })
      })
      if (!res.ok) {
        const error = await res.text()
        throw new Error(error)
      }
      showMessage('Tarefa editada com sucesso!', 2000)
    } else {
      const payload = { 
        titulo: taskData.titulo,
        descricao: taskData.descricao,
        responsavel: taskData.responsavel,
        estado: creatingStatus.value || 'TO_DO',
        dataInicio: taskData.dataInicio,
        dataFim: taskData.dataFim
      }
      
      const res = await fetchWithAuth(`${API_BASE_URL}/api/tasks`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
      
      if (!res.ok) {
        const error = await res.text()
        throw new Error(error)
      }
      showMessage('Tarefa criada com sucesso!', 2000)
    }
    
    await loadTasks()
    closeTaskModal()
  } catch (err) {
    console.error('Erro ao salvar tarefa:', err)
    showMessage('Erro ao salvar tarefa: ' + err.message, 3000)
  }
}

function showMessage(txt, timeout = 4000) {
  message.value = txt
  if (txt && timeout) setTimeout(() => {
    if (message.value === txt) message.value = ''
  }, timeout)
}

function handleGlobalClick(event) {
  const isTaskCard = event.target.closest('.task-card')
  if (!isTaskCard && selectedTask.value) {
    selectedTask.value = null
  }
}

function showRegisterPage() {
  previousSession.value = {
    token: localStorage.getItem('token'),
    email: localStorage.getItem('userEmail'),
    role: localStorage.getItem('userRole')
  }
  
  localStorage.removeItem('token')
  localStorage.removeItem('userEmail')
  localStorage.removeItem('userRole')
  isAuthenticated.value = false
  userEmail.value = ''
  userRole.value = ''
  tasks.value = []
  selectedTask.value = null
  currentPage.value = 'register'
}

function handleBackToKanban() {
  if (previousSession.value.token) {
    localStorage.setItem('token', previousSession.value.token)
    localStorage.setItem('userEmail', previousSession.value.email)
    localStorage.setItem('userRole', previousSession.value.role)
    
    isAuthenticated.value = true
    userEmail.value = previousSession.value.email
    userRole.value = previousSession.value.role
    currentPage.value = 'kanban'
    
    previousSession.value = { token: null, email: null, role: null }
    loadTasks()
  } else {
    currentPage.value = 'kanban'
  }
}

function handleLoginSuccess(data) {
  isAuthenticated.value = true
  userEmail.value = data.email
  userRole.value = data.role
  currentPage.value = 'kanban'
  loadTasks()
}

function handleRegisterSuccess(data) {
  previousSession.value = { token: null, email: null, role: null }
  isAuthenticated.value = true
  userEmail.value = data.email
  userRole.value = data.role
  currentPage.value = 'kanban'
  loadTasks()
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userEmail')
  localStorage.removeItem('userRole')
  isAuthenticated.value = false
  userEmail.value = ''
  userRole.value = ''
  tasks.value = []
  selectedTask.value = null
  currentPage.value = 'login'
  previousSession.value = { token: null, email: null, role: null }
}

async function fetchWithAuth(url, options = {}) {
  const token = localStorage.getItem('token')
  const headers = {
    ...options.headers,
    'Content-Type': 'application/json'
  }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  return fetch(url, { ...options, headers })
}

async function loadTasks() {
  selectedTask.value = null
  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/tasks`)
    if (!res.ok) throw new Error('Falha ao carregar tarefas')
    tasks.value = await res.json()
  } catch (err) {
    console.error(err)
    showMessage('Erro ao carregar as tarefas: ' + err.message)
  }
}

const handleSelectTask = (task) => {
  if (selectedTask.value?.id === task.id) {
    isTaskDetailModalOpen.value = true
  } else {
    selectedTask.value = task
    isTaskDetailModalOpen.value = false
  }
}

async function moveTask({ taskId, newStatus }) {
  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/tasks/${taskId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ estado: newStatus })
    })
    if (!res.ok) throw new Error()
    
    const selectedTaskId = selectedTask.value?.id
    await loadTasks()
    
    if (selectedTaskId) {
      const updatedTask = tasks.value.find(t => t.id === selectedTaskId)
      selectedTask.value = updatedTask || null
    }
  } catch {
    showMessage('Erro ao mover tarefa')
  }
}

function openFilterModal(columnStatus) {
  const column = dynamicColumns.value.find(c => c.status === columnStatus)
  filteringColumn.value = column
  isFilterModalOpen.value = true
}

function closeFilterModal() {
  isFilterModalOpen.value = false
  filteringColumn.value = null
}

function applyFilterToColumn(filters) {
  if (filteringColumn.value) {
    if (Object.keys(filters).length === 0) {
      const newFilters = { ...columnFilters.value }
      delete newFilters[filteringColumn.value.status]
      columnFilters.value = newFilters
    } else {
      columnFilters.value = {
        ...columnFilters.value,
        [filteringColumn.value.status]: filters
      }
    }
  }
}

onMounted(() => {
  loadColumns()
  const token = localStorage.getItem('token')
  if (token) {
    isAuthenticated.value = true
    userEmail.value = localStorage.getItem('userEmail') || ''
    userRole.value = localStorage.getItem('userRole') || ''
    currentPage.value = 'kanban'
    loadTasks()
  }
  document.addEventListener('click', handleGlobalClick)
})

onUnmounted(() => {
  document.removeEventListener('click', handleGlobalClick)
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', 'Inter', system-ui, -apple-system, sans-serif;
  background: linear-gradient(135deg, #f5f7fa 0%, #f8fafc 100%);
  color: #1e293b;
  overflow-x: hidden;
}

.kanban-app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

.kanban-header {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  flex-shrink: 0;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.logo-area {
  display: flex;
  flex-direction: column;
}

.logo {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
}

.highlight {
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.logo-subtitle {
  font-size: 0.75rem;
  opacity: 0.7;
  margin-top: 0.25rem;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.btn-new-column {
  background: linear-gradient(135deg, #8b5cf6, #6366f1);
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.5rem;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-new-column:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

.calendar-btn {
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.5rem;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.calendar-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
}

.kanban-main {
  flex: 1;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 80px);
  overflow-y: auto;
}

.kanban-board-wrapper {
  width: 100%;
  overflow-x: auto;
  overflow-y: visible;
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-top: auto;
  max-height: calc(100vh - 140px);
}

.kanban-board-wrapper::-webkit-scrollbar {
  height: 8px;
}

.kanban-board-wrapper::-webkit-scrollbar-track {
  background: #e2e8f0;
  border-radius: 10px;
}

.kanban-board-wrapper::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  border-radius: 10px;
}

.kanban-board-wrapper {
  scrollbar-width: thin;
  scrollbar-color: #38bdf8 #e2e8f0;
}

.kanban-board {
  display: flex;
  gap: 1.5rem;
  align-items: flex-start;
  width: fit-content;
  min-width: 100%;
  padding-bottom: 0.5rem;
}

.kanban-board > * {
  width: 320px;
  flex-shrink: 0;
}

@media (min-width: 1200px) {
  .kanban-board {
    justify-content: center;
  }
}

.message-toast {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  padding: 0.75rem 1.5rem;
  border-radius: 0.5rem;
  background: #10b981;
  color: white;
  font-size: 0.85rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  animation: slideIn 0.3s ease;
}

.message-toast.error {
  background: #ef4444;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.delete-confirm {
  text-align: center;
  padding: 1rem;
}

.delete-confirm i {
  font-size: 3rem;
  color: #ef4444;
  margin-bottom: 1rem;
}

.delete-confirm p {
  margin: 0.5rem 0;
  color: #475569;
}

.delete-warning {
  font-size: 0.85rem;
  color: #f59e0b;
  background: #fef3c7;
  padding: 0.5rem;
  border-radius: 0.5rem;
  margin-top: 0.5rem;
}

.delete-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1.5rem;
}

.delete-actions .btn-cancel {
  padding: 0.6rem 1.5rem;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.delete-actions .btn-cancel:hover {
  background: #e2e8f0;
}

.delete-actions .btn-confirm {
  padding: 0.6rem 1.5rem;
  background: #ef4444;
  border: none;
  border-radius: 0.5rem;
  color: white;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.delete-actions .btn-confirm:hover {
  background: #dc2626;
}

.modal-overlay {
  z-index: 1000;
}

.task-card.selected {
  cursor: pointer;
  position: relative;
  z-index: 10;
}

.scroll-spacer {
  height: 20px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .kanban-header {
    padding: 1rem;
  }
  
  .header-content {
    flex-direction: column;
    text-align: center;
  }
  
  .user-area {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .kanban-main {
    padding: 1rem;
    height: calc(100vh - 120px);
  }
  
  .kanban-board > * {
    width: 280px;
  }
}
</style>