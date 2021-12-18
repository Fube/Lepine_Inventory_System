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
        page.click("[href*=login]"),
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
