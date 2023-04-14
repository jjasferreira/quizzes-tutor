describe('Teacher Dashboard Test', () => {
    // enter as demo teacher
    beforeEach(() => {
        cy.deleteQuestionsAndAnswers();
        cy.demoTeacherLogin();
        cy.get('[data-cy="dashboardMenuButton"]').click();
    })

    it('test only one execution (no stats)', () => {
        let date = new Date();
        cy.addTopicAndAssessment();

        cy.createQuestion(
          'Dashboard Question 1 ' + date,
          'Question',
          'Option',
          'Option',
          'ChooseThisWrong',
          'Correct'
        );

        cy.get('[data-cy="Topics"]').click();
        cy.contains('Software Architecture').click();

        cy.createQuestion(
          'Dashboard Question 2 ' + date,
          'Question',
          'Option',
          'Option',
          'ChooseThisWrong',
          'Correct'
        );

        cy.createQuizzWith2Questions(
          'Dashboard Title ' + date,
          'Dashboard Question 1 ' + date,
          'Dashboard Question 2 ' + date
        );
        cy.contains('Logout').click();
        cy.demoStudentLogin();
        cy.solveQuizz('Dashboard Title ' + date, 2, 'ChooseThisWrong');
        cy.contains('Logout').click();
        cy.demoTeacherLogin();
        cy.get('[data-cy="dashboardMenuButton"]').click();

        cy.get('[data-cy="numAvailable"]').should('have.text', 2);
        cy.get('[data-cy="answeredQuestionsUnique"]').should('have.text', 2);
        cy.get('[data-cy="averageQuestionsAnswered"]').should('have.text', 2);

        // verificar que não são mostrados gráficos
        cy.get('[data-cy="question_stat_graph"]').should('not.exist');
        cy.get('[data-cy="quiz_stat_graph"]').should('not.exist');
    })
})
