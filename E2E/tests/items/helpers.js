const { expect } = require("@playwright/test");
const { nanoid } = require("nanoid");

async function createItem(page) {
    const SKU = nanoid(5);
    const NAME = "NAME";
    const DESCRIPTION = "DESCRIPTION";

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.goto("/items/new"),
    ]);

    // Get input fields
    const name = await page.$("form input[name=name]");
    const description = await page.$("form input[name=description]");
    const sku = await page.$("form input[name=sku]");

    // Fill input fields
    await sku.type(SKU);
    await name.type(NAME);
    await description.type(DESCRIPTION);

    // Submit form and intercept response
    await page.route("**/api/items", async (route) => {
        const data = route.request().postDataJSON();
        expect(data).toEqual({
            sku: SKU,
            name: NAME,
            description: DESCRIPTION,
        });
        route.continue();
    });

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        await page.click("form button[type=submit]"),
    ]);

    // Check redirect
    const title = await page.title();
    expect(title).toBe("Items");

    // Search for item
    const search = await page.$("input[type=search]");
    expect(search).toBeTruthy();
    await search.type(SKU, { delay: 1000 }); // Give Algolia time to load new item

    // Check that the item is in the table
    const table = await page.$("table");
    expect(table).toBeTruthy();
    const trs = await page.$$("table tbody tr");
    expect(trs.length).toBeGreaterThan(0);

    const toJSON = [];
    for (const tr of trs) {
        const tds = await tr.$$("td");
        toJSON.push({
            sku: await tds[0].innerText(),
            name: await tds[1].innerText(),
            description: await tds[2].innerText(),
            uuid: (await tr.getAttribute("href")).match(/items\/(.+)/).pop(),
        });
    }

    expect(toJSON).toContainEqual({
        sku: SKU,
        name: NAME,
        description: DESCRIPTION,
        uuid: expect.stringMatching(/^[a-z0-9-]+$/),
    });

    return toJSON.find((item) => item.sku === SKU);
}

module.exports = {
    createItem,
};
