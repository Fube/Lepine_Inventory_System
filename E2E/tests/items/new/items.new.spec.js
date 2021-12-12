const { test, expect } = require("@playwright/test");
const { createItem } = require("../helpers");

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

    // Clean up
    await page.evaluate(
        async (uuid) => await fetch(`/api/items/${uuid}`, { method: "DELETE" }),
        uuid
    );
});
