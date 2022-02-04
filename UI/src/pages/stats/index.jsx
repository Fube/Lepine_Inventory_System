import { useState } from "react";
import Head from "next/head";
import { DateTime } from "luxon-business-days";
import { axiosBackendAuth } from "../../config/axios";
import Paginate from "../../components/Pagination";

/**
 * @param {{
 * shipments: import('@lepine/ui-types').Shipment[],
 * totalPages: number,
 * pageNumber: number,
 * from: string,
 * to: string,
 * }}
 */
export default function ShowStats({
    shipments,
    totalPages,
    pageNumber,
    from,
    to,
}) {
    const header = (
        <Head>
            <title>Stats</title>
        </Head>
    );

    const fallback = (
        <h2 className="text-2xl text-center text-yellow-400">
            No shipments to show 😢
        </h2>
    );

    if (shipments.length <= 0) {
        return (
            <>
                {header}
                <main className="flex justify-center">
                    <div className="text-center">
                        <div className="mt-12">{fallback}</div>
                    </div>
                </main>
            </>
        );
    }

    const head = (
        <tr>
            <th>Order Number</th>
            <th>Shipment Date</th>
        </tr>
    );

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
                                    {...shipment}
                                    key={shipment.uuid}
                                />
                            ))}
                        </tbody>
                    </table>
                    <div className="flex justify-center mt-4">
                        <Paginate
                            onPageChange={(page) =>
                                router.push(
                                    `/stats?page=${page}&from=${from}&to=${to}`
                                )
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
}) {
    const [showTransfers, setShowTransfers] = useState(false);
    const withNoPropagation = (fn) => (e) => {
        e.stopPropagation();
        return fn(e);
    };
    const handleCloseModal = () => {
        setShowTransfers(false);
    };

    return (
        // <Link key={uuid} href={`/shipments/${uuid}`} passHref>

        <>
            <tr onClick={() => setShowTransfers(true)} className="hover">
                <td>{orderNumber}</td>
                <td>{new Date(expectedDate).toDateString()}</td>
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
                            onClick={handleCloseModal}
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
    const from = context.query.from || DateTime.now().startOf("week").toISO();
    const to = context.query.to || DateTime.now().endOf("week").toISO();

    const res = await axiosBackendAuth.get(
        `/shipments?confirmed=true&from=${from}&to=${to}&page=${page}`,
        {
            headers: { cookie: context?.req?.headers?.cookie ?? "" },
        }
    );

    return res
        .refine(({ content: shipments, totalPages, number: pageNumber }) => ({
            props: {
                shipments,
                totalPages,
                pageNumber,
                from,
                to,
            },
        }))
        .get();
}
