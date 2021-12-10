module.exports = {
    reactStrictMode: true,
    publicRuntimeConfig: {
        backEndUrl:
            process.env.NODE_ENV === "production"
                ? "https://api.nextjs-starter.com"
                : process.env?.SERVER_BACKEND_URL ?? "http://localhost:8080",
    },
    serverRuntimeConfig: {
        backEndUrl:
            process.env.NODE_ENV === "production"
                ? "https://api.nextjs-starter.com"
                : process.env?.SERVER_BACKEND_URL ?? "http://localhost:8080",
    },
};
