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
