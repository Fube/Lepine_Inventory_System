module.exports = {
    globalSetup: require.resolve("./tests/global-setup.js"),
    use: {
        baseURL: process.env.BASE_URL ?? "http://localhost:3000",
        storageState: "./storage/manager.json",
        viewport: { width: 1280, height: 720 },
        ignoreHTTPSErrors: true,
        screenshot: "only-on-failure",
        timeout: 5000,
    },
};
