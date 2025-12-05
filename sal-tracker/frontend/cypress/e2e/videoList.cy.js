describe("Home Page", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  // Prevent test from not running when hydration error thrown
  Cypress.on("uncaught:exception", (err) => {
    if (err.message.includes("Hydration failed")) {
      return false;
    }
  });

  it("should go to video list, then EnsantinaClippedPreview", () => {
    const fileName = "ensantinaClipped.mp4";
    cy.contains("Start Processing").click();
    cy.url().should("include", "/videos");

    cy.contains(fileName).click();

    cy.url().should("include", `preview/${fileName}`);
  });
});
