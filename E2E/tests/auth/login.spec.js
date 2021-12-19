const { test, expect, chromium } = require("@playwright/test");

test.beforeEach(async ({ page, context }) => {
    await page.goto("/");
    await page.evaluate(() => {
        localStorage.clear();
    });
    await context.clearCookies();
    await page.reload();
});

test("/login :: Go to through /index", async ({ page, browser }) => {
    // Click on "Login" button
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
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
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("a[href*=login]"),
    ]);

    // Fill in form
    await page.type('input[name="email"]', process.env.USERNAME ?? "manager");
    await page.type(
        'input[name="password"]',
        process.env.PASSWORD ?? "manager"
    );

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
