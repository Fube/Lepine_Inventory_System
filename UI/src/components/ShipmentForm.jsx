import { Formik, FieldArray } from "formik";
import { useEffect, useState, useContext } from "react";
import * as yup from "yup";
import { DateTime } from "luxon-business-days";
import {
    GenericErrorStatus,
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
    GenericFormSelectErrorCombo,
    DatePickerField,
} from "./FormikGenericComponents";

import "react-datepicker/dist/react-datepicker.css";
import { Icon } from "@iconify/react";
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
 * warehouses: import('@lepine/ui-types').Warehouse[], }
 * & import('@lepine/ui-types').Shipment }
 */
export default function ShipmentForm({
    uuid,
    status = "",
    transfers = [
        {
            quantity: 1,
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
    const { searchClient } = useContext(AlgoliaContext);
    const [algoliaFilter, setAlgoliaFilter] = useState("quantity > 0");
    const [selectedStockUuids, setSelectedStockUuids] = useState(new Set());
    const [selectedWarehouseUuid, setSelectedWarehouseUuid] = useState("");

    useEffect(() => {
        if (!selectedStockUuids || !selectedWarehouseUuid) return;

        let baseQuery = `quantity > 0`;

        if (selectedWarehouseUuid.length > 0) {
            baseQuery += ` AND NOT warehouseUuid:"${selectedWarehouseUuid}"`;
        }

        if (selectedStockUuids.size > 0) {
            for (const stockUuid of selectedStockUuids) {
                baseQuery += ` AND NOT objectID:"${stockUuid}"`;
            }
        }

        console.log(baseQuery);
        setAlgoliaFilter(baseQuery);
    }, [selectedStockUuids, selectedWarehouseUuid]);

    const mappedWarehouses = warehouses.map((warehouse) => ({
        key: `${warehouse.city}, ${warehouse.province} - ${warehouse.zipCode}`,
        value: warehouse.uuid,
    }));

    const shipmentSchema = yup.object().shape({
        orderNumber: yup.string().required("Order Number is required"),
        expectedDate: yup
            .date()
            .businessDay()
            .required("Expected Date is required"),
        to: yup.string().required("To is required"),
        transfers: yup
            .array()
            .of(
                yup.object().shape({
                    quantity: yup
                        .number()
                        .min(1, "Quantity must be greater than or equal to 1")
                        .required("Quantity is required"),
                    stockUuid: yup.string().required("Stock is required"),
                })
            )
            .min(1, "At least one transfer is required"),
    });

    /**
     *
     * @param {Date} date
     * @returns
     */
    const isAcceptableDate = (date) =>
        DateTime.fromJSDate(date).isBusinessDay();

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
                {({ values, setFieldValue }) => (
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

                        <DatePickerField
                            name="expectedDate"
                            placeholder="Expected Date"
                            minDate={DateTime.local()
                                .endOf("day")
                                .plusBusiness({ days: 4 })
                                .toJSDate()}
                            filterDate={isAcceptableDate}
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

                        <div className="flex items-center justify-center p-6 pt-2">
                            {editable && <GenericSubmitButton text="Save" />}
                        </div>
                    </GenericForm>
                )}
            </Formik>
        </>
    );
}

function AlgoliaStockOptionHit({
    hit: { objectID: uuid, description, name, sku, zipCode, quantity },
}) {
    return (
        <li value={uuid} className="border-2 hover:bg-blue-300 mb-2 p-2">
            <div className="flex items-center">
                <div className="ml-2">
                    <div className="text-sm leading-5 font-medium text-base-300">
                        {name}
                    </div>
                    <div className="text-base-200">
                        <div className="text-sm leading-5">
                            {sku} - {description}
                        </div>
                        <div className="text-sm leading-5 flex gap-6">
                            <div className="flex">
                                <span className="inline-flex items-center px-1.5 py-0.5 rounded-full text-xs font-medium leading-4 bg-green-100 text-green-800">
                                    <Icon
                                        icon="ic:baseline-warehouse"
                                        height={24}
                                    />
                                </span>
                                <span className="self-center">{zipCode}</span>
                            </div>

                            <div className="flex">
                                <span className="inline-flex items-center px-1.5 py-0.5 rounded-full text-xs font-medium leading-4 bg-green-100 text-green-800">
                                    <Icon
                                        icon="ic:baseline-plus-minus-alt"
                                        height={24}
                                    />
                                </span>
                                <span className="self-center">{quantity}</span>
                            </div>
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