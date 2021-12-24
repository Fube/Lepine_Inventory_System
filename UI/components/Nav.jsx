import Link from "next/link";
import { useRouter } from "next/router";
import useAuth from "../hooks/useAuth";

export default function Nav() {
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
    } else {
        pages.push("login");
    }

    return (
        <>
            <div className="navbar mb-2 shadow-lg bg-neutral text-neutral-content">
                <div className="flex-none px-2 mx-2">
                    <span className="text-lg font-bold">
                        <Link href="/">Lepine</Link>
                    </span>
                </div>
                <div className="flex-1 px-2 mx-2 lg:justify-between">
                    <div className="items-stretch hidden lg:flex">
                        {pages.map((path, key) => (
                            <Link key={key} href={`/${path}`}>
                                <a
                                    className={`btn btn-ghost btn-sm rounded-btn ${isActive(
                                        path
                                    )}`}
                                >
                                    {path}
                                </a>
                            </Link>
                        ))}
                    </div>
                    <div>
                        {isLoggedIn && (
                            <a
                                className={`btn btn-ghost btn-sm rounded-btn`}
                                onClick={() =>
                                    logout().then(() => router.push("/"))
                                }
                            >
                                logout
                            </a>
                        )}
                    </div>
                </div>
            </div>
        </>
    );
}
