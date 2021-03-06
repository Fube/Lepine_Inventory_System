import { Icon } from "@iconify/react";
import capitalize from "capitalize";
import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import Link from "next/link";
import { useRouter } from "next/router";
import Nav from "../../components/Nav";
import Paginate from "../../components/Pagination";
import { axiosBackendAuth } from "../../config/axios";

/**
 *
 * @param {{ users: import("@lepine/ui-types").User[] } & import("@lepine/ui-types").Pagination} param0
 * @returns
 */
export default function ShowUsers({ users, totalPages, pageNumber }) {
    const { t: tc } = useTranslation("common");
    const { t: tu } = useTranslation("users");

    const router = useRouter();

    const header = (
        <Head>
            <title>{tu("index.title")}</title>
        </Head>
    );

    const head = (
        <tr>
            <th>Email</th>
            <th className="flex justify-between">
                <div className="self-center">{tc("role")}</div>
                <button>
                    <Link href="/users/new" passHref>
                        <Icon icon="si-glyph:button-plus" width="32" />
                    </Link>
                </button>
            </th>
        </tr>
    );

    const fallback = (
        <h2 className="text-2xl text-center text-yellow-400">{tu("none")}</h2>
    );

    if (users.length <= 0) {
        return (
            <>
                {header}

                <main className="flex justify-center">
                    <div className="text-center">
                        <div className="mt-12">{fallback}</div>
                        <Link href="/users/new" passHref>
                            <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mt-12">
                                {tc("add_one_now")}
                            </button>
                        </Link>
                    </div>
                </main>
            </>
        );
    }

    return (
        <>
            {header}

            <div className="overflow-x-auto justify-center flex">
                <div className="md:w-1/2 w-3/4">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4">
                            {tu("index.title")}
                        </h1>
                    </div>
                    <table className="table table-zebra w-full sm:table-fixed">
                        <thead>{head}</thead>
                        <tbody>
                            {users.map((user) => (
                                <UserTableRow {...user} key={user.uuid} />
                            ))}
                        </tbody>
                    </table>
                    <div className="flex justify-center mt-4">
                        <Paginate
                            onPageChange={(page) =>
                                router.push(`/users?page=${page}`)
                            }
                            totalPages={totalPages}
                            pageNumber={pageNumber}
                        />
                    </div>
                </div>
            </div>
        </>
    );
}

function UserTableRow({ uuid, email, role }) {
    return (
        <Link href={`/users/${uuid}`} passHref>
            <tr className="hover">
                <td>{email}</td>
                <td>{capitalize(role.toLowerCase())}</td>
            </tr>
        </Link>
    );
}

/**
 *
 * @param {import("next/types").GetServerSidePropsContext} context
 * @returns
 */
export async function getServerSideProps(context) {
    const page = context.query.page || 1;
    const res = await axiosBackendAuth.get(`/users?page=${page}`, {
        headers: { cookie: context?.req?.headers?.cookie ?? "" },
    });

    const i18n = await serverSideTranslations(context.locale, [
        "common",
        "users",
        "nav",
    ]);

    return res
        .refine(({ content: users, totalPages, number: pageNumber }) => ({
            props: {
                users,
                totalPages,
                pageNumber,
                ...i18n,
            },
        }))
        .get();
}
