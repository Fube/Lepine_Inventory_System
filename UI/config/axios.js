import getConfig from "next/config";
import axios from "axios";

const {
    serverRuntimeConfig: { backEndUrl },
} = getConfig();

export const axiosBackend = axios.create({
    baseURL: backEndUrl,
});
