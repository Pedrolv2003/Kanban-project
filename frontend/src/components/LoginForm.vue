<!-- components/LoginForm.vue -->
<template>
  <div class="login-container">
    <!-- Lado esquerdo: área de branding e métricas/destaques -->
    <div class="login-hero">
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="highlight">Mini</span> Kanban
        </h1>
        <p class="hero-subtitle">Gestão de tarefas inteligente e visual</p>

        <!-- Cards de métricas -->
        <div class="metrics-grid">
          <div class="metric-card">
            <i class="fas fa-chart-line metric-icon"></i>
            <div class="metric-info">
              <span class="metric-value">+87%</span>
              <span class="metric-label">Produtividade</span>
            </div>
          </div>
          <div class="metric-card">
            <i class="fas fa-tasks metric-icon"></i>
            <div class="metric-info">
              <span class="metric-value">3x</span>
              <span class="metric-label">Mais tarefas</span>
            </div>
          </div>
          <div class="metric-card">
            <i class="fas fa-users metric-icon"></i>
            <div class="metric-info">
              <span class="metric-value">Equipes</span>
              <span class="metric-label">Colaboração real</span>
            </div>
          </div>
        </div>

        <div class="hero-footer">
          <p>© 2026 Mini Kanban. Todos os direitos reservados.</p>
        </div>
      </div>
    </div>

    <!-- Lado direito: formulário de login -->
    <div class="login-form-wrapper">
      <div class="login-card">
        <div class="form-header">
          <h2>Bem-vindo de volta</h2>
          <p>Por favor, insira os seus dados para entrar.</p>
        </div>

        <form @submit.prevent="handleLogin">
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

          <button type="submit" :disabled="loading" class="login-button">
            <i class="fas fa-arrow-right"></i>
            {{ loading ? 'Entrando...' : 'Entrar' }}
          </button>

          <div v-if="errorMessage" class="error-message">
            <i class="fas fa-exclamation-circle"></i>
            {{ errorMessage }}
          </div>
        </form>

        <div class="form-footer">
          <p>Não tem uma conta? 
            <button type="button" @click="$emit('switch-to-register')" class="link-button">
              Criar nova conta
            </button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['login-success', 'switch-to-register'])

const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const handleLogin = async () => {
  loading.value = true
  errorMessage.value = ''
  
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
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
    
    emit('login-success', data)
    
  } catch (err) {
    errorMessage.value = err.message || 'Erro ao fazer login'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-container {
  display: flex;
  min-height: 100vh;
  background: #f8fafc;
  overflow: hidden; /* Remove barra de rolagem */
}

/* Lado esquerdo - hero/área de branding */
.login-hero {
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

.login-hero::before {
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

/* Cards de métricas */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.75rem;
  margin-bottom: 2rem;
}

.metric-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 0.75rem;
  padding: 0.75rem;
  text-align: center;
  transition: transform 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.15);
}

.metric-icon {
  font-size: 1.25rem;
  color: #38bdf8;
  margin-bottom: 0.5rem;
}

.metric-value {
  display: block;
  font-size: 1.125rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.metric-label {
  font-size: 0.7rem;
  opacity: 0.8;
}

.hero-footer {
  margin-top: 1.5rem;
  font-size: 0.7rem;
  opacity: 0.6;
  text-align: center;
}

/* Lado direito - formulário */
.login-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  background: white;
  overflow-y: auto;
}

.login-card {
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

.login-button {
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

.login-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
}

.login-button:disabled {
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
  .login-container {
    flex-direction: column;
  }
  
  .login-hero {
    padding: 1.5rem;
    min-height: auto;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .metrics-grid {
    gap: 0.5rem;
  }
  
  .login-form-wrapper {
    padding: 1rem;
  }
  
  .login-card {
    padding: 1rem;
  }
}
</style>