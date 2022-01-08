const { expect, test } = require("@playwright/test");
const { createItem } = require("@lepine/e2e-helpers/items");

test("/items/:uuid :: Cannot update if field empty", async ({ page }) => {
    const created = await createItem(page);

    // Go to item's page
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click(`tr[href="/items/${created.uuid}"]`),
    ]);

    // Check it is the expected item
    const title = await page.title();
    expect(title).toBe("Item Details");
    const content = await page.content();
    expect(content).toContain(created.name);
    expect(content).toContain(created.description);
    expect(content).toContain(created.sku);

    // Check save button is not clickable
    const saveBtn = page.locator("button[type=submit]");
    expect(saveBtn.isDisabled()).toBeTruthy();

    // Dirty item, but with empty fields
    const nameInput = page.locator("input[name=name]");
    await nameInput.click({ clickCount: 3 });
    await page.keyboard.press("Backspace");
    await page.keyboard.press("Tab");

    // Check save button is not clickable
    expect(saveBtn.isDisabled()).toBeTruthy();

    // Check we have not changed pages
    const title2 = await page.title();
    expect(title2).toBe("Item Details");

    // Clean up
    await page.evaluate(
        async (uuid) => await fetch(`/api/items/${uuid}`, { method: "DELETE" }),
        created.uuid
    );
});
