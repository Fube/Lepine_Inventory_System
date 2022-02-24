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
 * @param {{ warehouses: import('@lepine/ui-types').Warehouse[] } & import("@lepine/ui-types").Pagination} param0
 */
export default function ShowWarehouses({ warehouses, totalPages, pageNumber }) {
    const { t: tc } = useTranslation("common");
    const { t: tw } = useTranslation("warehouses");

    const router = useRouter();
    const { role } = useAuth();

    const header = (
        <Head>
            <title>{tw("index.title")}</title>
        </Head>
    );

    const head = (
        <tr>
            <th>{tc("zipcode")}</th>
            <th>{tc("city")}</th>
            <th className="md:rounded-r-none rounded-r-lg flex justify-between md:table-cell">
                <div className="self-center">{tc("province")}</div>
                {role === "MANAGER" ? (
                    <button className="md:hidden">
                        <Link href="/warehouses/new" passHref>
                            <Icon icon="si-glyph:button-plus" width="32" />
                        </Link>
                    </button>
                ) : (
                    ""
                )}
            </th>
            <th className="hidden justify-between md:flex">
                {thou(
                    <>
                        <div className="self-center">{tc("active")}</div>
                        <button>
                            <Link href="/warehouses/new" passHref>
                                <Icon icon="si-glyph:button-plus" width="32" />
                            </Link>
                        </button>
                    </>
                )
                    .or(tc("active"))
                    .if(role === "MANAGER")}
            </th>
        </tr>
    );

    const fallback = (
        <h2 className="text-2xl text-center text-yellow-400">{tw("none")}</h2>
    );

    if (warehouses.length <= 0) {
        return (
            <>
                {header}

                <main className="flex justify-center">
                    <div className="text-center">
                        <div className="mt-12">{fallback}</div>
                        {role === "MANAGER" && (
                            <Link href="/warehouses/new" passHref>
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
                <div className="md:w-3/4 lg:w-1/2 w-full">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4">
                            {tw("index.title")}
                        </h1>
                    </div>
                    <table className="table table-zebra w-full sm:table-fixed">
                        <thead>{head}</thead>
                        <tbody>
                            {warehouses.map((user) => (
                                <WarehouseTableRow {...user} key={user.uuid} />
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
 * @param {Warehouse} param0
 */
function WarehouseTableRow({ uuid, zipCode, city, province, active }) {
    const { t: tc } = useTranslation("common");

    return (
        <Link key={uuid} href={`/warehouses/${uuid}`} passHref>
            <tr className="hover">
                <td className="td-wrap">{zipCode}</td>
                <td className="td-wrap">{city}</td>
                <td className="td-wrap md:rounded-r-none rounded-r-lg">
                    {province}
                </td>
                <td className="td-wrap hidden md:table-cell">
                    {tc(active ? "yes" : "no")}
                </td>
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
    const res = await axiosBackendAuth.get(`/warehouses?page=${page}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "warehouses",
        "nav",
    ]);

    return res
        .refine(({ content: warehouses, totalPages, number: pageNumber }) => ({
            props: {
                warehouses,
                totalPages,
                pageNumber,
                ...i18n,
            },
        }))
        .get();
}
