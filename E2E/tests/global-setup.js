const { chromium } = require("@playwright/test");
const { MANAGER_USERNAME, MANAGER_PASSWORD } = require("config");

module.exports = async (config) => {
    const { baseURL, storageState } = config.projects[0].use;
    const browser = await chromium.launch();
    const page = await browser.newPage();
    await page.goto(baseURL);

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("a[href*=login]"),
    ]);

    await page.type('input[name="email"]', MANAGER_USERNAME);
    await page.type('input[name="password"]', MANAGER_PASSWORD);

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("button[type=submit]"),
    ]);

    await page.context().storageState({ path: storageState });

    await browser.close();
};
