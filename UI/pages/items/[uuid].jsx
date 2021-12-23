import { useRouter } from "next/router";
import Head from "next/head";
import ItemBase from "../../components/Item";
import Nav from "../../components/Nav";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";

/**
 *
 * @param {{ item: import("../../components/Item").Item }} param0
 * @returns
 */
export default function Item({ item }) {
    const router = useRouter();
    const handleDelete = async () => {
        await axiosAPI.delete(`/items/${item.uuid}`);
        router.push("/items");
    };
    const handleSubmit = async (values, { setSubmitting }) => {
        setSubmitting(true);
        axiosAPI.put(`/items/${item.uuid}`, values).then(() => {
            setSubmitting(false);
            router.push("/items");
        });
    };
    return (
        <>
            <Head>
                <title>Item Details</title>
            </Head>
            <div className="flex flex-col h-screen">
                <div className="flex-shrink-0 flex-grow-0">
                    <Nav />
                </div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <ItemBase
                            editable
                            deletable
                            {...item}
                            handleDelete={handleDelete}
                            handleSubmit={handleSubmit}
                        />
                    </div>
                </div>
            </div>
        </>
    );
}

export async function getServerSideProps(context) {
    const { uuid } = context.query;
    const res = await axiosBackendAuth(`/items/${uuid}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    return res.refine((item) => ({ props: { item } })).get();
}
