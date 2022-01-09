export default function thou(a) {
    return {
        or(b) {
            return {
                get() {
                    return a ?? b;
                },

                if(condition) {
                    return condition ? a : b;
                },
            };
        },
    };
}
