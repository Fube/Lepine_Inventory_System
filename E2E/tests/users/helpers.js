/**
 *
 * @param {import("playwright-core").Page} page
 * @param {{ email:string , password:string }} param1
 */
async function loginAs(page, { email, password }) {
    await logout();

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.goto("/login"),
    ]);

    await page.type('input[name="email"]', email);
    await page.type('input[name="password"]', password);

    await Promise.all([
        page.waitForNavigation({ waitUntil: "networkidle0" }),
        page.click("button[type=submit]"),
    ]);
}

/**
 *
 * @param {import("playwright-core").Page} page
 */
async function logout(page, context = null) {
    if (context === null) {
        context = page.context();
    }
    await page.goto("/");
    await page.evaluate(() => {
        localStorage.clear();
    });
    await context.clearCookies();
    await page.reload();
}
module.exports = {
    loginAs,
    logout,
};
