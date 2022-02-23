import algoliasearch from "algoliasearch/lite";
import { appWithTranslation } from "next-i18next";
import { createContext, useEffect, useState } from "react";
import NavWrapper from "../components/NavWrapper";
import { axiosAPI } from "../config/axios";
import "../styles/globals.css";
import * as yupInjections from "../utils/injectYupMethods";

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
                <NavWrapper>
                    <Component {...pageProps} />
                </NavWrapper>
            </AlgoliaContext.Provider>
        </AuthContext.Provider>
    );
}

export default appWithTranslation(MyApp);
