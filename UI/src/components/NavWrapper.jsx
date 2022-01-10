import Link from "next/link";
import { useRouter } from "next/router";
import useAuth from "../hooks/useAuth";
import Nav from "./Nav";

export default function NavWrapper({ children }) {
    const { isLoggedIn, role, logout } = useAuth();
    const router = useRouter();

    const isActive = (path) =>
        router.pathname.substring(1).startsWith(path) ? "text-blue-400" : "";
    const pages = [];

    if (isLoggedIn) {
        if (role === "MANAGER") {
            pages.push("users");
        }
        pages.push("items");
        pages.push("warehouses");
    }

    return (
        <div className="drawer h-screen">
            <input id="nav-drawer" type="checkbox" className="drawer-toggle" />
            <div className="drawer-content">
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
            {pages.map((path, key) => (
                <Link key={key} href={`/${path}`}>
                    <li>
                        <a
                            className={`btn btn-ghost btn-sm rounded-btn !justify-start ${isActive(
                                path
                            )}`}
                        >
                            {path}
                        </a>
                    </li>
                </Link>
            ))}
        </ul>
    );
}
