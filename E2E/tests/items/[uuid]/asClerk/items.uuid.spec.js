const { expect, test } = require("@playwright/test");
const axios = require("axios").default;
const { MANAGER_USERNAME, MANAGER_PASSWORD } = require("config");

const clerkLogin = {
    email: "",
    password: "",
};

const targetItem = {
    name: "",
    description: "",
    sku: "",
};

test.beforeAll(({ baseURL }) => {
    axios.post(`${baseURL}/login`, {
        email: MANAGER_USERNAME,
        password: MANAGER_PASSWORD,
    });
});
