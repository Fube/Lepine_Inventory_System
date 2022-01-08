const { default: axios } = require("axios");
const { login } = require("@lepine/e2e-helpers/api/auth");

async function createItem(
    baseURL,
    { email, password },
    { name, description, sku }
) {
    const { headers } = await login(baseURL, { email, password });

    // Create a new item
    const { data } = await axios.post(
        `${baseURL}/api/items`,
        { name, description, sku },
        {
            headers: {
                cookie: headers["set-cookie"][0],
            },
        }
    );

    return data;
}

async function deleteItem(baseURL, { email, password }, uuid) {
    const { headers } = await login(baseURL, { email, password });

    // Delete item
    await axios.delete(`${baseURL}/api/items/${uuid}`, {
        headers: {
            cookie: headers["set-cookie"][0],
        },
    });
}

module.exports = {
    createItem,
    deleteItem,
};
