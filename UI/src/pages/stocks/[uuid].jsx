import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import { useRouter } from "next/router";
import StockForm from "../../components/StockForm";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";
/**
 *
 * @param {{ stock: import("@lepine/ui-types").Stock, activeWarehouses: import('@lepine/ui-types').Warehouse[] }} param0
 * @returns
 */
export default function StockDetails({ stock, activeWarehouses }) {
    const { t: te } = useTranslation("errors");
    const { t: ts } = useTranslation("stocks");

    const { role } = useAuth();
    const router = useRouter();

    const handleDelete = async () => {
        await axiosAPI.delete(`/stocks/${stock.uuid}`);
        router.push("/stocks");
    };

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            await axiosAPI.put(`/stocks/${stock.uuid}`, values).then(() => {
                setSubmitting(false);
                router.push("/stocks");
            });
            router.push("/stocks");
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
                <title>{ts("uuid.title")}</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <StockForm
                            title={ts("uuid.title")}
                            editable={role === "MANAGER"}
                            deletable={role === "MANAGER"}
                            {...stock}
                            handleDelete={handleDelete}
                            handleSubmit={handleSubmit}
                            warehouses={activeWarehouses}
                            disabled={new Set(["itemUuid", "warehouseUuid"])}
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

    const headers = { cookie: context?.req?.headers?.cookie ?? "" };
    const [stockRes, warehouseRes] = await Promise.all([
        axiosBackendAuth(`/stocks/${uuid}`, {
            headers,
        }),
        axiosBackendAuth(`/warehouses?size=100&active=true`, {
            headers,
        }),
    ]);

    const stock = stockRes.refine((n) => n).get();
    const activeWarehouses = warehouseRes.refine((page) => page.content).get();

    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "errors",
        "stocks",
        "warehouses",
        "nav",
    ]);

    return {
        props: {
            stock,
            activeWarehouses,
            ...i18n,
        },
    };
}
