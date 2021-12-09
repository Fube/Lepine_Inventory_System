import Nav from "../../components/Nav";
import Link from "next/link";
import { axiosBackend } from "../../config/axios";
import Paginate from "../../components/Pagination";
import { useRouter } from "next/router";
import { InstantSearch, SearchBox, Configure } from "react-instantsearch-dom";
import { useContext } from "react";
import { AlgoliaContext } from "../_app";
import {
    PaginateAdapter,
    TableHitsAdapter,
} from "../../components/AlgoliaAdapters";

/**
 *
 * @param {{ items: import("../../components/Item").ItemProps[] }}
 * @returns
 */
export default function ShowItems({ totalPages, pageNumber }) {
    const router = useRouter();
    const { searchClient } = useContext(AlgoliaContext);

    const loadNewPage = (newPage) => {
        router.push(`/items?page=${newPage}`);
    };

    return (
        <>
            <InstantSearch searchClient={searchClient} indexName="items">
                <Nav />
                <div className="overflow-x-auto justify-center flex">
                    <div className="w-1/2">
                        <div className="flex justify-around my-4">
                            <h1 className="text-4xl">Items</h1>
                            <SearchBox />
                        </div>
                        <Configure hitsPerPage={10} />
                        <TableHitsAdapter
                            headComponent={
                                <tr>
                                    <th>Name</th>
                                    <th>SKU</th>
                                    <th>Description</th>
                                </tr>
                            }
                            hitComponent={ItemHitAdapter}
                        />
                        <div className="flex justify-center mt-4">
                            <PaginateAdapter />
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
