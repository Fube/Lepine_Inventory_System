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

/**
 *
 * @param {{ items: import("../../components/Item").Item[] }}
 * @returns
 */
export default function ShowItems({ items, totalPages, pageNumber }) {
    const router = useRouter();
    const { searchClient } = useContext(AlgoliaContext);
    const [refresh, setRefresh] = useState(true);
    const [hasSearched, setHasSearched] = useState(false);

    useEffect(() => {
        setRefresh((ignore) => true);
        searchClient.clearCache().then(() => setRefresh((ignore) => false));
    }, []);

    const head = (
        <tr>
            <th>Name</th>
            <th>SKU</th>
            <th>Description</th>
        </tr>
    );

    return (
        <>
            <InstantSearch
                searchClient={searchClient}
                indexName="items"
                refresh={refresh}
            >
                <Nav />
                <div className="overflow-x-auto justify-center flex">
                    <div className="w-1/2">
                        <div className="flex justify-around my-4">
                            <h1 className="text-4xl">Items</h1>
                            <SearchBox onChange={() => setHasSearched(true)} />
                        </div>
                        {hasSearched ? (
                            <>
                                <Configure hitsPerPage={10} />
                                <TableHitsAdapter
                                    headComponent={head}
                                    hitComponent={ItemHitAdapter}
                                />
                            </>
                        ) : (
                            <table className="table table-zebra w-full table-fixed">
                                <thead>{head}</thead>
                                <tbody>
                                    {items.map(
                                        ({ uuid, name, description, sku }) => (
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
                        )}
                        <div className="flex justify-center mt-4">
                            {hasSearched ? (
                                <PaginateAdapter />
                            ) : (
                                <Paginate
                                    onPageChange={(page) =>
                                        router.push(`/items?page=${page}`)
                                    }
                                    totalPages={totalPages}
                                    pageNumber={pageNumber}
                                />
                            )}
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
    } = await axiosBackend("/items");
    return {
        props: {
            items,
            totalPages,
            pageNumber,
        },
    };
}
