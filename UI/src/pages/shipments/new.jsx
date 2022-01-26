import Head from "next/head";
import { useRouter } from "next/router";
import ShipmentForm from "../../components/ShipmentForm";
import WithClientSideAuth from "../../components/WithClientSideAuth";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import checkEmptyAuth from "../../utils/checkEmptyAuth";

/**
 *
 * @param { activeWarehouses: import('@lepine/ui-types').Warehouse[] } param0
 * @returns
 */
function CreateShipment({ activeWarehouses }) {
    const router = useRouter();

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        // setSubmitting(true);
        // try {
        //     await axiosAPI.post("/shipments", values);
        //     setStatus({
        //         isError: false,
        //         message: "Shipment successfully created",
        //     });
        //     router.push("/shipments");
        // } catch (error) {
        //     console.log(error);
        //     setStatus({
        //         isError: true,
        //         message:
        //             error?.response?.data?.message ?? "Something went wrong",
        //     });
        // } finally {
        //     setSubmitting(false);
        // }
        console.log(values);
        setSubmitting(false);
    };

    return (
        <>
            <Head>
                <title>Create Shipment</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <ShipmentForm
                            title={"Create Shipment"}
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

    return res
        .refine(({ content: activeWarehouses }) => ({
            props: {
                activeWarehouses,
            },
        }))
        .get();
}
