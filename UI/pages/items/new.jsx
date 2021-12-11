import Head from "next/head";
import Nav from "../../components/Nav";
import ItemBase from "../../components/Item";
import { axiosAPI } from "../../config/axios";

export default function CreateItem() {
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
                            }}
                        />
                    </div>
                </div>
            </div>
        </>
    );
}
