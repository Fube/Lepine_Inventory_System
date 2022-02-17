module.exports = {
    reactStrictMode: true,
    async rewrites() {
        return [
            {
                source: "/api/:path*",
                destination: `${process.env.SERVER_BACKEND_URL}/:path*`,
            },
        ];
    },
    async redirects() {
        return [
            {
                source: "/",
                destination: "/items",
                permanent: true,
            },
        ];
    },
};
