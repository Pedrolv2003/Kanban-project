<template>
  <div
    class="kanban-column"
    :data-status="status"
    :style="{ borderTop: `4px solid ${color}` }"
    @dragover.prevent
    @drop="onDrop"
  >
    <div class="column-header">
      <div class="column-icon" :style="{ background: `${color}20`, color: color }">
        <i :class="icon"></i>
      </div>
      <h3 class="column-title">{{ cleanTitle }}</h3>
      <span class="task-count">{{ countText(tasks.length) }}</span>
      <button class="filter-btn" :class="{ active: hasActiveFilter }" @click="$emit('open-filter', status)" title="Filtrar tarefas">
        <i class="fas fa-filter"></i>
      </button>
      <button 
        v-if="!isDefaultColumn" 
        class="delete-column-btn" 
        @click="openDeleteConfirm"
        title="Eliminar coluna"
      >
        <i class="fas fa-trash"></i>
      </button>
    </div>
    
    <button class="add-task-btn" @click="$emit('open-create-modal', status)">
      <i class="fas fa-plus"></i> Nova tarefa
    </button>
    
    <div class="tasks-container">
      <TaskCard
        v-for="task in filteredTasks"
        :key="task.id"
        :task="task"
        :is-selected="task.id === selectedId"
        @select="(task) => emit('select', task)"
      />
      
      <div v-if="filteredTasks.length === 0 && tasks.length > 0" class="empty-state">
        <i class="fas fa-filter"></i>
        <p>Nenhuma tarefa corresponde ao filtro</p>
      </div>
      
      <div v-else-if="tasks.length === 0" class="empty-state">
        <i class="fas fa-inbox"></i>
        <p>Nenhuma tarefa</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import TaskCard from './TaskCard.vue'

const props = defineProps({
  status: String,
  title: String,
  color: { type: String, default: '#3b82f6' },
  icon: { type: String, default: 'fas fa-list-ul' },
  tasks: Array,
  selectedId: [Number, null],
  activeFilters: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['select', 'drop', 'open-create-modal', 'delete-column', 'open-filter'])

const defaultStatuses = ['TO_DO', 'IN_PROGRESS', 'DONE']
const isDefaultColumn = defaultStatuses.includes(props.status)

const cleanTitle = computed(() => {
  return props.title.replace(/^[\u{1F300}-\u{1F6FF}\u{2600}-\u{26FF}\u{2700}-\u{27BF}]+/u, '').trim()
})

const hasActiveFilter = computed(() => {
  return props.activeFilters && Object.keys(props.activeFilters).length > 0
})

const countText = (n) => n === 1 ? '1 tarefa' : `${n} tarefas`

const openDeleteConfirm = () => {
  emit('delete-column', { status: props.status, title: props.title })
}

const filteredTasks = computed(() => {
  if (!props.activeFilters || Object.keys(props.activeFilters).length === 0) {
    return props.tasks
  }
  
  return props.tasks.filter(task => {
    let match = true
    
    if (match && props.activeFilters.id !== undefined) {
      match = task.id === props.activeFilters.id
    }
    if (match && props.activeFilters.titulo) {
      match = task.titulo.toLowerCase().includes(props.activeFilters.titulo)
    }
    if (match && props.activeFilters.responsavel) {
      match = task.responsavel.toLowerCase().includes(props.activeFilters.responsavel)
    }
    if (match && props.activeFilters.data) {
      const taskDate = new Date(task.dataCriacao).toISOString().split('T')[0]
      match = taskDate === props.activeFilters.data
    }
    
    return match
  })
})

function onDrop(e) {
  e.preventDefault()
  e.stopPropagation()
  const taskId = e.dataTransfer.getData('text/plain')
  if (taskId) {
    emit('drop', { 
      taskId: Number(taskId), 
      newStatus: props.status 
    })
  }
}
</script>

<style scoped>
.kanban-column {
  background: #f8fafc;
  border-radius: 1rem;
  padding: 1rem;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  height: fit-content;
  max-height: calc(100vh - 200px);
  min-height: 300px;
}

.kanban-column:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.column-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #e2e8f0;
  flex-shrink: 0;
}

.column-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.5rem;
  font-size: 1.1rem;
  flex-shrink: 0;
}

.column-title {
  flex: 1;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-count {
  font-size: 0.85rem;
  font-weight: 500;
  background: #e2e8f0;
  padding: 0.3rem 0.6rem;
  border-radius: 1rem;
  color: #475569;
  flex-shrink: 0;
}

.filter-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  color: #94a3b8;
  transition: all 0.2s;
  width: 28px;
  height: 28px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.filter-btn:hover {
  background: #e2e8f0;
  color: #38bdf8;
}

.filter-btn.active {
  color: #38bdf8;
  background: #e0f2fe;
}

.delete-column-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  color: #94a3b8;
  transition: all 0.2s;
  width: 28px;
  height: 28px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.delete-column-btn:hover {
  background: #fee2e2;
  color: #ef4444;
}

.add-task-btn {
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  border: none;
  border-radius: 0.5rem;
  padding: 0.7rem;
  margin-bottom: 1rem;
  color: white;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  flex-shrink: 0;
}

.add-task-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
}

.tasks-container {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  min-height: 100px;
  padding-right: 4px;
  max-height: calc(100vh - 320px);
}

.tasks-container::-webkit-scrollbar {
  width: 6px;
}

.tasks-container::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.tasks-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #94a3b8;
}

.empty-state i {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  opacity: 0.5;
}

.empty-state p {
  font-size: 0.9rem;
  margin: 0;
}
</style>