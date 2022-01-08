const { default: axios } = require("axios");

const login = (baseURL, { email, password }) =>
    axios.post(`${baseURL}/api/auth/login`, {
        email,
        password,
    });

module.exports = {
    login,
};
