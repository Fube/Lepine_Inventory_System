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

    const getTableOrSadFace = () => {
        if (items && items.length <= 0) {
            return (
                <h2 className="text-2xl text-center text-yellow-400">
                    Nothing to show 😢
                </h2>
            );
        }

        const mappedItems = items.map(({ uuid, name, sku, description }) => (
            <Link key={uuid} href={`/items/${uuid}`} passHref>
                <tr className="hover">
                    <td>{name}</td>
                    <td>{sku}</td>
                    <td>{description}</td>
                </tr>
            </Link>
        ));
        return (
            <table className="table table-zebra w-full table-fixed">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>SKU</th>
                        <th>Description</th>
                    </tr>
                </thead>
                <tbody>{mappedItems}</tbody>
            </table>
        );
    };

    return (
        <>
            <Nav />
            <div className="overflow-x-auto justify-center flex">
                <div className="w-1/2">
                    <h1 className="text-4xl text-left my-4">Items</h1>
                    {getTableOrSadFace()}
                    {totalPages > 1 && (
                        <div className="flex justify-center mt-4">
                            <Paginate
                                pageNumber={pageNumber}
                                totalPages={totalPages}
                                onPageChange={loadNewPage}
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
