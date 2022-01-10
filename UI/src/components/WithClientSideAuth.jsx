import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import roleRouteMappings from "../config/routeRoleMapping";
import useAuth from "../hooks/useAuth";

export default function WithClientSideAuth(WrappedComponent) {
    return function Wrap(props) {
        const router = useRouter();
        const { role } = useAuth();
        const [loading, setLoading] = useState(true);

        useEffect(() => {
            if (!role) return;

            const mapped = roleRouteMappings
                .get(router.asPath)
                .includes(role.toLocaleLowerCase());
            if (!mapped) {
                router.push("/login");
            }
            setLoading(false);
        }, [role]);

        if (loading) {
            return (
                <div className="flex justify-center h-screen">
                    <div className="flex flex-col justify-center">
                        <h1>Loading...</h1>
                    </div>
                </div>
            );
        }
        return <WrappedComponent {...props} />;
    };
}
