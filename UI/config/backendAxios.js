import axios from "axios";

console.log(process.env.SERVER_BACKEND_URL);
export const axiosBackend = axios.create({
    baseURL: process.env.SERVER_BACKEND_URL,
});
