import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import roleRouteMappings from "../config/routeRoleMapping";
import useAuth from "../hooks/useAuth";
import clientRedirectHelper from "../utils/clientRedirectHelper";

export default function WithClientSideAuth(WrappedComponent) {
    return function (props) {
        const router = useRouter();
        const { role } = useAuth();
        const [loading, setLoading] = useState(true);

        useEffect(() => {
            clientRedirectHelper(
                roleRouteMappings.get(router.asPath),
                role.toLocaleLowerCase(),
                router
            )();
            setLoading(false);
        }, []);

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
