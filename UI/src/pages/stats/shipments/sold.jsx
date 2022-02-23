import { DateTime } from "luxon-business-days";
import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import Link from "next/link";
import { useRouter } from "next/router";
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
    const { t: tc } = useTranslation("common");
    const { t: tStats } = useTranslation("stats");
    const { t: tItems } = useTranslation("items");

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
            <title>{tStats("best_sellers.title")}</title>
        </Head>
    );

    const head = (
        <tr>
            <th>{tItems("sku")}</th>
            <th>{tItems("name")}</th>
            <th>{tItems("description")}</th>
            <th>{tStats("best_sellers.transfered_quantity")}</th>
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
                                {tStats("items.best_selling.by_week")}
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

    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "stats",
        "items",
    ]);

    return res
        .refine(({ content: items, totalPages, number: pageNumber }) => ({
            props: {
                items,
                totalPages,
                pageNumber,
                from,
                to,
                ...i18n,
            },
        }))
        .get();
}

export default WithClientSideAuth(ShowStatsSold);
