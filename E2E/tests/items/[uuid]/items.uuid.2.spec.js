const { expect, test } = require("@playwright/test");
const { createItem } = require("../helpers");

test("/items/:uuid :: Update item through ", async ({ page }) => {
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
    const saveBtn = page.locator("button[type=submit][disabled]");
    expect(saveBtn).toBeTruthy();

    // Dirty item
    await page.type("input[name=name]", "New Name");
    await page.type("input[name=description]", "New Description");
    await page.type("input[name=sku]", created.sku);

    // Check save button is clickable
    const saveBtn2 = page.locator("button[type=submit]:not([disabled])");
    expect(saveBtn2).toBeTruthy();

    // Click on save button
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        saveBtn2.click(),
    ]);

    // Check we are back on the items page
    const title2 = await page.title();
    expect(title2).toBe("Items");

    // Check item is present when searching
    const search = page.locator("input[type=search]");
    await search.type(created.sku, { delay: 1000 });

    // Check item is updated
    const tr = page.locator(`tr[href="/items/${created.uuid}"]`);
    expect(await tr.innerText()).toContain("New Name");
    expect(await tr.innerText()).toContain("New Description");

    // Clean up
    await page.evaluate(
        async (uuid) => await fetch(`/api/items/${uuid}`, { method: "DELETE" }),
        created.uuid
    );
});
