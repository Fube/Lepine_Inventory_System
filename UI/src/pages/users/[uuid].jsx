import { useRouter } from "next/router";
import Head from "next/head";
import UserDetails from "../../components/UserDetails";
import Nav from "../../components/Nav";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";

/**
 *
 * @param {{ user: import("@lepine/types").User }} param0
 * @returns
 */

export default function User({ user }) {
    const { role } = useAuth();
    const router = useRouter();

    const handleDelete = async () => {
        await axiosAPI.delete(`/users/${user.uuid}`);
        router.push("/users");
    };
    const handleSubmit = async (values, { setSubmitting }) => {
        setSubmitting(true);
        console.log(values);
        axiosAPI
            .put(`/users/${user.uuid}`, {
                ...values,
                role: values.role.toLocaleUpperCase(),
            })
            .then(() => {
                setSubmitting(false);
                router.push("/users");
            })
            .catch(console.log);
    };
    return (
        <>
            <Head>
                <title>User Details</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <UserDetails
                            editable={role === "MANAGER"}
                            deletable={role === "MANAGER"}
                            {...user}
                            handleDelete={handleDelete}
                            handleSubmit={handleSubmit}
                        />
                    </div>
                </div>
            </div>
        </>
    );
}

/**
 *
 * @param {import("next/types").GetServerSidePropsContext} context
 * @returns
 */
export async function getServerSideProps(context) {
    const { uuid } = context.query;
    const res = await axiosBackendAuth(`/users/${uuid}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    return res.refine((user) => ({ props: { user } })).get();
}
