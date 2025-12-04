describe("Home Page", () => {
    beforeEach(() => {
        cy.visit("/");
    });

    it("should display the main heading", () => {
        cy.contains("Salamander Video Processor").should("be.visible");
    });

    it("should display the Start Processing button", () => {
        cy.contains("Start Processing").should("be.visible");
    });

    it("should navigate to videos page when clicking Start Processing", () => {
        cy.contains("Start Processing").click();
        cy.url().should("include", "/videos");
    });

    it("should display all four feature cards", () => {
        cy.contains("Upload Video").should("be.visible");
        cy.contains("Set Parameters").should("be.visible");
        cy.contains("Process").should("be.visible");
        cy.contains("Get Results").should("be.visible");
    });
});

