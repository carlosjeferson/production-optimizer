describe('Engenharia de Produtos - Testes com Mock', () => {
  
  beforeEach(() => {
    // 1. Configura as interceptações
    cy.intercept('GET', '**/products', { fixture: 'products.json' }).as('loadProducts');
    cy.intercept('GET', '**/raw-materials', {
      body: [{ id: 1, name: 'Pistão' }, { id: 2, name: 'Parafuso' }]
    }).as('loadMaterials');

    // 2. Visita a página (ela abre em Insumos por padrão)
    cy.visit('/');

    // 3. CLIQUE NO BOTÃO DE PRODUTOS PARA DISPARAR O FETCH
    // Como você tem um botão com a label "Produtos" no App.vue:
    cy.contains('button', 'Produtos').click();

    // 4. Agora sim, espera as duas requisições que devem acontecer
    cy.wait(['@loadProducts', '@loadMaterials']);
  });

  it('Deve carregar a lista inicial do Mock', () => {
    cy.get('.premium-table tbody tr').should('have.length', 2);
    cy.contains('Cadeira Gamer Premium').should('be.visible');
  });

  it('Deve validar scroll e preenchimento ao editar', () => {
    // Intercepta o PUT (Update)
    cy.intercept('PUT', '**/products/101', { statusCode: 200 }).as('updateReq');

    // Clica no editar do primeiro item (ID 101 do Mock)
    cy.get('.action-btn.edit').first().click();

    // Testa o Scroll Automático que você colocou no Vue
    cy.verifyScrollTop();

    // Usa o comando customizado para preencher
    cy.fillProductForm('PRD-EDITADO', 'Novo Nome Mock', '1300');
    
    cy.get('[data-cy="btn-save"]').click();

    cy.wait('@updateReq');
    cy.get('.toast-success').should('be.visible');
  });

  it('Deve validar erro de campos vazios', () => {
    // Limpa campos e tenta salvar
    cy.get('[data-cy="input-code"]').clear();
    cy.get('[data-cy="btn-save"]').click();
    
    cy.get('.toast-error').should('contain', 'campos obrigatórios');
  });
});