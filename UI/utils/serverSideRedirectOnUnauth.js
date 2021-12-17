export default async function serverSideRedirectOnUnauth(
    act,
    destination = "/login"
) {
    try {
        return await act();
    } catch (e) {
        if (e.response.status === 401 || e.response.status === 403) {
            return {
                redirect: {
                    destination,
                    permanent: false,
                },
            };
        }
    }
}
