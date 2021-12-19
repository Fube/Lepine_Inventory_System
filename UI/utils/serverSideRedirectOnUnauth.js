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
        console.log(e);
        const status = e?.response?.status || null;
        if (status === 401 || status === 403) {
            return {
                redirect: {
                    destination,
                    permanent: false,
                },
            };
        }
    }
}
