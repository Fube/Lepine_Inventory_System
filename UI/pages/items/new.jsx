import Head from "next/head";
import { useRouter } from "next/router";
import Nav from "../../components/Nav";
import ItemBase from "../../components/ItemForm";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import checkEmptyAuth from "../../utils/checkEmptyAuth";
import roleRouteMappings from "../../config/routeRoleMapping";
import useAuth from "../../hooks/useAuth";
import { useEffect } from "react";
import clientRedirectHelper from "../../utils/clientRedirectHelper";

export default function CreateItem() {
    const router = useRouter();
    const { role } = useAuth();

    useEffect(
        clientRedirectHelper(
            roleRouteMappings.get(router.asPath),
            role.toLocaleLowerCase(),
            router
        ),
        []
    );

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

export async function getServerSideProps(ctx) {
    return checkEmptyAuth(axiosBackendAuth, ctx);
}
