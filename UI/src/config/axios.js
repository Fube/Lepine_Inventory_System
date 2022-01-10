import axios from "axios";

class AxiosRedirectAwareResultChain {
    #data;
    #isRedirect;
    #refinements;

    /**
     *
     * @param {import("axios").AxiosResponse | import("axios").AxiosError} response
     */
    constructor(response) {
        this.#refinements = [];
        if (axios.isAxiosError(response)) {
            const status = response?.response.status ?? null;
            if (status === 401 || status === 403) {
                this.#isRedirect = true;
                this.#data = response.data;
            } else {
                return Promise.reject(response);
            }
        } else {
            this.#data = response.data;
            this.#isRedirect = false;
        }
    }

    /**
     *
     * @param {(data: any) => any} refiner
     * @returns
     */
    refine(refiner) {
        this.#refinements.push(refiner);
        return this;
    }

    get() {
        if (this.#isRedirect) {
            return {
                redirect: {
                    destination: "/login",
                    permanent: false,
                },
            };
        }

        return this.#refinements.reduce(
            (data, refiner) => refiner(data),
            this.#data
        );
    }
}

export const axiosBackendAuth = axios.create({
    baseURL: process.env.SERVER_BACKEND_URL,
});

export const axiosBackendNoAuth = axios.create({
    baseURL: process.env.SERVER_BACKEND_URL,
});

axiosBackendAuth.interceptors.response.use(
    (res) => new AxiosRedirectAwareResultChain(res),
    (err) => {
        console.log("Failed in Axios interceptor");
        return new AxiosRedirectAwareResultChain(err);
    }
);

export const axiosAPI = axios.create({
    baseURL: "/api",
});
