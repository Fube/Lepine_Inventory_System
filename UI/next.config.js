module.exports = {
    reactStrictMode: true,
    async rewrites() {
        console.log(process.env.SERVER_BACKEND_URL, "Backend url");
        return [
            {
                source: "/api/:path*",
                destination: `${process.env.SERVER_BACKEND_URL}/:path*`,
            },
        ];
    },
};
