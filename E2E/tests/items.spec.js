const { test, expect } = require("@playwright/test");

test("Go to /items through nav", async ({ page }) => {
    await page.goto("/");
    // Click on ITEMS XPath and wait for the page to load
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        await page.click("//html/body/div[1]/div[1]/div[2]/div/a"),
    ]);

    // Check that the page is loaded
    const title = await page.title();
    expect(title).toBe("Items");

    // Check that the page has the correct content
    const content = await page.content();
    if (content.indexOf("table") == -1) {
        expect(content).toContain("No items to show");
        expect(content).toContain("Add One Now!");
    } else {
        const ths = await page.$$("thead tr th");
        expect(ths.length).toBe(3);
        expect(await ths[0].textContent()).toBe("SKU");
        expect(await ths[1].textContent()).toBe("Name");
        expect(await ths[2].textContent()).toBe("Description");

        expect(await ths[0].innerText()).toBe("SKU");
        expect(await ths[1].innerText()).toBe("NAME");
        expect(await ths[2].innerText()).toBe("DESCRIPTION");

        const plusBtn = await page.$("thead tr th:last-child > button");
        expect(plusBtn).toBeTruthy();
    }

    // Check "Items" in nav is active
    const navItemsActive = await page.$$(
        "html > body > div > div  a.btn.btn-ghost.text-blue-400[href]"
    );

    expect(navItemsActive.length).toBe(1);
    const active = await navItemsActive[0];
    expect(await active.textContent()).toBe("Items");
    expect(await active.getAttribute("href")).toBe("/items");
});

test("Go to /items/new through /items", async ({ page }) => {
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

// test("Create item from /items/new", async ({ page }) => {
//     await Promise.all([
//         page.waitForNavigation({ waitUntil: "networkidle0" }),
//         page.goto("/items/new"),
//     ]);

//     // Get input fields
//     const name = await page.$("form input[name=name]");
//     const description = await page.$("form input[name=description]");
//     const sku = await page.$("form input[name=sku]");

//     // Fill input fields
//     await name.type("Test Item");
//     await description.type("Test Description");
//     await sku.type("Test SKU");

//     // Submit form and intercept response
//     await page.route("**/api/items", async (route) => {
//         console.log(route.request().postDataJSON());
//         route.continue();
//     });

//     await Promise.all([
//         page.waitForNavigation({ waitUntil: "networkidle0" }),
//         await page.click("form button[type=submit]"),
//     ]);

//     // Check that item was created
// });
