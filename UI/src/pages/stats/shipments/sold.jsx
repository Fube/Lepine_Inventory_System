import { DateTime } from "luxon-business-days";
import Head from "next/head";
import Link from "next/link";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import DatePicker from "react-datepicker";
import Paginate from "../../../components/Pagination";
import WithClientSideAuth from "../../../components/WithClientSideAuth";
import { axiosBackendAuth } from "../../../config/axios";

/**
 * @param {{
 * items: import('@lepine/ui-types').ItemQuantityTuple[],
 * totalPages: number,
 * pageNumber: number,
 * from: string,
 * to: string,
 * }}
 */
function ShowStatsSold({ items, totalPages, pageNumber, from, to }) {
    const router = useRouter();
    const [dateRange, setDateRange] = useState([null, null]);

    const [startDate, endDate] = dateRange;
    const [uriFrom, uriTo] = [from, to].map(encodeURIComponent);

    useEffect(() => {
        const [start, end] = dateRange;

        console.log(start, end);

        if (start === null || end === null) {
            return;
        }

        router.push({
            pathname: "/stats",
            query: {
                from: start.toISOString(),
                to: end.toISOString(),
            },
        });
    }, [dateRange]);

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
            <title>Stats - Best Sellers</title>
        </Head>
    );

    const head = (
        <tr>
            <th>SKU</th>
            <th>Name</th>
            <th>Description</th>
            <th>Transferred Quantity</th>
        </tr>
    );

    return (
        <>
            {header}

            <div className="overflow-x-auto justify-center flex">
                <div className="md:w-3/4 lg:w-1/2 w-full">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4 text-center">
                            <p className="mb-2">Most Transferred Items</p>
                            <p className="text-lg">
                                <Link className="text-3xl" href={getPrevURL()}>
                                    {"< "}
                                </Link>
                                Between {new Date(from).toDateString()} and{" "}
                                {new Date(to).toDateString()}
                                <Link className="text-3xl" href={getNextURL()}>
                                    {" >"}
                                </Link>
                            </p>
                        </h1>
                    </div>

                    <table className="table table-zebra w-full sm:table-fixed">
                        <thead>{head}</thead>
                        <tbody>
                            {items.map((item) => (
                                <ItemTableRow {...item} key={item.uuid} />
                            ))}
                        </tbody>
                    </table>
                    <div className="flex justify-center mt-4">
                        <Paginate
                            onPageChange={(page) =>
                                router.push(
                                    `?page=${page}&from=${from}&to=${to}`
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
 * @param {import('@lepine/ui-types').ItemQuantityTuple} param0
 */
function ItemTableRow({ item: { uuid, name, description, sku }, quantity }) {
    return (
        <Link key={uuid} href={`/items/${uuid}`} passHref>
            <tr className="hover">
                <td className="td-wrap">{sku}</td>
                <td className="td-wrap">{name}</td>
                <td className="td-wrap">{description}</td>
                <td className="td-wrap">{quantity}</td>
            </tr>
        </Link>
    );
}

/**
 *
 * @param {import("next/types").GetServerSidePropsContext} context
 * @returns
 */
export async function getServerSideProps(context) {
    const page = context.query.page || 1;
    const from = context.query.from || DateTime.now().startOf("week").toISO();
    const to = context.query.to || DateTime.now().endOf("week").toISO();

    const res = await axiosBackendAuth.get(
        `/items/bestseller?page=${page}&from=${encodeURIComponent(
            from
        )}&to=${encodeURIComponent(to)}`,
        {
            headers: { cookie: context?.req?.headers?.cookie ?? "" },
        }
    );

    return res
        .refine(({ content: items, totalPages, number: pageNumber }) => ({
            props: {
                items,
                totalPages,
                pageNumber,
                from,
                to,
            },
        }))
        .get();
}

export default WithClientSideAuth(ShowStatsSold);
