import "../styles/globals.css";
import algoliasearch from "algoliasearch/lite";
import { createContext } from "react";

const searchClient = algoliasearch(
    "3VJL1MLU0K",
    "d5abea3b2d8eff8328c34155b1713c39"
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
