const { test, expect } = require("@playwright/test");

test("/items :: Go to through nav", async ({ page }) => {
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.goto("/"),
    ]);
    // Click on ITEMS XPath and wait for the page to load
    await Promise.all([
        Promise.race([
            page.waitForSelector("table"),
            page.waitForSelector("h2"),
        ]),
        page.click("a[href*=items]"),
    ]);

    // Check that the page is loaded
    const title = await page.title();
    expect(title).toBe("Items");

    // Check that the page has the correct content
    const content = await page.content();
    const table = await page.$("table");

    if (!table) {
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
});
