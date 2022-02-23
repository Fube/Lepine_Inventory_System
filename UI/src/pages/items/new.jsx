import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import { useRouter } from "next/router";
import ItemBase from "../../components/ItemForm";
import WithClientSideAuth from "../../components/WithClientSideAuth";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import checkEmptyAuth from "../../utils/checkEmptyAuth";

function CreateItem() {
    const router = useRouter();

    const { t: tc } = useTranslation("common");
    const { t: ti } = useTranslation("items");
    const { t: te } = useTranslation("errors");

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            await axiosAPI.post("/items", values);
            setStatus({
                isError: false,
                message: ti("new.created"),
            });
            router.push("/items");
        } catch (error) {
            console.log(error);
            setStatus({
                isError: true,
                message:
                    error?.response?.data?.message ?? te("unknown"),
            });
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <>
            <Head>
                <title>{ti("new.title")}</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <ItemBase
                            title={ti("new.title")}
                            ti={ti}
                            tc={tc}
                            te={te} 
                            editable 
                            handleSubmit={handleSubmit} />
                    </div>
                </div>
            </div>
        </>
    );
}

export default WithClientSideAuth(CreateItem);

/**
 *
 * @param {import("next/types").GetServerSidePropsContext} context
 * @returns
 */
export async function getServerSideProps(ctx) {
    return checkEmptyAuth(axiosBackendAuth, ctx, {
        ...await serverSideTranslations(ctx.locale, [
            "common",
            "items",
            "errors",
        ])
    });
}
