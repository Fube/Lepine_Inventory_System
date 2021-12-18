const { test, expect } = require("@playwright/test");

test("/users/new :: Go to through /users", async ({ page }) => {
    // Go to users
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.goto("/users"),
    ]);

    // Click on "New User" button
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("[href*=new]"),
    ]);

    // Check that the page is loaded
    const title = await page.title();
    expect(title).toBe("Add User");

    // Check form is present
    const form = await page.$("form");
    expect(form).toBeTruthy();

    // Check form contains input fields
    const inputFields = await page.$$("form input");
    expect(inputFields.length).toBe(3);

    const inputFieldsNames = await Promise.all(
        inputFields.map((n) => n.getAttribute("name"))
    );
    expect(inputFieldsNames).toContain("email");
    expect(inputFieldsNames).toContain("password");
    expect(inputFieldsNames).toContain("confirmPassword");

    // Check form contains select field for role
    const selectField = await page.locator("form select");
    const selectFieldName = await selectField.getAttribute("name");
    expect(selectFieldName).toBe("role");

    // Check form contains "Register" button
    const registerButton = page.locator("form button[type=submit]");
    const registerButtonText = await registerButton.innerText();
    expect(registerButtonText.toLowerCase()).toBe("register");
});
