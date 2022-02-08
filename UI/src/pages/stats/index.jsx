import { DateTime } from "luxon-business-days";
import Head from "next/head";
import Link from "next/link";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import DatePicker from "react-datepicker";
import Paginate from "../../components/Pagination";
import WithClientSideAuth from "../../components/WithClientSideAuth";
import { axiosBackendAuth } from "../../config/axios";
/**
 * @param {{
 * shipments: import('@lepine/ui-types').Shipment[],
 * totalPages: number,
 * pageNumber: number,
 * from: string,
 * to: string,
 * }}
 */
function ShowStats({}) {
    const header = (
        <Head>
            <title>Stats</title>
        </Head>
    );

    return (
        <>
            {header}

            <div className="overflow-x-auto justify-center flex">
                <div className="md:w-3/4 lg:w-1/2 w-full">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4 text-center">
                            <p className="mb-2">Stats</p>
                        </h1>
                    </div>
                    <div className="flex flex-col gap-2">
                        <Link href="/stats/shipments/confirmed" passHref>
                            <button className="btn text-lg">
                                Fully Confirmed Shipments
                            </button>
                        </Link>
                        <Link href="/stats/shipments/sold" passHref>
                            <button className="btn text-lg">
                                Best Selling Items By Week
                            </button>
                        </Link>
                    </div>
                </div>
            </div>
        </>
    );
}

/**
 *
 * @param {import("next").GetServerSidePropsContext} context
 * @returns
 */
// export async function getServerSideProps(context) {}

export default WithClientSideAuth(ShowStats);
