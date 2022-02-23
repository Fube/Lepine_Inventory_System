import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import { useRouter } from "next/router";
import ShipmentForm from "../../components/ShipmentForm";
import WithClientSideAuth from "../../components/WithClientSideAuth";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";

/**
 *
 * @param { activeWarehouses: import('@lepine/ui-types').Warehouse[] } param0
 * @returns
 */
function CreateShipment({ activeWarehouses }) {
    const { t: te } = useTranslation("errors");
    const { t: ts } = useTranslation("shipments");

    const router = useRouter();

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        console.log(values);
        setSubmitting(true);
        try {
            await axiosAPI.post("/shipments", values);
            setStatus({
                isError: false,
                message: ts("new.created"),
            });
            router.push("/shipments");
        } catch (error) {
            console.log(error);
            setStatus({
                isError: true,
                message: error?.response?.data?.message ?? te("unknown"),
            });
        } finally {
            setSubmitting(false);
        }
        setSubmitting(false);
    };

    return (
        <>
            <Head>
                <title>{ts("new.title")}</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <ShipmentForm
                            title={ts("new.title")}
                            handleSubmit={handleSubmit}
                            warehouses={activeWarehouses}
                            editable
                        />
                    </div>
                </div>
            </div>
        </>
    );
}

export default WithClientSideAuth(CreateShipment);

/**
 *
 * @param {import("next/types").GetServerSidePropsContext} context
 * @returns
 */
export async function getServerSideProps(ctx) {
    const res = await axiosBackendAuth.get(`/warehouses?size=100&active=true`, {
        headers: { cookie: ctx?.req?.headers?.cookie ?? "" },
    });

    const i18n = await serverSideTranslations(ctx.locale, [
        "common",
        "errors",
        "shipments",
        "warehouses",
        "nav",
    ]);

    return res
        .refine(({ content: activeWarehouses }) => ({
            props: {
                activeWarehouses,
                ...i18n,
            },
        }))
        .get();
}
