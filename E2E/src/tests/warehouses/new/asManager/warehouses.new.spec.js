const { expect, test } = require("@playwright/test");
const RandExp = require("randexp");
const { deleteWarehouse } = require("@lepine/e2e-helpers/api/warehouses");
const {
    READONLY_WAREHOUSE_ZIP_CODE,
    MANAGER_USERNAME,
    MANAGER_PASSWORD,
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
        const addNewButton = page.locator('[href*="warehouses/new"]');
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

        // Use GMaps alternative to fill form
        const gmapsLocator = page.locator("input:not([name])");
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
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouses"
            ),
            saveButton.click(),
        ]);
    });
});
