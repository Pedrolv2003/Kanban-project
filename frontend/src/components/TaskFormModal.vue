<template>
  <div class="task-form-modal">
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="titulo">Titulo</label>
        <div class="input-icon">
          <i class="fas fa-heading"></i>
          <input 
            type="text" 
            id="titulo" 
            v-model="form.titulo" 
            required 
            placeholder="Digite o titulo da tarefa"
            autofocus
          />
        </div>
      </div>

      <div class="form-group">
        <label for="descricao">Descricao</label>
        <div class="input-icon">
          <i class="fas fa-align-left"></i>
          <textarea 
            id="descricao" 
            v-model="form.descricao" 
            required 
            placeholder="Descreva a tarefa"
            rows="3"
          ></textarea>
        </div>
      </div>

      <div class="form-group">
        <label for="responsavel">Responsavel</label>
        <div class="input-icon">
          <i class="fas fa-user"></i>
          <input 
            type="text" 
            id="responsavel" 
            v-model="form.responsavel" 
            required 
            placeholder="Nome do responsavel"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-group half">
          <label for="dataInicio">Data de Inicio</label>
          <div class="input-icon">
            <i class="fas fa-calendar-alt"></i>
            <input 
              type="date" 
              id="dataInicio" 
              v-model="form.dataInicio" 
              required 
              :min="minDate"
              @change="validateDates"
            />
          </div>
        </div>

        <div class="form-group half">
          <label for="dataFim">Data de Fim</label>
          <div class="input-icon">
            <i class="fas fa-calendar-check"></i>
            <input 
              type="date" 
              id="dataFim" 
              v-model="form.dataFim" 
              required 
              :min="form.dataInicio || minDate"
              @change="validateDates"
            />
          </div>
        </div>
      </div>

      <div v-if="dateError" class="error-message">
        <i class="fas fa-exclamation-circle"></i>
        {{ dateError }}
      </div>

      <div class="form-actions">
        <button type="button" @click="$emit('cancel')" class="btn-cancel">
          Cancelar
        </button>
        <button type="submit" :disabled="!isValid || dateError !== ''" class="btn-submit">
          <i :class="isEditing ? 'fas fa-save' : 'fas fa-plus'"></i>
          {{ isEditing ? 'Salvar Alteracoes' : 'Criar Tarefa' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, computed, watch, ref } from 'vue'

const props = defineProps({
  initialData: {
    type: Object,
    default: null
  },
  isEditing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

const form = reactive({
  titulo: '',
  descricao: '',
  responsavel: '',
  dataInicio: '',
  dataFim: ''
})

const dateError = ref('')
const minDate = new Date().toISOString().split('T')[0]

const validateDates = () => {
  if (form.dataInicio && form.dataFim) {
    const inicio = new Date(form.dataInicio)
    const fim = new Date(form.dataFim)
    
    if (fim <= inicio) {
      dateError.value = 'A data de fim deve ser pelo menos um dia apos a data de inicio'
      return false
    } else {
      dateError.value = ''
      return true
    }
  }
  return true
}

watch(() => [form.dataInicio, form.dataFim], () => {
  validateDates()
})

watch(() => props.initialData, (data) => {
  if (data) {
    form.titulo = data.titulo || ''
    form.descricao = data.descricao || ''
    form.responsavel = data.responsavel || ''
    if (data.dataInicio) {
      form.dataInicio = data.dataInicio.split('T')[0]
    }
    if (data.dataFim) {
      form.dataFim = data.dataFim.split('T')[0]
    }
  }
}, { immediate: true })

const isValid = computed(() => {
  const valid = form.titulo.trim() !== '' && 
         form.descricao.trim() !== '' && 
         form.responsavel.trim() !== '' &&
         form.dataInicio !== '' &&
         form.dataFim !== ''
  
  console.log('Validacao:', { valid, dateError: dateError.value, form })
  return valid
})

const handleSubmit = () => {
  if (!isValid.value || dateError.value) {
    console.log('Formulario invalido:', { isValid: isValid.value, dateError: dateError.value })
    return
  }
  
  const submitData = {
    titulo: form.titulo,
    descricao: form.descricao,
    responsavel: form.responsavel,
    dataInicio: form.dataInicio,
    dataFim: form.dataFim
  }
  
  console.log('Submetendo tarefa:', submitData)
  emit('submit', submitData)
}
</script>

<style scoped>
.task-form-modal {
  width: 100%;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.form-group.half {
  flex: 1;
  margin-bottom: 0;
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

.input-icon input,
.input-icon textarea {
  width: 100%;
  padding: 0.8rem 0.9rem 0.8rem 2.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 0.9rem;
  transition: all 0.2s ease;
  background: #f8fafc;
  font-family: inherit;
}

.input-icon textarea {
  resize: vertical;
  min-height: 80px;
}

.input-icon input:focus,
.input-icon textarea:focus {
  outline: none;
  border-color: #38bdf8;
  box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.1);
  background: white;
}

.error-message {
  margin: 0.5rem 0 1rem;
  padding: 0.5rem;
  background: #fef2f2;
  border-radius: 0.5rem;
  color: #ef4444;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1rem;
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