import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import Link from "next/link";
import WithClientSideAuth from "../../components/WithClientSideAuth";
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
    const { t: tc } = useTranslation("common");
    const { t: ts } = useTranslation("stats");

    const header = (
        <Head>
            <title>{ts("index.title")}</title>
        </Head>
    );

    return (
        <>
            {header}

            <div className="overflow-x-auto justify-center flex">
                <div className="md:w-3/4 lg:w-1/2 w-full">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4 text-center">
                            <p className="mb-2">{ts("index.title")}</p>
                        </h1>
                    </div>
                    <div className="flex flex-col gap-2">
                        <Link href="/stats/shipments/confirmed" passHref>
                            <button className="btn text-lg">
                                {ts("shipments.fully_confirmed")}
                            </button>
                        </Link>
                        <Link href="/stats/shipments/sold" passHref>
                            <button className="btn text-lg">
                                {ts("items.best_selling.by_week")}
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
export async function getServerSideProps(context) {
    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "stats",
    ]);

    return {
        props: {
            ...i18n,
        },
    };
}

export default WithClientSideAuth(ShowStats);
