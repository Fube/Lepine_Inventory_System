const { expect, test } = require("@playwright/test");
const RandExp = require("randexp");
const {
    createWarehouse,
    deleteWarehouse,
} = require("@lepine/e2e-helpers/api/warehouses");
const {
    READONLY_WAREHOUSE_CITY,
    READONLY_WAREHOUSE_PROVINCE,
    MANAGER_USERNAME,
    MANAGER_PASSWORD,
} = require("@lepine/e2e-config");

test.describe.parallel("RgtXQeVKVM: Manager /warehouses/[uuid] tests", () => {
    let uuid = null,
        uuidForDelete = null;
    const toClean = new Set();

    const zipGen = new RandExp(/([A-Z][0-9]){3}/);
    const zipCode = zipGen.gen();
    const zipCodeForDelete = zipGen.gen();

    const baseWarehouse = {
        zipCode,
        city: READONLY_WAREHOUSE_CITY,
        province: READONLY_WAREHOUSE_PROVINCE,
    };
    const managerCredentials = {
        email: MANAGER_USERNAME,
        password: MANAGER_PASSWORD,
    };

    test.beforeAll(async ({ baseURL }) => {
        const [data, dataForDelete] = await Promise.all([
            createWarehouse(baseURL, managerCredentials, baseWarehouse),
            createWarehouse(baseURL, managerCredentials, {
                ...baseWarehouse,
                zipCode: zipCodeForDelete,
            }),
        ]);
        uuid = data.uuid;
        uuidForDelete = dataForDelete.uuid;
        toClean.add(uuid);
        toClean.add(uuidForDelete);
    });

    test.afterAll(async ({ baseURL }) => {
        await Promise.all(
            [...toClean].map((uuid) =>
                deleteWarehouse(baseURL, managerCredentials, uuid)
            )
        );
    });

    test("ecyfhswOhq: /warehouses/[uuid] :: Go to through /warehouses as manager", async ({
        page,
    }) => {
        // Go to /warehouses
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouses"
            ),
            page.goto("/warehouses"),
        ]);

        // Find warehouse
        let warehouse = null;
        do {
            warehouse = await page.$(`tr[href*="${uuid}"]`);
            if (warehouse) break;

            // Go to next page
            const nextBtn = await page.$(
                "table + div button:last-of-type:not([disabled])"
            );
            if (!nextBtn) break;

            await Promise.all([
                page.waitForNavigation({ waitUntil: "networkidle" }),
                nextBtn.click(),
            ]);
        } while (!warehouse);

        expect(warehouse).toBeTruthy();

        // Click on warehouse
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouse Details"
            ),
            warehouse.click(),
        ]);

        // Check it is the expected page
        const title = await page.title();
        expect(title).toBe("Warehouse Details");

        // Check that the page contains the warehouse we created
        const content = await page.content();
        expect(content).toContain(zipCode);
        expect(content).toContain(READONLY_WAREHOUSE_CITY);
        expect(content).toContain(READONLY_WAREHOUSE_PROVINCE);
        const active = page.locator("[name=active]");
        expect(await active.inputValue()).toBe("true");

        // Check save is disabled
        const saveBtn = page.locator("button[type=submit]");
        expect(await saveBtn.isDisabled()).toBe(true);

        // Check delete is enabled
        const deleteBtn = page.locator("* button:last-of-type");
        expect(await deleteBtn.isEnabled()).toBe(true);
    });

    test("XxidqKpVfB: /warehouses/[uuid] :: Delete warehouse", async ({
        page,
    }) => {
        // Go to /warehouses
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouse Details"
            ),
            page.goto(`/warehouses/${uuidForDelete}`),
        ]);

        // Click delete
        const deleteBtn = page.locator("* button:last-of-type");
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouses"
            ),
            deleteBtn.click(),
        ]);

        // Remove from toClean
        toClean.delete(uuidForDelete);
    });
});
