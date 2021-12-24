const { expect, test } = require("@playwright/test");

test.describe.parallel("sTGOgmDVws: Clerk /items/new tests", () => {
    test.use({
        storageState: "./storage/clerk.json",
    });

    test("mmDClHtrKN: /items/new :: View as clerk", async ({ page }) => {
        // Go to /items/new
        await Promise.all([
            page.waitForNavigation({ waitUntil: "networkidle0" }),
            page.goto(`/items/new`),
        ]);

        // Check redirected to /login
        const url = page.url();
        expect(url).toContain("/login");
    });
});
