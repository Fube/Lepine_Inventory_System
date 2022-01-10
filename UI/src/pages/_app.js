import "../styles/globals.css";
import algoliasearch from "algoliasearch/lite";
import { createContext, useEffect, useState } from "react";
import { axiosAPI } from "../config/axios";
import injectYupMethods from "../utils/injectYupMethods";

const searchClient = algoliasearch(
    process.env.NEXT_PUBLIC_ALGOLIA_APP_ID,
    process.env.NEXT_PUBLIC_ALGOLIA_SEARCH_KEY
);

export const AlgoliaContext = createContext({
    searchClient,
});

export const AuthContext = createContext({
    isLoggedIn: false,
    role: "",
    email: "",
    setIsLoggedIn: () => {},
    setRole: () => {},
    setEmail: () => {},
    logout: async () => {},
});

function MyApp({ Component, pageProps }) {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [role, setRole] = useState("");
    const [email, setEmail] = useState("");

    useEffect(() => {
        const role = localStorage.getItem("role");
        const email = localStorage.getItem("email");
        if (role && email) {
            setIsLoggedIn(true);
            setRole(role);
            setEmail(email);
        }
    }, []);

    return (
        <AuthContext.Provider
            value={{
                isLoggedIn,
                role,
                email,
                setIsLoggedIn,
                setRole,
                setEmail,
                logout: async () => {
                    await axiosAPI.head("/auth/logout");
                    localStorage.removeItem("role");
                    localStorage.removeItem("email");
                    setIsLoggedIn(false);
                    setRole("");
                    setEmail("");
                },
            }}
        >
            <AlgoliaContext.Provider value={{ searchClient }}>
                <div className="drawer h-screen">
                    <input
                        id="nav-drawer"
                        type="checkbox"
                        className="drawer-toggle"
                    />
                    <div className="drawer-content">
                        <Component {...pageProps} />
                    </div>
                    <div className="drawer-side">
                        <label
                            htmlFor="nav-drawer"
                            className="drawer-overlay"
                        ></label>
                        <ul className="menu p-4 overflow-y-auto w-80 bg-base-100 text-base-content">
                            <li>
                                <a>Menu Item</a>
                            </li>
                            <li>
                                <a>Menu Item</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </AlgoliaContext.Provider>
        </AuthContext.Provider>
    );
}

export default MyApp;
