<template>
  <Modal :is-open="isOpen" title="Detalhes da Tarefa" @close="close">
    <div class="task-detail-modal">
      <div class="task-header-detail">
        <span class="task-id-detail">#{{ task.id }}</span>
        <div :class="['status-badge', getStatusClass(task.estado)]">
          {{ getStatusName(task.estado) }}
        </div>
      </div>

      <div class="detail-section">
        <h4>Titulo</h4>
        <p>{{ task.titulo }}</p>
      </div>

      <div class="detail-section">
        <h4>Descricao</h4>
        <p>{{ task.descricao || 'Sem descricao' }}</p>
      </div>

      <div class="detail-section">
        <h4>Responsavel</h4>
        <p>{{ task.responsavel }}</p>
      </div>

      <div class="detail-row">
        <div class="detail-section half">
          <h4>Data de Inicio</h4>
          <p><i class="fas fa-calendar-alt"></i> {{ formatDate(task.dataInicio) }}</p>
        </div>
        <div class="detail-section half">
          <h4>Data de Fim</h4>
          <p :class="{ overdue: isOverdue }">
            <i class="fas fa-calendar-check"></i> {{ formatDate(task.dataFim) }}
          </p>
        </div>
      </div>

      <div class="detail-section">
        <h4>Criado por</h4>
        <p><i class="fas fa-user-plus"></i> {{ task.criadoPor || task.modificadoPor || 'N/A' }}</p>
        <p class="small-text"><i class="fas fa-clock"></i> {{ formatDateTime(task.dataCriacao) }}</p>
      </div>

      <div class="detail-section">
        <h4>Ultima Modificacao</h4>
        <p><i class="fas fa-user-edit"></i> {{ task.modificadoPor || task.criadoPor || 'N/A' }}</p>
        <p class="small-text"><i class="fas fa-clock"></i> {{ formatDateTime(task.dataModificacao) }}</p>
      </div>

      <div class="detail-actions">
        <button class="btn-edit" @click="editTask">
          <i class="fas fa-edit"></i> Editar Tarefa
        </button>
        <button class="btn-delete" @click="deleteTask">
          <i class="fas fa-trash"></i> Eliminar Tarefa
        </button>
      </div>
    </div>
  </Modal>
</template>

<script setup>
import { computed } from 'vue'
import Modal from './Modal.vue'

const props = defineProps({
  isOpen: Boolean,
  task: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'edit', 'delete'])

const isOverdue = computed(() => {
  if (!props.task?.dataFim) return false
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)
  const dataFim = new Date(props.task.dataFim)
  return dataFim < hoje && props.task.estado !== 'DONE'
})

const formatDate = (date) => {
  if (!date) return 'Nao definida'
  const d = new Date(date)
  return d.toLocaleDateString('pt-PT')
}

const formatDateTime = (date) => {
  if (!date) return 'Nao definida'
  const d = new Date(date)
  return d.toLocaleString('pt-PT')
}

const getStatusClass = (status) => {
  const classes = {
    'TO_DO': 'status-todo',
    'IN_PROGRESS': 'status-progress',
    'DONE': 'status-done'
  }
  return classes[status] || 'status-todo'
}

const getStatusName = (status) => {
  const names = {
    'TO_DO': 'A Fazer',
    'IN_PROGRESS': 'Em Progresso',
    'DONE': 'Concluida'
  }
  return names[status] || status
}

const close = () => {
  emit('close')
}

const editTask = () => {
  emit('edit', props.task)
  close()
}

const deleteTask = () => {
  emit('delete', props.task)
  close()
}
</script>

<style scoped>
.task-detail-modal {
  width: 100%;
}

.task-header-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.task-id-detail {
  font-size: 1.1rem;
  font-weight: 600;
  color: #38bdf8;
  background: #e0f2fe;
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-todo {
  background: #dbeafe;
  color: #3b82f6;
}

.status-progress {
  background: #fed7aa;
  color: #f59e0b;
}

.status-done {
  background: #d1fae5;
  color: #10b981;
}

.detail-section {
  margin-bottom: 1.25rem;
}

.detail-section h4 {
  font-size: 0.8rem;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-section p {
  font-size: 0.95rem;
  color: #1e293b;
  line-height: 1.5;
  word-break: break-word;
}

.detail-section p i {
  color: #94a3b8;
  width: 20px;
  margin-right: 0.5rem;
}

.small-text {
  font-size: 0.75rem;
  color: #94a3b8;
  margin-top: 0.25rem;
}

.detail-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.detail-section.half {
  flex: 1;
  margin-bottom: 0;
}

.overdue {
  color: #ef4444;
  font-weight: 500;
}

.detail-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.btn-edit {
  padding: 0.6rem 1.25rem;
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  border: none;
  border-radius: 0.5rem;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: white;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-edit:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
}

.btn-delete {
  padding: 0.6rem 1.25rem;
  background: #ef4444;
  border: none;
  border-radius: 0.5rem;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: white;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-delete:hover {
  background: #dc2626;
  transform: translateY(-1px);
}
</style>