<template>
  <div class="page-container">
    <header class="page-header">
      <div class="title-group">
        <div class="icon-bg-premium optimization-icon">🚀</div>
        <div class="header-titles">
          <h1>Planejamento de Produção</h1>
          <p>Otimize o uso de insumos para alcançar o maior retorno financeiro possível.</p>
        </div>
      </div>
    </header>

    <main>
      <div class="action-banner premium-shadow">
        <div class="banner-content">
          <div class="banner-text">
            <h3>Gerar Novo Plano de Produção</h3>
            <p>O algoritmo analisará o estoque atual e priorizará os produtos de maior valor para sugerir o cenário mais lucrativo.</p>
          </div>
          <button @click="runOptimization" class="btn-premium btn-calculate" :disabled="isLoading">
            <span v-if="isLoading" class="spinner-small"></span>
            <span v-else>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon-btn"><path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>
              Calcular Melhor Cenário
            </span>
          </button>
        </div>
      </div>

      <div v-if="isLoading" class="loading-state">
        <div class="radar-spinner"></div>
        <p>Calculando rentabilidade máxima no servidor Quarkus...</p>
      </div>

      <transition name="fade-up">
        <div v-if="result && !isLoading" class="results-dashboard">
          
          <div class="kpi-grid">
            <div class="kpi-card highlight">
              <div class="kpi-icon">💰</div>
              <div class="kpi-info">
                <span class="kpi-label">Faturamento Máximo Projetado</span>
                <span class="kpi-value">R$ {{ result.totalValue?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}</span>
              </div>
            </div>
            
            <div class="kpi-card">
              <div class="kpi-icon">📦</div>
              <div class="kpi-info">
                <span class="kpi-label">Total de Itens a Produzir</span>
                <span class="kpi-value">{{ result.totalItemsToProduce }} <small>unidades</small></span>
              </div>
            </div>
          </div>

          <div class="details-single-column">
            <div class="main-card premium-shadow">
              <div class="table-header-bar p-20">
                <h3>🛠️ Plano de Fabricação Sugerido</h3>
              </div>
              <div class="table-responsive">
                <table class="premium-table">
                  <thead>
                    <tr>
                      <th>NOME DO PRODUTO</th>
                      <th class="text-right">QUANTIDADE A FABRICAR</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="item in result.productionPlan" :key="item.productName">
                      <td>
                        <span class="font-medium text-dark">{{ item.productName }}</span>
                      </td>
                      <td class="text-right">
                        <span class="qty-badge">{{ item.quantityToProduce }} un</span>
                      </td>
                    </tr>
                    <tr v-if="!result.productionPlan?.length">
                      <td colspan="2" class="text-center text-muted p-20">
                        <div class="empty-state">
                          <div class="empty-icon">⚠️</div>
                          <p>Estoque insuficiente.</p>
                          <span>Não há matéria-prima suficiente para fabricar nenhum produto cadastrado.</span>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          
        </div>
      </transition>
    </main>

    <transition name="toast-slide">
      <div v-if="toast.show" class="toast-notification" :class="`toast-${toast.type}`">
        <span class="toast-icon">{{ toast.type === 'success' ? '✅' : '⚠️' }}</span>
        <span class="toast-message">{{ toast.message }}</span>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import api from '../services/api';

const isLoading = ref(false);
const result = ref(null);
const toast = ref({ show: false, message: '', type: 'success' });

const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type };
  setTimeout(() => { toast.value.show = false; }, 3500);
};

// Chamada para a API
const runOptimization = async () => {
  isLoading.value = true;
  result.value = null;

  try {
    const response = await api.get('/production-optimization');
    const data = response.data;

    const planArray = Object.entries(data.productsToProduce || {}).map(([name, qty]) => {
      return {
        productName: name,
        quantityToProduce: qty
      };
    });

    const totalItems = planArray.reduce((acc, curr) => acc + curr.quantityToProduce, 0);

    result.value = {
      totalValue: data.totalEstimatedValue, 
      totalItemsToProduce: totalItems,
      productionPlan: planArray
    };

    showToast("Cenário de produção calculado com sucesso!", "success");
    
  } catch (err) {
    showToast("Erro ao calcular otimização. Verifique a conexão com o backend.", "error");
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* Base Premium Layout */
.page-container { max-width: 1000px; margin: 0 auto; padding: 40px 20px; font-family: 'Inter', system-ui, sans-serif; }
.title-group { display: flex; align-items: center; gap: 20px; margin-bottom: 30px; }
.icon-bg-premium { font-size: 2rem; background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%); padding: 16px; border-radius: 16px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid #e2e8f0; }
.optimization-icon { background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%); border-color: #a5b4fc; }
.header-titles h1 { font-size: 1.8rem; font-weight: 800; color: #0f172a; margin: 0 0 4px 0; letter-spacing: -0.03em; }
.header-titles p { color: #64748b; margin: 0; font-size: 0.95rem; }
.premium-shadow { box-shadow: 0 10px 15px -3px rgba(0,0,0,0.03), 0 4px 6px -4px rgba(0,0,0,0.02); }

/* Action Banner */
.action-banner { background: #ffffff; border-radius: 20px; border: 1px solid #e2e8f0; padding: 30px; margin-bottom: 30px; }
.banner-content { display: flex; justify-content: space-between; align-items: center; gap: 20px; flex-wrap: wrap; }
.banner-text h3 { margin: 0 0 8px 0; font-size: 1.3rem; color: #0f172a; font-weight: 800; }
.banner-text p { margin: 0; color: #64748b; font-size: 0.95rem; max-width: 600px; }

.btn-premium { background: #0f172a; color: #ffffff; border: none; padding: 14px 28px; border-radius: 12px; font-size: 1rem; font-weight: 700; cursor: pointer; transition: all 0.2s ease; display: flex; align-items: center; gap: 10px; }
.btn-premium:hover:not(:disabled) { background: #1e293b; transform: translateY(-2px); box-shadow: 0 10px 15px -3px rgba(15, 23, 42, 0.2); }
.btn-calculate { background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%); box-shadow: 0 4px 14px rgba(79, 70, 229, 0.3); }
.btn-calculate:hover:not(:disabled) { background: linear-gradient(135deg, #4338ca 0%, #2563eb 100%); box-shadow: 0 6px 20px rgba(79, 70, 229, 0.4); }
.icon-btn { width: 20px; height: 20px; }

/* Loading State */
.loading-state { text-align: center; padding: 60px 20px; }
.radar-spinner { width: 50px; height: 50px; border-radius: 50%; border: 3px solid transparent; border-top-color: #4f46e5; border-right-color: #4f46e5; animation: spin 1s linear infinite; margin: 0 auto 20px auto; }
.loading-state p { color: #64748b; font-weight: 500; font-size: 1.1rem; }

/* KPIs Grid */
.kpi-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-bottom: 30px; }
.kpi-card { background: #ffffff; border-radius: 20px; padding: 25px; border: 1px solid #e2e8f0; display: flex; align-items: center; gap: 20px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.02); }
.kpi-card.highlight { background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); border: none; color: white; box-shadow: 0 10px 15px -3px rgba(15, 23, 42, 0.3); }
.kpi-icon { font-size: 2.5rem; background: rgba(255,255,255,0.1); padding: 15px; border-radius: 16px; }
.kpi-card:not(.highlight) .kpi-icon { background: #f1f5f9; }
.kpi-info { display: flex; flex-direction: column; gap: 4px; }
.kpi-label { font-size: 0.85rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; color: #94a3b8; }
.kpi-card:not(.highlight) .kpi-label { color: #64748b; }
.kpi-value { font-size: 2rem; font-weight: 800; line-height: 1.2; }
.kpi-card:not(.highlight) .kpi-value { color: #0f172a; }
.kpi-value small { font-size: 1rem; font-weight: 600; color: #94a3b8; }

/* Tabela Centralizada */
.details-single-column { max-width: 800px; margin: 0 auto; }
.main-card { background: #ffffff; border-radius: 20px; overflow: hidden; border: 1px solid #e2e8f0; }
.table-header-bar { border-bottom: 1px solid #e2e8f0; }
.table-header-bar h3 { margin: 0; font-size: 1.1rem; color: #1e293b; font-weight: 700; }
.p-20 { padding: 20px; }

.premium-table { width: 100%; border-collapse: separate; border-spacing: 0; }
.premium-table th { background: #f8fafc; padding: 14px 25px; font-size: 0.75rem; font-weight: 700; color: #64748b; text-transform: uppercase; border-bottom: 1px solid #e2e8f0; text-align: left; }
.premium-table th.text-right { text-align: right; }
.premium-table td { padding: 16px 25px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.premium-table tbody tr:hover { background-color: #f8fafc; }

/* Elementos Visuais da Tabela */
.text-dark { color: #0f172a; font-weight: 600; font-size: 1.05rem; }
.text-center { text-align: center; }
.text-right { text-align: right; }
.text-muted { color: #94a3b8; }
.qty-badge { display: inline-block; background: #eff6ff; color: #2563eb; font-weight: 800; padding: 6px 16px; border-radius: 20px; font-size: 1rem; }

/* Empty State */
.empty-state { padding: 20px; }
.empty-icon { font-size: 2rem; margin-bottom: 10px; opacity: 0.7; }
.empty-state p { margin: 0; font-weight: 600; color: #475569; font-size: 1.1rem;}
.empty-state span { font-size: 0.9rem; color: #94a3b8; }

/* Utilitários Extras */
.spinner-small { display: inline-block; width: 18px; height: 18px; border: 2px solid rgba(255,255,255,0.3); border-radius: 50%; border-top-color: #fff; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.fade-up-enter-active, .fade-up-leave-active { transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1); }
.fade-up-enter-from { opacity: 0; transform: translateY(20px); }
.fade-up-leave-to { opacity: 0; transform: translateY(-20px); }

.toast-notification { position: fixed; bottom: 30px; right: 30px; padding: 16px 20px; border-radius: 12px; display: flex; align-items: center; gap: 12px; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); z-index: 1000; font-weight: 600; font-size: 0.95rem; }
.toast-success { background: #10b981; color: white; }
.toast-error { background: #ef4444; color: white; }
.toast-slide-enter-active, .toast-slide-leave-active { transition: all 0.3s ease; }
.toast-slide-enter-from { opacity: 0; transform: translateX(50px); }
.toast-slide-leave-to { opacity: 0; transform: translateY(20px); }
</style>