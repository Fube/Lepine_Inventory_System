export default async function checkEmptyAuth(axios, ctx) {
    try {
        await axios.get("/auth/fake/path", { headers: { ...ctx.req.headers } });
    } catch (e) {
        if (e.response.status === 401 || e.response.status === 403) {
            return {
                redirect: {
                    destination: "/login",
                    permanent: false,
                },
            };
        }
    }
    return { props: {} };
}
