const { test, expect } = require("@playwright/test");
const { MANAGER_USERNAME, MANAGER_PASSWORD } = require("config");

test.describe.parallel("Login tests", () => {
    test.use({
        storageState: "./storage/none.json",
    });

    test.beforeEach(async ({ page }) => {
        await page.goto("/");
    });

    test("/login :: Go to through /index", async ({ page, browser }) => {
        // Go to home
        await Promise.all([
            page.waitForNavigation({ waitUntil: "networkidle" }),
            page.goto("/"),
        ]);

        // Click on "Login" button
        await Promise.all([
            page.waitForSelector("form"),
            page.click("a[href*=login]"),
        ]);

        // Check that the page is loaded
        const title = await page.title();
        expect(title).toBe("Login");

        // Check form is present
        const form = await page.$("form");
        expect(form).toBeTruthy();

        // Check form contains input fields
        const inputFields = await page.$$("form input");
        expect(inputFields.length).toBe(2);

        const inputFieldsNames = await Promise.all(
            inputFields.map((n) => n.getAttribute("name"))
        );
        expect(inputFieldsNames).toContain("email");
        expect(inputFieldsNames).toContain("password");
    });

    test("/login :: Login with valid information", async ({ page }) => {
        // Goto login
        await Promise.all([
            page.waitForSelector("form"),
            page.click("a[href*=login]"),
        ]);

        // Fill in form
        const loginTitle = await page.title();
        expect(loginTitle).toBe("Login");
        await page.type('input[name="email"]', MANAGER_USERNAME);
        await page.type('input[name="password"]', MANAGER_PASSWORD);

        // Submit form
        await Promise.all([
            page.waitForNavigation({ waitUntil: "networkidle0" }),
            page.click("button[type=submit]"),
        ]);

        // Check that the page is loaded
        const title = await page.title();
        expect(title).toBe("Home");

        // Check that the user is logged in
        const localStorage = await page.evaluate(() => {
            return localStorage;
        });

        expect(localStorage.email).toBeTruthy();
        expect(localStorage.role).toBeTruthy();

        const cookies = await page.context().cookies();
        expect(cookies.find((n) => n.name === "token")).toBeTruthy();
    });
});
