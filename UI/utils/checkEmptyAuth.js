export default async function checkEmptyAuth(axios, context) {
    try {
        await axios.get("/auth/fake/path", {
            headers: { cookie: context.req.headers.cookie },
        });
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
