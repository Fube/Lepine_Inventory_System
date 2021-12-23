import Nav from "../../components/Nav";
import Link from "next/link";
import { useRouter } from "next/router";
import Head from "next/head";
import { InstantSearch, SearchBox, Configure } from "react-instantsearch-dom";
import { useContext, useEffect, useState } from "react";
import { AlgoliaContext } from "../_app";
import {
    PaginateAdapter,
    TableHitsAdapter,
} from "../../components/AlgoliaAdapters";
import Paginate from "../../components/Pagination";
import thou from "../../utils/thou";
import { Icon } from "@iconify/react";
import { axiosBackendAuth } from "../../config/axios";
import useAuth from "../../hooks/useAuth";

/**
 *
 * @param {{ items: import("../../components/Item").Item[] }}
 * @returns
 */
export default function ShowItems({ items, totalPages, pageNumber }) {
    const { role } = useAuth();
    const router = useRouter();

    const { searchClient } = useContext(AlgoliaContext);
    const [isSearching, setIsSearching] = useState(false);
    const [refresh, setRefresh] = useState(false);

    useEffect(() => {
        searchClient.clearCache();
        setRefresh((ignore) => setRefresh(true));
    }, []);
    useEffect(() => {
        setRefresh((ignore) => setRefresh(false));
    }, [refresh]);

    const head = (
        <tr>
            <th>SKU</th>
            <th>Name</th>
            <th className="flex justify-between">
                {thou(
                    <>
                        <div className="self-center">Description</div>
                        <button>
                            <Link href="/items/new" passHref>
                                <Icon icon="si-glyph:button-plus" width="32" />
                            </Link>
                        </button>
                    </>
                )
                    .or("Description")
                    .if(role === "MANAGER")}
            </th>
        </tr>
    );

    const fallback = (
        <h2 className="text-2xl text-center text-yellow-400">
            No items to show 😢
        </h2>
    );

    const header = (
        <Head>
            <title>Items</title>
        </Head>
    );

    if (items.length <= 0) {
        return (
            <>
                {header}
                <Nav />
                <main className="flex justify-center">
                    <div className="text-center">
                        <div className="mt-12">{fallback}</div>
                        <Link href="/items/new" passHref>
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
            <InstantSearch
                searchClient={searchClient}
                indexName="items"
                refresh={refresh}
            >
                <Nav />
                <div className="overflow-x-auto justify-center flex">
                    <div className="md:w-4/5 w-3/4">
                        <div className="md:flex justify-around my-4">
                            <h1 className="text-4xl md:mb-0 mb-4">Items</h1>
                            <SearchBox
                                onChange={(a) =>
                                    setIsSearching(
                                        a.currentTarget.value.length > 0
                                    )
                                }
                            />
                        </div>
                        {thou(
                            <>
                                <Configure hitsPerPage={10} />
                                <TableHitsAdapter
                                    headComponent={head}
                                    hitComponent={ItemHitAdapter}
                                    fallbackComponent={fallback}
                                />
                            </>
                        )
                            .or(
                                <table className="table table-zebra w-full !whitespace-normal !break-words sm:table-fixed">
                                    <thead>{head}</thead>
                                    <tbody>
                                        {items.map(
                                            ({
                                                uuid,
                                                name,
                                                description,
                                                sku,
                                            }) => (
                                                <ItemHitAdapter
                                                    key={uuid}
                                                    hit={{
                                                        objectID: uuid,
                                                        description,
                                                        name,
                                                        sku,
                                                    }}
                                                />
                                            )
                                        )}
                                    </tbody>
                                </table>
                            )
                            .if(isSearching)}
                        <div className="flex justify-center mt-4">
                            {thou(<PaginateAdapter />)
                                .or(
                                    <Paginate
                                        onPageChange={(page) =>
                                            router.push(`/items?page=${page}`)
                                        }
                                        totalPages={totalPages}
                                        pageNumber={pageNumber}
                                    />
                                )
                                .if(isSearching)}
                        </div>
                    </div>
                </div>
            </InstantSearch>
        </>
    );
}

function ItemHitAdapter({ hit: { objectID: uuid, description, name, sku } }) {
    return (
        <Link key={uuid} href={`/items/${uuid}`} passHref>
            <tr className="hover ">
                <td className="td-wrap">{sku}</td>
                <td className="td-wrap">{name}</td>
                <td className="td-wrap">{description}</td>
            </tr>
        </Link>
    );
}

export async function getServerSideProps(context) {
    const page = context.query.page || 1;

    const res = await axiosBackendAuth.get(`/items?page=${page}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    return res
        .refine(({ content: items, totalPages, number: pageNumber }) => ({
            props: {
                items,
                totalPages,
                pageNumber,
            },
        }))
        .get();
}
