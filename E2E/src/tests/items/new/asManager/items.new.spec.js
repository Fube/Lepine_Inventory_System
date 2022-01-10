const { test, expect } = require("@playwright/test");
const { createItem: apiCreateItem } = require("@lepine/e2e-helpers/api/items");
const { createItem } = require("@lepine/e2e-helpers/items");
const { MANAGER_PASSWORD, MANAGER_USERNAME } = require("@lepine/e2e-config");

test.describe.parallel("LGknOhhxOS: Manager /items/new tests", async () => {
    const skuGen = new RandExp(/[a-zA-Z0-9]{1,6}/);

    const baseItem = {
        sku: skuGen.gen(),
        name: "Test item",
        description: "Test description",
    };

    const managerCredentials = {
        email: MANAGER_USERNAME,
        password: MANAGER_PASSWORD,
    };

    test("/items/new :: Go to through /items", async ({ page }) => {
        await page.goto("/items");

        // Get the "Add One Now!" button
        const tableExists = await page.$("table");
        let addOneNow = null;
        if (tableExists) {
            addOneNow = await page.$("thead tr th:last-child > button");
            expect(addOneNow).toBeTruthy();
        } else {
            addOneNow = await page.$("//html/body/div[1]/main/div/button");
            expect(addOneNow).toBeTruthy();
        }

        // Click on "Add One Now!" button
        await Promise.all([
            page.waitForNavigation({ waitUntil: "networkidle0" }),
            await addOneNow.click(),
        ]);

        // Check that the page is loaded
        const title = await page.title();
        expect(title).toBe("Add Item");

        // Check form is present
        const form = await page.$("form");
        expect(form).toBeTruthy();

        // Check form contains input fields
        const inputFields = await page.$$("form input");
        expect(inputFields.length).toBe(3);
        const inputFieldsNames = await Promise.all(
            inputFields.map((n) => n.getAttribute("name"))
        );
        expect(inputFieldsNames).toEqual(["name", "description", "sku"]);
    });

    test("/items/new :: Create item from", async ({ page }) => {
        const { uuid } = await createItem(page);

        // Clean up
        await page.evaluate(
            async (uuid) =>
                await fetch(`/api/items/${uuid}`, { method: "DELETE" }),
            uuid
        );
    });

    test.describe.parallel("With duplicate SKU setup", () => {
        const sku = skuGen.gen();
        let uuid = null;

        test.beforeAll(async ({ baseURL }) => {
            const data = await apiCreateItem({
                ...baseItem,
                sku,
            });
            uuid = data.uuid;
        });

        test.afterAll(async ({ baseURL }) => {
            await page.evaluate(
                async (uuid) =>
                    await fetch(`/api/items/${uuid}`, { method: "DELETE" }),
                uuid
            );
        });

        test("PVGlvMEslE: /items/new :: Create item with duplicate SKU", async ({
            page,
        }) => {
            // Go to /items/new
            await Promise.all([
                page.waitForFunction(
                    () => document.querySelector`title`.text === "Add Item"
                ),
                page.goto("/items/new"),
            ]);

            // Fill out form
            await page.type("form input[name=name]", baseItem.name);
            await page.type(
                "form input[name=description]",
                baseItem.description
            );
            await page.type("form input[name=sku]", sku);

            // Submit form
            await Promise.all([
                page.waitForResponse(/.*api\/items.*/i),
                page.click("form button"),
            ]);

            // Check for error
            const content = await page.content();
            expect(content).toContain(`Item with SKU ${sku} already exists`);
        });
    });
});
