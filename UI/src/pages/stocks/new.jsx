import Head from "next/head";
import { useRouter } from "next/router";
import StockForm from "../../components/StockForm";
import WithClientSideAuth from "../../components/WithClientSideAuth";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";


 function CreateStock({activeWarehouses}){
    const router = useRouter();

    const handleSubmit = async (values, {setSubmitting, setStatus}) => {
        console.log(values);
        setSubmitting(true);
        try {
            await axiosAPI.post("/stocks", values);
            setStatus({
                isError: false,
                message: "Stock successfully created",
            });
            router.push("/stocks");
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
        setSubmitting(false);
    };

    return (
        <>
            <Head>
                <title>Create Stock</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <StockForm
                            title={"Create Stock"}
                            handleSubmit={handleSubmit}
                            items={["itemsExist"]}
                            warehouses={activeWarehouses}
                            editable
                        />
                    </div>
                </div>
            </div>
        </>
    );
}

export default WithClientSideAuth(CreateStock);

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