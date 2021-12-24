const { expect, test } = require("@playwright/test");
const {
    READONLY_ITEM_NAME,
    READONLY_ITEM_SKU,
    READONLY_ITEM_DESCRIPTION,
    dynamicLoad,
} = require("config");

const targetItem = {
    name: "",
    description: "",
    sku: "",
};

test.describe.parallel("fIvHGIlnwi: Clerk /items tests", () => {
    test.use({
        storageState: "./storage/clerk.json",
    });

    test("hpgBHJdpxb: /items/:uuid :: View as clerk", async ({ page }) => {
        while (!dynamicLoad.get("READONLY_ITEM_UUID")) {
            console.log(require("config").dynamicLoad);
            await page.waitForTimeout(1000);
        }
        const READONLY_ITEM_UUID = dynamicLoad.get("READONLY_ITEM_UUID");

        // Go to /items/:uuid
        await Promise.all([
            page.waitForSelector("form"),
            page.goto(`/items/${READONLY_ITEM_UUID}`),
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
