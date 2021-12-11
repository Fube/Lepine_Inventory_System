import Nav from "../../components/Nav";
import Link from "next/link";
import { useRouter } from "next/router";
import { InstantSearch, SearchBox, Configure } from "react-instantsearch-dom";
import { useContext, useEffect, useState } from "react";
import { AlgoliaContext } from "../_app";
import {
    PaginateAdapter,
    TableHitsAdapter,
} from "../../components/AlgoliaAdapters";
import { axiosBackend } from "../../config/axios";
import Paginate from "../../components/Pagination";
import thou from "../../../utils/thou";

/**
 *
 * @param {{ items: import("../../components/Item").Item[] }}
 * @returns
 */
export default function ShowItems({ items, totalPages, pageNumber }) {
    const router = useRouter();
    const { searchClient } = useContext(AlgoliaContext);
    const [isSearching, setIsSearching] = useState(false);

    const head = (
        <tr>
            <th>Name</th>
            <th>SKU</th>
            <th>Description</th>
        </tr>
    );

    const fallback = (
        <h2 className="text-2xl text-center text-yellow-400">
            No items to show 😢
        </h2>
    );

    if (items.length <= 0) {
        return (
            <>
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
            <InstantSearch searchClient={searchClient} indexName="items">
                <Nav />
                <div className="overflow-x-auto justify-center flex">
                    <div className="w-1/2">
                        <div className="flex justify-around my-4">
                            <h1 className="text-4xl">Items</h1>
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
                                <table className="table table-zebra w-full table-fixed">
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
                <td>{name}</td>
                <td>{sku}</td>
                <td>{description}</td>
            </tr>
        </Link>
    );
}

export async function getServerSideProps(context) {
    const {
        data: { content: items, totalPages, number: pageNumber },
    } = await axiosBackend("/items").catch(console.log);
    return {
        props: {
            items,
            totalPages,
            pageNumber,
        },
    };
}
