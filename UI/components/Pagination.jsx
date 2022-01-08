import { useEffect, useState } from "react";

/**
 *
 * @param {import('@lepine/types').PaginationProps} param0
 */
export default function Paginate({
    startAt = 1,
    totalPages,
    pageNumber = 1,
    onNext = () => {},
    onPrevious = () => {},
    onPageChange = () => {},
}) {
    const [nextDisabled, setNextDisabled] = useState();
    const [previousDisabled, setPreviousDisabled] = useState();
    const [currentPage, setCurrentPage] = useState(pageNumber);

    useEffect(() => {
        setNextDisabled(currentPage >= totalPages);
        setPreviousDisabled(currentPage <= 1);
    }, [currentPage, startAt, totalPages]);

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

    const handlePageChange = (page) => {
        if (page === currentPage || page < 1 || page > totalPages) {
            return;
        }

        if (page > currentPage) {
            onNext(currentPage, page);
        } else {
            onPrevious(currentPage, page);
        }

        onPageChange(page);
        setCurrentPage(page);
    };

    if (totalPages <= 1) {
        return <></>;
    }

    return (
        <div className="btn-group">
            <button
                className="btn"
                disabled={previousDisabled}
                onClick={() => handlePageChange(currentPage - 1)}
            >
                {"<"}
            </button>

            {paginate(pageNumber, totalPages).map((page) => (
                <button
                    key={page}
                    className={`btn ${page === currentPage && "btn-active"}`}
                    disabled={page === currentPage || page === "..."}
                    onClick={() => handlePageChange(page)}
                >
                    {page}
                </button>
            ))}

            <button
                className="btn"
                disabled={nextDisabled}
                onClick={() => handlePageChange(currentPage + 1)}
            >
                {">"}
            </button>
        </div>
    );
}
