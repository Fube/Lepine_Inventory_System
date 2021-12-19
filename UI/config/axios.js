import axios from "axios";

export const axiosAPI = axios.create({
    baseURL: "/api",
});
