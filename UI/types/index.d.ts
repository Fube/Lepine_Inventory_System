export type Pagination = {
    totalPages: number;
    pageNumber: number;
};

export type PaginationProps = {
    startAt: number;
    onNext: (oldPage: number, newPage: number) => void;
    onPrevious: (oldPage: number, newPage: number) => void;
    onPageChange: (page: number) => void;
} & Pagination;

export type Warehouse = {
    uuid: string;
    zipCode: string;
    city: string;
    province: string;
    active: boolean;
};

export type Item = {
    uuid: string;
    name: string;
    description: string;
    sku: string;
};

export type User = {
    uuid: string;
    email: string;
    role: string;
};

export type Stock = {
    uuid: string;
    item: Item;
    warehouse: Warehouse;
    quantity: number;
};

export type Shipment = {
    uuid: string;
    status: string;
    transfers: Transfer[];
    expectedDate: string;
    orderNumber: string;
    createdBy: string;
    to: string;
};

export type Transfer = {
    uuid: string;
    stock: Stock;
    quantity: number;
};

export type ItemQuantityTuple = {
    item: Item;
    quantity: number;
};
