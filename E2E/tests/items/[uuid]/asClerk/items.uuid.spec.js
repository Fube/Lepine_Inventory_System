const { expect, test } = require("@playwright/test");

const targetItem = {
    name: "",
    description: "",
    sku: "",
};

test.describe.parallel("fIvHGIlnwi: Clerk /items tests", () => {
    test.use({
        storageState: "./storage/clerk.json",
    });

    test("", () => {});
});
