<template>
  <div
    class="task-card"
    :class="{ selected: isSelected }"
    draggable="true"
    @dragstart="onDragStart"
    @click.stop="onClick"
  >
    <div class="task-header">
      <span class="task-id">#{{ task.id }}</span>
      <div class="task-actions">
        <button class="task-btn edit" @click.stop="$emit('edit', task)" title="Editar">
          <i class="fas fa-edit"></i>
        </button>
        <button class="task-btn delete" @click.stop="$emit('delete', task)" title="Eliminar">
          <i class="fas fa-trash"></i>
        </button>
        <i class="fas fa-grip-vertical drag-handle"></i>
      </div>
    </div>
    
    <h4 class="task-title">{{ task.titulo }}</h4>
    
    <div v-if="isSelected" class="task-details">
      <div class="detail-row">
        <i class="fas fa-align-left"></i>
        <span>{{ task.descricao }}</span>
      </div>
      <div class="detail-row">
        <i class="fas fa-user"></i>
        <span>{{ task.responsavel }}</span>
      </div>
      <div class="detail-row">
        <i class="fas fa-calendar"></i>
        <span>{{ new Date(task.dataCriacao).toLocaleDateString('pt-PT') }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
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

const emit = defineEmits(['select', 'dragstart', 'edit', 'delete'])

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
  padding: 0.75rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.task-card:hover {
  transform: translateX(4px);
  border-color: #cbd5e1;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.task-card.selected {
  background: linear-gradient(135deg, #fef9e3 0%, #fff 100%);
  border-left: 4px solid #f59e0b;
  border-color: #f59e0b;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.task-id {
  font-size: 0.7rem;
  font-weight: 500;
  color: #94a3b8;
  background: #f1f5f9;
  padding: 0.2rem 0.5rem;
  border-radius: 0.5rem;
}

.task-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.task-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 0.25rem;
  transition: all 0.2s;
  font-size: 0.75rem;
  opacity: 0;
  transition: opacity 0.2s;
}

.task-card:hover .task-btn {
  opacity: 1;
}

.task-btn.edit {
  color: #f59e0b;
}

.task-btn.edit:hover {
  background: #fef3c7;
  transform: scale(1.1);
}

.task-btn.delete {
  color: #ef4444;
}

.task-btn.delete:hover {
  background: #fee2e2;
  transform: scale(1.1);
}

.drag-handle {
  font-size: 0.8rem;
  color: #cbd5e1;
  cursor: grab;
}

.drag-handle:active {
  cursor: grabbing;
}

.task-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  line-height: 1.4;
}

.task-details {
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
  width: 16px;
  font-size: 0.7rem;
  color: #94a3b8;
  margin-top: 0.1rem;
}

.detail-row span {
  flex: 1;
  word-break: break-word;
}
</style>