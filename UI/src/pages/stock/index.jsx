import Head from "next/head";
import Link from "next/link";
import { useRouter } from "next/router";
import { Icon } from "@iconify/react";
import Paginate from "../../components/Pagination";
import Nav from "../../components/Nav";
import { axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";
import thou from "../../utils/thou";
/**
*@param {{ stock: import('@lepine/types').Stock[] } & import("@lepine/types").Pagination} param0
*/
export default function ShowStock({ stock, totalPages, pageNumber }) {
    const router = useRouter();
    const { role } = useAuth();

    const header = (
        <Head>
            <title>Stock</title>
        </Head>
    );

    const head = (
        <tr>
            <th>Item</th>
            <th>Warehouse</th>
            <th>Quantity</th>
            <th className="flex justify-between">
                <button>
                    <Link href="/stock/new" passHref>
                        <Icon icon="si-glyph:button-plus" width="32" />
                    </Link>
                </button>
            </th>
        </tr>
    );

    const fallback = (
        <h2 className="text-2xl text-center text-yellow-400">
            No stock to show 😢
        </h2>
    );

    if (stock.length <= 0) {
        return (
            <>
                {header}
                <Nav />
                <main className="flex justify-center">
                    <div className="text-center">
                        <div className="mt-12">{fallback}</div>
                        <Link href="/stock/new" passHref>
                            <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mt-12">
                                Add One Now!
                            </button>
                        </Link>
                    </div>
                </main>
            </>
        );
    }

    return (
        <>
            {header}
            <Nav />
            <div className="overflow-x-auto justify-center flex">
            <div className="md:w-1/2 w-3/4">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4">Stock</h1>
                    </div>
                    <table className="table table-zebra w-full sm:table-fixed">
                        <thead>{head}</thead>
                        <tbody>
                            {stock.map((user) => (
                                <stock {...user} key={user.uuid} />
                            ))}
                        </tbody>
                    </table>
                    <div className="flex justify-center mt-4">
                        <Paginate
                            onPageChange={(page) =>
                                router.push(`/stock?page=${page}`)
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
 * @param {Stock} param0
 */
function StockTableRow({ uuid, item, warehouse, quantity }) {
    return (
        <tr>
            <td>{item}</td>
            <td>{warehouse}</td>
            <td>{quantity}</td>
        </tr>
    );
}

/**
 *
 * @param {import("next").GetServerSidePropsContext} context
 * @returns
 */
export async function getServerSideProps(context) {
    const page = context.query.page || 1;
    const res = await axiosBackendAuth.get(`/stock?page=${page}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    return res
        .refine(({ content: stock, totalPages, number: pageNumber }) => ({
            props: {
                stock,
                totalPages,
                pageNumber,
            },
        }))
        .get();
}