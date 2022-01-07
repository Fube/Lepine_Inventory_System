import { useEffect, useState } from "react";
import { Formik } from "formik";
import * as yup from "yup";
import GooglePlacesAutocomplete, {
    geocodeByPlaceId,
} from "react-google-places-autocomplete";
import {
    GenericErrorStatus,
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
} from "./FormikGenericComponents";

/**
 * @param {{
 * editable: boolean,
 * deletable: boolean,
 * handleDelete: (uuid: string) => void,
 * handleSubmit: ({ values, setSubmitting: (isSubmitting: boolean)=>void }) => void }
 * & import('@lepine/types').Warehouse } )
 */
export default function WarehouseForm({
    uuid,
    zipCode = "",
    city = "",
    province = "",
    active,
    editable,
    deletable,
    handleDelete = () => {},
    handleSubmit = () => {},
    blackList = [],
}) {
    const rawSchema = {
        zipCode: yup
            .string()
            .required("Zip Code is required")
            .matches(
                /^[a-zA-Z][0-9][a-zA-Z] ?[0-9][a-zA-Z][0-9]$/,
                "Zip Code must be valid"
            ),
        city: yup.string().required("City is required"),
        province: yup.string().required("Province is required"),
        active: yup.boolean(),
    };
    const warehouseSchema = yup.object().shape(
        // Filter out fields in blacklist from rawSchema
        Object.entries(rawSchema).reduce((acc, [key, value]) => {
            if (blackList.includes(key)) return acc;
            return {
                ...acc,
                [key]: value,
            };
        }, {})
    );
    const fields = {
        zipCode: (
            <GenericFormInputErrorCombo
                disabled={!editable}
                name="zipCode"
                type="text"
                placeholder="Zip Code"
            />
        ),
        city: (
            <GenericFormInputErrorCombo
                disabled={!editable}
                name="city"
                type="text"
                placeholder="City"
            />
        ),
        province: (
            <GenericFormInputErrorCombo
                disabled={!editable}
                name="province"
                type="text"
                placeholder="Province"
            />
        ),
        active: (
            <GenericFormInputErrorCombo
                disabled={!editable}
                name="active"
                type="checkbox"
                placeholder="Active"
            />
        ),
    };

    return (
        <>
            <Formik
                initialValues={{
                    zipCode,
                    city,
                    province,
                }}
                validationSchema={warehouseSchema}
                onSubmit={handleSubmit}
            >
                {({ setFieldValue }) => (
                    <GenericForm title="Create Warehouse">
                        <GenericErrorStatus />
                        {Object.values(
                            Object.entries(fields).reduce(
                                (acc, [key, value]) => {
                                    if (blackList.includes(key)) return acc;
                                    return {
                                        ...acc,
                                        [key]: value,
                                    };
                                },
                                {}
                            )
                        )}
                        {editable && (
                            <>
                                <h3 className="text-center text-4xl text-black">
                                    OR
                                </h3>
                                <GoogleLocationFormikCombo
                                    setFieldValue={setFieldValue}
                                />
                            </>
                        )}
                        <div className="flex items-center justify-end p-6">
                            {editable && <GenericSubmitButton text="Save" />}
                            {deletable && (
                                <button
                                    className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded ml-4 focus:outline-none focus:shadow-outline"
                                    type="button"
                                    onClick={handleDelete}
                                >
                                    Delete
                                </button>
                            )}
                        </div>
                    </GenericForm>
                )}
            </Formik>
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
