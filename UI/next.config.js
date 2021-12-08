module.exports = {
    reactStrictMode: true,
    publicRuntimeConfig: {
        backEndUrl:
            process.env.NODE_ENV === "production"
                ? "https://api.nextjs-starter.com"
                : "http://localhost:8080",
    },
};
