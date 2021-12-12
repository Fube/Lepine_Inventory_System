import capitalize from "capitalize";
import Link from "next/link";
import { useRouter } from "next/router";

export default function Nav() {
    const router = useRouter();
    const isActive = (path) =>
        router.pathname.substr(1).startsWith(path) ? "text-blue-400" : "";
    return (
        <>
            <div className="navbar mb-2 shadow-lg bg-neutral text-neutral-content">
                <div className="flex-none px-2 mx-2">
                    <span className="text-lg font-bold">
                        <Link href="/">Lepine</Link>
                    </span>
                </div>
                <div className="flex-1 px-2 mx-2">
                    <div className="items-stretch hidden lg:flex">
                        {["items"].map((path, key) => (
                            <Link key={key} href={`/${path}`}>
                                <a
                                    className={`btn btn-ghost btn-sm rounded-btn ${isActive(
                                        path
                                    )}`}
                                >
                                    {capitalize(path)}
                                </a>
                            </Link>
                        ))}
                    </div>
                </div>
            </div>
        </>
    );
}
