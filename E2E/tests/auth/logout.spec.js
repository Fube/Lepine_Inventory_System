const { test, expect } = require("@playwright/test");

test("Logout", async ({ page }) => {
    // Go to home
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.goto("/"),
    ]);

    // Get all nav items
    const navItems = await page.$$(".navbar a");

    // Find logout
    let logout = null;
    for (const item of navItems) {
        const text = await item.innerText();
        if (text.toLowerCase() === "logout") {
            logout = item;
            break;
        }
    }
    expect(logout).toBeTruthy();

    // Click on logout
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        logout.click(),
    ]);

    // Check that we are back to home
    const title = await page.title();
    expect(title).toBe("Home");

    // Check that we are logged out
    const localStorage = await page.evaluate(() => {
        return localStorage;
    });

    expect(localStorage.email).toBeFalsy();
    expect(localStorage.role).toBeFalsy();

    const cookies = await page.context().cookies();
    expect(cookies.find((n) => n.name === "token")).toBeFalsy();
});
