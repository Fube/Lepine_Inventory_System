const { chromium } = require("@playwright/test");
module.exports = async (config) => {
    const { baseURL, storageState } = config.projects[0].use;
    const browser = await chromium.launch({
        headless: false,
    });
    const page = await browser.newPage();
    await page.goto(baseURL);

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("[href*=login]"),
    ]);

    await page.type('input[name="email"]', process.env.USERNAME ?? "manager");
    await page.type(
        'input[name="password"]',
        process.env.PASSWORD ?? "manager"
    );

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("button[type=submit]"),
    ]);

    await page.context().storageState({ path: storageState });

    await browser.close();
};
