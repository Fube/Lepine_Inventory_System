import { connectHits } from "react-instantsearch-dom";
import Link from "next/link";

function internal({ hits: items, hitComponent: HitComponent, headComponent }) {
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
}

export default connectHits(internal);
