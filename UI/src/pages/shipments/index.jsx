import Head from "next/head";
import Link from "next/link";
import { useState } from "react";
import { Icon } from "@iconify/react";
import * as yup from "yup";
import Paginate from "../../components/Pagination";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";
import thou from "../../utils/thou";
import capitalize from "capitalize";
import axios from "axios";

/**
 * @param {{ shipments: import('@lepine/ui-types').Shipment[] } & import("@lepine/ui-types").Pagination} param0
 */
export default function ShowShipments({
    shipments: rawShipments,
    totalPages,
    pageNumber,
}) {
    const { role } = useAuth();
    const [shipments, setShipments] = useState(rawShipments);

    const header = (
        <Head>
            <title>Shipments</title>
        </Head>
    );

    const fallback = (
        <h2 className="text-2xl text-center text-yellow-400">
            No shipments to show 😢
        </h2>
    );

    const patchStatus = async (uuid, status) => {
        try {
            await axiosAPI.patch(
                `/shipments/${uuid}`,
                [
                    {
                        op: "replace",
                        path: "/status",
                        value: status,
                    },
                ],
                {
                    headers: { "Content-Type": "application/json-patch+json" },
                }
            );
        } catch (error) {
            console.error(error);
            return false;
        }

        return true;
    };

    const handleAccept = async (uuid) => {
        const success = await patchStatus(uuid, "ACCEPTED");
        if (!success) return;

        const target = shipments.findIndex(
            (shipment) => shipment.uuid === uuid
        );
        shipments[target].status = "ACCEPTED";

        setShipments([...shipments]);
    };

    const handleDeny = async (uuid) => {
        const success = await patchStatus(uuid, "DENIED");
        if (!success) return;

        const target = shipments.findIndex(
            (shipment) => shipment.uuid === uuid
        );
        shipments[target].status = "DENIED";

        setShipments([...shipments]);
    };

    const head = (
        <tr>
            <th>Order Number</th>
            <th>Shipment Date</th>

            {thou(
                <th className="flex justify-between">
                    <div className="self-center">Status</div>
                    <button>
                        <Link href="/shipments/new" passHref>
                            <Icon icon="si-glyph:button-plus" width="32" />
                        </Link>
                    </button>
                </th>
            )
                .or(<th>Status</th>)
                .if(role === "SALESPERSON")}

            {(role === "MANAGER" || role === "CLERK") && (
                <th className="flex justify-between">
                    {thou(
                        <>
                            <div className="self-center">Actions</div>
                            <button>
                                <Link href="/shipments/new" passHref>
                                    <Icon
                                        icon="si-glyph:button-plus"
                                        width="32"
                                    />
                                </Link>
                            </button>
                        </>
                    )
                        .or("Actions")
                        .if(role === "MANAGER")}
                </th>
            )}
        </tr>
    );

    if (shipments.length <= 0) {
        return (
            <>
                {header}
                <main className="flex justify-center">
                    <div className="text-center">
                        <div className="mt-12">{fallback}</div>
                        {(role === "MANAGER" || role === "SALESPERSON") && (
                            <Link href="/shipments/new" passHref>
                                <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mt-12">
                                    Add One Now!
                                </button>
                            </Link>
                        )}
                    </div>
                </main>
            </>
        );
    }

    return (
        <>
            {header}

            <div className="overflow-x-auto justify-center flex">
                <div className="md:w-3/4 lg:w-1/2 w-full">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4">Shipments</h1>
                    </div>
                    <table className="table table-zebra w-full sm:table-fixed">
                        <thead>{head}</thead>
                        <tbody>
                            {shipments.map((shipment) => (
                                <ShipmentTableRow
                                    onAccept={handleAccept}
                                    onDeny={handleDeny}
                                    {...shipment}
                                    key={shipment.uuid}
                                />
                            ))}
                        </tbody>
                    </table>
                    <div className="flex justify-center mt-4">
                        <Paginate
                            onPageChange={(page) =>
                                router.push(`/warehouses?page=${page}`)
                            }
                            totalPages={totalPages}
                            pageNumber={pageNumber}
                        />
                    </div>
                </div>
            </div>
        </>
    );
}

/**
 *
 * @param {import('@lepine/ui-types').Shipment} param0
 */
function ShipmentTableRow({
    uuid,
    status,
    orderNumber,
    createdBy,
    to,
    transfers,
    expectedDate,
    onAccept = () => {},
    onDeny = () => {},
}) {
    const [showTransfers, setShowTransfers] = useState(false);
    const [isInConfirmationMode, setIsInConfirmationMode] = useState(false);
    const [confirmations, setConfirmations] = useState(new Map());
    const [responseError, setResponseError] = useState(null);

    const { role } = useAuth();
    const withNoPropagation = (fn) => (e) => {
        e.stopPropagation();
        return fn(e);
    };

    const handleConfirm = () => {
        setIsInConfirmationMode(true);
        setShowTransfers(true);
    };

    const handleCloseModal = () => {
        setIsInConfirmationMode(false);
        setShowTransfers(false);
    };

    const handleConfirmationChange = (uuid, quantity) => {
        if (quantity <= 0) {
            confirmations.delete(uuid);
            return setConfirmations(new Map(confirmations));
        }

        confirmations.set(uuid, quantity);
    };

    const handleSubmitConfirmations = async () => {
        try {
            for (const [transferUuid, quantity] of confirmations) {
                await axiosAPI.post("/confirmations", {
                    transferUuid,
                    quantity,
                });
            }

            setResponseError(null);
            setConfirmations(new Map());
            setIsInConfirmationMode(false);
            setShowTransfers(false);
        } catch (error) {
            setResponseError(
                error?.response?.data?.message ?? "Something went wrong"
            );
        }
    };

    return (
        // <Link key={uuid} href={`/shipments/${uuid}`} passHref>

        <>
            <tr onClick={() => setShowTransfers(true)} className="hover">
                <td>{orderNumber}</td>
                <td>{new Date(expectedDate).toDateString()}</td>
                <td>{capitalize(status)}</td>
                {role === "MANAGER" && (
                    <td>
                        {thou(
                            <>
                                <button
                                    onClick={withNoPropagation(() =>
                                        onAccept(uuid)
                                    )}
                                    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                                >
                                    Accept
                                </button>
                                <button
                                    onClick={withNoPropagation(() =>
                                        onDeny(uuid)
                                    )}
                                    className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded ml-4"
                                >
                                    Deny
                                </button>
                            </>
                        )
                            .or("None Available")
                            .if(status.toUpperCase() === "PENDING")}
                    </td>
                )}
                {role === "CLERK" && (
                    <td className="flex justify-center">
                        <button
                            onClick={withNoPropagation(handleConfirm)}
                            className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                        >
                            Confirm
                        </button>
                    </td>
                )}
            </tr>

            <input
                type="checkbox"
                id={`${uuid}-transfers`}
                className="modal-toggle"
                checked={showTransfers}
            />
            <div onClick={handleCloseModal} className="modal">
                <div
                    onClick={withNoPropagation(() => {})}
                    className="modal-box"
                    style={{
                        maxWidth: "unset",
                        width: "700px",
                    }}
                >
                    <div className="flex justify-between items-center">
                        <h1 className="text-2xl">Order {orderNumber}</h1>
                    </div>

                    {isInConfirmationMode && responseError !== null && (
                        <div className="text-red-500 text-center">
                            {responseError}
                        </div>
                    )}

                    <table className="table table-zebra w-full sm:table-fixed">
                        <thead>
                            <tr>
                                <th>From</th>
                                <th>Item</th>
                                <th>Quantity</th>
                            </tr>
                        </thead>
                        <tbody>
                            {transfers.map((transfer) => (
                                <tr className="hover" key={transfer.uuid}>
                                    <td>
                                        {transfer.stock.warehouse.city} -{" "}
                                        {transfer.stock.warehouse.zipCode}
                                    </td>
                                    <td>
                                        {transfer.stock.item.name} -{" "}
                                        {transfer.stock.item.sku}
                                    </td>
                                    <td>
                                        {thou(
                                            <NumberInputWithButtons
                                                validationSchema={yup
                                                    .number()
                                                    .min(0)
                                                    .max(transfer.quantity)}
                                                onChange={(quantity) =>
                                                    handleConfirmationChange(
                                                        transfer.uuid,
                                                        quantity
                                                    )
                                                }
                                            />
                                        )
                                            .or(transfer.quantity)
                                            .if(isInConfirmationMode)}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    <div className="modal-action">
                        {thou(
                            <label
                                onClick={handleSubmitConfirmations}
                                htmlFor={`${uuid}-transfers`}
                                className="btn"
                            >
                                Confirm
                            </label>
                        )
                            .or(
                                <label
                                    onClick={handleCloseModal}
                                    htmlFor={`${uuid}-transfers`}
                                    className="btn"
                                >
                                    Close
                                </label>
                            )
                            .if(isInConfirmationMode)}
                    </div>
                </div>
            </div>
        </>
        // </Link>
    );
}

function NumberInputWithButtons({
    validationSchema = null,
    onChange = () => {},
}) {
    const [input, setInput] = useState(0);

    const increment = () => validateAndUpdate(input + 1);
    const decrement = () => validateAndUpdate(input - 1);

    const validateAndUpdate = async (value) => {
        if (!validationSchema) return setInput(value);
        try {
            if (validationSchema) await validationSchema.validate(value);
            setInput(value);
            onChange(value);
        } catch (e) {
            console.error(e);
        }
    };

    const handleOnChange = (e) => validateAndUpdate(e.target.value);

    return (
        <>
            <div className="relative">
                <button
                    onClick={decrement}
                    className="absolute left-0 top-0 rounded-r-none btn btn-square"
                >
                    -
                </button>
                <input
                    value={input}
                    onChange={handleOnChange}
                    type="text"
                    className="w-full text-center px-12 input input-bordered"
                />
                <button
                    onClick={increment}
                    className="absolute right-0 top-0 rounded-l-none btn btn-square"
                >
                    +
                </button>
            </div>
        </>
    );
}

/**
 *
 * @param {import("next").GetServerSidePropsContext} context
 * @returns
 */
export async function getServerSideProps(context) {
    const page = context.query.page || 1;
    const res = await axiosBackendAuth.get(`/shipments?page=${page}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    return res
        .refine(({ content: shipments, totalPages, number: pageNumber }) => ({
            props: {
                shipments,
                totalPages,
                pageNumber,
            },
        }))
        .get();
}
