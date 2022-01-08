const { expect, test } = require("@playwright/test");
const { createWarehouse, deleteWarehouse } = require("helpers/api/warehouses");
const {
    READONLY_WAREHOUSE_ZIP_CODE,
    READONLY_WAREHOUSE_CITY,
    READONLY_WAREHOUSE_PROVINCE,
    MANAGER_USERNAME,
    MANAGER_PASSWORD,
} = require("config");

test.describe.parallel("hNDKKdIIjM: Manager /warehouses tests", () => {
    let uuid = null;

    test.beforeAll(async ({ baseURL }) => {
        const data = await createWarehouse(
            baseURL,
            {
                email: MANAGER_USERNAME,
                password: MANAGER_PASSWORD,
            },
            {
                zipCode: READONLY_WAREHOUSE_ZIP_CODE,
                city: READONLY_WAREHOUSE_CITY,
                province: READONLY_WAREHOUSE_PROVINCE,
            }
        );
        uuid = data.uuid;
    });

    test.afterAll(async ({ baseURL }) => {
        await deleteWarehouse(
            baseURL,
            {
                email: MANAGER_USERNAME,
                password: MANAGER_PASSWORD,
            },
            uuid
        );
    });

    test("CiTqmVQqYd: /warehouses :: Go to through /index as manager", async ({
        page,
    }) => {
        // Go to home
        await Promise.all([
            page.waitForNavigation({ waitUntil: "networkidle" }),
            page.goto("/"),
        ]);

        // Click on "Warehouses" button
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouses"
            ),
            page.click("a[href*=warehouses]"),
        ]);

        // Check it is the expected page
        const title = await page.title();
        expect(title).toBe("Warehouses");

        // Check that the page contains a table or a message saying there are no warehouses
        const table = await page.$("table");
        if (table) {
            const header = await page.$eval(
                "table thead tr",
                (el) => el.innerText
            );
            expect(header).toBeTruthy();
            expect(header.toLowerCase()).toContain("zip code");
            expect(header.toLowerCase()).toContain("city");
            expect(header.toLowerCase()).toContain("province");
            expect(header.toLowerCase()).toContain("active");

            // Check that the table contains a row
            const rows = await page.$$("table tbody tr");
            expect(rows.length).toBeGreaterThan(0);

            // Check that the table contains a row with the warehouse we created
            let has = false;
            do {
                const content = await page.content();
                if (content.includes(READONLY_WAREHOUSE_ZIP_CODE)) {
                    has = true;
                    break;
                }

                // Go to next page
                const nextBtn = await page.$(
                    "table + div button:last-of-type:not([disabled])"
                );
                if (!nextBtn) break;

                await Promise.all([
                    page.waitForNavigation({ waitUntil: "networkidle" }),
                    nextBtn.click(),
                ]);
            } while (!has);

            expect(has).toBe(true);
        } else {
            const message = await page.$eval("h2", (n) => n.innerText);
            expect(message).toBeTruthy();
            expect(message.toLowerCase()).toContain("no warehouses");
        }
    });
});
