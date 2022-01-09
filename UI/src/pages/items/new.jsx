import Head from "next/head";
import { useRouter } from "next/router";
import Nav from "../../components/Nav";
import ItemBase from "../../components/ItemForm";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import checkEmptyAuth from "../../utils/checkEmptyAuth";
import WithClientSideAuth from "../../components/WithClientSideAuth";

function CreateItem() {
    const router = useRouter();

    return (
        <>
            <Head>
                <title>Add Item</title>
            </Head>
            <div className="flex flex-col h-screen">
                <div className="flex-shrink-0 flex-grow-0">
                    <Nav />
                </div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <ItemBase
                            editable
                            handleSubmit={async (values, { setSubmitting }) => {
                                setSubmitting(true);
                                await axiosAPI.post("/items", values);
                                setSubmitting(false);
                                router.push("/items");
                            }}
                        />
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
