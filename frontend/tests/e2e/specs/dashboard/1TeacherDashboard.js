describe('2023 Teacher Dashboard', () => {

  it('test if the 2023 values are correct and the 2023, 2022 and 2019 graphs are shown', () => {
    // Login as demo teacher and navigate to change course menu
    cy.demoTeacherLogin();
    cy.contains('Change course').click();

    // Select the 2022 course execution and check if the values are correct
    cy.contains('New Course ' +
      '(2023NewCourse)').click();
    cy.get('[data-cy="dashboardMenuButton"]').click();
    cy.get('[data-cy="numStudents"]').should('have.text', 5);
    cy.get('[data-cy="numMore75CorrectQuestions"]').should('have.text', 1);
    cy.get('[data-cy="numAtLeast3Quizzes"]').should('have.text', 2);

    // Check if the graphs are shown and logout
    cy.get('[data-cy="student_stat_graph"]').should('exist');
    cy.get('[data-cy="student_stat_graph"]').eq(0).scrollIntoView().wait(3000).screenshot("testImage");
    cy.contains('Logout').click();

    const PNG = require('pngjs').PNG;
    // pixelmatch library will handle comparison
    const pixelmatch = require('pixelmatch');

    cy.readFile(
      './tests/e2e/base-reference/1baseImage.png', 'base64'
    ).then(baseImage => {
      cy.readFile(
        './tests/e2e/screenshots/dashboard/1TeacherDashboard.js/testImage.png', 'base64'
      ).then(testImage => {
				// load both pictures
        const img1 = PNG.sync.read(Buffer.from(baseImage, 'base64'));
        const img2 = PNG.sync.read(Buffer.from(testImage, 'base64'));

        const { width, height } = img1;
        const diff = new PNG({ width, height });

				// calling pixelmatch return how many pixels are different
        const numDiffPixels = pixelmatch(img1.data, img2.data, diff.data, width, height, { threshold: 0.1 });

				// calculating a percent diff
        const diffPercent = (numDiffPixels / (width * height) * 100);

        expect(diffPercent).to.be.below(20);
      });
    });
  })
})
