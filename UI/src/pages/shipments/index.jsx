import Head from "next/head";
import Link from "next/link";
import { useState } from "react";
import { Icon } from "@iconify/react";
import Paginate from "../../components/Pagination";
import { axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";
import thou from "../../utils/thou";
import capitalize from "capitalize";

/**
 * @param {{ shipments: import('@lepine/ui-types').Shipment[] } & import("@lepine/ui-types").Pagination} param0
 */
export default function ShowShipments({ shipments, totalPages, pageNumber }) {
    const { role } = useAuth();

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

    const handleAccept = async (uuid) => {
        console.log(`Accepting shipment ${uuid}`);
    };

    const handleDeny = async (uuid) => {
        console.log(`Denying shipment ${uuid}`);
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
                    <div className="self-center">Actions</div>
                    <button>
                        <Link href="/shipments/new" passHref>
                            <Icon icon="si-glyph:button-plus" width="32" />
                        </Link>
                    </button>
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
    const { role } = useAuth();

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
                                    onClick={() => onAccept(uuid)}
                                    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
                                >
                                    Accept
                                </button>
                                <button
                                    onClick={() => onDeny(uuid)}
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
            </tr>

            <input
                type="checkbox"
                id={`${uuid}-transfers`}
                className="modal-toggle"
                checked={showTransfers}
            />
            <div onClick={() => setShowTransfers(false)} className="modal">
                <div onClick={(e) => e.stopPropagation()} className="modal-box">
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
                                    <td>{transfer.quantity}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    <div className="modal-action">
                        <label
                            onClick={() => setShowTransfers(false)}
                            htmlFor={`${uuid}-transfers`}
                            className="btn"
                        >
                            Close
                        </label>
                    </div>
                </div>
            </div>
        </>
        // </Link>
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
