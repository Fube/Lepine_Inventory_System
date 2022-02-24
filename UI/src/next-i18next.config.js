const path = require("path");

module.exports = {
    i18n: {
        defaultLocale: "en",
        locales: ["en", "fr"],
        localeDetection: true,
        localePath: path.resolve("./public/locales"),
    },
    reloadOnPrerender: true,
};
