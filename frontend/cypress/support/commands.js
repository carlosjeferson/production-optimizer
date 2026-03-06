Cypress.Commands.add('fillProductForm', (code, name, price) => {
  cy.get('[data-cy="input-code"]').clear().type(code);
  cy.get('[data-cy="input-name"]').clear().type(name);
  cy.get('input[type="number"]').first().clear().type(price);
});

Cypress.Commands.add('fillMaterialForm', (code, name, qty) => {
  cy.get('[data-cy="input-rm-code"]').clear().type(code);
  cy.get('[data-cy="input-rm-name"]').clear().type(name);
  if(qty) cy.get('[data-cy="input-rm-qty"]').clear().type(qty);
});

Cypress.Commands.add('verifyScrollTop', () => {
  cy.window().its('scrollY').should('equal', 0);
});