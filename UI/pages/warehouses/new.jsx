import { Formik } from "formik";
import Head from "next/head";
import * as yup from "yup";
import {
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
} from "../../components/FormikGenericComponents";
import Nav from "../../components/Nav";

export default function CreateWarehouse() {
    const warehouseSchema = yup.object().shape({
        zipCode: yup
            .string()
            .required("Zip Code is required")
            .matches(
                /^[a-zA-Z][0-9][a-zA-Z] ?[0-9][a-zA-Z][0-9]$/,
                "Zip Code must be valid"
            ),
        city: yup.string().required("City is required"),
        province: yup.string().required("Province is required"),
    });

    const handleSubmit = async (values, { setSubmitting }) => {};

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
                        <Formik
                            initialValues={{
                                zipCode: "",
                                city: "",
                                province: "",
                            }}
                            validationSchema={warehouseSchema}
                            onSubmit={handleSubmit}
                        >
                            {() => (
                                <GenericForm title="Create Warehouse">
                                    <GenericFormInputErrorCombo
                                        name="zipCode"
                                        type="text"
                                        placeholder="Zip Code"
                                    />

                                    <GenericFormInputErrorCombo
                                        name="city"
                                        type="text"
                                        placeholder="City"
                                    />

                                    <GenericFormInputErrorCombo
                                        name="province"
                                        type="text"
                                        placeholder="Province"
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
