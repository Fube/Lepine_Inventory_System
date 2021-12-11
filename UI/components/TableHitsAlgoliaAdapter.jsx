import { connectHits } from "react-instantsearch-dom";
import Link from "next/link";

function internal({
    hits: items,
    hitComponent: HitComponent,
    headComponent,
    fallbackComponent = <>Nothing found</>,
}) {
    if (items && items.length <= 0) {
        return fallbackComponent;
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
