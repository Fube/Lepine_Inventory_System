const emptyProps = { props: {} };

export default async function checkEmptyAuth(axios, context) {
    try {
        const res = await axios.get("/auth/fake/path", {
            headers: { cookie: context?.req?.headers?.cookie ?? "" },
        });
        return res.refine(() => emptyProps).get();
    } catch (e) {
        return emptyProps;
    }
}
