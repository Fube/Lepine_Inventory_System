import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import { useRouter } from "next/router";
import UserDetails from "../../components/UserDetails";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";

/**
 *
 * @param {{ user: import("@lepine/types").User }} param0
 * @returns
 */

export default function User({ user }) {
    const { t: tc } = useTranslation("common");
    const { t: te } = useTranslation("errors");
    const { t: tu } = useTranslation("users");

    const { role } = useAuth();
    const router = useRouter();

    const handleDelete = async () => {
        await axiosAPI.delete(`/users/${user.uuid}`);
        router.push("/users");
    };
    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            await axiosAPI.put(`/users/${user.uuid}`, {
                ...values,
                role: values.role.toLocaleUpperCase(),
            });
            router.push("/users");
        } catch (error) {
            console.log(error);
            setStatus({
                isError: true,
                message: error?.response?.data?.message ?? te("unknown"),
            });
        } finally {
            setSubmitting(false);
        }
    };
    return (
        <>
            <Head>
                <title>{tu("uuid.title")}</title>
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

    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "errors",
        "users",
    ]);

    return res.refine((user) => ({ props: { user, ...i18n } })).get();
}
