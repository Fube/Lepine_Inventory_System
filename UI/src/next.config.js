const { i18n } = require("./next-i18next.config");

module.exports = {
    reactStrictMode: true,
    i18n,
    async rewrites() {
        return [
            {
                source: "/:locale?/api/:path*",
                destination: `${process.env.SERVER_BACKEND_URL}/:path*`,
                locale: false,
            },
        ];
    },
    async redirects() {
        return [
            {
                source: "/",
                destination: "/items",
                permanent: false,
            },
        ];
    },
};
