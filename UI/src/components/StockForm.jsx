import { Formik } from "formik";
import { useState, useContext } from "react";
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

/**
 * @param {{
 * editable: boolean,
 * deletable: boolean,
 * handleDelete: (uuid: string) => void,
 * handleSubmit: ({ values, setSubmitting: (isSubmitting: boolean)=>void }) => void
 * title: string,
 * warehouses: import('@lepine/ui-types').Warehouse[] }
 * & import('@lepine/ui-types').Stock }
 */

export default function StockForm({
    uuid,
    item,
    warehouse,
    warehouses = [],
    quantity = "",
    editable,
    deletable,
    disabled = new Set(),
    title,
    handleDelete = () => {},
    handleSubmit = () => {},
}) {
    const { searchClient } = useContext(AlgoliaContext);

    const mappedWarehouses = warehouses.map((warehouse) => ({
        key: `${warehouse.city}, ${warehouse.province} - ${warehouse.zipCode}`,
        value: warehouse.uuid,
    }));

    const stockSchema = yup.object().shape({
        itemUuid: yup.string().required("Item is required"),
        warehouseUuid: yup.string().required("Warehouse is required"),
        quantity: yup.number().required("Quantity is required"),
    });

    const initialValues = {
        itemUuid: item?.uuid,
        warehouseUuid: warehouse?.uuid,
        quantity: quantity,
    };

    const wrapAsDummy = (hit) => (
        <span className="text-black">
            {hit.sku} - {hit.name}
        </span>
    );

    console.log(disabled);

    return (
        <>
            <Formik
                initialValues={initialValues}
                validationSchema={stockSchema}
                onSubmit={handleSubmit}
            >
                {({ setFieldValue }) => (
                    <GenericForm title={title}>
                        <GenericErrorStatus />

                        <GenericFormSelectErrorCombo
                            disabled={
                                !editable || disabled.has("warehouseUuid")
                            }
                            name="warehouseUuid"
                            placeholder="Warehouse"
                            title="Select a Warehouse"
                            options={mappedWarehouses}
                            onChange={(e) =>
                                setFieldValue("warehouseUuid", e.target.value)
                            }
                        />

                        <span className="block text-gray-700 text-sm font-bold mb-2">
                            Item
                        </span>

                        {thou(
                            <AlgoliaSearchAsDropDown
                                searchClient={searchClient}
                                indexName="items"
                                hitComponent={ItemHitAdapter}
                                initialDummyValue={item && wrapAsDummy(item)}
                                hitAsDummy={wrapAsDummy}
                                onSelect={(hit) =>
                                    setFieldValue("itemUuid", hit.objectID)
                                }
                                onReset={() => setFieldValue("itemUuid", "")}
                            />
                        )
                            .or(item ? wrapAsDummy(item) : "Unknown item")
                            .if(editable && !disabled.has("itemUuid"))}

                        <GenericFormInputErrorCombo
                            disabled={!editable || disabled.has("quantity")}
                            name={`quantity`}
                            type="number"
                            placeholder="Quantity"
                            min={1}
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

/**
 *
 * @param {{ hit: import('@lepine/ui-types').Item & { objectID: string } }} param0
 * @returns
 */
function ItemHitAdapter({ hit: { objectID: uuid, description, name, sku } }) {
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
    initialDummyValue,
    hitAsDummy,
    searchClient,
    onSelect = () => {},
    onReset = () => {},
}) {
    const [showHits, setShowHits] = useState(false);
    const [dummySearch, setDummySearch] = useState(initialDummyValue || null);
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
                <Configure />
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
