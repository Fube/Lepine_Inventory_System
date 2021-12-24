export default function WithClientSideAuth(WrappedComponent) {
    return function (props) {
        const router = useRouter();
        const { role } = useAuth();

        useEffect(
            clientRedirectHelper(
                roleRouteMappings.get(router.asPath),
                role.toLocaleLowerCase(),
                router
            ),
            []
        );

        return <WrappedComponent {...props} />;
    };
}
