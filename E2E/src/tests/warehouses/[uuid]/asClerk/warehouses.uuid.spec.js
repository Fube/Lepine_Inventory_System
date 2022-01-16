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

test.describe.parallel("WQWEbbEeBW: Clerk /warehouses/[uuid] tests", () => {
    test.use({
        storageState: "./storage/clerk.json",
    });

    let uuid = null;
    const toClean = new Set();

    const zipGen = new RandExp(/([A-Z][0-9]){3}/);
    const zipCode = zipGen.gen();

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
        const data = await createWarehouse(
            baseURL,
            managerCredentials,
            baseWarehouse
        );
        uuid = data.uuid;
        toClean.add(uuid);
    });

    test.afterAll(async ({ baseURL }) => {
        await Promise.all(
            [...toClean].map((uuid) =>
                deleteWarehouse(baseURL, managerCredentials, uuid)
            )
        );
    });

    test("OEVjicoOex: /warehouses/[uuid] :: Go to through /warehouses as clerk", async ({
        page,
    }) => {
        // Go to /warehouses
        await Promise.all([
            page.waitForFunction(
                () => document?.querySelector("title")?.text === "Warehouses"
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
                page.waitForResponse(/.*items.*/i),
                nextBtn.click(),
            ]);
        } while (!warehouse);

        expect(warehouse).toBeTruthy();

        // Click on warehouse
        await Promise.all([
            page.waitForFunction(
                () =>
                    document?.querySelector("title")?.text ===
                    "Warehouse Details"
            ),
            warehouse.click(),
        ]);

        // Check it is the expected page
        const title = await page.title();
        expect(title).toBe("Warehouse Details");

        // Check that the page contains the warehouse we created
        const zipCodeInput = page.locator("input[name=zipCode]");
        const cityInput = page.locator("input[name=city]");
        const provinceInput = page.locator("input[name=province]");
        const activeInput = page.locator("[name=active]");
        expect(await activeInput.inputValue()).toBe("true");

        // Check save is not there
        const saveBtn = await page.$("button[type=submit]");
        expect(saveBtn).toBeFalsy();

        // Check delete is not there
        const deleteBtn = await page.$("* button:last-of-type");
        expect(deleteBtn).toBeFalsy();

        // Check fields are disabled
        expect(await zipCodeInput.isEnabled()).toBe(false);
        expect(await cityInput.isEnabled()).toBe(false);
        expect(await provinceInput.isEnabled()).toBe(false);
        expect(await activeInput.isEnabled()).toBe(false);
    });
});
