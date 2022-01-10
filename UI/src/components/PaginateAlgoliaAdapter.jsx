import Paginate from "./Pagination";
import { connectPagination } from "react-instantsearch-dom";

function internal({
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
}

export default connectPagination(internal);
