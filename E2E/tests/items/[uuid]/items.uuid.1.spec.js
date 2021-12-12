const { expect, test } = require("@playwright/test");
const { createItem } = require("../helpers");

test("/items/:uuid :: Delete item through", async ({ page }) => {
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
    const saveBtn = await page.locator("button[type=submit][disabled]");
    expect(saveBtn).toBeTruthy();

    // Check delete button is clickable
    const deleteBtn = await page.locator(
        "form button[type=button]:not([disabled])"
    );
    expect(deleteBtn).toBeTruthy();

    // Click on delete button
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        deleteBtn.click(),
    ]);

    // Check we are back on the items page
    const title2 = await page.title();
    expect(title2).toBe("Items");

    // Check item is not present when searching
    const table = await page.$("table");

    if (table) {
        const search = await page.locator("input[type=search]");
        await search.type(created.sku, { delay: 1000 });
    }

    // Check no item message is there
    expect(await page.content()).toContain("No items to show");
});
