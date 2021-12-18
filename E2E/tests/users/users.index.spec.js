const { test, expect } = require("@playwright/test");

test("/users :: Go to from /index", async ({ page, browser }) => {
    // Go to home
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.goto("/"),
    ]);

    // Click on "Users" button
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("[href*=users]"),
    ]);

    // Check that the page is loaded
    const title = await page.title();
    expect(title).toBe("Users");

    // Check that the page contains a table or a message saying that there are no users
    const table = await page.$("table");
    if (table) {
        // Check that the table contains a header
        const header = await page.$eval("table thead tr", (n) => n.innerText);
        expect(header).toBeTruthy();
        expect(header.toLowerCase()).toContain("email");
        expect(header.toLowerCase()).toContain("role");

        // Check that the table contains a row
        const rows = await page.$$("table tbody tr");
        expect(rows.length).toBeGreaterThan(0);
    } else {
        const message = await page.$eval("h2", (n) => n.innerText);
        expect(message).toBeTruthy();
        expect(message.toLowerCase()).toContain("no users");
    }
});
