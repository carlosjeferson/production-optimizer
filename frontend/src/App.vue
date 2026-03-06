<template>
  <div class="app-wrapper">
    <header class="main-nav">
      <div class="nav-content">
        <div class="brand-group">
          <div class="logo-icon">F</div>
          <span class="brand">Factory<span>Manager</span></span>
        </div>
        
        <nav class="nav-links">
          <button 
            @click="view = 'materials'" 
            :class="{ active: view === 'materials' }"
            class="nav-btn"
          >
            <span class="icon">📦</span>
            <span class="label">Insumos</span>
            <div class="active-indicator"></div>
          </button>

          <button 
            @click="view = 'products'" 
            :class="{ active: view === 'products' }"
            class="nav-btn"
          >
            <span class="icon">🛠️</span>
            <span class="label">Produtos</span>
            <div class="active-indicator"></div>
          </button>

          <button 
            @click="view = 'optimize'" 
            :class="{ active: view === 'optimize' }"
            class="nav-btn"
          >
            <span class="icon">🚀</span>
            <span class="label">Otimização</span>
            <div class="active-indicator"></div>
          </button>
          
        </nav>

        <div class="user-profile">
          <div class="avatar">CJ</div>
        </div>
      </div>
    </header>
    
    <main class="content-container">
      <transition name="fade" mode="out-in">
        <component :is="currentView" />
      </transition>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import RawMaterials from './components/RawMaterials.vue';
import Products from './components/Products.vue';
import Optimization from './components/Optimization.vue';

const view = ref('materials');

const currentView = computed(() => {
  if (view.value === 'materials') return RawMaterials;
  if (view.value === 'products') return Products;
  if (view.value === 'optimize') return Optimization;
  
  return RawMaterials;
});
</script>

<style>
:root {
  --nav-bg: #ffffff;
  --nav-height: 72px;
  --transition-speed: 0.3s;
}

.app-wrapper { 
  scroll-behavior: smooth;
  min-height: 100vh; 
  background-color: #f8fafc;
}

.main-nav { 
  background: var(--nav-bg); 
  height: var(--nav-height);
  border-bottom: 1px solid rgba(0,0,0,0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 0 2rem;
}

.nav-content { 
  max-width: 1300px; 
  margin: 0 auto; 
  height: 100%; 
  display: flex; 
  align-items: center; 
  justify-content: space-between; 
}

.brand-group { display: flex; align-items: center; gap: 12px; }
.logo-icon {
  background: var(--primary);
  color: violet; 
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

.brand { 
  font-family: Arial, Helvetica, sans-serif;
  font-weight: 700; 
  font-size: 1.1rem; 
  letter-spacing: -0.02em; 
  color: #1e293b;
}
.brand span { color: var(--primary); }

.nav-links { 
  display: flex; 
  gap: 4px; 
  background: #f1f5f9;
  padding: 4px;
  border-radius: 12px;
}

.nav-btn { 
  background: transparent; 
  border: none; 
  color: #64748b; 
  font-size: 0.875rem; 
  font-weight: 600;
  padding: 8px 18px; 
  border-radius: 8px; 
  cursor: pointer; 
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  transition: all var(--transition-speed);
}

.nav-btn.active { 
  color: var(--primary); 
  background: white; 
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.nav-btn:hover:not(.active) { 
  color: #1e293b;
  background: rgba(255,255,255,0.5);
}

.user-profile {
  display: flex;
  align-items: center;
}
.avatar {
  width: 38px;
  height: 38px;
  background: #e2e8f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  color: #475569;
  border: 2px solid white;
  box-shadow: 0 0 0 1px #e2e8f0;
}

.content-container {
  max-width: 1300px;
  margin: 0 auto;
  padding: 2rem;
}

/* Transição Suave de Telas */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.fade-enter-from { opacity: 0; transform: translateY(10px); }
.fade-leave-to { opacity: 0; transform: translateY(-10px); }

@media (max-width: 768px) {
  .main-nav {
    padding: 0 1rem;
    height: 64px; 
  }

  .brand {
    display: none;
  }

  .nav-btn {
    padding: 8px 12px;
    gap: 4px;
    font-size: 0.8rem;
  }

  .user-profile {
    display: none;
  }

  .nav-links {
    flex: 1;
    justify-content: center; 
    margin-left: 10px;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }
  
  .nav-links::-webkit-scrollbar {
    display: none;
  }

  .content-container {
    padding: 1rem 0; 
  }
}

@media (max-width: 380px) {
  .nav-btn .label {
    display: none; 
  }
}
</style>