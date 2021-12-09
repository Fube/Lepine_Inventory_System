import Nav from "../../components/Nav";
import Link from "next/link";
import { axiosBackend } from "../../config/axios";
import Paginate from "../../components/Pagination";
import { useRouter } from "next/router";
import {
    InstantSearch,
    Hits,
    SearchBox,
    Pagination,
    Highlight,
    ClearRefinements,
    RefinementList,
    Configure,
    connectHits,
    connectPagination,
} from "react-instantsearch-dom";
import { useContext } from "react";
import { AlgoliaContext } from "../_app";
import Item from "../../components/Item";

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
                        <h1 className="text-4xl text-left my-4">Items</h1>
                        <SearchBox />
                        <Configure hitsPerPage={10} />
                        <ItemHitTableAdapter
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
                            <ItemPaginationAdapter />
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

const ItemHitTableAdapter = connectHits(function ({
    hits: items,
    hitComponent: HitComponent,
    headComponent,
}) {
    if (items && items.length <= 0) {
        return (
            <h2 className="text-2xl text-center text-yellow-400">
                Nothing to show 😢
            </h2>
        );
    }
    const mappedItems = items.map((item, key) => (
        <HitComponent hit={item} key={key} />
    ));

    return (
        <table className="table table-zebra w-full table-fixed">
            <thead>{headComponent}</thead>
            <tbody>{mappedItems}</tbody>
        </table>
    );
});

const ItemPaginationAdapter = connectPagination(function ({
    nbPages: totalPages,
    currentRefinement: pageNumber,
    refine,
}) {
    return (
        <Paginate
            pageNumber={pageNumber}
            totalPages={totalPages}
            onPageChange={refine}
        />
    );
});
