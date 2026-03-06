describe('Módulo de Insumos Industriais', () => {
  
  beforeEach(() => {
    cy.intercept('GET', '**/raw-materials', {
      body: [
        { id: 1, code: 'RM-001', name: 'Aço Carbono', quantity: 50 },
        { id: 2, code: 'RM-002', name: 'Pintura Epóxi', quantity: 10 }
      ]
    }).as('getMaterials');

    cy.visit('/'); 
    cy.wait('@getMaterials');
  });

  it('Deve validar campos obrigatórios ao adicionar', () => {
    cy.get('[data-cy="btn-rm-save"]').click();
    cy.get('.toast-error').should('contain', 'Preencha o Código e o Nome');
  });

  it('Deve criar um novo insumo com sucesso', () => {
    cy.intercept('POST', '**/raw-materials', { statusCode: 201 }).as('postMaterial');

    cy.get('[data-cy="input-rm-code"]').type('RM-TEST');
    cy.get('[data-cy="input-rm-name"]').type('Material de Teste');
    cy.get('[data-cy="input-rm-qty"]').type('100');
    
    cy.get('[data-cy="btn-rm-save"]').click();

    cy.wait('@postMaterial');
    cy.get('.toast-success').should('contain', 'Novo insumo adicionado');
  });

  it('Deve editar um insumo existente e verificar o scroll', () => {
    cy.intercept('PUT', '**/raw-materials/*', { statusCode: 200 }).as('putMaterial');

    cy.get('.action-btn.edit').first().click();

    cy.get('.section-title').should('contain', 'Editar Insumo');
    cy.window().its('scrollY').should('equal', 0);

    cy.get('[data-cy="input-rm-name"]').clear().type('Aço Carbono Editado');
    cy.get('[data-cy="btn-rm-save"]').click();

    cy.wait('@putMaterial');
    cy.get('.toast-success').should('contain', 'atualizado');
  });

  it('Deve exibir erro ao tentar excluir item vinculado a um produto', () => {
    cy.intercept('DELETE', '**/raw-materials/*', {
      statusCode: 400,
      body: { message: 'Item em uso' }
    }).as('deleteError');

    cy.get('.action-btn.delete').first().click();
    cy.get('.btn-confirm-delete').click();

    cy.wait('@deleteError');
    cy.get('.toast-error').should('contain', 'O item pode estar em uso');
  });
});