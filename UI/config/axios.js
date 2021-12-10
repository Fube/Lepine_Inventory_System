import axios from "axios";

export const axiosBackend = axios.create({
    baseURL: process.env.SERVER_BACKEND_URL,
});

export const axiosAPI = axios.create({
    baseURL: "/api",
});
