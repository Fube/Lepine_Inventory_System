import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import { useRouter } from "next/router";
import WarehouseForm from "../../components/WarehouseForm";
import WithClientSideAuth from "../../components/WithClientSideAuth";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import checkEmptyAuth from "../../utils/checkEmptyAuth";

function CreateWarehouse() {
    const { t: te } = useTranslation("errors");
    const { t: tw } = useTranslation("warehouses");

    const router = useRouter();

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            await axiosAPI.post("/warehouses", values);
            setStatus({
                isError: false,
                message: tw("new.created"),
            });
            router.push("/warehouses");
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
                <title>{tw("new.title")}</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <WarehouseForm
                            title={tw("new.title")}
                            handleSubmit={handleSubmit}
                            blackList={["active"]}
                            editable
                        />
                    </div>
                </div>
            </div>
        </>
    );
}

export default WithClientSideAuth(CreateWarehouse);

/**
 *
 * @param {import("next/types").GetServerSidePropsContext} context
 * @returns
 */
export async function getServerSideProps(ctx) {
    const i18n = await serverSideTranslations(ctx.locale, [
        "common",
        "errors",
        "warehouses",
    ]);
    return checkEmptyAuth(axiosBackendAuth, ctx, {
        ...i18n,
    });
}
