import Head from "next/head";
import { useRouter } from "next/router";
import Nav from "../components/Nav";
import * as yup from "yup";
import { Formik, Form, Field } from "formik";
import { axiosAPI, axiosBackend } from "../config/axios";

export default function Login() {
    const loginSchema = yup.object().shape({
        email: yup.string().required("Email is required"),
        password: yup.string().required("Password is required"),
    });

    const router = useRouter();

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            const { data } = await axiosAPI.post("/auth/login", values);
            console.log(data);
            localStorage.setItem("role", data.role);
            localStorage.setItem("email", data.email);
            setStatus({ isError: false, message: "Successfully logged in" });
            router.push("/");
        } catch (e) {
            console.log(e);
            setStatus({
                isError: true,
                message: e?.response?.data?.message ?? "Something went wrong",
            });
        }
        setSubmitting(false);
    };

    return (
        <>
            <Head>
                <title>Login</title>
            </Head>
            <Nav />

            <main className="flex justify-center">
                <div className="text-center">
                    <div className="mt-12">
                        <h2 className="text-2xl text-center text-yellow-400">
                            Login
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
                                                    Login
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
                                                        E-mail
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
                                                        placeholder="E-mail"
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
                                                        Password
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
                                                        placeholder="Password"
                                                    />

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
                                                            Login
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
    try {
        const res = await axiosBackend.get("/auth/fake/path", {
            headers: { ...context.req.headers },
        });
    } catch (e) {
        const { response: res } = e;
        if (res.status !== 401 && res.status !== 403) {
            return {
                redirect: {
                    destination: "/",
                    permanent: true,
                },
            };
        }
    }
}
