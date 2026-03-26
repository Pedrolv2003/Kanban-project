<template>
  <div
    class="task-card"
    :class="{ selected: isSelected, overdue: isOverdue }"
    draggable="true"
    @dragstart="onDragStart"
    @click.stop="onClick"
  >
    <div class="task-header">
      <span class="task-id">#{{ task.id }}</span>
      <div class="task-actions">
        <i class="fas fa-grip-vertical drag-handle"></i>
      </div>
    </div>
    
    <h4 class="task-title">{{ task.titulo }}</h4>
    
    <!-- Detalhes expandidos quando selecionada (sem pop-up) -->
    <div v-if="isSelected" class="task-details-expanded">
      <div class="detail-row">
        <i class="fas fa-align-left"></i>
        <span>{{ task.descricao }}</span>
      </div>
      <div class="detail-row">
        <i class="fas fa-user"></i>
        <span>{{ task.responsavel }}</span>
      </div>
      <div class="detail-row">
        <i class="fas fa-calendar-alt"></i>
        <span>{{ formatDate(task.dataInicio) }} - {{ formatDate(task.dataFim) }}</span>
      </div>
      <div class="detail-row small">
        <i class="fas fa-info-circle"></i>
        <span>Clique novamente para ver todos os detalhes</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  task: {
    type: Object,
    required: true
  },
  isSelected: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['select', 'dragstart'])

const isOverdue = computed(() => {
  if (!props.task.dataFim) return false
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)
  const dataFim = new Date(props.task.dataFim)
  return dataFim < hoje && props.task.estado !== 'DONE'
})

const formatDate = (date) => {
  if (!date) return 'Nao definida'
  return new Date(date).toLocaleDateString('pt-PT')
}

function onDragStart(e) {
  e.dataTransfer.setData('text/plain', props.task.id.toString())
  e.dataTransfer.effectAllowed = 'move'
  emit('dragstart', props.task)
}

function onClick() {
  emit('select', props.task)
}
</script>

<style scoped>
.task-card {
  background: white;
  border-radius: 0.75rem;
  padding: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
  position: relative;
}

.task-card.overdue {
  border-left: 4px solid #ef4444;
  background: #fef2f2;
}

.task-card:hover {
  transform: translateX(4px);
  border-color: #cbd5e1;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.task-card.selected {
  background: linear-gradient(135deg, #fef9e3 0%, #fff 100%);
  border-left: 4px solid #f59e0b;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.6rem;
}

.task-id {
  font-size: 0.8rem;
  font-weight: 500;
  color: #94a3b8;
  background: #f1f5f9;
  padding: 0.25rem 0.6rem;
  border-radius: 0.5rem;
}

.drag-handle {
  font-size: 0.9rem;
  color: #cbd5e1;
  cursor: grab;
}

.drag-handle:active {
  cursor: grabbing;
}

.task-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  line-height: 1.4;
  word-break: break-word;
}

.task-details-expanded {
  margin-top: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  font-size: 0.75rem;
  color: #475569;
}

.detail-row i {
  width: 14px;
  font-size: 0.7rem;
  color: #94a3b8;
  margin-top: 0.1rem;
  flex-shrink: 0;
}

.detail-row span {
  flex: 1;
  word-break: break-word;
  line-height: 1.3;
}

.detail-row.small {
  font-size: 0.65rem;
  color: #94a3b8;
  margin-top: 0.25rem;
}

.detail-row.small i {
  color: #cbd5e1;
}
</style>