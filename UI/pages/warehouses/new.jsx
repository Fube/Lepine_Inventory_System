import { Formik } from "formik";
import Head from "next/head";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import GooglePlacesAutocomplete, {
    geocodeByPlaceId,
} from "react-google-places-autocomplete";
import * as yup from "yup";
import {
    GenericErrorStatus,
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
} from "../../components/FormikGenericComponents";
import Nav from "../../components/Nav";
import { axiosAPI } from "../../config/axios";

export default function CreateWarehouse() {
    const router = useRouter();

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
                        <Formik
                            initialValues={{
                                zipCode: "",
                                city: "",
                                province: "",
                            }}
                            validationSchema={warehouseSchema}
                            onSubmit={handleSubmit}
                        >
                            {({ setFieldValue }) => (
                                <GenericForm title="Create Warehouse">
                                    <GenericErrorStatus />
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
                                    <h3 className="text-center text-4xl text-black">
                                        OR
                                    </h3>
                                    <GoogleLocationFormikCombo
                                        setFieldValue={setFieldValue}
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

function GoogleLocationFormikCombo({ setFieldValue }) {
    const [autoComplete, setAutoComplete] = useState(null);

    const handleAutoComplete = async (placeId) => {
        const result = await geocodeByPlaceId(placeId);
        const zipCode = result[0].address_components.find((component) =>
            component.types.includes("postal_code")
        );
        const city = result[0].address_components.find((component) =>
            component.types.includes("locality")
        );
        const province = result[0].address_components.find((component) =>
            component.types.includes("administrative_area_level_1")
        );
        const [zc, ct, pv] = [zipCode, city, province].map(
            (component) => component.long_name
        );

        setFieldValue("zipCode", zc);
        setFieldValue("city", ct);
        setFieldValue("province", pv);
    };

    useEffect(() => {
        if (!autoComplete) return;
        console.log(autoComplete);
        handleAutoComplete(autoComplete.value.place_id);
    }, [autoComplete]);
    return (
        <div className="text-black">
            <GooglePlacesAutocomplete
                apiKey={process.env.NEXT_PUBLIC_GOOGLE_PLACES_API_KEY}
                selectProps={{
                    value: autoComplete,
                    onChange: setAutoComplete,
                }}
            />
        </div>
    );
}
