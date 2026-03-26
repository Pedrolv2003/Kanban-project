<template>
  <div class="register-container">
    <!-- Lado esquerdo: área de branding -->
    <div class="register-hero">
      <div class="hero-content">
        <!-- Botão KANBAN no topo -->
        <button @click="goBackToKanban" class="kanban-home-button">
          <i class="fas fa-columns"></i> Voltar para o Kanban
        </button>
        
        <h1 class="hero-title">
          <span class="highlight">Mini</span> Kanban
        </h1>
        <p class="hero-subtitle">Junte-se a nós e organize suas tarefas de forma inteligente</p>

        <div class="features-list">
          <div class="feature-item">
            <i class="fas fa-check-circle"></i>
            <span>Organização visual com Kanban</span>
          </div>
          <div class="feature-item">
            <i class="fas fa-users"></i>
            <span>Colabore com sua equipe</span>
          </div>
          <div class="feature-item">
            <i class="fas fa-chart-line"></i>
            <span>Acompanhe seu progresso</span>
          </div>
        </div>

        <div class="hero-footer">
          <p>© 2026 Mini Kanban. Todos os direitos reservados.</p>
        </div>
      </div>
    </div>

    <!-- Lado direito: formulário de registro -->
    <div class="register-form-wrapper">
      <div class="register-card">
        <div class="form-header">
          <h2>Criar nova conta</h2>
          <p>Preencha os dados abaixo para começar.</p>
        </div>

        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="email">E-mail</label>
            <div class="input-icon">
              <i class="fas fa-envelope"></i>
              <input 
                type="email" 
                id="email" 
                v-model="email" 
                required 
                placeholder="seu@email.com"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="password">Senha</label>
            <div class="input-icon">
              <i class="fas fa-lock"></i>
              <input 
                type="password" 
                id="password" 
                v-model="password" 
                required 
                placeholder="********"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword">Confirmar Senha</label>
            <div class="input-icon">
              <i class="fas fa-check-circle"></i>
              <input 
                type="password" 
                id="confirmPassword" 
                v-model="confirmPassword" 
                required 
                placeholder="********"
              />
            </div>
          </div>

          <button type="submit" :disabled="loading" class="register-button">
            <i class="fas fa-user-plus"></i>
            {{ loading ? 'Criando...' : 'Criar Conta' }}
          </button>

          <div v-if="errorMessage" class="error-message">
            <i class="fas fa-exclamation-circle"></i>
            {{ errorMessage }}
          </div>
        </form>

        <div class="form-footer">
          <p>Já tem uma conta? 
            <button type="button" @click="goBackToLogin" class="link-button">
              Voltar para o login
            </button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['register-success', 'back-to-login', 'back-to-kanban'])

const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const errorMessage = ref('')

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'As senhas não conferem'
    return
  }
  
  loading.value = true
  errorMessage.value = ''
  
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value, password: password.value })
    })
    
    if (!response.ok) {
      const error = await response.text()
      throw new Error(error)
    }
    
    const data = await response.json()
    
    localStorage.setItem('token', data.token)
    localStorage.setItem('userEmail', data.email)
    localStorage.setItem('userRole', data.role)
    
    emit('register-success', data)
    
  } catch (err) {
    errorMessage.value = err.message || 'Erro ao criar conta'
  } finally {
    loading.value = false
  }
}

const goBackToLogin = () => {
  emit('back-to-login')
}

const goBackToKanban = () => {
  emit('back-to-kanban')
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.register-container {
  display: flex;
  min-height: 100vh;
  background: #f8fafc;
  overflow: hidden;
}

/* Lado esquerdo */
.register-hero {
  flex: 1;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  overflow: hidden;
}

.register-hero::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.1) 0%, transparent 70%);
  animation: pulse 8s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.05); }
}

.hero-content {
  max-width: 420px;
  width: 100%;
  position: relative;
  z-index: 1;
}

/* Botão KANBAN no topo */
.kanban-home-button {
  background: rgba(56, 189, 248, 0.2);
  border: 1px solid rgba(56, 189, 248, 0.5);
  color: #38bdf8;
  padding: 0.6rem 1.2rem;
  border-radius: 2rem;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 2rem;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
}

.kanban-home-button:hover {
  background: rgba(56, 189, 248, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.2);
}

.hero-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
  line-height: 1.2;
}

.highlight {
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.hero-subtitle {
  font-size: 1rem;
  opacity: 0.9;
  margin-bottom: 2rem;
  line-height: 1.5;
}

.features-list {
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.85rem;
  opacity: 0.9;
}

.feature-item i {
  color: #38bdf8;
  font-size: 1rem;
}

.hero-footer {
  margin-top: 1.5rem;
  font-size: 0.7rem;
  opacity: 0.6;
  text-align: center;
}

/* Lado direito - formulário */
.register-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  background: white;
  overflow-y: auto;
}

.register-card {
  max-width: 400px;
  width: 100%;
  padding: 1.5rem;
}

.form-header {
  margin-bottom: 1.5rem;
  text-align: center;
}

.form-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 0.5rem;
}

.form-header p {
  color: #64748b;
  font-size: 0.8rem;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.4rem;
  font-size: 0.8rem;
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
  left: 0.75rem;
  color: #94a3b8;
  font-size: 0.9rem;
}

.input-icon input {
  width: 100%;
  padding: 0.6rem 0.75rem 0.6rem 2.25rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 0.85rem;
  transition: all 0.2s ease;
  background: #f8fafc;
}

.input-icon input:focus {
  outline: none;
  border-color: #38bdf8;
  box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.1);
  background: white;
}

.register-button {
  width: 100%;
  padding: 0.6rem;
  background: linear-gradient(135deg, #38bdf8, #818cf8);
  color: white;
  border: none;
  border-radius: 0.5rem;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.register-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
}

.register-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  margin-top: 1rem;
  padding: 0.6rem;
  background: #fef2f2;
  border: 1px solid #fee2e2;
  border-radius: 0.5rem;
  color: #dc2626;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-footer {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.8rem;
  color: #64748b;
}

.link-button {
  background: none;
  border: none;
  color: #38bdf8;
  font-weight: 500;
  cursor: pointer;
  padding: 0;
  font-size: 0.8rem;
}

.link-button:hover {
  text-decoration: underline;
  color: #0ea5e9;
}

/* Responsividade */
@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
  }
  
  .register-hero {
    padding: 1.5rem;
    min-height: auto;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .features-list {
    gap: 0.5rem;
  }
  
  .register-form-wrapper {
    padding: 1rem;
  }
  
  .register-card {
    padding: 1rem;
  }
  
  .kanban-home-button {
    margin-bottom: 1rem;
  }
}
</style>