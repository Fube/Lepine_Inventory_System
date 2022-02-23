import { Field, Form, Formik } from "formik";
import Head from "next/head";
import { useRouter } from "next/router";
import { useEffect } from "react";
import * as yup from "yup";
import { axiosAPI, axiosBackendNoAuth } from "../config/axios";
import useAuth from "../hooks/useAuth";
import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";

export default function Login() {

    const { t: tc } = useTranslation("common");
    const { t: te } = useTranslation("errors");
    const { t: tl } = useTranslation("login");

    const { setEmail, setIsLoggedIn, setRole, logout } = useAuth();
    const loginSchema = yup.object().shape({
        email: yup.string().required(te("email.required")),
        password: yup.string().required(te("password.required")),
    });

    useEffect(() => {
        logout();
    }, []);

    const router = useRouter();

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            const { data } = await axiosAPI.post("/auth/login", values);

            localStorage.setItem("role", data.role);
            localStorage.setItem("email", data.email);

            setIsLoggedIn(true);
            setEmail(data.email);
            setRole(data.role);

            setStatus({ isError: false, message: tl("successful") });
            router.push("/");
        } catch (e) {
            setStatus({
                isError: true,
                message: e?.response?.data?.message ?? te("unknown"),
            });
        }
        setSubmitting(false);
    };

    return (
        <>
            <Head>
                <title>{tc('login')}</title>
            </Head>

            <main className="flex justify-center">
                <div className="text-center">
                    <div className="mt-12">
                        <h2 className="text-2xl text-center text-yellow-400 mb-6">
                            {tc('login')}
                        </h2>
                        <Formik
                            initialValues={{
                                email: "",
                                password: "",
                            }}
                            validationSchema={loginSchema}
                            onSubmit={handleSubmit}
                        >
                            {({
                                errors,
                                touched,
                                isValid,
                                isSubmitting,
                                dirty,
                                status,
                            }) => (
                                <Form>
                                    <div className="flex justify-center">
                                        <div className="w-full max-w-sm">
                                            <div className="flex flex-col break-words bg-white border-2 rounded shadow-md">
                                                <div className="font-semibold bg-gray-200 text-gray-700 py-3 px-6 mb-0">
                                                    {tc("login")}
                                                </div>
                                                {dirty && status && (
                                                    <div
                                                        className={`text-${
                                                            status.isError
                                                                ? "red"
                                                                : "green"
                                                        }-500 text-lg`}
                                                    >
                                                        {status.message}
                                                    </div>
                                                )}
                                                <div className="p-6">
                                                    <label
                                                        className="block text-gray-700 text-sm font-bold mb-2"
                                                        htmlFor="email"
                                                    >
                                                        {tc("email")}
                                                    </label>
                                                    <Field
                                                        className={
                                                            "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" +
                                                            (errors.email &&
                                                            touched.email
                                                                ? " border-red-500"
                                                                : "")
                                                        }
                                                        name="email"
                                                        type="text"
                                                        placeholder={tc("email")}
                                                    />
                                                    {errors.email &&
                                                        touched.email && (
                                                            <div className="text-red-500 text-xs italic">
                                                                {errors.email}
                                                            </div>
                                                        )}

                                                    <label
                                                        className="block text-gray-700 text-sm font-bold mb-2"
                                                        htmlFor="password"
                                                    >
                                                        {tc("password")}
                                                    </label>
                                                    <Field
                                                        className={
                                                            "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" +
                                                            (errors.password &&
                                                            touched.password
                                                                ? " border-red-500"
                                                                : "")
                                                        }
                                                        name="password"
                                                        type="password"
                                                        placeholder={tc("password")}
                                                    />
                                                    {errors.password &&
                                                        touched.password && (
                                                            <div className="text-red-500 text-xs italic">
                                                                {
                                                                    errors.password
                                                                }
                                                            </div>
                                                        )}

                                                    <div className="flex items-center justify-end p-6">
                                                        <button
                                                            className={`bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline ${
                                                                !isValid ||
                                                                !dirty ||
                                                                isSubmitting
                                                                    ? "opacity-50 cursor-not-allowed"
                                                                    : ""
                                                            }`}
                                                            type="submit"
                                                            disabled={
                                                                !isValid ||
                                                                !dirty ||
                                                                isSubmitting
                                                            }
                                                        >
                                                            {tc("login")}
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </Form>
                            )}
                        </Formik>
                    </div>
                </div>
            </main>
        </>
    );
}

export async function getServerSideProps(context) {
    const cookie = context?.req?.header?.cookie ?? "";

    try {
        await axiosBackendNoAuth.get("/auth/fake/path", {
            headers: { cookie },
        });
    } catch (err) {
        const status = err?.response?.data?.status ?? null;

        if (status === 404) {
            return {
                redirect: {
                    destination: "/",
                    permanent: true,
                },
            };
        }
    }

    const i18n = await serverSideTranslations(context.locale, ["common", "errors", "login"]);
    return { props: {...i18n} };
}
