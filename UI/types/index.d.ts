export type Pagination = {
    totalPages: number;
    pageNumber: number;
};

export type Warehouse = {
    uuid: string;
    zipCode: string;
    city: string;
    province: string;
    active: boolean;
};
