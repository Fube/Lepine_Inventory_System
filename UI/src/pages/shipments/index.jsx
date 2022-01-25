import Head from "next/head";
import Link from "next/link";
import Paginate from "../../components/Pagination";
import { axiosBackendAuth } from "../../config/axios";

/**
 * @param {{ shipments: import('@lepine/ui-types').Shipment[] } & import("@lepine/ui-types").Pagination} param0
 */
export default function ShowShipments({ shipments, totalPages, pageNumber }) {
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

    const head = (
        <tr>
            <th>Order Number</th>
            <th>Shipment Date</th>
        </tr>
    );

    if (shipments.length <= 0) {
        return (
            <>
                {header}
                <div className="flex justify-center">{fallback}</div>
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
}) {
    return (
        <Link key={uuid} href={`/shipments/${uuid}`} passHref>
            <tr className="hover">
                <td>{orderNumber}</td>
                <td>{new Date(expectedDate).toDateString()}</td>
            </tr>
        </Link>
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
