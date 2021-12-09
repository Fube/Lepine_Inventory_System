import "../styles/globals.css";
import { InstantSearch } from "react-instantsearch-dom";
import algoliasearch from "algoliasearch/lite";

const searchClient = algoliasearch(
    "latency",
    "6be0576ff61c053d5f9a3225e2a90f76"
);

function MyApp({ Component, pageProps }) {
    return (
        <InstantSearch searchClient={searchClient} indexName="instant_search">
            <Component {...pageProps} />
        </InstantSearch>
    );
}

export default MyApp;
