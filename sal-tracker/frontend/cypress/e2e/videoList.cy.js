describe("Home Page", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("should go to video list, then EnsantinaClippedPreview", () => {
    const fileName = "ensantinaClipped.mp4";

    cy.contains("Start Processing").click();
    cy.url().should("include", "/videos");

    // Ensure videos have loaded
    cy.contains(fileName, { timeout: 8000 }).click();

    cy.url().should("include", `preview/${fileName}`);
  });
});
