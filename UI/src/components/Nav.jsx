import { Icon } from "@iconify/react";
import { useTranslation } from "next-i18next";
import Head from "next/head";
import Link from "next/link";
import { useRouter } from "next/router";
import useAuth from "../hooks/useAuth";

export default function Nav({ pages, isActive }) {
    const { t: tn } = useTranslation("nav");

    const { isLoggedIn, logout } = useAuth();
    const router = useRouter();

    return (
        <>
            <Head>
                <meta
                    name="viewport"
                    content="width=device-width, initial-scale=1"
                />
            </Head>
            <div className="navbar mb-2 shadow-lg bg-neutral text-neutral-content">
                <div className="flex-none px-2 mx-2">
                    <span className="text-lg font-bold">
                        <Link href="/items">Lepine</Link>
                    </span>
                </div>
                <div className="flex-1 px-2 mx-2 justify-between drawer">
                    <label
                        htmlFor="nav-drawer"
                        className="sm:hidden flex btn btn-ghost btn-sm rounded-btn self-center"
                    >
                        <Icon icon="radix-icons:hamburger-menu" width="32" />
                        <span className="ml-2">Navigation</span>
                    </label>
                    <div className="items-stretch sm:flex hidden">
                        {pages.map(({ url: path, display }, key) => (
                            <Link key={key} href={`/${path}`}>
                                <a
                                    className={`btn btn-ghost btn-sm rounded-btn ${isActive(
                                        path
                                    )}`}
                                >
                                    {display}
                                </a>
                            </Link>
                        ))}
                    </div>
                    <div>
                        {isLoggedIn ? (
                            <a
                                className={`btn btn-ghost btn-sm rounded-btn`}
                                onClick={() =>
                                    logout().then(() => router.push("/"))
                                }
                            >
                                {tn("logout")}
                            </a>
                        ) : (
                            <Link href="/login">
                                <a className="btn btn-ghost btn-sm rounded-btn self-center">
                                    {tn("login")}
                                </a>
                            </Link>
                        )}
                    </div>
                </div>
            </div>
        </>
    );
}
