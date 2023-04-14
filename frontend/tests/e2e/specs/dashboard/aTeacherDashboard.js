describe('a Teacher Dashboard', () => {

  it('test if the 2022 values are correct and the 2022 and 2019 graphs are shown', () => {
    // Login as demo teacher and navigate to change course menu
    cy.demoTeacherLogin();
    cy.contains('Change course').click();

    // Select the 2022 course execution and check if the values are correct
    cy.contains('New Course (2022NewCourse)').click();
    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.get('[data-cy="numQuizzes"]').should('have.text', 4);
    cy.get('[data-cy="numUniqueAnsweredQuizzes"]').should('have.text', 3);
    cy.get('[data-cy="averageQuizzesSolved"]').should('have.text', 2.33);

    // Check if the graphs are shown and logout
    cy.get('[data-cy="quiz_stats_graph"]').should('exist');
    cy.contains('Logout').click();
  })

})
