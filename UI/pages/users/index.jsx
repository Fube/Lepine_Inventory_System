import { Icon } from "@iconify/react";
import Head from "next/head";
import Link from "next/link";
import { useRouter } from "next/router";
import Nav from "../../components/Nav";
import Paginate from "../../components/Pagination";
import { axiosBackend } from "../../config/axios";
import serverSideRedirectOnUnauth from "../../utils/serverSideRedirectOnUnauth";

export default function ShowUsers({ users, totalPages, pageNumber }) {
    const router = useRouter();
    const head = (
        <tr>
            <th>Email</th>
            <th className="flex justify-between">
                <div className="self-center">Role</div>
                <button>
                    <Link href="/users/new" passHref>
                        <Icon icon="si-glyph:button-plus" width="32" />
                    </Link>
                </button>
            </th>
        </tr>
    );
    return (
        <>
            <Head>
                <title>Users</title>
            </Head>
            <Nav />
            <div className="overflow-x-auto justify-center flex">
                <div className="md:w-1/2 w-3/4">
                    <div className="md:flex justify-around my-4">
                        <h1 className="text-4xl md:mb-0 mb-4">Users</h1>
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
        // <Link href={`/users/${uuid}`} passHref>
        <tr className="hover">
            <td>{email}</td>
            <td>{role}</td>
        </tr>
        // </Link>
    );
}

async function naiveGetServerSideProps(context) {
    const page = context.query.page || 1;
    const {
        data: { content: users, totalPages, number: pageNumber },
    } = await axiosBackend.get(`/users?page=${page}`, {
        headers: { ...context.req.headers },
    });
    return {
        props: {
            users,
            totalPages,
            pageNumber,
        },
    };
}

export async function getServerSideProps(context) {
    return await serverSideRedirectOnUnauth(() =>
        naiveGetServerSideProps(context)
    );
}
