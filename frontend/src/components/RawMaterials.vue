<template>
  <div class="page-container">
    <header class="page-header">
      <div class="title-group">
        <div class="icon-bg-premium">📦</div>
        <div class="header-titles">
          <h1>Insumos Industriais</h1>
          <p>Gerencie o estoque de matérias-primas da fábrica com precisão.</p>
        </div>
      </div>
    </header>

    <main class="main-card premium-shadow">
      <section class="form-section p-25" :class="{ 'is-editing': form.id }">
        <div class="section-header">
          <h3 class="section-title">
            <span class="icon">{{ form.id ? '📝' : '✨' }}</span>
            {{ form.id ? 'Editar Insumo' : 'Novo Insumo' }}
          </h3>
          <button v-if="form.id" @click="resetForm" class="btn-text" title="Cancelar Edição">
            Cancelar
          </button>
        </div>

        <div class="form-grid">
          <div class="input-wrapper">
            <label>Código do Item</label>
            <input 
              v-model="form.code" 
              data-cy="input-rm-code"
              type="text" 
              placeholder="Ex: RM-101" 
              class="premium-input" 
              :disabled="isLoading"
            />
          </div>
          
          <div class="input-wrapper name-col">
            <label>Descrição da Matéria-Prima</label>
            <input 
              v-model="form.name" 
              data-cy="input-rm-name"
              type="text" 
              placeholder="Ex: Bobina de Aço 1045" 
              class="premium-input" 
              :disabled="isLoading"
            />
          </div>
          
          <div class="input-wrapper qty-col">
            <label>Qtd. Estoque</label>
            <div class="input-with-suffix">
              <input 
                v-model.number="form.quantity" 
                data-cy="input-rm-qty"
                type="number" 
                placeholder="0" 
                class="premium-input" 
                :disabled="isLoading"
              />
              <span class="suffix">un</span>
            </div>
          </div>

          <div class="action-col">
            <button 
              @click="saveMaterial" 
              class="btn-premium" 
              data-cy="btn-rm-save"
              :class="{ 'btn-update': form.id }"
              :disabled="isLoading"
            >
              <span v-if="isLoading" class="spinner"></span>
              <span v-else>{{ form.id ? 'Atualizar' : 'Adicionar' }}</span>
            </button>
          </div>
        </div>
      </section>

      <section class="table-section">
        <div class="table-header-bar p-25">
          <h3>📋 Itens Cadastrados <span class="badge-count">{{ materials.length }}</span></h3>
        </div>
        
        <div class="table-responsive">
          <table class="premium-table">
            <thead>
              <tr>
                <th>CÓDIGO</th>
                <th>INFORMAÇÕES DO INSUMO</th>
                <th>DISPONÍVEL</th>
                <th class="text-right">AÇÕES</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in materials" :key="item.id" :class="{'active-row': form.id === item.id}">
                <td>
                  <span class="code-badge">{{ item.code }}</span>
                </td>
                <td class="font-medium text-dark">{{ item.name }}</td>
                <td>
                  <div class="qty-display">
                    <span class="qty-number">{{ item.quantity }}</span>
                    <span class="qty-unit">un</span>
                  </div>
                </td>
                <td class="actions-cell">
                  <div class="action-buttons">
                    <button class="action-btn edit" @click="editMaterial(item)" :disabled="isLoading" title="Editar">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                    </button>
                    <button class="action-btn delete" @click="confirmDelete(item)" :disabled="isLoading" title="Excluir">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="materials.length === 0 && !isLoading">
                <td colspan="4">
                  <div class="empty-state">
                    <div class="empty-icon">📭</div>
                    <p>Nenhum insumo encontrado no estoque.</p>
                    <span>Comece adicionando um novo item acima.</span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>

    <transition name="modal-fade">
      <div v-if="deleteModal.isOpen" class="modal-overlay" @click.self="closeDeleteModal">
        <div class="modal-card">
          <div class="modal-icon-warning">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
          </div>
          <h3>Excluir Insumo?</h3>
          <p>Tem certeza que deseja remover <strong>{{ deleteModal.item?.name }}</strong>? Esta ação não pode ser desfeita.</p>
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

const materials = ref([]);
const isLoading = ref(false);

const form = ref({ id: null, code: '', name: '', quantity: null });

const toast = ref({ show: false, message: '', type: 'success' });

const deleteModal = ref({ isOpen: false, item: null });

const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type };
  setTimeout(() => { toast.value.show = false; }, 3500);
};

const resetForm = () => {
  form.value = { id: null, code: '', name: '', quantity: null };
};

const confirmDelete = (item) => {
  deleteModal.value = { isOpen: true, item };
};

const closeDeleteModal = () => {
  deleteModal.value = { isOpen: false, item: null };
};

const fetchMaterials = async () => {
  isLoading.value = true;
  try {
    const response = await api.get('/raw-materials');
    materials.value = response.data;
  } catch (err) { 
    showToast("Erro ao carregar dados do servidor.", "error");
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

const saveMaterial = async () => {
  if (!form.value.code || !form.value.name) {
    showToast("Preencha o Código e o Nome.", "error");
    return;
  }

  isLoading.value = true;
  try {
    const payload = {
      code: form.value.code,
      name: form.value.name,
      quantity: form.value.quantity || 0
    };

    if (form.value.id) {
      await api.put(`/raw-materials/${form.value.id}`, payload);
      showToast("Insumo atualizado com sucesso!");
    } else {
      await api.post('/raw-materials', payload);
      showToast("Novo insumo adicionado!");
    }
    
    resetForm();
    await fetchMaterials();
  } catch (err) { 
    showToast(err, "error");
    console.log(err);
  } finally {
    isLoading.value = false;
  }
};

const editMaterial = (item) => {
  form.value = { ...item };
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const executeDelete = async () => {
  if (!deleteModal.value.item) return;
  
  isLoading.value = true;
  try {
    await api.delete(`/raw-materials/${deleteModal.value.item.id}`);
    
    if (form.value.id === deleteModal.value.item.id) resetForm();
    
    showToast("Insumo removido com sucesso!");
    closeDeleteModal();
    await fetchMaterials();
  } catch (err) {
    showToast("Não foi possível excluir. O item pode estar em uso.", "error");
    closeDeleteModal();
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchMaterials();
});
</script>

<style scoped>
.page-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: 'Inter', system-ui, sans-serif;
}

/* Headers */
.title-group {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}
.icon-bg-premium {
  font-size: 2rem;
  background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%);
  padding: 16px;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05), 0 2px 4px -2px rgba(0,0,0,0.02);
  border: 1px solid #e2e8f0;
}
.header-titles h1 {
  font-size: 1.8rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 4px 0;
  letter-spacing: -0.03em;
}
.header-titles p {
  color: #64748b;
  margin: 0;
  font-size: 0.95rem;
}

/* Main Card */
.main-card {
  background: #ffffff;
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}
.premium-shadow {
  box-shadow: 0 10px 15px -3px rgba(0,0,0,0.03), 0 4px 6px -4px rgba(0,0,0,0.02);
}

/* Formulário e Inputs */
.form-section {
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  transition: background-color 0.4s ease;
}
.form-section.is-editing {
  background: #f0f9ff; 
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.section-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}
.form-grid {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 16px;
}
.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.input-wrapper label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.premium-input {
  padding: 12px 16px;
  border: 1px solid #cbd5e1;
  border-radius: 10px;
  font-size: 0.95rem;
  color: #1e293b;
  background: #ffffff;
  transition: all 0.2s ease;
  outline: none;
}
.premium-input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
}
.premium-input:disabled {
  background: #f1f5f9;
  cursor: not-allowed;
  opacity: 0.7;
}

/* Ajustes de Grade */
.name-col { flex: 2; min-width: 250px; }
.qty-col { flex: 1; min-width: 120px; }
.action-col { flex: 0 0 auto; }

/* Input com Sulfixo (unidade) */
.input-with-suffix {
  position: relative;
  display: flex;
  align-items: center;
}
.input-with-suffix input { width: 100%; padding-right: 40px; }
.suffix {
  position: absolute;
  right: 16px;
  color: #94a3b8;
  font-size: 0.85rem;
  font-weight: 600;
}

/* Botões */
.btn-premium {
  background: #0f172a;
  color: #ffffff;
  border: none;
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 130px;
  height: 45px;
}
.btn-premium:hover:not(:disabled) {
  background: #1e293b;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(15, 23, 42, 0.2);
}
.btn-premium:disabled { opacity: 0.7; cursor: not-allowed; }

.btn-update {
  background: #3b82f6;
}
.btn-update:hover:not(:disabled) {
  background: #2563eb;
  box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.3);
}

.btn-text {
  background: transparent;
  border: none;
  color: #64748b;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.85rem;
}
.btn-text:hover { color: #0f172a; text-decoration: underline; }

/* Tabela Premium */
.table-responsive {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}
.table-header-bar {
  display: flex;
  align-items: center;
}
.table-header-bar h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 10px;
}
.badge-count {
  background: #e2e8f0;
  color: #475569;
  padding: 2px 8px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 700;
}

.premium-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  min-width: 600px; 
}
.premium-table th {
  background: #f8fafc;
  padding: 14px 25px;
  font-size: 0.75rem;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}
.premium-table td {
  padding: 16px 25px;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
}
.premium-table tbody tr {
  transition: background-color 0.2s;
}
.premium-table tbody tr:hover { background-color: #f8fafc; }
.active-row { background-color: #f0f9ff !important; }

/* Elementos da Tabela */
.code-badge {
  background: #f1f5f9;
  color: #475569;
  padding: 4px 8px;
  border-radius: 6px;
  font-family: monospace;
  font-size: 0.85rem;
  font-weight: 600;
  border: 1px solid #e2e8f0;
}
.text-dark { color: #0f172a; font-weight: 600; }

.qty-display { display: flex; align-items: baseline; gap: 4px; }
.qty-number { font-weight: 800; color: #1e293b; font-size: 1.05rem; }
.qty-unit { color: #94a3b8; font-size: 0.8rem; font-weight: 600; }

/* Ações da Tabela */
.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.action-btn {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  color: #64748b;
  width: 34px;
  height: 34px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}
.action-btn svg { width: 16px; height: 16px; }
.action-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.action-btn.edit:hover { border-color: #3b82f6; color: #3b82f6; background: #eff6ff; }
.action-btn.delete:hover { border-color: #ef4444; color: #ef4444; background: #fef2f2; }

/* Empty State */
.empty-state {
  text-align: center;
  padding: 40px 20px;
}
.empty-icon { font-size: 2.5rem; margin-bottom: 15px; opacity: 0.5; }
.empty-state p { margin: 0; font-weight: 600; color: #475569; }
.empty-state span { font-size: 0.85rem; color: #94a3b8; }

/* MODAL CONFIRMAÇÃO */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}
.modal-card {
  background: white;
  padding: 30px;
  border-radius: 20px;
  width: 90%;
  max-width: 400px;
  text-align: center;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}
.modal-icon-warning {
  background: #fef2f2;
  color: #ef4444;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px auto;
}
.modal-icon-warning svg { width: 30px; height: 30px; }
.modal-card h3 { margin: 0 0 10px 0; color: #0f172a; font-size: 1.3rem; font-weight: 800; }
.modal-card p { color: #64748b; font-size: 0.95rem; margin-bottom: 25px; line-height: 1.5; }
.modal-actions { display: flex; gap: 12px; }
.btn-cancel {
  flex: 1;
  padding: 12px;
  background: #f1f5f9;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-cancel:hover { background: #e2e8f0; }
.btn-confirm-delete {
  flex: 1;
  padding: 12px;
  background: #ef4444;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 45px;
}
.btn-confirm-delete:hover { background: #dc2626; }

/* TOAST NOTIFICATION */
.toast-notification {
  position: fixed;
  bottom: 30px;
  right: 30px;
  padding: 16px 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1);
  z-index: 1000;
  font-weight: 600;
  font-size: 0.95rem;
  max-width: 90vw;
}
.toast-success { background: #10b981; color: white; }
.toast-error { background: #ef4444; color: white; }

/* ANIMAÇÕES E SPINNERS */
.spinner {
  width: 20px; height: 20px;
  border: 3px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
}
.spinner-small {
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* Animação Modal */
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-active .modal-card { animation: bounceIn 0.3s; }
@keyframes bounceIn {
  0% { transform: scale(0.9); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

/* Animação Toast */
.toast-slide-enter-active, .toast-slide-leave-active { transition: all 0.3s ease; }
.toast-slide-enter-from { opacity: 0; transform: translateX(50px); }
.toast-slide-leave-to { opacity: 0; transform: translateY(20px); }

/* Utilitários Extras */
.p-25 { padding: 25px; }
.text-right { text-align: right; }

@media (max-width: 768px) {
  .page-container {
    padding: 20px 15px; 
  }

  .title-group {
    flex-direction: column; 
    align-items: flex-start;
    gap: 12px;
  }

  .header-titles h1 {
    font-size: 1.5rem; 
  }

  .form-grid {
    flex-direction: column; 
    align-items: stretch;
  }

  .input-wrapper, .name-col, .qty-col, .action-col {
    width: 100%;
    min-width: 100%;
  }

  .btn-premium {
    width: 100%;
    margin-top: 10px;
  }

  .p-25 {
    padding: 15px; 
  }

  .premium-table th, .premium-table td {
    padding: 12px 15px;
  }

  .toast-notification {
    bottom: 20px;
    right: 20px;
    left: 20px;
    justify-content: center;
  }
}
</style>