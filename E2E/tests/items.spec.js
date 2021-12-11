const { test, expect } = require("@playwright/test");

test("Go to /items through nav", async ({ page }) => {
    await page.goto("/");
    // Click on ITEMS XPath and wait for the page to load
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        await page.click("//html/body/div[1]/div[1]/div[2]/div/a"),
    ]);

    // Check that the page is loaded
    const title = await page.title();
    expect(title).toBe("Items");

    // Check that the page has the correct content
    const content = await page.content();
    expect(content).toContain("No items to show");
    expect(content).toContain("Add One Now!");

    // Check "Items" in nav is active
    const navItemsActive = await page.$$(
        "html > body > div > div  a.btn.btn-ghost.text-blue-400[href]"
    );

    expect(navItemsActive.length).toBe(1);
    const active = await navItemsActive[0];
    expect(await active.textContent()).toBe("Items");
    expect(await active.getAttribute("href")).toBe("/items");
});
