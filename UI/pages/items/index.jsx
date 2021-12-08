import Nav from "../../components/Nav";
import Link from "next/link";
import { useState } from "react";
import { axiosBackend } from "../../config/axios";

/**
 *
 * @param {{ items: import("../../components/Item").ItemProps[] }}
 * @returns
 */
export default function ShowItems({ items, totalPages, pageNumber }) {
    const [currentPage, setCurrentPage] = useState(pageNumber);

    const paginate = (currentPage, lastPage, delta = 3) => {
        const range = [];
        for (
            let i = Math.max(2, currentPage - delta);
            i <= Math.min(lastPage - 1, currentPage + delta);
            i += 1
        ) {
            range.push(i);
        }

        if (currentPage - delta > 2) {
            range.unshift("...");
        }
        if (currentPage + delta < lastPage - 1) {
            range.push("...");
        }

        range.unshift(1);
        if (lastPage !== 1) range.push(lastPage);

        return range;
    };

    return (
        <>
            <Nav />
            <h1 className="text-4xl text-center my-4">Items</h1>
            <div className="overflow-x-auto justify-center flex">
                <div className="w-1/2">
                    <table className="table table-zebra w-full">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>SKU</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            {items.map(({ uuid, name, sku, description }) => (
                                <Link
                                    key={uuid}
                                    href={`/items/${uuid}`}
                                    passHref
                                >
                                    <tr className="hover">
                                        <td>{name}</td>
                                        <td>{sku}</td>
                                        <td>{description}</td>
                                    </tr>
                                </Link>
                            ))}
                        </tbody>
                    </table>

                    {totalPages > 1 && (
                        <div className="flex justify-center mt-4">
                            <div className="btn-group">
                                <button className="btn">Previous</button>

                                {paginate(pageNumber, totalPages).map(
                                    (page) => (
                                        <button
                                            key={page}
                                            className={`btn ${
                                                page === pageNumber
                                                    ? "btn-primary"
                                                    : "btn-outline-primary"
                                            }`}
                                            disabled={
                                                page === pageNumber ||
                                                page === "..."
                                            }
                                        >
                                            {page}
                                        </button>
                                    )
                                )}

                                <button className="btn">Next</button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </>
    );
}

export async function getServerSideProps(ctx) {
    const {
        data: {
            content: items,
            pageable: { pageNumber },
            totalPages,
        },
    } = await axiosBackend.get(`/items`);
    return {
        props: {
            items,
            pageNumber,
            totalPages,
        },
    };
}
