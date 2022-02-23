import { useTranslation } from "next-i18next";
import Link from "next/link";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import useAuth from "../hooks/useAuth";
import Nav from "./Nav";

export default function NavWrapper({ children }) {
    const { t: tn } = useTranslation("nav");

    const [isOpen, setIsOpen] = useState(false);
    const { isLoggedIn, role } = useAuth();
    const router = useRouter();

    const i18nPages = tn("pages", {
        returnObjects: true,
    });

    useEffect(() => {
        setIsOpen(false);
    }, [router.pathname]);

    const isActive = (path) =>
        router.pathname.substring(1).startsWith(path) ? "!text-blue-400" : "";

    const toggleIsOpen = () => setIsOpen(!isOpen);

    const pages = [];
    if (isLoggedIn) {
        if (role === "MANAGER") {
            pages.push(i18nPages["users"]);
            pages.push(i18nPages["stats"]);
        }
        pages.push(i18nPages["items"]);
        pages.push(i18nPages["warehouses"]);
        pages.push(i18nPages["stocks"]);
        pages.push(i18nPages["shipments"]);
    }

    return (
        <div className="drawer h-screen">
            <input
                id="nav-drawer"
                type="checkbox"
                className="drawer-toggle"
                checked={isOpen}
                onChange={toggleIsOpen}
            />
            <div className="drawer-content flex flex-col">
                <Nav pages={pages} isActive={isActive} />
                {children}
            </div>
            <div className="drawer-side sm:!hidden">
                <label htmlFor="nav-drawer" className="drawer-overlay"></label>
                <NavSideBar pages={pages} isActive={isActive} />
            </div>
        </div>
    );
}

function NavSideBar({ pages, isActive }) {
    return (
        <ul className="menu p-4 overflow-y-auto w-80 bg-base-100 text-base-content">
            {pages.map(({ url: path, display }, key) => (
                <Link key={key} href={`/${path}`}>
                    <li>
                        <a
                            className={`btn btn-ghost btn-sm rounded-btn !justify-start ${isActive(
                                path
                            )}`}
                        >
                            {display}
                        </a>
                    </li>
                </Link>
            ))}
        </ul>
    );
}
