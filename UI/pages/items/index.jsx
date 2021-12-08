import Nav from "../../components/Nav";
import Link from "next/link";
import Item from "../../components/Item";
import getConfig from "next/config";
import axios from "axios";
import { axiosBackend } from "../../config/axios";

/**
 *
 * @param {{ items: import("../../components/Item").ItemProps[] }}
 * @returns
 */
export default function ShowItems({ items, url }) {
    return (
        <>
            <Nav />
            <h1 className="text-4xl text-center my-4">Items</h1>
            <div className="overflow-x-auto justify-center flex">
                <table className="table w-1/2 table-zebra">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>SKU</th>
                            <th>Description</th>
                        </tr>
                    </thead>
                    <tbody>
                        {items.map(({ uuid, name, sku, description }) => (
                            <Link key={uuid} href={`/items/${uuid}`} passHref>
                                <tr className="hover">
                                    <td>{name}</td>
                                    <td>{sku}</td>
                                    <td>{description}</td>
                                </tr>
                            </Link>
                        ))}
                    </tbody>
                </table>
            </div>
        </>
    );
}

export async function getServerSideProps(ctx) {
    const { data } = await axiosBackend.get(`/items`);
    return {
        props: {
            items: data.content,
        },
    };
}
