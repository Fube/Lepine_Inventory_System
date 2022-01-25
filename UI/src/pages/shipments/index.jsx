import Head from "next/head";

/**
 * @param {{ shipments: import('@lepine/ui-types').Shipment[] } & import("@lepine/ui-types").Pagination} param0
 */
export default function ShowShipments({ shipments, totalPages, pageNumber }) {
    const header = (
        <Head>
            <title>Shipments</title>
        </Head>
    );
    return (
        <>
            {header}
            Hello
        </>
    );
}
