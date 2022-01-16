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
const { clearThenType } = require("@lepine/e2e-helpers/page");

test.describe.parallel("RgtXQeVKVM: Manager /warehouses/[uuid] tests", () => {
    let uuid = null;
    let zipCode = null;
    const toClean = new Set();

    const zipGen = new RandExp(/([A-Z][0-9]){3}/);
    const cityGen = new RandExp(/[a-zA-Z]{1,10}/);
    const provinceGen = new RandExp(/[a-zA-Z]{1,10}/);

    const baseWarehouse = {
        city: READONLY_WAREHOUSE_CITY,
        province: READONLY_WAREHOUSE_PROVINCE,
    };
    const managerCredentials = {
        email: MANAGER_USERNAME,
        password: MANAGER_PASSWORD,
    };

    test.beforeEach(async ({ baseURL }) => {
        const zip = zipGen.gen();
        const data = await createWarehouse(baseURL, managerCredentials, {
            ...baseWarehouse,
            zipCode: zip,
        });
        uuid = data.uuid;
        zipCode = zip;

        toClean.add(uuid);
    });

    test.afterEach(async ({ baseURL }) => {
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
            page.goto(`/warehouses/${uuid}`),
        ]);

        // Click delete
        const deleteBtn = page.locator("* button:last-of-type");
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouses"
            ),
            deleteBtn.click(),
        ]);
    });

    test("OxZrIinseB: /warehouses/[uuid] :: Update warehouse", async ({
        page,
    }) => {
        // Go to /warehouses
        await Promise.all([
            page.waitForFunction(
                () =>
                    document?.querySelector("title")?.text ===
                    "Warehouse Details"
            ),
            page.goto(`/warehouses/${uuid}`),
        ]);

        // Update zipCode
        const zipCodeInput = page.locator("[name=zipCode]");
        const updatedZip = zipGen.gen();
        await clearThenType(page, zipCodeInput, updatedZip);

        // Update city
        const cityInput = page.locator("[name=city]");
        const updatedCity = cityGen.gen();
        await clearThenType(page, cityInput, updatedCity);

        // Update province
        const provinceInput = page.locator("[name=province]");
        const updatedProvince = provinceGen.gen();
        await clearThenType(page, provinceInput, updatedProvince);

        // Update active
        const activeInput = page.locator("[name=active]");
        const oldValue = await activeInput.isChecked();
        await activeInput.click();

        // Check save is enabled
        const saveBtn = page.locator("button[type=submit]");
        expect(await saveBtn.isEnabled()).toBe(true);

        // Click save
        await Promise.all([
            page.waitForFunction(
                () => document?.querySelector("title")?.text === "Warehouses"
            ),
            saveBtn.click(),
        ]);

        // Check it is the expected page
        const title = await page.title();
        expect(title).toBe("Warehouses");

        // Go back to warehouse
        await Promise.all([
            page.waitForFunction(
                () =>
                    document?.querySelector("title")?.text ===
                    "Warehouse Details"
            ),
            page.goto(`/warehouses/${uuid}`),
        ]);

        // Check that the page contains the warehouse we created
        expect(await zipCodeInput.inputValue()).toEqual(updatedZip);
        expect(await cityInput.inputValue()).toEqual(updatedCity);
        expect(await provinceInput.inputValue()).toEqual(updatedProvince);
        expect(await activeInput.isChecked()).toEqual(!oldValue);
    });

    test.describe.parallel("Dual warehouse setup", () => {
        let preUuid = null;
        let preZip = null;

        test.beforeEach(async ({ baseURL }) => {
            preZip = zipGen.gen();
            const preCity = cityGen.gen();
            const preProvince = provinceGen.gen();
            const { uuid } = await createWarehouse(
                baseURL,
                managerCredentials,
                {
                    zipCode: preZip,
                    city: preCity,
                    province: preProvince,
                }
            );

            preUuid = uuid;

            toClean.add(uuid);
        });

        test("dcaVQOHILy: /warehouses/[uuid] :: Update warehouse duplicate zipCode", async ({
            page,
            baseURL,
        }) => {
            // Go to /warehouses/[uuid]
            await Promise.all([
                page.waitForFunction(
                    () =>
                        document.querySelector`title`.text ===
                        "Warehouse Details"
                ),
                page.goto(`/warehouses/${preUuid}`),
            ]);

            // Update zipCode
            const zipCodeInput = page.locator("[name=zipCode]");
            await clearThenType(page, zipCodeInput, zipCode);

            // Try to save
            const saveBtn = page.locator("button[type=submit]");
            await Promise.all([
                page.waitForResponse(/.*api\/warehouse.*/i),
                saveBtn.click(),
            ]);

            // Check still on the same page
            const title = await page.title();
            expect(title).toBe("Warehouse Details");

            // Check for error
            const error = await page.content();
            expect(error).toContain(`Zipcode ${zipCode} already in use`);
        });
    });
});
