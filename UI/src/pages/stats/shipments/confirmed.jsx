import { DateTime } from "luxon-business-days";
import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import Link from "next/link";
import { useRouter } from "next/router";
import { useState } from "react";
import Paginate from "../../../components/Pagination";
import WithClientSideAuth from "../../../components/WithClientSideAuth";
import { axiosBackendAuth } from "../../../config/axios";
/**
 * @param {{
 * shipments: import('@lepine/ui-types').Shipment[],
 * totalPages: number,
 * pageNumber: number,
 * from: string,
 * to: string,
 * }}
 */
function ShowStatsTabular({ shipments, totalPages, pageNumber, from, to }) {
    const { t: tc } = useTranslation("common");
    const { t: tStats } = useTranslation("stats");
    const { t: tShipments } = useTranslation("shipments");

    const router = useRouter();

    const getNextURL = () => {
        // +One week from 'to'
        const nextTo = DateTime.fromISO(to).plus({ days: 7 });
        const nextFrom = to;

        return `?from=${encodeURIComponent(nextFrom)}&to=${encodeURIComponent(
            nextTo.toISO()
        )}`;
    };

    const getPrevURL = () => {
        // -One week from 'from'
        const prevTo = from;
        const prevFrom = DateTime.fromISO(from).minus({ days: 7 });

        return `?from=${encodeURIComponent(
            prevFrom.toISO()
        )}&to=${encodeURIComponent(prevTo)}`;
    };

    const header = (
        <Head>
            <title>{tStats("index.title")}</title>
        </Head>
    );

    const head = (
        <tr>
            <th>{tShipments("order_number")}</th>
            <th>{tShipments("shipment_date")}</th>
        </tr>
    );

    return (
        <>
            {header}

            <div className="overflow-x-auto justify-center flex">
                <div className="md:w-3/4 lg:w-1/2 w-full">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4 text-center">
                            <p className="mb-2">
                                {tStats("shipments.fully_confirmed")}
                            </p>
                            <p className="text-lg">
                                <Link className="text-3xl" href={getPrevURL()}>
                                    {"< "}
                                </Link>
                                {tc("between", {
                                    x: new Date(from).toLocaleDateString(),
                                    y: new Date(to).toLocaleDateString(),
                                })}
                                <Link className="text-3xl" href={getNextURL()}>
                                    {" >"}
                                </Link>
                            </p>
                        </h1>
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
                        <h1 className="text-2xl">
                            {tc("order")} {orderNumber}
                        </h1>
                    </div>

                    <table className="table table-zebra w-full sm:table-fixed">
                        <thead>
                            <tr>
                                <th>{tc("from")}</th>
                                <th>{tc("item")}</th>
                                <th>{tc("quantity")}</th>
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
                            {tc("close")}
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
        `/shipments?confirmed=true&from=${encodeURIComponent(
            from
        )}&to=${encodeURIComponent(to)}&page=${page}`,
        {
            headers: { cookie: context?.req?.headers?.cookie ?? "" },
        }
    );

    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "stats",
        "shipments",
    ]);

    return res
        .refine(({ content: shipments, totalPages, number: pageNumber }) => ({
            props: {
                shipments,
                totalPages,
                pageNumber,
                from,
                to,
                ...i18n,
            },
        }))
        .get();
}

export default WithClientSideAuth(ShowStatsTabular);
