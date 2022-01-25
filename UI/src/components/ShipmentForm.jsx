import { Formik, Field, FieldArray } from "formik";
import { useEffect, useState } from "react";
import * as yup from "yup";
import {
    GenericErrorStatus,
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
    GenericFormSelectErrorCombo,
    DatePickerField,
} from "./FormikGenericComponents";

import "react-datepicker/dist/react-datepicker.css";

/**
 * @param {{
 * editable: boolean,
 * deletable: boolean,
 * handleDelete: (uuid: string) => void,
 * handleSubmit: ({ values, setSubmitting: (isSubmitting: boolean)=>void }) => void
 * title: string,
 * warehouses: import('@lepine/ui-types').Warehouse[], }
 * & import('@lepine/ui-types').Shipment }
 */
export default function ShipmentForm({
    uuid,
    status = "",
    transfers = [
        {
            quantity: 0,
            stock: "",
        },
    ],
    expectedDate = "",
    orderNumber = "",
    createdBy = "",
    to = "",
    editable,
    deletable,
    title,
    warehouses = [],
    handleDelete = () => {},
    handleSubmit = () => {},
}) {
    const mappedWarehouses = warehouses.map((warehouse) => ({
        key: `${warehouse.city}, ${warehouse.province} - ${warehouse.zipCode}`,
        value: warehouse.uuid,
    }));

    const shipmentSchema = yup.object().shape({
        orderNumber: yup.string().required("Order Number is required"),
    });

    return (
        <>
            <Formik
                initialValues={{
                    uuid,
                    status,
                    transfers,
                    expectedDate,
                    orderNumber,
                    createdBy,
                    to,
                }}
                onSubmit={handleSubmit}
                validationSchema={shipmentSchema}
            >
                {({}) => (
                    <GenericForm title={title}>
                        <GenericErrorStatus />
                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="orderNumber"
                            type="text"
                            placeholder="Order Number"
                        />
                        <GenericFormSelectErrorCombo
                            disabled={!editable}
                            name="to"
                            placeholder="To"
                            title="Select a warehouse"
                            options={mappedWarehouses}
                        />

                        <DatePickerField
                            name="expectedDate"
                            placeholder="Expected Date"
                            minDate={
                                new Date(
                                    new Date().getTime() +
                                        3 * 24 * 60 * 60 * 1000
                                )
                            }
                        />

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
