module.exports = {
    reactStrictMode: true,
    publicRuntimeConfig: {
        backEndUrl: "http://localhost:8080",
    },
    async rewrites() {
        return [
            {
                source: "/api/:path*",
                destination: `${
                    process.env.SERVER_BACKEND_URL ?? "http://localhost:8080"
                }/:path*`,
            },
        ];
    },
};
