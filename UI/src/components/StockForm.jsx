import { Formik, FieldArray } from "formik";
import { useEffect, useState, useContext } from "react";
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
 * handleSubmit: ({ values, setSubmitting: (isSubmitting: boolean)=>void }) => void
 * title: string,
 * warehouses: import('@lepine/ui-types').Warehouse[], 
 * items: import('@lepine/ui-types').Item[], }
 * & import('@lepine/ui-types').Stock }
 */

export default function StockForm({
    uuid,
    items = [],
    warehouses = [],
    quantity = "",
    editable,
    deletable,
    title,
    handleDelete = () => {},
    handleSubmit = () => {},
    // blackList = [],
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


    useEffect(() => {
        if (!selectedItemUuids || !selectedWarehouseUuid) return;

        let baseQuery = `quantity > 0`;

        if (selectedWarehouseUuid.length > 0) {
            baseQuery += ` AND NOT warehouseUuid:"${selectedWarehouseUuid}"`;
        }

        if (selectedItemUuids.size > 0) {
            for (const itemUuid of selectedItemUuids) {
                baseQuery += ` AND NOT objectID:"${itemUuid}"`;
            }
        }

        console.log(baseQuery);
        setAlgoliaFilter(baseQuery);
    }, [selectedItemUuids, selectedWarehouseUuid]);
    
    const mappedWarehouses = warehouses.map((warehouse) => ({
        key: `${warehouse.city}, ${warehouse.province} - ${warehouse.zipCode}`,
        value: warehouse.uuid,
    }));
    
    // const mappedItems = items.map((item) => ({
    //     key: `${item.sku}, ${item.name}, ${item.description}`,
    //     value: item.uuid,
    // }));


    //const stockSchema = yup.object().shape(filterOut(rawSchema));

    const stockSchema=  yup.object().shape({
        item: yup.string().required("Item is required"),
        warehouse: yup.string().required("Warehouse is required"),
        quantity: yup.number().required("Quantity is required"),  
    });

    
    return (
        <>
            <Formik
                initialValues={{
                        items,
                        warehouses,
                        quantity,
                    }}
                validationSchema={stockSchema}
                onSubmit={handleSubmit}
                >
                {({values, setFieldValue}) => (
                    <GenericForm title={title}>
                        <GenericErrorStatus />

                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="item"
                            type="text"
                            placeholder="Item"
                        />

                        <GenericFormSelectErrorCombo
                            disabled={!editable}
                            name="warehouse"
                            placeholder="Warehouse"
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
                        
                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="quantity"
                            type="number"
                            placeholder="Quantity"
                        />

{values.to && values.to.length > 0 && (
                            <>
                                <div className="divider before:!bg-base-300 after:!bg-base-300 mt-2" />
                                <span className="block text-gray-700 text-lg font-bold mb-2">
                                    Transfers
                                </span>

                                <FieldArray name="transfers">
                                    {({ remove, push }) => (
                                        <>
                                            {values.transfers.map(
                                                (transfer, index) => (
                                                    <div
                                                        key={index}
                                                        className="mb-6"
                                                    >
                                                        <AlgoliaSearchAsDropDown
                                                            filter={
                                                                algoliaFilter
                                                            }
                                                            hitComponent={
                                                                AlgoliaStockOptionHit
                                                            }
                                                            indexName="stocks"
                                                            searchClient={
                                                                searchClient
                                                            }
                                                            selectName={`transfers[${index}].stockUuid`}
                                                            hitAsDummy={(
                                                                hit
                                                            ) => {
                                                                setFieldValue(
                                                                    `transfers[${index}].stockUuid`,
                                                                    hit.objectID
                                                                );
                                                                return (
                                                                    <span className="text-black">
                                                                        {
                                                                            hit.sku
                                                                        }{" "}
                                                                        -{" "}
                                                                        {
                                                                            hit.name
                                                                        }
                                                                    </span>
                                                                );
                                                            }}
                                                            onSelect={(hit) => {
                                                                setSelectedStockUuids(
                                                                    new Set([
                                                                        ...selectedStockUuids,
                                                                        hit.objectID,
                                                                    ])
                                                                );
                                                            }}
                                                            onReset={(
                                                                lastHit
                                                            ) => {
                                                                selectedStockUuids.delete(
                                                                    lastHit.objectID
                                                                );
                                                                setSelectedStockUuids(
                                                                    [
                                                                        ...selectedStockUuids,
                                                                    ]
                                                                );
                                                            }}
                                                        />

                                                        <GenericFormInputErrorCombo
                                                            disabled={!editable}
                                                            name={`transfers.${index}.quantity`}
                                                            type="number"
                                                            placeholder="Quantity"
                                                            min={1}
                                                        />
                                                        {editable && (
                                                            <button
                                                                type="button"
                                                                className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline mt-2"
                                                                onClick={() =>
                                                                    remove(
                                                                        index
                                                                    )
                                                                }
                                                                disabled={
                                                                    !editable
                                                                }
                                                            >
                                                                <Icon icon="gridicons:trash" />
                                                            </button>
                                                        )}
                                                    </div>
                                                )
                                            )}
                                            <div className="flex justify-start mt-4">
                                                <button
                                                    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                                                    type="button"
                                                    onClick={() =>
                                                        push({
                                                            quantity: 1,
                                                            stock: "",
                                                        })
                                                    }
                                                >
                                                    Add transfer
                                                </button>
                                            </div>
                                        </>
                                    )}
                                </FieldArray>
                                <div className="divider before:!bg-base-300 after:!bg-base-300 mt-2 mb-0" />
                            </>
                        )}


                        

                        {/* before this  */}


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

function AlgoliaItemOptionHit({ 
    hit: {objectID: uuid, sku, description, name },
}) {
    return (
        <li value={uuid} className="border-2 hover:bg-blue-300 mb-2 p-2">
            <div className="flex items-center">
                <div className="ml-2">
                    <div className="text-sm leading-5 font-medium text-base-300">
                        {sku}
                    </div>
                    <div className="text-base-200">
                        <div className="text-sm leading-5">
                            {name} - {description}
                        </div>
                    </div>
                </div>
            </div>
        </li>
    );
}

function AlgoliaSelectHitsInternal({
    hits,
    hitComponent: HitComponent,
    fallbackComponent = <span className="text-red-500">Nothing found</span>,
    selectName,
    onSelect = () => {},
}) {
    if (!hits || hits.length <= 0) {
        return fallbackComponent;
    }

    const mapped = hits.map((hit) => (
        <span key={hit.objectID} onClick={() => onSelect(hit)}>
            <HitComponent hit={hit} />
        </span>
    ));

    return (
        <ul className="text-black" autoFocus={true} name={selectName}>
            {mapped}
        </ul>
    );
}

function AlgoliaSearchAsDropDown({
    selectName,
    indexName,
    hitComponent,
    hitAsDummy,
    searchClient,
    onSelect = () => {},
    onReset = () => {},
    filter = "",
}) {
    const [showHits, setShowHits] = useState(false);
    const [dummySearch, setDummySearch] = useState(null);
    const [lastHit, setLastHit] = useState(null);

    const handleSelect = (hit) => {
        setShowHits(false);
        setDummySearch(hitAsDummy(hit));
        setLastHit(hit);
        onSelect(hit);
    };

    const handleDummyClick = () => {
        onReset(lastHit);
        setDummySearch(null);
    };

    return thou(<div onClick={handleDummyClick}>{dummySearch}</div>)
        .or(
            <InstantSearch searchClient={searchClient} indexName={indexName}>
                <Configure filters={filter} />
                <SearchBox
                    className="text-black"
                    onChange={(e) =>
                        setShowHits(e?.currentTarget?.value?.length > 0)
                    }
                    autoFocus={true}
                />
                {showHits && (
                    <AlgoliaSelectHits
                        hitComponent={hitComponent}
                        selectName={selectName}
                        onSelect={handleSelect}
                    />
                )}
            </InstantSearch>
        )
        .if(dummySearch !== null);
}

const AlgoliaSelectHits = connectHits(AlgoliaSelectHitsInternal);