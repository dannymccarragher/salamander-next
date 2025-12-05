// ***********************************************************
// This file is processed and loaded automatically before your test files.
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
// import './commands'

// Ignore Next.js hydration errors during tests
Cypress.on("uncaught:exception", (err) => {
    if (err.message.includes("Hydration failed")) {
      return false;
    }
});