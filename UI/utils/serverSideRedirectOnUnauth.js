/**
 * NOTE:
 *  This god forsaken pattern I have come up with is really bad
 *  but I cannot find a better solution with getInitialProps gone.
 *  Perhaps a HOC would be better. This is to be revised before more pages are added.
 */
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
