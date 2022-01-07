import Head from "next/head";
import { useRouter } from "next/router";
import Nav from "../../components/Nav";
import WarehouseForm from "../../components/WarehouseForm";
import WithClientSideAuth from "../../components/WithClientSideAuth";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import checkEmptyAuth from "../../utils/checkEmptyAuth";

function CreateWarehouse() {
    const router = useRouter();

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            await axiosAPI.post("/warehouses", values);
            setStatus({
                isError: false,
                message: "Warehouse successfully created",
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
                <title>Create Warehouse</title>
            </Head>
            <div className="flex flex-col h-screen">
                <div className="flex-shrink-0 flex-grow-0">
                    <Nav />
                </div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <WarehouseForm
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

export async function getServerSideProps(ctx) {
    return checkEmptyAuth(axiosBackendAuth, ctx);
}
