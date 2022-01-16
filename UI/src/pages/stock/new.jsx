
import { Formik } from "formik";
import Head from "next/head";
import * as yup from "yup";
import {
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
} from "../../components/FormikGenericComponents";
import Nav from "../../components/Nav";

export default function CreateStock(){
    const stockSchema= yup.object().shape({
        item: yup
        .string()
        .required('Item is required'),
        warehouse: yup
        .string()
        .required('Warehouse is required'),
        quantity: yup
        .number()
        .required('Quantity is required'),
    });

    const handleSubmit = async (values, {setSubmitting}) => {};

    return (
        <>
            <Head>
                <title>Create Stock</title>
            </Head>
            <div className="flex flex-col h-screen">
                <div className="flex-shrink-0 flex-grow-0">
                    <Nav />
                </div>
                <div className="flex-grow flex justify-center items-center">
                <div className="w-full">
                        <Formik
                            initialValues={{
                                item: "",
                                warehouse: "",
                                quantity: "",
                            }}
                            validationSchema={stockSchema}
                            onSubmit={handleSubmit}
                        >
                            {() => (
                                <GenericForm title="Create Stock">
                                    <GenericFormInputErrorCombo
                                        name="item"
                                        type="text"
                                        placeholder="Item"
                                    />

                                    <GenericFormInputErrorCombo
                                        name="warehouse"
                                        type="text"
                                        placeholder="Warehouse"
                                    />

                                    <GenericFormInputErrorCombo
                                        name="quantity"
                                        type="number"
                                        placeholder="Quantity"
                                    />
                                    <div className="flex items-center justify-end p-6">
                                        <GenericSubmitButton text="Save" />
                                    </div>
                                </GenericForm>
                            )}
                        </Formik>
                    </div>
                </div>
            </div>
        </>
    );
}