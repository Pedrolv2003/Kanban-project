<template>
  <div class="calendar-page">
    <div class="calendar-page-header">
      <button class="back-btn" @click="$emit('close')">
        <i class="fas fa-arrow-left"></i> Voltar ao Kanban
      </button>
      <h1><i class="fas fa-calendar-alt"></i> Calendario de Tarefas</h1>
    </div>

    <div class="calendar-page-content">
      <!-- Coluna Esquerda - Calendário -->
      <div class="calendar-section">
        <div class="calendar-nav">
          <button @click="prevMonth" class="nav-btn">
            <i class="fas fa-chevron-left"></i>
          </button>
          <span class="current-month">{{ currentMonthName }} {{ currentYear }}</span>
          <button @click="nextMonth" class="nav-btn">
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>

        <div class="calendar-weekdays">
          <span>Seg</span>
          <span>Ter</span>
          <span>Qua</span>
          <span>Qui</span>
          <span>Sex</span>
          <span>Sab</span>
          <span>Dom</span>
        </div>

        <div class="calendar-grid" ref="calendarGridRef">
          <div
            v-for="day in calendarDays"
            :key="day.key"
            class="calendar-cell"
            :class="{
              'other-month': !day.isCurrentMonth,
              'today': day.isToday,
              'has-tasks': day.tasks.length > 0,
              'selected': selectedDate === day.date
            }"
            @click="selectDate(day.date, day.day)"
          >
            <span class="day-number">{{ day.day }}</span>
            <div class="day-tasks-list" :id="`tasks-${day.key}`">
              <div
                v-for="(task, idx) in getVisibleTasks(day)"
                :key="task.id"
                class="task-chip"
                :style="getTaskStyle(task.estado)"
              >
                {{ task.titulo.substring(0, 8) }}{{ task.titulo.length > 8 ? '...' : '' }}
              </div>
              <div v-if="getShowMoreButton(day)" class="more-tasks-btn">
                +{{ day.tasks.length - getVisibleCount(day) }}
              </div>
            </div>
          </div>
        </div>

        <div class="calendar-filters">
          <h4>Filtrar Tarefas</h4>
          <div class="filter-row">
            <div class="filter-input">
              <i class="fas fa-hashtag"></i>
              <input 
                type="number" 
                v-model.number="filters.id" 
                placeholder="ID"
                @input="refreshVisibleTasks"
              />
            </div>
            <div class="filter-input">
              <i class="fas fa-heading"></i>
              <input 
                type="text" 
                v-model="filters.titulo" 
                placeholder="Titulo"
                @input="refreshVisibleTasks"
              />
            </div>
            <div class="filter-input">
              <i class="fas fa-user"></i>
              <input 
                type="text" 
                v-model="filters.responsavel" 
                placeholder="Responsavel"
                @input="refreshVisibleTasks"
              />
            </div>
          </div>
          <button class="btn-clear-filters" @click="clearFilters">
            <i class="fas fa-eraser"></i> Limpar Filtros
          </button>
        </div>
      </div>

      <!-- Coluna Direita - Tarefas do Dia -->
      <div class="tasks-section">
        <div class="tasks-section-header">
          <h2>
            <i class="fas fa-calendar-day"></i>
            Tarefas do dia {{ selectedDateFormatted }}
          </h2>
          <span class="task-count">{{ selectedDayTasks.length }} tarefa{{ selectedDayTasks.length !== 1 ? 's' : '' }}</span>
        </div>

        <div v-if="selectedDayTasks.length === 0" class="no-tasks">
          <i class="fas fa-inbox"></i>
          <p>Nenhuma tarefa para este dia</p>
        </div>

        <div v-else class="tasks-list">
          <div
            v-for="task in selectedDayTasks"
            :key="task.id"
            class="task-item"
            :style="{ borderLeftColor: getColumnColor(task.estado) }"
            @click="openTaskDetail(task)"
          >
            <div class="task-item-header">
              <span class="task-id">#{{ task.id }}</span>
              <span 
                class="task-status" 
                :style="{ 
                  backgroundColor: getColumnColor(task.estado) + '20',
                  color: getColumnColor(task.estado)
                }"
              >
                {{ getStatusName(task.estado) }}
              </span>
            </div>
            <h4 class="task-title">{{ task.titulo }}</h4>
            <p class="task-desc">{{ task.descricao?.substring(0, 100) }}{{ task.descricao?.length > 100 ? '...' : '' }}</p>
            <div class="task-meta">
              <span><i class="fas fa-user"></i> {{ task.responsavel }}</span>
              <span><i class="fas fa-calendar-alt"></i> {{ formatDate(task.dataInicio) }} - {{ formatDate(task.dataFim) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modais -->
    <TaskDetailModal
      :is-open="isTaskDetailModalOpen"
      :task="selectedTask"
      @close="closeTaskDetailModal"
      @edit="openEditFromDetail"
      @delete="handleDeleteTask"
    />

    <Modal :is-open="isEditModalOpen" title="Editar Tarefa" @close="closeEditModal">
      <TaskFormModal
        :initial-data="editingTask"
        :is-editing="true"
        @submit="handleEditSubmit"
        @cancel="closeEditModal"
      />
    </Modal>

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
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import Modal from './Modal.vue'
import TaskFormModal from './TaskFormModal.vue'
import TaskDetailModal from './TaskDetailModal.vue'

const props = defineProps({
  tasks: {
    type: Array,
    default: () => []
  },
  userEmail: {
    type: String,
    default: ''
  },
  columns: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['close', 'refresh-tasks'])

const currentDate = ref(new Date())
const selectedDate = ref('')
const selectedDay = ref('')
const filters = ref({
  id: '',
  titulo: '',
  responsavel: ''
})

const calendarGridRef = ref(null)
const visibleCounts = ref({})

// Estados dos modais
const isTaskDetailModalOpen = ref(false)
const isEditModalOpen = ref(false)
const isDeleteModalOpen = ref(false)
const selectedTask = ref(null)
const editingTask = ref(null)
const deletingTask = ref(null)

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

// Mapear cores das colunas
const columnColorMap = computed(() => {
  const map = {}
  props.columns.forEach(col => {
    map[col.status] = col.color
  })
  return map
})

const getColumnColor = (estado) => {
  if (columnColorMap.value[estado]) {
    return columnColorMap.value[estado]
  }
  const defaultColors = {
    'TO_DO': '#3b82f6',
    'IN_PROGRESS': '#f59e0b',
    'DONE': '#10b981'
  }
  return defaultColors[estado] || '#3b82f6'
}

const getTaskStyle = (estado) => {
  const color = getColumnColor(estado)
  return {
    backgroundColor: `${color}20`,
    color: color,
    borderLeft: `2px solid ${color}`
  }
}

const currentYear = computed(() => currentDate.value.getFullYear())
const currentMonth = computed(() => currentDate.value.getMonth())
const currentMonthName = computed(() => {
  const months = ['Janeiro', 'Fevereiro', 'Marco', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro']
  return months[currentMonth.value]
})

const selectedDateFormatted = computed(() => {
  if (!selectedDate.value) return 'Selecionado'
  const [year, month, day] = selectedDate.value.split('-')
  return `${day}/${month}/${year}`
})

const filteredTasks = computed(() => {
  let result = [...props.tasks]

  if (filters.value.id && filters.value.id > 0) {
    result = result.filter(t => t.id === filters.value.id)
  }
  if (filters.value.titulo && filters.value.titulo.trim()) {
    result = result.filter(t => t.titulo && t.titulo.toLowerCase().includes(filters.value.titulo.toLowerCase()))
  }
  if (filters.value.responsavel && filters.value.responsavel.trim()) {
    result = result.filter(t => t.responsavel && t.responsavel.toLowerCase().includes(filters.value.responsavel.toLowerCase()))
  }

  return result
})

const calendarDays = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value
  
  const firstDay = new Date(year, month, 1)
  let firstDayWeek = firstDay.getDay()
  firstDayWeek = firstDayWeek === 0 ? 6 : firstDayWeek - 1
  
  const startDate = new Date(year, month, 1)
  startDate.setDate(1 - firstDayWeek)
  
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  const days = []
  
  for (let i = 0; i < 42; i++) {
    const date = new Date(startDate)
    date.setDate(startDate.getDate() + i)
    
    const isCurrentMonth = date.getMonth() === month
    const isToday = date.toDateString() === today.toDateString()
    const dateStr = date.toISOString().split('T')[0]
    
    const dayTasks = filteredTasks.value.filter(task => {
      if (!task.dataInicio || !task.dataFim) return false
      const inicio = task.dataInicio.split('T')[0]
      const fim = task.dataFim.split('T')[0]
      return dateStr >= inicio && dateStr <= fim
    })
    
    days.push({
      key: `${year}-${month}-${i}`,
      date: dateStr,
      day: date.getDate(),
      isCurrentMonth,
      isToday,
      tasks: dayTasks
    })
  }
  
  return days
})

// Função para calcular quantas tarefas cabem em uma célula
const calculateVisibleCount = (containerId) => {
  const container = document.getElementById(containerId)
  if (!container) return 3
  
  const cell = container.closest('.calendar-cell')
  if (!cell) return 3
  
  const cellHeight = cell.clientHeight
  const dayNumberHeight = 28
  const padding = 8
  const availableHeight = cellHeight - dayNumberHeight - padding
  
  const chipHeight = 20
  const maxVisible = Math.floor(availableHeight / chipHeight)
  
  return Math.max(1, Math.min(maxVisible, 4))
}

// Obter tarefas visíveis para um dia
const getVisibleTasks = (day) => {
  const maxVisible = visibleCounts.value[day.key] || 3
  return day.tasks.slice(0, maxVisible)
}

// Obter número visível para um dia
const getVisibleCount = (day) => {
  return visibleCounts.value[day.key] || 3
}

// Verificar se mostra botão "+"
const getShowMoreButton = (day) => {
  const maxVisible = visibleCounts.value[day.key] || 3
  return day.tasks.length > maxVisible
}

// Atualizar contagens visíveis para todos os dias
const refreshVisibleTasks = async () => {
  await nextTick()
  
  setTimeout(() => {
    calendarDays.value.forEach(day => {
      const containerId = `tasks-${day.key}`
      const maxVisible = calculateVisibleCount(containerId)
      visibleCounts.value[day.key] = maxVisible
    })
  }, 50)
}

// Atualizar quando as tarefas mudarem
watch([filteredTasks, () => calendarDays.value.length], () => {
  refreshVisibleTasks()
}, { deep: true })

// Atualizar quando o mês mudar
watch([currentMonth, currentYear], () => {
  refreshVisibleTasks()
})

// Atualizar quando a janela for redimensionada
const handleResize = () => {
  refreshVisibleTasks()
}

const selectedDayTasks = computed(() => {
  if (!selectedDate.value) return []
  return filteredTasks.value.filter(task => {
    const inicio = task.dataInicio?.split('T')[0]
    const fim = task.dataFim?.split('T')[0]
    return selectedDate.value >= inicio && selectedDate.value <= fim
  })
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('pt-PT')
}

const getStatusName = (estado) => {
  const names = {
    'TO_DO': 'A Fazer',
    'IN_PROGRESS': 'Em Progresso',
    'DONE': 'Concluida'
  }
  return names[estado] || estado
}

const selectDate = (date, day) => {
  selectedDate.value = date
  selectedDay.value = day
}

const prevMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value - 1, 1)
}

const nextMonth = () => {
  currentDate.value = new Date(currentYear.value, currentMonth.value + 1, 1)
}

const clearFilters = () => {
  filters.value = {
    id: '',
    titulo: '',
    responsavel: ''
  }
}

// Funções dos modais
const openTaskDetail = (task) => {
  selectedTask.value = task
  isTaskDetailModalOpen.value = true
}

const closeTaskDetailModal = () => {
  isTaskDetailModalOpen.value = false
  selectedTask.value = null
}

const openEditFromDetail = (task) => {
  editingTask.value = { ...task }
  isTaskDetailModalOpen.value = false
  isEditModalOpen.value = true
}

const closeEditModal = () => {
  isEditModalOpen.value = false
  editingTask.value = null
}

const handleEditSubmit = async (taskData) => {
  try {
    const res = await fetchWithAuth(`${API_BASE_URL}/api/tasks/${editingTask.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        titulo: taskData.titulo,
        descricao: taskData.descricao,
        responsavel: taskData.responsavel,
        estado: editingTask.value.estado,
        dataInicio: taskData.dataInicio,
        dataFim: taskData.dataFim
      })
    })
    if (!res.ok) throw new Error()
    emit('refresh-tasks')
    closeEditModal()
  } catch (err) {
    console.error('Erro ao editar tarefa:', err)
  }
}

const handleDeleteTask = (task) => {
  deletingTask.value = task
  isTaskDetailModalOpen.value = false
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
    emit('refresh-tasks')
    closeDeleteModal()
  } catch {
    console.error('Erro ao eliminar tarefa')
  }
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

// Selecionar o dia atual ao abrir
onMounted(() => {
  const today = new Date().toISOString().split('T')[0]
  selectDate(today, new Date().getDate())
  
  setTimeout(() => {
    refreshVisibleTasks()
  }, 100)
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.calendar-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #f8fafc 100%);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.calendar-page-header {
  background: linear-gradient(135deg, #0f172a, #1e293b);
  color: white;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  gap: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(-2px);
}

.calendar-page-header h1 {
  margin: 0;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.calendar-page-content {
  display: flex;
  gap: 1.5rem;
  padding: 1.25rem;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

/* Coluna Esquerda - Calendário */
.calendar-section {
  flex: 1;
  background: white;
  border-radius: 1rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 1rem;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.calendar-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  margin-bottom: 0.75rem;
  flex-shrink: 0;
}

.nav-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  color: #475569;
  border-radius: 0.5rem;
  font-size: 1rem;
}

.nav-btn:hover {
  background: #e2e8f0;
  color: #38bdf8;
}

.current-month {
  font-weight: 600;
  color: #1e293b;
  font-size: 1rem;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  padding: 0.6rem;
  background: #f1f5f9;
  font-weight: 600;
  font-size: 0.85rem;
  color: #475569;
  border-radius: 0.5rem;
  margin-bottom: 0.75rem;
  flex-shrink: 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  background: #e2e8f0;
  border-radius: 0.5rem;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}

.calendar-cell {
  background: white;
  min-height: 65px;
  padding: 0.25rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.calendar-cell:hover {
  background: #f8fafc;
}

.calendar-cell.selected {
  background: #e0f2fe;
  border: 1px solid #38bdf8;
}

.calendar-cell.other-month {
  background: #f8fafc;
  opacity: 0.6;
}

.calendar-cell.today {
  background: #fef9e3;
}

.calendar-cell.has-tasks {
  background: #fef9e3;
}

.day-number {
  font-size: 0.8rem;
  font-weight: 600;
  color: #475569;
  display: inline-block;
  width: 24px;
  height: 24px;
  text-align: center;
  line-height: 24px;
  border-radius: 50%;
  margin-bottom: 0.15rem;
  flex-shrink: 0;
}

.calendar-cell.today .day-number {
  background: #38bdf8;
  color: white;
}

.day-tasks-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.task-chip {
  font-size: 0.6rem;
  padding: 0.08rem 0.25rem;
  border-radius: 0.25rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: default;
  transition: all 0.2s;
  border-left: 2px solid;
  flex-shrink: 0;
}

.more-tasks-btn {
  font-size: 0.55rem;
  padding: 0.06rem 0.2rem;
  background: #e2e8f0;
  border-radius: 0.25rem;
  color: #475569;
  font-weight: 500;
  text-align: center;
  cursor: default;
  flex-shrink: 0;
}

/* Filtros */
.calendar-filters {
  padding: 0.75rem;
  border-top: 1px solid #e2e8f0;
  margin-top: 0.75rem;
  flex-shrink: 0;
}

.calendar-filters h4 {
  font-size: 0.75rem;
  margin-bottom: 0.5rem;
  color: #334155;
}

.filter-row {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.filter-input {
  flex: 1;
  position: relative;
}

.filter-input i {
  position: absolute;
  left: 0.6rem;
  top: 0.45rem;
  color: #94a3b8;
  font-size: 0.7rem;
}

.filter-input input {
  width: 100%;
  padding: 0.4rem 0.4rem 0.4rem 1.6rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 0.75rem;
  background: #f8fafc;
}

.btn-clear-filters {
  width: 100%;
  padding: 0.4rem;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  cursor: pointer;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
}

.btn-clear-filters:hover {
  background: #e2e8f0;
}

/* Coluna Direita - Tarefas */
.tasks-section {
  width: 360px;
  background: white;
  border-radius: 1rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex-shrink: 0;
}

.tasks-section-header {
  padding: 0.8rem 1rem;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.tasks-section-header h2 {
  margin: 0;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #1e293b;
}

.task-count {
  background: #e2e8f0;
  padding: 0.2rem 0.7rem;
  border-radius: 1rem;
  font-size: 0.75rem;
  color: #475569;
}

.tasks-list {
  flex: 1;
  overflow-y: auto;
  padding: 0.8rem;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  min-height: 0;
}

.task-item {
  background: #f8fafc;
  border-radius: 0.75rem;
  padding: 0.8rem;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 4px solid;
}

.task-item:hover {
  transform: translateX(4px);
  background: #f1f5f9;
}

.task-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.task-id {
  font-size: 0.7rem;
  font-weight: 500;
  color: #94a3b8;
  background: #e2e8f0;
  padding: 0.2rem 0.5rem;
  border-radius: 0.5rem;
}

.task-status {
  font-size: 0.7rem;
  padding: 0.2rem 0.6rem;
  border-radius: 1rem;
  font-weight: 500;
  white-space: nowrap;
}

.task-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 0.5rem 0;
}

.task-desc {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0 0 0.5rem 0;
  line-height: 1.4;
}

.task-meta {
  display: flex;
  gap: 0.8rem;
  font-size: 0.7rem;
  color: #94a3b8;
}

.task-meta i {
  margin-right: 0.2rem;
}

.no-tasks {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  gap: 0.8rem;
}

.no-tasks i {
  font-size: 2.2rem;
}

.delete-confirm {
  text-align: center;
  padding: 1rem;
}

.delete-confirm i {
  font-size: 2rem;
  color: #ef4444;
  margin-bottom: 1rem;
}

.delete-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1rem;
}

.btn-cancel, .btn-confirm {
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  cursor: pointer;
  border: none;
  font-size: 0.8rem;
}

.btn-cancel {
  background: #e2e8f0;
}

.btn-confirm {
  background: #ef4444;
  color: white;
}

@media (max-width: 1024px) {
  .calendar-page-content {
    flex-direction: column;
    padding: 1rem;
  }
  
  .tasks-section {
    width: 100%;
    max-height: 350px;
  }
  
  .calendar-section {
    max-height: 500px;
  }
}

@media (max-width: 768px) {
  .calendar-page-header {
    padding: 0.75rem 1rem;
  }
  
  .calendar-page-header h1 {
    font-size: 1rem;
  }
  
  .calendar-page-content {
    padding: 0.75rem;
    gap: 1rem;
  }
  
  .calendar-cell {
    min-height: 80px;
  }
  
  .task-chip {
    font-size: 0.6rem;
  }
}
</style>