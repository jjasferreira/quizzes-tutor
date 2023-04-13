// Testar se os valores mostrados no dashboard para a execução de 2022 estão correctos e verificar que
// os dois gráficos com duas barras relativos às execuções de 2022 e 2019 são mostrados.

describe('Teacher Dashboard', () => {
  // enter as demo teacher
  beforeEach(() => {
    cy.demoTeacherLogin();
  })

  it('test if the 2022 values are correct and the 2022 and 2019 graphs are shown', () => {
    let date = new Date();
    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.contains('Change Course').click();

    // Select the 2022 course execution and check if the values are correct
    cy.contains('Demo Course (2022DemoCourse)').click();
    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.get('[data-cy="numQuizzes"]').should('have.text', 4);
    cy.get('[data-cy="numUniqueAnsweredQuizzes"]').should('have.text', 3);
    cy.get('[data-cy="averageQuizzesSolved"]').should('have.text', 3);

    // Check if the graphs are shown
    cy.get('[data-cy="quiz_stat_graph"]').should('exist');
  })

  afterEach(() => {
    cy.contains('Logout').click();
  })
})
