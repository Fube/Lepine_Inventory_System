module.exports = {
    MANAGER_USERNAME: process.env.MANAGER_USERNAME ?? "manager",
    MANAGER_PASSWORD: process.env.MANAGER_PASSWORD ?? "manager",
    CLERK_USERNAME: process.env.CLERK_USERNAME,
    CLERK_PASSWORD: process.env.CLERK_PASSWORD,
    READONLY_ITEM_NAME: process.env.READONLY_ITEM_NAME ?? "SomeItem",
    READONLY_ITEM_SKU: process.env.READONLY_ITEM_SKU ?? "SomeSKU",
    READONLY_ITEM_DESCRIPTION: process.env.READONLY_ITEM_DESCRIPTION ?? "SomeDescription",
    READONLY_WAREHOUSE_ZIP_CODE: process.env.READONLY_WAREHOUSE_ZIP_CODE ?? "A1B2C3",
    READONLY_WAREHOUSE_CITY: process.env.READONLY_WAREHOUSE_CITY ?? "Laval",
    READONLY_WAREHOUSE_PROVINCE: process.env.READONLY_WAREHOUSE_PROVINCE ?? "Quebec",
    READONLY_USER_EMAIL: process.env.READONLY_USER_EMAIL ?? "some@email.com",
    READONLY_USER_PASSWORD: process.env.READONLY_USER_PASSWORD ?? "S0m3P@ssw0rd",

};
