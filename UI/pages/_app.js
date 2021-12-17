import "../styles/globals.css";
import algoliasearch from "algoliasearch/lite";
import { createNullCache } from "@algolia/cache-common";
import { createContext, useEffect, useState } from "react";
import { axiosAPI } from "../config/axios";

const searchClient = algoliasearch(
    "3VJL1MLU0K",
    "d5abea3b2d8eff8328c34155b1713c39"
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
    logout: () => {},
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
                <Component {...pageProps} />
            </AlgoliaContext.Provider>
        </AuthContext.Provider>
    );
}

export default MyApp;
