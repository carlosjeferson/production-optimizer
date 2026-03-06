describe('Engenharia de Produtos - Testes com Mock', () => {
  
  beforeEach(() => {
    cy.intercept('GET', '**/products', { fixture: 'products.json' }).as('loadProducts');
    cy.intercept('GET', '**/raw-materials', {
      body: [{ id: 1, name: 'Pistão' }, { id: 2, name: 'Parafuso' }]
    }).as('loadMaterials');

    cy.visit('/');

    cy.contains('button', 'Produtos').click();

    cy.wait(['@loadProducts', '@loadMaterials']);
  });

  it('Deve carregar a lista inicial do Mock', () => {
    cy.get('.premium-table tbody tr').should('have.length', 2);
    cy.contains('Cadeira Gamer Premium').should('be.visible');
  });

  it('Deve validar scroll e preenchimento ao editar', () => {
    cy.intercept('PUT', '**/products/101', { statusCode: 200 }).as('updateReq');

    cy.get('.action-btn.edit').first().click();

    cy.verifyScrollTop();

    cy.fillProductForm('PRD-EDITADO', 'Novo Nome Mock', '1300');
    
    cy.get('[data-cy="btn-save"]').click();

    cy.wait('@updateReq');
    cy.get('.toast-success').should('be.visible');
  });

  it('Deve validar erro de campos vazios', () => {
    cy.get('[data-cy="input-code"]').clear();
    cy.get('[data-cy="btn-save"]').click();
    
    cy.get('.toast-error').should('contain', 'campos obrigatórios');
  });
});