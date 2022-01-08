const { expect, test } = require("@playwright/test");

test.describe.parallel("hNDKKdIIjM: Manager /warehouses tests", () => {
    let uuid = null;

    test.beforeAll(async ({ baseURL }) => {
        const data = createWarehouse(
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
});
