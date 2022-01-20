const { expect, test } = require("@playwright/test");
const {
    READONLY_ITEM_NAME,
    READONLY_ITEM_SKU,
    READONLY_ITEM_DESCRIPTION,
    MANAGER_USERNAME,
    MANAGER_PASSWORD,
} = require("@lepine/e2e-config");
const { createItem, deleteItem } = require("@lepine/e2e-helpers/api/items");
const { clearThenType, waitForTitle } = require("@lepine/e2e-helpers/page");
const RandExp = require("randexp");

test.describe.parallel("BrLGZduqdM: Manager /items/[uuid] tests", () => {
    const toClean = new Set();
    const skuGen = new RandExp(/[a-zA-Z0-9]{1,6}/);

    let uuid = null;

    const baseItem = {
        name: READONLY_ITEM_NAME,
        sku: skuGen.gen(),
        description: READONLY_ITEM_DESCRIPTION,
    };

    const managerCredentials = {
        email: MANAGER_USERNAME,
        password: MANAGER_PASSWORD,
    };

    test.beforeEach(async ({ baseURL, page }) => {
        const data = await createItem(baseURL, managerCredentials, baseItem);
        uuid = data.uuid;
        toClean.add(uuid);

        // Go to item's page
        await Promise.all([
            waitForTitle(page, "Item Details"),
            page.goto(`/items/${uuid}`),
        ]);
    });

    test.afterEach(async ({ baseURL }) => {
        await Promise.all(
            [...toClean].map((uuid) =>
                deleteItem(baseURL, managerCredentials, uuid)
            )
        );
    });

    test("/items/:uuid :: Delete item through", async ({ page }) => {
        // Check it is the expected item
        const title = await page.title();
        expect(title).toBe("Item Details");
        const content = await page.content();
        expect(content).toContain(baseItem.name);
        expect(content).toContain(baseItem.description);
        expect(content).toContain(baseItem.sku);

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
            await search.type(baseItem.sku, { delay: 1000 });
        }

        // Check no item message is there
        expect(await page.content()).toContain("No items to show");
    });

    test("/items/:uuid :: Update item through ", async ({ page }) => {
        // Check it is the expected item
        const title = await page.title();
        expect(title).toBe("Item Details");
        const content = await page.content();
        expect(content).toContain(baseItem.name);
        expect(content).toContain(baseItem.description);
        expect(content).toContain(baseItem.sku);

        // Check save button is not clickable
        const saveBtn = page.locator("button[type=submit][disabled]");
        expect(saveBtn).toBeTruthy();

        // Dirty item
        await page.type("input[name=name]", "New Name");
        await page.type("input[name=description]", "New Description");
        await page.type("input[name=sku]", baseItem.sku);

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
        await search.type(baseItem.sku, { delay: 1000 });

        // Check item is updated
        const tr = page.locator(`tr[href="/items/${uuid}"]`);
        expect(await tr.innerText()).toContain("New Name");
        expect(await tr.innerText()).toContain("New Description");
    });

    test("/items/:uuid :: Cannot update if field empty", async ({ page }) => {
        // Check it is the expected item
        const title = await page.title();
        expect(title).toBe("Item Details");
        const content = await page.content();
        expect(content).toContain(baseItem.name);
        expect(content).toContain(baseItem.description);
        expect(content).toContain(baseItem.sku);

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
    });

    test.describe.parallel("With duplicate SKU setup", () => {
        let uuid = null;

        test.beforeAll(async ({ baseURL }) => {
            const data = await createItem(baseURL, managerCredentials, {
                ...baseItem,
                sku: skuGen.gen(),
            });
            uuid = data.uuid;
            toClean.add(uuid);
        });

        test("GUxNvMRfOP: /items/:uuid :: Update with duplicate SKU", async ({
            page,
        }) => {
            // Change to other item
            await Promise.all([
                waitForTitle(page, "Item Details"),
                page.goto(`/items/${uuid}`),
            ]);

            // Set SKU to the same as the previous item
            const skuInput = page.locator("input[name=sku]");
            await clearThenType(page, skuInput, baseItem.sku);

            // Save
            const saveBtn = page.locator("button[type=submit]");
            await Promise.all([
                page.waitForResponse(/.*api\/items.*/i),
                saveBtn.click(),
            ]);

            // Check for error
            const error = await page.content();
            expect(error).toContain(
                `Item with SKU ${baseItem.sku} already exists`
            );
        });
    });
});
