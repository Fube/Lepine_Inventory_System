import { Icon } from "@iconify/react";
import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import Link from "next/link";
import { useRouter } from "next/router";
import Paginate from "../../components/Pagination";
import { axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";
import thou from "../../utils/thou";
/**
 *@param {{ stock: import('@lepine/ui-types').Stock[] } & import("@lepine/ui-types").Pagination} param0
 */
export default function ShowStock({ stocks, totalPages, pageNumber }) {
    const { t: tc } = useTranslation("common");
    const { t: ts } = useTranslation("stocks");

    const router = useRouter();
    const { role } = useAuth();

    const header = (
        <Head>
            <title>{ts("index.title")}</title>
        </Head>
    );

    const head = (
        <tr>
            <th>{tc("item")}</th>
            <th>{tc("warehouse")}</th>
            {thou(
                <th className="flex justify-between">
                    <span className="self-center">{tc("quantity")}</span>
                    <button>
                        <Link href="/stocks/new" passHref>
                            <Icon icon="si-glyph:button-plus" width="32" />
                        </Link>
                    </button>
                </th>
            )
                .or(<th>{tc("quantity")}</th>)
                .if(role === "MANAGER")}
        </tr>
    );

    const fallback = (
        <h2 className="text-2xl text-center text-yellow-400">{ts("none")}</h2>
    );

    if (stocks.length <= 0) {
        return (
            <>
                {header}

                <main className="flex justify-center">
                    <div className="text-center">
                        <div className="mt-12">{fallback}</div>
                        {role === "MANAGER" && (
                            <Link href="/stocks/new" passHref>
                                <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mt-12">
                                    {tc("add_one_now")}
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
                <div className="md:w-1/2 w-3/4">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4">
                            {ts("index.title")}
                        </h1>
                    </div>
                    <table className="table table-zebra w-full sm:table-fixed">
                        <thead>{head}</thead>
                        <tbody>
                            {stocks.map((user) => (
                                <StockTableRow {...user} key={user.uuid} />
                            ))}
                        </tbody>
                    </table>
                    <div className="flex justify-center mt-4">
                        <Paginate
                            onPageChange={(page) =>
                                router.push(`/stocks?page=${page}`)
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
 * @param {import('@lepine/ui-types').Stock} param0
 */
function StockTableRow({ uuid, item, warehouse, quantity }) {
    return (
        <Link key={uuid} href={`/stocks/${uuid}`} passHref>
            <tr className="hover">
                <td className="td-wrap">{item.sku}</td>
                <td className="td-wrap">{warehouse.zipCode}</td>
                <td className="td-wrap">{quantity}</td>
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
    const res = await axiosBackendAuth.get(`/stocks?page=${page}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "errors",
        "stocks",
        "warehouses",
        "nav",
    ]);

    return res
        .refine(({ content: stocks, totalPages, number: pageNumber }) => ({
            props: {
                stocks,
                totalPages,
                pageNumber,
                ...i18n,
            },
        }))
        .get();
}
