const { expect, test } = require("@playwright/test");
const RandExp = require("randexp");
const {
    createUser,
    deleteUser,
} = require("@lepine/e2e-helpers/api/users");
const {
    READONLY_USER_EMAIL,
    READONLY_USER_PASSWORD,
    MANAGER_USERNAME,
    MANAGER_PASSWORD,
} = require("@lepine/e2e-config");
const { clearThenType } = require("@lepine/e2e-helpers/page");

test.describe.parallel("RgtXQeVKVM: Manager /users/[uuid] tests", () => {
    let uuid = null;
    let email = null;
    const toClean = new Set();
    const emailGen = new RandExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);
    const passwordGen = new RandExp(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$/);
    const baseUser = {
        email: READONLY_USER_EMAIL,
        password: READONLY_USER_PASSWORD,
    };
    const managerCredentials = {
        email: MANAGER_USERNAME,
        password: MANAGER_PASSWORD,
    };

    test.beforeEach(async ({ baseURL }) => {

        const data = await createUser(baseURL, managerCredentials, {
            ...baseUser,

        });
        uuid = data.uuid;


        toClean.add(uuid);
    });

    test.afterEach(async ({ baseURL }) => {
        await Promise.all(
            [...toClean].map((uuid) =>
                deleteUser(baseURL, managerCredentials, uuid)
            )
        );
    });

    test("TJbFoGiWQI: /users/[uuid] :: Go to through /users as manager", async ({
                                                                                              page,
                                                                                          }) => {
        // Go to /warehouses
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Users"
            ),
            page.goto("/users"),
        ]);

        // Find warehouse
        let user = null;
        do {
            user = await page.$(`tr[href*="${uuid}"]`);
            if (user) break;

            // Go to next page
            const nextBtn = await page.$(
                "table + div button:last-of-type:not([disabled])"
            );
            if (!nextBtn) break;

            await Promise.all([
                page.waitForNavigation({ waitUntil: "networkidle" }),
                nextBtn.click(),
            ]);
        } while (!user);

        expect(user).toBeTruthy();

        // Click on warehouse
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "User Details"
            ),
            user.click(),
        ]);

        // Check it is the expected page
        const title = await page.title();
        expect(title).toBe("User Details");

        // Check that the page contains the warehouse we created
        const content = await page.content();
        expect(content).toContain(READONLY_USER_EMAIL);
        expect(content).toContain(READONLY_USER_PASSWORD);
        const active = page.locator("[name=active]");
        expect(await active.inputValue()).toBe("true");

        // Check save is disabled
        const saveBtn = page.locator("button[type=submit]");
        expect(await saveBtn.isDisabled()).toBe(true);

        // Check delete is enabled
        const deleteBtn = page.locator("* button:last-of-type");
        expect(await deleteBtn.isEnabled()).toBe(true);
    });

    test("vUkFglQMbZ: /users/[uuid] :: Delete user", async ({
                                                                          page,
                                                                      }) => {
        // Go to /users
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "User Details"
            ),
            page.goto(`/users/${uuid}`),
        ]);

        // Click delete
        const deleteBtn = page.locator("* button:last-of-type");
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Users"
            ),
            deleteBtn.click(),
        ]);
    });

    test("OmuplAvGPg: /users/[uuid] :: Update user", async ({
                                                                          page,
                                                                      }) => {
        // Go to /warehouses
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "User Details"
            ),
            page.goto(`/users/${uuid}`),
        ]);

        // Update email
        const emailInput = page.locator("[name=email]");
        const updatedEmail = emailGen.gen();
        await clearThenType(page, emailInput, updatedEmail);

        // Update password
        const passwordInput = page.locator("[name=password]");
        const updatedCity = passwordGen.gen();
        await clearThenType(page, passwordInput, updatedPassword);


        // Check save is enabled
        const saveBtn = page.locator("button[type=submit]");
        expect(await saveBtn.isEnabled()).toBe(true);

        // Click save
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "Warehouses"
            ),
            saveBtn.click(),
        ]);

        // Check it is the expected page
        const title = await page.title();
        expect(title).toBe("Users");

        // Go back to user
        await Promise.all([
            page.waitForFunction(
                () => document.querySelector`title`.text === "User Details"
            ),
            page.goto(`/users/${uuid}`),
        ]);

        // Check that the page contains the warehouse we created
        expect(await emailInput.inputValue()).toEqual(updatedEmail);
        expect(await passwordInput.inputValue()).toEqual(updatedPassword);

    });

    test.describe.parallel("Dual user setup", () => {
        let preUuid = null;
        let premail = null;

        test.beforeEach(async ({ baseURL }) => {
            premail = emailGen.gen();
            const ppassword = passwordGen.gen();
            const { uuid } = await createUser(
                baseURL,
                managerCredentials,
                {
                    email: premail,
                    password: ppassword,
                }
            );

            preUuid = uuid;

            toClean.add(uuid);
        });

        test("fzkrekYTsB: /users/[uuid] :: Update user duplicate email", async ({
                                                                                                page,
                                                                                                baseURL,
                                                                                            }) => {
            // Go to /users/[uuid]
            await Promise.all([
                page.waitForFunction(
                    () =>
                        document.querySelector`title`.text ===
                        "User Details"
                ),
                page.goto(`/users/${preUuid}`),
            ]);

            // Update email
            const emailInput = page.locator("[name=email]");
            await clearThenType(page, emailInput, email);

            // Try to save
            const saveBtn = page.locator("button[type=submit]");
            await Promise.all([
                page.waitForResponse(/.*api\/user.*/i),
                saveBtn.click(),
            ]);

            // Check still on the same page
            const title = await page.title();
            expect(title).toBe("User Details");

            // Check for error
            const error = await page.content();
            expect(error).toContain(`Email ${email} already in use`);
        });
    });
});
