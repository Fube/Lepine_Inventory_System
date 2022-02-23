import { useTranslation } from "next-i18next";
import Head from "next/head";
import { useRouter } from "next/router";
import WarehouseForm from "../../components/WarehouseForm";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";
/**
 *
 * @param {{ warehouse: import("@lepine/ui-types").Warehouse }} param0
 * @returns
 */
export default function WarehouseDetails({ warehouse }) {
    const { t: te } = useTranslation("errors");
    const { t: tw } = useTranslation("warehouses");

    const { role } = useAuth();
    const router = useRouter();

    const handleDelete = async () => {
        await axiosAPI.delete(`/warehouses/${warehouse.uuid}`);
        router.push("/warehouses");
    };

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            await axiosAPI
                .put(`/warehouses/${warehouse.uuid}`, values)
                .then(() => {
                    setSubmitting(false);
                    router.push("/warehouses");
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
                <title>{tw("uuid.title")}</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <WarehouseForm
                            title={tw("uuid.title")}
                            editable={role === "MANAGER"}
                            deletable={role === "MANAGER"}
                            {...warehouse}
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
    const res = await axiosBackendAuth(`/warehouses/${uuid}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "errors",
        "warehouses",
        "nav",
    ]);

    return res.refine((warehouse) => ({ props: { warehouse, ...i18n } })).get();
}
