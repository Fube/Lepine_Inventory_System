const emptyProps = {};

export default async function checkEmptyAuth(axios, context, props=emptyProps) {
    try {
        const res = await axios.get("/auth/fake/path", {
            headers: { cookie: context?.req?.headers?.cookie ?? "" },
        });
        return res.refine(() => ({props})).get();
    } catch (e) {
        return {props};
    }
}
