<!-- components/SettingsMenu.vue -->
<template>
  <div class="settings-menu">
    <button class="settings-btn" @click="toggleMenu">
      <i class="fas fa-cog"></i>
    </button>
    
    <div v-if="isOpen" class="settings-popup" @click.stop>
      <div class="popup-header">
        <i class="fas fa-user-circle"></i>
        <span class="user-email">{{ userEmail }}</span>
      </div>
      
      <div class="popup-divider"></div>
      
      <button class="menu-item" @click="handleRegister">
        <i class="fas fa-user-plus"></i>
        <span>Nova Conta</span>
      </button>
      
      <button class="menu-item logout" @click="handleLogout">
        <i class="fas fa-sign-out-alt"></i>
        <span>Sair</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  userEmail: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['register', 'logout'])

const isOpen = ref(false)

const toggleMenu = () => {
  isOpen.value = !isOpen.value
}

const closeMenu = () => {
  isOpen.value = false
}

const handleRegister = () => {
  closeMenu()
  emit('register')
}

const handleLogout = () => {
  closeMenu()
  emit('logout')
}

// Fechar menu ao clicar fora
const handleClickOutside = (event) => {
  if (isOpen.value && !event.target.closest('.settings-menu')) {
    closeMenu()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.settings-menu {
  position: relative;
}

.settings-btn {
  background: linear-gradient(135deg, #475569, #334155);
  color: white;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 0.5rem;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.settings-btn:hover {
  transform: translateY(-2px);
  background: linear-gradient(135deg, #5b6e8c, #475569);
  box-shadow: 0 4px 12px rgba(71, 85, 105, 0.3);
}

.settings-popup {
  position: absolute;
  top: 45px;
  right: 0;
  width: 260px;
  background: white;
  border-radius: 0.75rem;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.02);
  z-index: 150;
  overflow: hidden;
  animation: slideDown 0.2s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.popup-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-bottom: 1px solid #e2e8f0;
}

.popup-header i {
  font-size: 1.5rem;
  color: #38bdf8;
}

.user-email {
  font-size: 0.85rem;
  font-weight: 500;
  color: #1e293b;
  word-break: break-all;
  flex: 1;
}

.popup-divider {
  height: 1px;
  background: #e2e8f0;
  margin: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  padding: 0.75rem 1rem;
  background: white;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.85rem;
  color: #334155;
  text-align: left;
}

.menu-item i {
  width: 20px;
  font-size: 1rem;
  color: #64748b;
}

.menu-item:hover {
  background: #f1f5f9;
}

.menu-item.logout:hover {
  background: #fef2f2;
}

.menu-item.logout:hover i,
.menu-item.logout:hover span {
  color: #ef4444;
}
</style>