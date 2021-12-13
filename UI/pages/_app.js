import "../styles/globals.css";
import algoliasearch from "algoliasearch/lite";
import { createNullCache } from "@algolia/cache-common";
import { createContext, useEffect, useState } from "react";

const searchClient = algoliasearch(
    "OGNEYVQBFT",
    "780a6f12d6cab55b9c650ae1a4340a64"
);

export const AlgoliaContext = createContext({
    searchClient,
});

function MyApp({ Component, pageProps }) {
    return (
        <AlgoliaContext.Provider value={{ searchClient }}>
            <Component {...pageProps} />
        </AlgoliaContext.Provider>
    );
}

export default MyApp;
