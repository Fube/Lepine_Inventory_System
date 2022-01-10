const { expect, test } = require("@playwright/test");
const RandExp = require("randexp");
const {
    deleteWarehouse,
    createWarehouse,
} = require("@lepine/e2e-helpers/api/warehouses");
const {
    READONLY_WAREHOUSE_ZIP_CODE,
    MANAGER_USERNAME,
    MANAGER_PASSWORD,
    READONLY_WAREHOUSE_CITY,
    READONLY_WAREHOUSE_PROVINCE,
} = require("@lepine/e2e-config");
const { clearThenType } = require("@lepine/e2e-helpers/page");

test.describe.parallel("rJrgbjJUwU: Manager /warehouses/new tests", () => {
    const toClean = new Set();

    const zipGen = new RandExp(/([A-Z][0-9]){3}/);

    const zipCode = zipGen.gen();

    const managerCredentials = {
        email: MANAGER_USERNAME,
        password: MANAGER_PASSWORD,
    };

    test.afterAll(async ({ baseURL }) => {
        await Promise.all(
            [...toClean].map((uuid) =>
                deleteWarehouse(baseURL, managerCredentials, uuid)
            )
        );
    });

    test("PUlsdrEQvy: /warehouses/new :: Go to through /warehouses as manager", async ({
        page,
    }) => {
        // Go to /warehouses
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouses"
            ),
            page.goto("/warehouses"),
        ]);

        // Find the button to create a new warehouse
        const addNewButton = page.locator(
            '[href*="warehouses/new"] >> visible=true'
        );
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Create Warehouse"
            ),
            addNewButton.click(),
        ]);

        // Check form is displayed
        const form = await page.$("form");
        expect(form).toBeTruthy();

        // Check form fields are displayed
        const zipCodeField = await page.$('input[name="zipCode"]');
        const cityField = await page.$('input[name="city"]');
        const provinceField = await page.$('input[name="province"]');
        expect(zipCodeField).toBeTruthy();
        expect(cityField).toBeTruthy();
        expect(provinceField).toBeTruthy();

        // Check 'active' toggle is not there
        const activeToggle = await page.$('input[name="active"]');
        expect(activeToggle).toBeFalsy();

        // Check GMaps alternative is there
        const gmapsAlternative = await page.$("input:not([name])");
        expect(gmapsAlternative).toBeTruthy();

        // Check 'Save' button is there
        const saveButton = await page.$('[type="submit"]');
        expect(saveButton).toBeTruthy();
    });

    test("RnBjGFFnKQ: /warehouses/new :: Create a new warehouse as manager", async ({
        page,
    }) => {
        // Go to /warehouses/new
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`?.text === "Create Warehouse"
            ),
            page.goto("/warehouses/new"),
        ]);

        // Use GMaps alternative to fill form
        const gmapsLocator = page.locator("input[type=text]:not([name])");
        await gmapsLocator.type(READONLY_WAREHOUSE_ZIP_CODE, { delay: 500 }); // To ensure valid zip code
        await Promise.all([
            page.waitForResponse(/.*maps\.googleapis.*/i),
            page.keyboard.press("Enter"),
        ]);
        await page.waitForTimeout(250);

        // Check all fields are filled out
        const zipCodeLocator = page.locator('input[name="zipCode"]');
        const cityLocator = page.locator('input[name="city"]');
        const provinceLocator = page.locator('input[name="province"]');
        expect((await zipCodeLocator.inputValue()).replace(" ", "")).toBe(
            READONLY_WAREHOUSE_ZIP_CODE.replace(" ", "")
        );
        expect((await cityLocator.inputValue()).length).toBeGreaterThan(0);
        expect((await provinceLocator.inputValue()).length).toBeGreaterThan(0);

        // Swap out zipCode to avoid clashes
        await clearThenType(page, zipCodeLocator, zipCode);

        page.addListener("response", async (res) => {
            if (
                res.request().method().toLowerCase() == "post" &&
                res.url().includes("api/warehouse")
            ) {
                const { uuid } = await res.json();
                toClean.add(uuid);
            }
        });

        // Click save
        const saveButton = page.locator('[type="submit"]');
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouses"
            ),
            saveButton.click(),
        ]);
    });

    test("IqfotWuott: /warehouses/new :: Create a new warehouse as manager with duplicate zipCode", async ({
        page,
        baseURL,
    }) => {
        const preZip = zipGen.gen();
        const { uuid: preUuid } = await createWarehouse(
            baseURL,
            managerCredentials,
            {
                zipCode: preZip,
                city: READONLY_WAREHOUSE_CITY,
                province: READONLY_WAREHOUSE_PROVINCE,
            }
        );
        toClean.add(preUuid);

        // Hook listener for clean up
        page.addListener("response", async (res) => {
            if (
                res.request().method().toLowerCase() == "post" &&
                res.ok() &&
                res.url().includes("api/warehouse")
            ) {
                const { uuid } = await res.json();
                toClean.add(uuid);
            }
        });

        // Go to /warehouses/new
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`?.text === "Create Warehouse"
            ),
            page.goto("/warehouses/new"),
        ]);

        // Fill out form
        const zipCodeLocator = page.locator('input[name="zipCode"]');
        const cityLocator = page.locator('input[name="city"]');
        const provinceLocator = page.locator('input[name="province"]');

        await zipCodeLocator.type(preZip);
        await cityLocator.type(READONLY_WAREHOUSE_CITY);
        await provinceLocator.type(READONLY_WAREHOUSE_PROVINCE);

        // Click save
        const saveButton = page.locator('[type="submit"]');
        await Promise.all([
            page.waitForResponse(/.api\/warehouse.*/i),
            saveButton.click(),
        ]);

        // Check error message
        const errorMessage = page.locator(".text-red-500");
        expect(await errorMessage.textContent()).toBe(
            `Zipcode ${preZip} already in use`
        );
    });
});
