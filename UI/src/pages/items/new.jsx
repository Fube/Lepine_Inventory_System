import Head from "next/head";
import { useRouter } from "next/router";
import Nav from "../../components/Nav";
import ItemBase from "../../components/ItemForm";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import checkEmptyAuth from "../../utils/checkEmptyAuth";
import WithClientSideAuth from "../../components/WithClientSideAuth";

function CreateItem() {
    const router = useRouter();

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            await axiosAPI.post("/items", values);
            setStatus({
                isError: false,
                message: "Item successfully created",
            });
            router.push("/items");
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
                <title>Add Item</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <ItemBase editable handleSubmit={handleSubmit} />
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
    return checkEmptyAuth(axiosBackendAuth, ctx);
}
