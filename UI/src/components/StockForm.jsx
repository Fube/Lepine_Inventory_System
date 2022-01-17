import { useEffect, useState } from "react";
import { Formik } from "formik";
import * as yup from "yup";
import {
    GenericErrorStatus,
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
} from "./FormikGenericComponents";

const rawSchema= {
    item: yup.string().required("Item is required"),
    warehouse: yup.string().required("Warehouse is required"),
    quantity: yup.number().required("Quantity is required"),  
};

/**
 * @param {{
 * editable: boolean,
 * deletable: boolean,
 * handleDelete: (uuid: string) => void,
 * handleSubmit: ({ values, setSubmitting: (isSubmitting: boolean)=>void }) => void }
 * title: string,
 * & import('../../types').Stock } )
 */

export default function StockForm({
    uuid,
    item = "",
    warehouse = "",
    quantity = "",
    editable,
    deletable,
    title,
    handleDelete = () => {},
    handleSubmit = () => {},
    blackList = [],
}) {
    const filterOut = (toFilter) => {
        return Object.entries(toFilter).reduce((acc, [key, value]) => {
            if (blackList.includes(key)) return acc;
            return {
                ...acc,
                [key]: value,
            };
        }, {});
    };

    const stockSchema = yup.object().shape(filterOut(rawSchema));

    const fields = {
        item: (
            <GenericFormInputErrorCombo
                disabled={!editable}
                name="item"
                type="text"
                placeholder="Item"
            />
        ),
        warehouse: (
            <GenericFormInputErrorCombo
                disabled={!editable}
                name="warehouse"
                type="text"
                placeholder="Warehouse"
            />
        ),
        quantity: (
            <GenericFormInputErrorCombo
                disabled={!editable}
                name="quantity"
                type="number"
                placeholder="Quantity"
            />
        ),
    };

    const initialValues = {
        item,
        warehouse,
        quantity,
    };

    return (
        <>
            <Formik
                initialValues={filterOut(initialValues)}
                validationSchema={stockSchema}
                onSubmit={handleSubmit}
                >
                {() => (
                    <GenericForm title={title}>
                        <GenericErrorStatus />
                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="item"
                            type="text"
                            placeholder="Item"
                        />

                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="warehouse"
                            type="text"
                            placeholder="Warehouse"
                        />

                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="quantity"
                            type="number"
                            placeholder="Quantity"
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