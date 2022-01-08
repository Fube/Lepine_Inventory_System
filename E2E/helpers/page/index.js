/**
 *
 * @param {import("playwright-core").Page} page
 * @param {import("playwright-core").Locator} input
 * @param {*} newValue
 */
async function clearThenType(page, input, newValue) {
    await input.click({
        clickCount: 3,
    });
    await page.keyboard.press("Backspace");
    await input.type(newValue);
}

module.exports = {
    clearThenType,
};
