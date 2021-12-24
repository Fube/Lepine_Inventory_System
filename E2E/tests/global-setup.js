const { chromium } = require("@playwright/test");
const { default: axios } = require("axios");
const {
    MANAGER_USERNAME,
    MANAGER_PASSWORD,
    READONLY_ITEM_NAME,
    READONLY_ITEM_SKU,
    READONLY_ITEM_DESCRIPTION,
    dynamicLoad,
} = require("config");
const { customAlphabet } = require("nanoid");

const alphaOnly = customAlphabet("abcdefghijklmnopqrstuvwxyz", 10);

module.exports = async (config) => {
    const { baseURL, storageState } = config.projects[0].use;

    const browser = await chromium.launch();
    const page = await browser.newPage();
    await page.goto(baseURL);
    await page.context().storageState({ path: "./storage/none.json" });
    await browser.close();

    const clerkLogin = await register(baseURL, "CLERK");
    await loginAndSave(baseURL, clerkLogin, "./storage/clerk.json");

    await loginAndSave(
        baseURL,
        { email: MANAGER_USERNAME, password: MANAGER_PASSWORD },
        storageState
    );
};

async function register(baseURL, role) {
    const clerkLogin = {
        email: `${alphaOnly()}@example.com`,
        password: "St0ngPa$$w0rd",
    };

    const { headers } = await axios.post(`${baseURL}/api/auth/login`, {
        email: MANAGER_USERNAME,
        password: MANAGER_PASSWORD,
    });

    // Register a new clerk
    await axios.post(
        `${baseURL}/api/users`,
        { ...clerkLogin, role },
        {
            headers: {
                cookie: headers["set-cookie"][0],
            },
        }
    );

    return clerkLogin;
}

async function loginAndSave(baseURL, { email, password }, storagePath) {
    const browser = await chromium.launch();
    const page = await browser.newPage();
    await page.goto(baseURL);

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("a[href*=login]"),
    ]);

    await page.type('input[name="email"]', email);
    await page.type('input[name="password"]', password);

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("button[type=submit]"),
    ]);

    await page.context().storageState({ path: storagePath });

    await browser.close();
}
