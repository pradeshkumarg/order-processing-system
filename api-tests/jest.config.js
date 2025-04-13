module.exports = {
  // Tell Jest to only run our k8s-tests.test.js file
  testMatch: ['**/k8s-tests.test.js'],
  // Increase the test timeout
  testTimeout: 30000,
  // Verbose output
  verbose: true
};
