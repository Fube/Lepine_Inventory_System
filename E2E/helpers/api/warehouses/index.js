const { default: axios } = require("axios");
const { login } = require("@lepine/e2e-helpers/api/auth");

async function createWarehouse(
    baseURL,
    { email, password },
    { zipCode, city, province }
) {
    const { headers } = await login(baseURL, { email, password });

    // Create a new warehouse
    const { data } = await axios.post(
        `${baseURL}/api/warehouses`,
        { zipCode, city, province },
        {
            headers: {
                cookie: headers["set-cookie"][0],
            },
        }
    );

    return data;
}

async function deleteWarehouse(baseURL, { email, password }, uuid) {
    const { headers } = await login(baseURL, { email, password });

    // Delete warehouse
    await axios.delete(`${baseURL}/api/warehouses/${uuid}`, {
        headers: {
            cookie: headers["set-cookie"][0],
        },
    });
}

module.exports = {
    createWarehouse,
    deleteWarehouse,
};
