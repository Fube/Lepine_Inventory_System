import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import { useRouter } from "next/router";
import ItemForm from "../../components/ItemForm";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";

/**
 *
 * @param {{ item: import("@lepine/ui-types").Item }} param0
 * @returns
 */
export default function Item({ item }) {
    const { t: tc } = useTranslation("common");
    const { t: te } = useTranslation("errors");
    const { t: ti } = useTranslation("items");

    const { role } = useAuth();
    const router = useRouter();

    const handleDelete = async () => {
        await axiosAPI.delete(`/items/${item.uuid}`);
        router.push("/items");
    };
    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            await axiosAPI.put(`/items/${item.uuid}`, values).then(() => {
                setSubmitting(false);
                router.push("/items");
            });
            setStatus({
                isError: false,
                message: ti("uuid.updated"),
            });
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
                <title>{ti("uuid.title")}</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <ItemForm
                            title={ti("uuid.title")}
                            ti={ti}
                            tc={tc}
                            te={te}
                            editable={role === "MANAGER"}
                            deletable={role === "MANAGER"}
                            {...item}
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
    const res = await axiosBackendAuth(`/items/${uuid}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "errors",
        "items",
    ]);

    return res.refine((item) => ({ props: { item, ...i18n } })).get();
}
