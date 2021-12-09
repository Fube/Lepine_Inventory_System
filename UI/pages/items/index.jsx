import Nav from "../../components/Nav";
import Link from "next/link";
import { axiosBackend } from "../../config/axios";
import Paginate from "../../components/Pagination";
import { useRouter } from "next/router";

/**
 *
 * @param {{ items: import("../../components/Item").ItemProps[] }}
 * @returns
 */
export default function ShowItems({ items, totalPages, pageNumber }) {
    const router = useRouter();

    const loadNewPage = (newPage) => {
        router.push(`/items?page=${newPage}`);
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
                            <Paginate
                                pageNumber={pageNumber}
                                totalPages={totalPages}
                                onPageChange={(page) => loadNewPage(page)}
                            />
                        </div>
                    )}
                </div>
            </div>
        </>
    );
}

export async function getServerSideProps({ query: { page = 1 } }) {
    const {
        data: { content: items, number: pageNumber, totalPages },
    } = await axiosBackend.get(`/items?page=${page}`);
    return {
        props: {
            items,
            pageNumber,
            totalPages,
        },
    };
}
