<template>
  <Modal :is-open="isOpen" :title="`Filtrar - ${columnTitle}`" @close="close">
    <div class="filter-modal">
      <form @submit.prevent="applyFilter">
        <div class="filter-group">
          <label for="filterId">ID da Tarefa</label>
          <div class="input-icon">
            <i class="fas fa-hashtag"></i>
            <input 
              type="number" 
              id="filterId" 
              v-model="filters.id" 
              min="1"
              step="1"
              placeholder="Ex: 1, 2, 3..."
              @input="validateId"
            />
          </div>
          <span v-if="idError" class="error-hint">
            <i class="fas fa-exclamation-circle"></i> O ID deve ser maior ou igual a 1
          </span>
        </div>

        <div class="filter-group">
          <label for="filterTitulo">Titulo</label>
          <div class="input-icon">
            <i class="fas fa-heading"></i>
            <input 
              type="text" 
              id="filterTitulo" 
              v-model="filters.titulo" 
              placeholder="Digite parte do titulo..."
            />
          </div>
        </div>

        <div class="filter-group">
          <label for="filterResponsavel">Responsavel</label>
          <div class="input-with-button">
            <div class="input-icon">
              <i class="fas fa-user"></i>
              <input 
                type="text" 
                id="filterResponsavel" 
                v-model="filters.responsavel" 
                placeholder="Digite o nome do responsavel..."
              />
            </div>
            <button type="button" class="list-button" @click="openResponsaveisList">
              <i class="fas fa-list"></i>
            </button>
          </div>
        </div>

        <div class="filter-group">
          <label for="filterData">Data de Criacao</label>
          <div class="input-icon">
            <i class="fas fa-calendar"></i>
            <input 
              type="date" 
              id="filterData" 
              v-model="filters.data" 
            />
          </div>
        </div>

        <div class="filter-actions">
          <button type="button" @click="clearFilters" class="btn-clear">
            <i class="fas fa-eraser"></i> Limpar Filtros
          </button>
          <button type="submit" class="btn-apply" :disabled="hasIdError">
            <i class="fas fa-search"></i> Aplicar Filtro
          </button>
        </div>
      </form>

      <Modal :is-open="isResponsaveisModalOpen" title="Selecionar Responsavel" @close="closeResponsaveisModal">
        <div class="responsaveis-list">
          <div class="search-responsavel">
            <i class="fas fa-search"></i>
            <input 
              type="text" 
              v-model="searchResponsavel" 
              placeholder="Buscar responsavel..."
              class="search-input"
            />
          </div>
          <div class="responsaveis-container">
            <div 
              v-for="resp in filteredResponsaveis" 
              :key="resp"
              class="responsavel-item"
              @click="selectResponsavel(resp)"
            >
              <i class="fas fa-user-circle"></i>
              <span>{{ resp }}</span>
            </div>
            <div v-if="filteredResponsaveis.length === 0" class="no-results">
              <i class="fas fa-users"></i>
              <p>Nenhum responsavel encontrado</p>
            </div>
          </div>
        </div>
      </Modal>
    </div>
  </Modal>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import Modal from './Modal.vue'

const props = defineProps({
  isOpen: Boolean,
  columnTitle: String,
  allTasks: Array
})

const emit = defineEmits(['close', 'apply-filter'])

const filters = ref({
  id: '',
  titulo: '',
  responsavel: '',
  data: ''
})

const idError = ref(false)
const hasIdError = computed(() => idError.value)

const isResponsaveisModalOpen = ref(false)
const searchResponsavel = ref('')

const responsaveisList = computed(() => {
  const responsaveis = new Set()
  props.allTasks.forEach(task => {
    if (task.responsavel && task.responsavel.trim()) {
      responsaveis.add(task.responsavel)
    }
  })
  return Array.from(responsaveis).sort()
})

const filteredResponsaveis = computed(() => {
  if (!searchResponsavel.value) return responsaveisList.value
  return responsaveisList.value.filter(r => 
    r.toLowerCase().includes(searchResponsavel.value.toLowerCase())
  )
})

const validateId = () => {
  if (filters.value.id !== '' && filters.value.id !== null) {
    const numId = parseInt(filters.value.id)
    if (numId < 1 || isNaN(numId)) {
      idError.value = true
      filters.value.id = ''
    } else {
      idError.value = false
    }
  } else {
    idError.value = false
  }
}

const close = () => {
  idError.value = false
  emit('close')
}

const applyFilter = () => {
  if (filters.value.id !== '' && filters.value.id !== null) {
    const numId = parseInt(filters.value.id)
    if (numId < 1 || isNaN(numId)) {
      idError.value = true
      return
    }
  }
  
  const activeFilters = {}
  
  if (filters.value.id && filters.value.id !== '') {
    activeFilters.id = parseInt(filters.value.id)
  }
  if (filters.value.titulo && filters.value.titulo.trim() !== '') {
    activeFilters.titulo = filters.value.titulo.trim().toLowerCase()
  }
  if (filters.value.responsavel && filters.value.responsavel.trim() !== '') {
    activeFilters.responsavel = filters.value.responsavel.trim().toLowerCase()
  }
  if (filters.value.data && filters.value.data !== '') {
    activeFilters.data = filters.value.data
  }
  
  emit('apply-filter', activeFilters)
  close()
}

const clearFilters = () => {
  filters.value = {
    id: '',
    titulo: '',
    responsavel: '',
    data: ''
  }
  idError.value = false
  applyFilter()
}

const openResponsaveisList = () => {
  searchResponsavel.value = ''
  isResponsaveisModalOpen.value = true
}

const closeResponsaveisModal = () => {
  isResponsaveisModalOpen.value = false
  searchResponsavel.value = ''
}

const selectResponsavel = (responsavel) => {
  filters.value.responsavel = responsavel
  closeResponsaveisModal()
}

watch(() => props.isOpen, (newVal) => {
  if (!newVal) {
    idError.value = false
  }
})
</script>

<style scoped>
.filter-modal {
  width: 100%;
}

.filter-group {
  margin-bottom: 1.25rem;
}

.filter-group label {
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

.error-hint {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  font-size: 0.75rem;
  color: #ef4444;
}

.input-with-button {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.input-with-button .input-icon {
  flex: 1;
}

.list-button {
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  border: none;
  border-radius: 0.5rem;
  padding: 0.8rem 1rem;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.list-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
}

.filter-actions {
  display: flex;
  gap: 1rem;
  justify-content: space-between;
  margin-top: 1.5rem;
}

.btn-clear {
  flex: 1;
  padding: 0.8rem;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: #475569;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.btn-clear:hover {
  background: #e2e8f0;
}

.btn-apply {
  flex: 1;
  padding: 0.8rem;
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
  justify-content: center;
  gap: 0.5rem;
}

.btn-apply:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
}

.btn-apply:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.responsaveis-list {
  width: 100%;
}

.search-responsavel {
  position: relative;
  margin-bottom: 1rem;
}

.search-responsavel i {
  position: absolute;
  left: 0.9rem;
  top: 0.8rem;
  color: #94a3b8;
  font-size: 1rem;
}

.search-input {
  width: 100%;
  padding: 0.8rem 0.9rem 0.8rem 2.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 0.9rem;
  background: #f8fafc;
}

.search-input:focus {
  outline: none;
  border-color: #38bdf8;
  box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.1);
}

.responsaveis-container {
  max-height: 300px;
  overflow-y: auto;
}

.responsavel-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #f1f5f9;
}

.responsavel-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.responsavel-item i {
  color: #38bdf8;
  font-size: 1.1rem;
}

.responsavel-item span {
  color: #1e293b;
  font-size: 0.9rem;
}

.no-results {
  text-align: center;
  padding: 2rem;
  color: #94a3b8;
}

.no-results i {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  opacity: 0.5;
}

.no-results p {
  font-size: 0.9rem;
  margin: 0;
}
</style>