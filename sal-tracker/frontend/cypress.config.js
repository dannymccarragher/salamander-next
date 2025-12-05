const { defineConfig } = require("cypress");

module.exports = defineConfig({
  video: false,
  screenshotOnRunFailure: true,
  e2e: {
    baseUrl: "http://localhost:3001",
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
});
