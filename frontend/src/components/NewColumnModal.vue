<template>
  <Modal :is-open="isOpen" title="Criar Nova Coluna" @close="close">
    <div class="new-column-form">
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="columnTitle">Titulo da Coluna</label>
          <div class="input-icon">
            <i class="fas fa-columns"></i>
            <input 
              type="text" 
              id="columnTitle" 
              v-model="title" 
              required 
              placeholder="Ex: Review, Testing, etc."
              autofocus
            />
          </div>
        </div>

        <div class="form-group">
          <label for="columnColor">Cor da Coluna</label>
          <div class="color-selector">
            <div 
              class="color-option" 
              :class="{ selected: color === '#3b82f6' }"
              style="background: #3b82f6"
              @click="color = '#3b82f6'"
            ></div>
            <div 
              class="color-option" 
              :class="{ selected: color === '#f59e0b' }"
              style="background: #f59e0b"
              @click="color = '#f59e0b'"
            ></div>
            <div 
              class="color-option" 
              :class="{ selected: color === '#10b981' }"
              style="background: #10b981"
              @click="color = '#10b981'"
            ></div>
            <div 
              class="color-option" 
              :class="{ selected: color === '#ef4444' }"
              style="background: #ef4444"
              @click="color = '#ef4444'"
            ></div>
            <div 
              class="color-option" 
              :class="{ selected: color === '#8b5cf6' }"
              style="background: #8b5cf6"
              @click="color = '#8b5cf6'"
            ></div>
            <div 
              class="color-option" 
              :class="{ selected: color === '#ec489a' }"
              style="background: #ec489a"
              @click="color = '#ec489a'"
            ></div>
          </div>
        </div>

        <div class="form-group">
          <label for="columnIcon">Icone</label>
          <div class="icon-selector">
            <div 
              v-for="icon in icons" 
              :key="icon"
              class="icon-option"
              :class="{ selected: selectedIcon === icon }"
              @click="selectedIcon = icon"
            >
              <i :class="icon"></i>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" @click="close" class="btn-cancel">
            Cancelar
          </button>
          <button type="submit" :disabled="!title.trim()" class="btn-submit">
            <i class="fas fa-plus"></i> Criar Coluna
          </button>
        </div>
      </form>
    </div>
  </Modal>
</template>

<script setup>
import { ref } from 'vue'
import Modal from './Modal.vue'

const props = defineProps({
  isOpen: Boolean
})

const emit = defineEmits(['close', 'create'])

const title = ref('')
const color = ref('#3b82f6')
const selectedIcon = ref('fas fa-list-ul')

const icons = [
  'fas fa-list-ul',
  'fas fa-tasks',
  'fas fa-star',
  'fas fa-flag',
  'fas fa-clock',
  'fas fa-code',
  'fas fa-paint-brush',
  'fas fa-music',
  'fas fa-chart-line',
  'fas fa-bug'
]

const close = () => {
  title.value = ''
  color.value = '#3b82f6'
  selectedIcon.value = 'fas fa-list-ul'
  emit('close')
}

const handleSubmit = () => {
  if (!title.value.trim()) return
  
  emit('create', {
    title: title.value.trim(),
    color: color.value,
    icon: selectedIcon.value,
    status: title.value.trim().toUpperCase().replace(/\s+/g, '_')
  })
  
  close()
}
</script>

<style scoped>
.new-column-form {
  width: 100%;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.6rem;
  font-size: 0.9rem;
  font-weight: 500;
  color: #334155;
}

.input-icon {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon i {
  position: absolute;
  left: 0.9rem;
  color: #94a3b8;
  font-size: 1rem;
}

.input-icon input {
  width: 100%;
  padding: 0.8rem 0.9rem 0.8rem 2.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 0.9rem;
  transition: all 0.2s ease;
  background: #f8fafc;
}

.input-icon input:focus {
  outline: none;
  border-color: #38bdf8;
  box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.1);
  background: white;
}

.color-selector {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.color-option {
  width: 40px;
  height: 40px;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
}

.color-option:hover {
  transform: scale(1.05);
}

.color-option.selected {
  border-color: #1e293b;
  box-shadow: 0 0 0 2px white, 0 0 0 4px #1e293b;
}

.icon-selector {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.icon-option {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e2e8f0;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 1.2rem;
  color: #475569;
}

.icon-option:hover {
  border-color: #38bdf8;
  background: #f0f9ff;
}

.icon-option.selected {
  border-color: #38bdf8;
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  color: white;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

.btn-cancel {
  padding: 0.8rem 1.5rem;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: #475569;
}

.btn-cancel:hover {
  background: #e2e8f0;
}

.btn-submit {
  padding: 0.8rem 1.5rem;
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  border: none;
  border-radius: 0.5rem;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: white;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>