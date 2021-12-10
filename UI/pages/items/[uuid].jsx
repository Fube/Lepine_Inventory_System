import ItemBase from "../../components/Item";
import Nav from "../../components/Nav";
import { axiosBackend } from "../../config/axios";

/**
 *
 * @param {{ item: import("../../components/Item").Item }} param0
 * @returns
 */
export default function Item({ item }) {
    return (
        <div className="flex flex-col h-screen">
            <div className="flex-shrink-0 flex-grow-0">
                <Nav />
            </div>
            <div className="flex-grow flex justify-center items-center">
                <div className="w-full">
                    <ItemBase editable {...item} />
                </div>
            </div>
        </div>
    );
}

export async function getServerSideProps(ctx) {
    const { uuid } = ctx.query;
    const { data: item } = await axiosBackend(`/items/${uuid}`);
    return { props: { item } };
}
