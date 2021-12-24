export default function clientRedirectHelper(mappings, role, router) {
    return () => {
        const mapped = mappings.includes(role);
        if (!mapped) {
            router.push("/login");
        }
    };
}
