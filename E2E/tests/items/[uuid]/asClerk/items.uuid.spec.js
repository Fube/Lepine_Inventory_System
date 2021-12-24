const { expect, test } = require("@playwright/test");
const {
    READONLY_ITEM_NAME,
    READONLY_ITEM_SKU,
    READONLY_ITEM_DESCRIPTION,
    MANAGER_USERNAME,
    MANAGER_PASSWORD,
} = require("config");
const { createItem, deleteItem } = require("helpers/api/items");

test.describe.parallel("fIvHGIlnwi: Clerk /items tests", () => {
    test.use({
        storageState: "./storage/clerk.json",
    });

    let uuid = null;

    test.beforeAll(async ({ baseURL }) => {
        const data = await createItem(
            baseURL,
            {
                email: MANAGER_USERNAME,
                password: MANAGER_PASSWORD,
            },
            {
                name: READONLY_ITEM_NAME,
                sku: READONLY_ITEM_SKU,
                description: READONLY_ITEM_DESCRIPTION,
            }
        );
        uuid = data.uuid;
    });

    test.afterAll(async ({ baseURL }) => {
        await deleteItem(
            baseURL,
            {
                email: MANAGER_USERNAME,
                password: MANAGER_PASSWORD,
            },
            uuid
        );
    });

    test("hpgBHJdpxb: /items/:uuid :: View as clerk", async ({ page }) => {
        // Go to /items/:uuid
        await Promise.all([
            page.waitForSelector("form"),
            page.goto(`/items/${uuid}`),
        ]);

        // Check it is the expected item
        const title = await page.title();
        expect(title).toBe("Item Details");
        const content = await page.content();
        expect(content).toContain(READONLY_ITEM_NAME);
        expect(content).toContain(READONLY_ITEM_DESCRIPTION);
        expect(content).toContain(READONLY_ITEM_SKU);

        // Check there is no save button
        const saveBtn = await page.$("button[type=submit]");
        expect(saveBtn).toBeFalsy();

        // Check there is no delete button
        const deleteBtn = await page.$("form button[type=button]");
        expect(deleteBtn).toBeFalsy();

        // Check all fields are not editable
        const fields = await page.$$("input[type=text][disabled]");
        expect(fields.length).toBe(3);
    });
});
