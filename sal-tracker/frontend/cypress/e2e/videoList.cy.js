// cypress/e2e/videoList.cy.js

describe("Home Page", () => {
  beforeEach(() => {
    // 🔹 Mock the backend /api/videos response
    cy.intercept("GET", "**/api/videos", {
      statusCode: 200,
      body: ["ensantinaClipped.mp4"],
    }).as("mockVideos");

    // Visit home page
    cy.visit("/");
  });

  it("should go to video list, then EnsantinaClippedPreview", () => {
    const fileName = "ensantinaClipped.mp4";

    // Click Start Processing to go to /videos
    cy.contains("Start Processing").click();
    cy.url().should("include", "/videos");

    // Wait until the mocked videos list is loaded
    cy.wait("@mockVideos");

    // Now the page should show ensantinaClipped.mp4
    cy.contains(fileName, { timeout: 8000 }).click();

    // Check that we navigated to the correct preview page
    cy.url().should("include", `preview/${fileName}`);
  });
});