import { useRouter } from "next/router";
import Head from "next/head";
import Nav from "../../components/Nav";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";
import WarehouseForm from "../../components/WarehouseForm";
/**
 *
 * @param {{ warehouse: import("@lepine/types").Warehouse }} param0
 * @returns
 */
export default function WarehouseDetails({ warehouse }) {
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
                message:
                    error?.response?.data?.message ?? "Something went wrong",
            });
        } finally {
            setSubmitting(false);
        }
    };
    return (
        <>
            <Head>
                <title>Warehouse Details</title>
            </Head>
            <div className="flex flex-col h-screen">
                <div className="flex-shrink-0 flex-grow-0">
                    <Nav />
                </div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <WarehouseForm
                            title={"Warehouse Details"}
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

    return res.refine((warehouse) => ({ props: { warehouse } })).get();
}
