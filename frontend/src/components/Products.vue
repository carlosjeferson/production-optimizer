<template>
  <div class="page-container">
    <header class="page-header">
      <div class="title-group">
        <div class="icon-bg-premium">🛠️</div>
        <div class="header-titles">
          <h1>Engenharia de Produtos</h1>
          <p>Configure a ficha técnica, custos e composição de fabricação.</p>
        </div>
      </div>
    </header>

    <div class="grid-layout">
      <aside class="side-panel">
        <div class="main-card premium-shadow p-25 form-section" :class="{ 'is-editing': form.id }">
          <div class="section-header">
            <h3 class="section-title">
              <span class="icon">{{ form.id ? '📝' : '✨' }}</span>
              {{ form.id ? 'Editar Produto' : 'Novo Produto' }}
            </h3>
            <button v-if="form.id" @click="resetForm" class="btn-text" title="Cancelar Edição">
              Cancelar
            </button>
          </div>
          
          <div class="form-vertical">
            <div class="input-wrapper">
              <label>Código do Produto</label>
              <input v-model="form.code" 
              type="text"
              data-cy="input-code"
              placeholder="Ex: PRD-789" 
              class="premium-input" 
              :disabled="isLoading" />
            </div>

            <div class="input-wrapper mt-15">
              <label>Nome Comercial</label>
              <input v-model="form.name" type="text" placeholder="Ex: Cadeira Ergonômica" data-cy="input-name" class="premium-input" :disabled="isLoading" />
            </div>

            <div class="input-wrapper mt-15">
              <label>Preço de Venda</label>
              <div class="input-with-prefix">
                <span class="prefix">R$</span>
                <input v-model.number="form.price" type="number" step="0.01" class="premium-input pl-35" placeholder="0.00" :disabled="isLoading" />
              </div>
            </div>

            <div class="composition-box mt-20">
              <h4 class="sub-title">🔗 Receita (Matérias-Primas)</h4>
              
              <div class="builder-row">
                <select v-model="selectedMaterialId" class="premium-input" :disabled="isLoading">
                  <option :value="null" disabled>Selecione o Insumo...</option>
                  <option v-for="m in availableMaterials" :key="m.id" :value="m.id">
                    {{ m.name }}
                  </option>
                </select>
                
                <div class="input-with-suffix flex-1">
                  <input v-model.number="requiredQty" type="number" placeholder="Qtd" class="premium-input" :disabled="isLoading" />
                  <span class="suffix">un</span>
                </div>
                
                <button @click="addMaterialToComposition" class="btn-add-item" title="Adicionar à lista" :disabled="isLoading">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" /></svg>
                </button>
              </div>

              <div class="composition-list" v-if="form.composition.length > 0">
                <div v-for="(item, index) in form.composition" :key="index" class="comp-item animate-in">
                  <div class="item-info">
                    <span class="item-name">{{ getMaterialName(item.rawMaterial.id) }}</span>
                    <span class="item-qty">{{ item.requiredQuantity }} un</span>
                  </div>
                  <button @click="removeMaterialFromComposition(index)" class="btn-remove-item" :disabled="isLoading">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
                  </button>
                </div>
              </div>
              <div v-else class="empty-composition">Nenhum insumo adicionado.</div>
            </div>

            <button @click="saveProduct" class="btn-premium w-100 mt-20" data-cy="btn-save" :class="{ 'btn-update': form.id }" :disabled="isLoading">
              <span v-if="isLoading" class="spinner-small"></span>
              <span v-else>{{ form.id ? 'Atualizar Produto' : 'Finalizar Cadastro' }}</span>
            </button>
          </div>
        </div>
      </aside>

      <main class="content-panel">
        <div class="main-card premium-shadow">
          <div class="table-header-bar p-25">
            <h3>📋 Catálogo de Produtos <span class="badge-count">{{ products.length }}</span></h3>
          </div>
          
          <div class="table-responsive">
            <table class="premium-table">
              <thead>
                <tr>
                  <th>CÓDIGO</th>
                  <th>PRODUTO</th>
                  <th>PREÇO (R$)</th>
                  <th>COMPOSIÇÃO</th>
                  <th class="text-right">AÇÕES</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="p in products" :key="p.id" :class="{'active-row': form.id === p.id}">
                  <td><span class="code-badge">{{ p.code }}</span></td>
                  <td class="font-medium text-dark">{{ p.name }}</td>
                  <td class="font-bold text-success">
                    R$ {{ p.price?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}
                  </td>
                  <td>
                    <div class="stack-badges">
                      <span v-for="c in p.composition" :key="c.id" class="mini-badge" :title="getMaterialName(c.rawMaterial.id)">
                        {{ getMaterialName(c.rawMaterial.id) }} ({{ c.requiredQuantity }})
                      </span>
                      <span v-if="!p.composition?.length" class="text-muted small">Sem receita</span>
                    </div>
                  </td>
                  <td class="actions-cell">
                    <div class="action-buttons">
                      <button class="action-btn edit" @click="editProduct(p)" :disabled="isLoading" title="Editar">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                      </button>
                      <button class="action-btn delete" @click="confirmDelete(p)" :disabled="isLoading" title="Excluir">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="products.length === 0 && !isLoading">
                  <td colspan="5">
                    <div class="empty-state">
                      <div class="empty-icon">🛋️</div>
                      <p>Nenhum produto cadastrado.</p>
                      <span>Crie seu primeiro produto no painel lateral.</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>

    <transition name="modal-fade">
      <div v-if="deleteModal.isOpen" class="modal-overlay" @click.self="closeDeleteModal">
        <div class="modal-card">
          <div class="modal-icon-warning">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
          </div>
          <h3>Excluir Produto?</h3>
          <p>Deseja remover o produto <strong>{{ deleteModal.item?.name }}</strong> e sua receita? Esta ação é irreversível.</p>
          <div class="modal-actions">
            <button class="btn-cancel" @click="closeDeleteModal">Cancelar</button>
            <button class="btn-confirm-delete" @click="executeDelete">
              <span v-if="isLoading" class="spinner-small"></span>
              <span v-else>Sim, Excluir</span>
            </button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="toast-slide">
      <div v-if="toast.show" class="toast-notification" :class="`toast-${toast.type}`">
        <span class="toast-icon">{{ toast.type === 'success' ? '✅' : '⚠️' }}</span>
        <span class="toast-message">{{ toast.message }}</span>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const products = ref([]);
const availableMaterials = ref([]);
const isLoading = ref(false);

const form = ref({ id: null, code: '', name: '', price: null, composition: [] });
const selectedMaterialId = ref(null);
const requiredQty = ref(null);

const toast = ref({ show: false, message: '', type: 'success' });
const deleteModal = ref({ isOpen: false, item: null });

const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type };
  setTimeout(() => { toast.value.show = false; }, 3500);
};

const resetForm = () => {
  form.value = { id: null, code: '', name: '', price: null, composition: [] };
  selectedMaterialId.value = null;
  requiredQty.value = null;
};

const confirmDelete = (item) => { deleteModal.value = { isOpen: true, item }; };
const closeDeleteModal = () => { deleteModal.value = { isOpen: false, item: null }; };

const getMaterialName = (id) => availableMaterials.value.find(m => m.id === id)?.name || 'Insumo Excluído';

const addMaterialToComposition = () => {
  if (!selectedMaterialId.value || !requiredQty.value) {
    showToast("Selecione o insumo e informe a quantidade.", "error");
    return;
  }
  
  const exists = form.value.composition.find(c => c.rawMaterial.id === selectedMaterialId.value);
  if (exists) {
    showToast("Este insumo já está na receita. Remova-o para alterar a quantidade.", "error");
    return;
  }

  form.value.composition.push({
    rawMaterial: { id: selectedMaterialId.value },
    requiredQuantity: requiredQty.value
  });
  
  selectedMaterialId.value = null;
  requiredQty.value = null;
};

const removeMaterialFromComposition = (index) => {
  form.value.composition.splice(index, 1);
};


const fetchAll = async () => {
  isLoading.value = true;
  try {
    const [prodRes, matRes] = await Promise.all([
      api.get('/products'),
      api.get('/raw-materials')
    ]);
    products.value = prodRes.data;
    availableMaterials.value = matRes.data;
  } catch (err) { 
    showToast("Erro ao carregar dados do servidor.", "error");
  } finally {
    isLoading.value = false;
  }
};

const saveProduct = async () => {
  if (!form.value.code || !form.value.name || !form.value.price) {
    showToast("Preencha todos os campos obrigatórios.", "error");
    return;
  }

  if (!form.value.composition || form.value.composition.length === 0) {
    showToast("Selecione pelo menos um insumo para a composição.", "error");
    return;
  }

  isLoading.value = true;
  try {
    const payload = {
      code: form.value.code,
      name: form.value.name,
      price: form.value.price,
      composition: form.value.composition
    };

    if (form.value.id) {
      await api.put(`/products/${form.value.id}`, payload);
      showToast("Produto atualizado com sucesso!");
    } else {
      await api.post('/products', payload);
      showToast("Novo produto criado com sucesso!");
    }

    resetForm();
    await fetchAll();
  } catch (err) { 
    showToast(err, "error");
    console.log(err);
  } finally {
    isLoading.value = false;
  }
};

const editProduct = (p) => {
  form.value = {
    id: p.id,
    code: p.code,
    name: p.name,
    price: p.price,
    composition: p.composition.map(c => ({
      rawMaterial: { id: c.rawMaterial.id },
      requiredQuantity: c.requiredQuantity
    }))
  };
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const executeDelete = async () => {
  if (!deleteModal.value.item) return;
  
  isLoading.value = true;
  try {
    await api.delete(`/products/${deleteModal.value.item.id}`);
    
    if (form.value.id === deleteModal.value.item.id) resetForm();
    
    showToast("Produto excluído com sucesso!");
    closeDeleteModal();
    await fetchAll();
  } catch (err) {
    showToast("Erro ao excluir o produto.", "error");
    closeDeleteModal();
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchAll);
</script>

<style scoped>
.page-container * { box-sizing: border-box; }

.page-container { max-width: 1200px; margin: 0 auto; padding: 40px 20px; font-family: 'Inter', system-ui, sans-serif; }

/* Headers */
.title-group { display: flex; align-items: center; gap: 20px; margin-bottom: 30px; }
.icon-bg-premium { flex-shrink: 0; font-size: 2rem; background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%); padding: 16px; border-radius: 16px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid #e2e8f0; }
.header-titles h1 { font-size: 1.8rem; font-weight: 800; color: #0f172a; margin: 0 0 4px 0; letter-spacing: -0.03em; line-height: 1.2; }
.header-titles p { color: #64748b; margin: 0; font-size: 0.95rem; }

/* Layout Grid Lateral */
.grid-layout { display: grid; grid-template-columns: 360px 1fr; gap: 25px; align-items: start; width: 100%; }

/* Cards & Sombras */
.main-card { background: #ffffff; border-radius: 20px; overflow: hidden; border: 1px solid #e2e8f0; width: 100%; }
.premium-shadow { box-shadow: 0 10px 15px -3px rgba(0,0,0,0.03), 0 4px 6px -4px rgba(0,0,0,0.02); }

/* Sidebar Formulário Vertical */
.form-section { transition: background-color 0.4s ease; background: #ffffff; }
.form-section.is-editing { background: #f0f9ff; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; border-bottom: 1px solid #e2e8f0; padding-bottom: 15px; }
.section-title { font-size: 1.1rem; font-weight: 700; color: #1e293b; display: flex; align-items: center; gap: 8px; margin: 0; }

.input-wrapper { display: flex; flex-direction: column; gap: 8px; width: 100%; }
.input-wrapper label { font-size: 0.75rem; font-weight: 600; color: #475569; text-transform: uppercase; letter-spacing: 0.05em; }
.premium-input { padding: 12px 16px; border: 1px solid #cbd5e1; border-radius: 10px; font-size: 0.95rem; color: #1e293b; background: #ffffff; transition: all 0.2s ease; outline: none; width: 100%; }
.premium-input:focus { border-color: #6366f1; box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15); }
.premium-input:disabled { background: #f1f5f9; cursor: not-allowed; opacity: 0.7; }

/* Prefixo e Sufixo */
.input-with-prefix, .input-with-suffix { position: relative; display: flex; align-items: center; width: 100%; }
.input-with-prefix .prefix { position: absolute; left: 16px; color: #64748b; font-weight: 600; font-size: 0.9rem; }
.pl-35 { padding-left: 42px !important; }
.input-with-suffix input { padding-right: 40px; }
.suffix { position: absolute; right: 16px; color: #94a3b8; font-size: 0.85rem; font-weight: 600; }

/* Bloco de Composição */
.composition-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 15px; width: 100%; }
.sub-title { font-size: 0.85rem; font-weight: 700; color: #475569; margin-bottom: 12px; margin-top: 0; }
.builder-row { display: flex; flex-direction: column; gap: 8px; margin-bottom: 15px; align-items: stretch; width: 100%; }

.btn-add-item { background: #0f172a; color: white; border: none; border-radius: 8px; padding: 8px 16px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: background 0.2s; flex-shrink: 0; }
.btn-add-item:hover:not(:disabled) { background: #1e293b; }
.btn-add-item svg { width: 18px; height: 18px; }

.composition-list { display: flex; flex-direction: column; gap: 8px; }
.comp-item { background: white; border: 1px solid #e2e8f0; padding: 8px 12px; border-radius: 8px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 1px 2px rgba(0,0,0,0.02); }
.item-info { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.item-name { font-size: 0.85rem; font-weight: 600; color: #1e293b; }
.item-qty { font-size: 0.75rem; background: #f1f5f9; padding: 2px 6px; border-radius: 6px; color: #64748b; font-weight: 600; }
.btn-remove-item { color: #ef4444; background: #fef2f2; border: none; border-radius: 6px; padding: 4px; cursor: pointer; display: flex; align-items: center; }
.btn-remove-item:hover { background: #fee2e2; }
.btn-remove-item svg { width: 14px; height: 14px; }
.empty-composition { text-align: center; font-size: 0.8rem; color: #94a3b8; padding: 12px; border: 1px dashed #cbd5e1; border-radius: 8px; }

/* Botões Principais */
.btn-premium { background: #0f172a; color: #ffffff; border: none; padding: 12px 24px; border-radius: 10px; font-size: 0.95rem; font-weight: 600; cursor: pointer; transition: all 0.2s ease; display: flex; align-items: center; justify-content: center; height: 45px; width: 100%; }
.btn-premium:hover:not(:disabled) { background: #1e293b; transform: translateY(-1px); box-shadow: 0 4px 6px -1px rgba(15, 23, 42, 0.2); }
.btn-premium:disabled { opacity: 0.7; cursor: not-allowed; }
.btn-update { background: #3b82f6; }
.btn-update:hover:not(:disabled) { background: #2563eb; box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.3); }

.btn-text { background: transparent; border: none; color: #64748b; font-weight: 600; cursor: pointer; font-size: 0.85rem; }
.btn-text:hover { color: #0f172a; text-decoration: underline; }

/* Tabela Premium e Container Responsivo */
.table-responsive { width: 100%; overflow-x: auto; -webkit-overflow-scrolling: touch; }
.table-header-bar { display: flex; align-items: center; flex-wrap: wrap; gap: 10px; }
.table-header-bar h3 { margin: 0; font-size: 1.1rem; color: #1e293b; display: flex; align-items: center; gap: 10px; }
.badge-count { background: #e2e8f0; color: #475569; padding: 2px 8px; border-radius: 20px; font-size: 0.8rem; font-weight: 700; }

.premium-table { width: 100%; border-collapse: separate; border-spacing: 0; min-width: 650px; /* Força a tabela a ter tamanho mínimo para scrollar e não esmagar no mobile */ }
.premium-table th { background: #f8fafc; padding: 14px 20px; font-size: 0.75rem; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.05em; text-align: left; border-bottom: 1px solid #e2e8f0; }
.premium-table td { padding: 16px 20px; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.premium-table tbody tr { transition: background-color 0.2s; }
.premium-table tbody tr:hover { background-color: #f8fafc; }
.active-row { background-color: #f0f9ff !important; }

/* Elementos da Tabela (Produtos) */
.code-badge { background: #f1f5f9; color: #475569; padding: 4px 8px; border-radius: 6px; font-family: monospace; font-size: 0.85rem; font-weight: 600; border: 1px solid #e2e8f0; }
.text-dark { color: #0f172a; font-weight: 600; }
.text-success { color: #059669; }
.stack-badges { display: flex; flex-wrap: wrap; gap: 6px; }
.mini-badge { font-size: 0.75rem; background: #e0f2fe; color: #0284c7; padding: 2px 8px; border-radius: 6px; font-weight: 600; border: 1px solid #bae6fd; white-space: nowrap; }

/* Ações da Tabela */
.action-buttons { display: flex; justify-content: flex-end; gap: 8px; }
.action-btn { background: #ffffff; border: 1px solid #e2e8f0; color: #64748b; width: 34px; height: 34px; border-radius: 8px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: all 0.2s; }
.action-btn svg { width: 16px; height: 16px; }
.action-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.action-btn.edit:hover { border-color: #3b82f6; color: #3b82f6; background: #eff6ff; }
.action-btn.delete:hover { border-color: #ef4444; color: #ef4444; background: #fef2f2; }

/* MODAL E TOASTS */
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(15, 23, 42, 0.4); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 999; padding: 15px; }
.modal-card { background: white; padding: 30px; border-radius: 20px; width: 100%; max-width: 400px; text-align: center; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1); }
.modal-icon-warning { background: #fef2f2; color: #ef4444; width: 60px; height: 60px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 20px auto; }
.modal-icon-warning svg { width: 30px; height: 30px; }
.modal-card h3 { margin: 0 0 10px 0; color: #0f172a; font-size: 1.3rem; font-weight: 800; }
.modal-card p { color: #64748b; font-size: 0.95rem; margin-bottom: 25px; line-height: 1.5; }
.modal-actions { display: flex; gap: 12px; flex-wrap: wrap; }
.btn-cancel { flex: 1; min-width: 120px; padding: 12px; background: #f1f5f9; border: none; border-radius: 10px; font-weight: 600; color: #475569; cursor: pointer; transition: background 0.2s; }
.btn-cancel:hover { background: #e2e8f0; }
.btn-confirm-delete { flex: 1; min-width: 120px; padding: 12px; background: #ef4444; border: none; border-radius: 10px; font-weight: 600; color: white; cursor: pointer; transition: background 0.2s; display: flex; align-items: center; justify-content: center; height: 45px; }
.btn-confirm-delete:hover { background: #dc2626; }

.toast-notification { position: fixed; bottom: 30px; right: 30px; padding: 16px 20px; border-radius: 12px; display: flex; align-items: center; gap: 12px; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); z-index: 1000; font-weight: 600; font-size: 0.95rem; max-width: 90vw; }
.toast-success { background: #10b981; color: white; }
.toast-error { background: #ef4444; color: white; }

.empty-state { text-align: center; padding: 40px 20px; }
.empty-icon { font-size: 2.5rem; margin-bottom: 15px; opacity: 0.5; }
.empty-state p { margin: 0; font-weight: 600; color: #475569; }
.empty-state span { font-size: 0.85rem; color: #94a3b8; }

.spinner-small { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3); border-radius: 50%; border-top-color: #fff; animation: spin 1s ease-in-out infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-active .modal-card { animation: bounceIn 0.3s; }
@keyframes bounceIn { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }
.toast-slide-enter-active, .toast-slide-leave-active { transition: all 0.3s ease; }
.toast-slide-enter-from { opacity: 0; transform: translateX(50px); }
.toast-slide-leave-to { opacity: 0; transform: translateY(20px); }
.animate-in { animation: slideIn 0.2s ease-out; }
@keyframes slideIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }

/* Utilitários */
.p-25 { padding: 25px; } .mt-15 { margin-top: 15px; } .mt-20 { margin-top: 20px; } .w-100 { width: 100%; } .text-right { text-align: right; }

@media (max-width: 1024px) {
  .grid-layout { grid-template-columns: 1fr; }
  .builder-row {
    flex-direction: column;
    align-items: stretch;
  }

  .btn-add-item {
    padding: 12px;
  }

}

@media (max-width: 768px) {
  .page-container {
    padding: 15px 10px; 
  }

  .title-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    margin-bottom: 20px;
  }

  .header-titles h1 {
    font-size: 1.4rem;
  }

  .p-25 {
    padding: 15px;
  }

  .builder-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .btn-add-item {
    padding: 12px;
  }

  .toast-notification {
    bottom: 20px;
    right: 15px;
    left: 15px;
    justify-content: center;
    text-align: center;
  }
}
</style>