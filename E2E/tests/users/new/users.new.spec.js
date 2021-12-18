const { test, expect } = require("@playwright/test");
const { loginAs } = require("../../auth/helpers");

const validUser = {
    email: "foo@bar.com",
    password: "F00b@rbaz",
    role: "Clerk",
};

const invalidUser = {
    email: "abc",
    password: "123",
};

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

test("/users/new :: Create new user", async ({ page }) => {
    // Go to users/new
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.goto("/users/new"),
    ]);

    // Fill in form
    const { email, password, role } = validUser;
    await page.type('input[name="email"]', email);
    await page.type('input[name="password"]', password);
    await page.type('input[name="confirmPassword"]', password);
    await page.selectOption('select[name="role"]', role);

    // Submit form
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("button[type=submit]"),
    ]);

    // Check that the page is loaded
    const title = await page.title();
    expect(title).toBe("Users");

    await loginAs(page, validUser);

    // Check that the user is logged in
    const localStorage = await page.evaluate(() => {
        return localStorage;
    });

    expect(localStorage.email).toEqual(validUser.email);
});

test("/users/new :: Try to create with invalid data", async ({ page }) => {
    // Go to users/new
    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.goto("/users/new"),
    ]);

    // Dirty the form
    const { email, password } = invalidUser;
    await page.type('input[name="email"]', email);
    await page.type('input[name="password"]', password);

    const roleLocator = page.locator('select[name="role"]');
    await roleLocator.click();

    const emailLocator = page.locator('input[name="email"]');
    await emailLocator.click();

    const confirmLocator = page.locator('input[name="confirmPassword"]');
    await confirmLocator.click();
    await emailLocator.click();

    // Check that the page is loaded
    const title = await page.title();
    expect(title).toBe("Add User");

    // Check form is present
    const form = await page.$("form");
    expect(form).toBeTruthy();

    // Check form contains error messages
    const errorMessages = await page.$$("form .text-red-500");
    expect(errorMessages.length).toBe(4);

    const errorMessagesText = await Promise.all(
        errorMessages.map((n) => n.innerText())
    );

    expect(errorMessagesText).toContain("Must be a valid email");
    expect(
        errorMessagesText.find((n) => /Password must include.*/.test(n))
    ).toBeTruthy();
    expect(errorMessagesText).toContain("Role is required");
    expect(errorMessagesText).toContain("Passwords must match");
});
