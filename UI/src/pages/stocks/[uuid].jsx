import { useRouter } from "next/router";
import Head from "next/head";
import Nav from "../../components/Nav";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";
import StockForm from "../../components/StockForm";
/**
 *
 * @param {{ stock: import("@lepine/ui-types").Stock }} param0
 * @returns
 */
export default function StockDetails({ stock }) {
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
                <title>Stock Details</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <StockForm
                            title={"Stock Details"}
                            editable={role === "MANAGER"}
                            deletable={role === "MANAGER"}
                            {...stock}
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
    const res = await axiosBackendAuth(`/stockss/${uuid}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    return res.refine((stock) => ({ props: { stock } })).get();
}
