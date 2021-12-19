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
import serverSideRedirectOnUnauth from "../../utils/serverSideRedirectOnUnauth";
import { axiosBackend } from "../../config/backendAxios";

/**
 *
 * @param {{ items: import("../../components/Item").Item[] }}
 * @returns
 */
export default function ShowItems({ items, totalPages, pageNumber }) {
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
                <div className="self-center">Description</div>
                <button>
                    <Link href="/items/new">
                        <Icon icon="si-glyph:button-plus" width="32" />
                    </Link>
                </button>
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
                        <Link href="/items/new">
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
                    <div className="md:w-1/2 w-3/4">
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
                                <table className="table table-zebra w-full sm:table-fixed">
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
            <tr className="hover">
                <td>{sku}</td>
                <td>{name}</td>
                <td>{description}</td>
            </tr>
        </Link>
    );
}

async function naiveGetServerSideProps(context) {
    const page = context.query.page || 1;
    const {
        data: { content: items, totalPages, number: pageNumber },
    } = await axiosBackend.get(`/items?page=${page}`, {
        headers: { ...context.req.headers },
    });
    return {
        props: {
            items,
            totalPages,
            pageNumber,
        },
    };
}

export async function getServerSideProps(context) {
    return await serverSideRedirectOnUnauth(() =>
        naiveGetServerSideProps(context)
    );
}
