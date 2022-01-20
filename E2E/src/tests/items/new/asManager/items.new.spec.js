const { test, expect } = require("@playwright/test");
const {
    createItem: apiCreateItem,
    deleteItem: apiDeleteItem,
} = require("@lepine/e2e-helpers/api/items");
const { createItem } = require("@lepine/e2e-helpers/items");
const { MANAGER_PASSWORD, MANAGER_USERNAME } = require("@lepine/e2e-config");
const RandExp = require("randexp");
const { waitForTitle } = require("@lepine/e2e-helpers/page");

test.describe.parallel("LGknOhhxOS: Manager /items/new tests", async () => {
    const toClean = new Set();
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

    test.afterAll(async ({ baseURL }) => {
        await Promise.all(
            [...toClean].map((uuid) =>
                apiDeleteItem(baseURL, managerCredentials, uuid)
            )
        );
    });

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
        toClean.add(uuid);
    });

    test.describe.parallel("With duplicate SKU", () => {
        const sku = skuGen.gen();
        let uuid = null;

        test.beforeAll(async ({ baseURL }) => {
            const data = await apiCreateItem(baseURL, managerCredentials, {
                ...baseItem,
                sku,
            });
            uuid = data.uuid;
            toClean.add(uuid);
        });

        test("PVGlvMEslE: /items/new :: Create item with duplicate SKU", async ({
            page,
        }) => {
            // Go to /items/new
            await Promise.all([
                waitForTitle(page, "Add Item"),
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
