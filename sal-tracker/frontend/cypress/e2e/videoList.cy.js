describe("Home Page", () => {
  const fileName = "ensantinaClipped.mp4";

  beforeEach(() => {
    // Mock the /api/videos endpoint to return our test video
    cy.intercept("GET", "**/api/videos", {
      statusCode: 200,
      body: [fileName],
    }).as("getVideos");

    cy.visit("/");
  });

  it("should go to video list, then EnsantinaClippedPreview", () => {
    cy.contains("Start Processing").click();
    cy.url().should("include", "/videos");

    // Wait for the mocked API call to complete
    cy.wait("@getVideos");

    cy.contains(fileName).click();

    cy.url().should("include", `preview/${fileName}`);
  });
});
