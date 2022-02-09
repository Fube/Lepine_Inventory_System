import { useEffect, useState, useContext } from "react";
import { Formik, FieldArray } from "formik";
import * as yup from "yup";
import {
    GenericErrorStatus,
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
    GenericFormSelectErrorCombo,
} from "./FormikGenericComponents";

import { connectHits, InstantSearch } from "react-instantsearch-core";
import { SearchBox, Configure } from "react-instantsearch-dom";
import { AlgoliaContext } from "../pages/_app";
import thou from "../utils/thou";

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
 * warehouses: import('@lepine/ui-types').Warehouse[],
 * items: import('@lepine/ui-types').Item[], 
 * import('@lepine/ui-types').Stock } )
 */

export default function StockForm({
    uuid,
    item = [],
    warehouse = [],
    quantity = "",
    editable,
    deletable,
    title,
    handleDelete = () => {},
    handleSubmit = () => {},
}) {

    // const filterOut = (toFilter) => {
    //     return Object.entries(toFilter).reduce((acc, [key, value]) => {
    //         if (blackList.includes(key)) return acc;
    //         return {
    //             ...acc,
    //             [key]: value,
    //         };
    //     }, {});
    // };

    const { searchClient } = useContext(AlgoliaContext);
    const [algoliaFilter, setAlgoliaFilter] = useState("quantity > 0");
    const [selectedItemUuids, setSelectedItemUuids] = useState(new Set());
    const [selectedWarehouseUuid, setSelectedWarehouseUuid] = useState("");

    // This needs to return active warehouse.

    // useEffect(() => {
    //     if (!selectedItemUuids || !selectedWarehouseUuid) return;

    //     let baseQuery = `quantity > 0`;

    //     if (selectedWarehouseUuid.length > 0) {
    //         baseQuery += ` AND NOT warehouseUuid:"${selectedWarehouseUuid}"`;
    //     }

    //     if (selectedItemUuids.size > 0) {
    //         for (const itemUuid of selectedItemUuids) {
    //             baseQuery += ` AND NOT objectID:"${itemUuid}"`;
    //         }
    //     }

    //     console.log(baseQuery);
    //     setAlgoliaFilter(baseQuery);
    // }, [selectedItemUuids, selectedWarehouseUuid]);
    
     const mappedWarehouses = warehouses.map((warehouse) => ({
        key: `${warehouse.city}, ${warehouse.province} - ${warehouse.zipCode}`,
        value: warehouse.uuid,
    }));
    
    const mappedItems = items.map((item) => ({
        key: `${item.sku}, ${item.name}, ${item.sku}`,
        value: item.uuid,
    }));


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
            <GenericFormSelectErrorCombo
            disabled={!editable}
            name="warehouse"
            title="Select a warehouse"
            options={mappedWarehouses}
            onChange={(e) => {
                const warehouseUuid = e.target.value;

                // Send to end of event loop
                setTimeout(
                    () => setFieldValue("to", warehouseUuid),
                    0
                );

                setSelectedWarehouseUuid(warehouseUuid);
            }}
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
                        
                        {Object.values(filterOut(fields))}
                       
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
