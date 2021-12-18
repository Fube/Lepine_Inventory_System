import { Form, Formik, Field } from "formik";
import Head from "next/head";
import { useRouter } from "next/router";
import * as yup from "yup";
import Nav from "../../components/Nav";

// TODO: Fetch this from DB
const roles = ["Manager", "Clerk", "Salesperson"];

export default function CreateUser() {
    const router = useRouter();

    const userSchema = yup.object().shape({
        email: yup
            .string()
            .email("Must be a valid email")
            .required("Email is required"),
        password: yup
            .string()
            .strongPassword()
            .required("Password is required"),
        confirmPassword: yup
            .string()
            .test("passwords-match", "Passwords must match", function (value) {
                return this.parent.password === value;
            }),
    });

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            const { data } = await axiosAPI.post("/auth/login", values);

            localStorage.setItem("role", data.role);
            localStorage.setItem("email", data.email);

            setIsLoggedIn(true);
            setEmail(data.email);
            setRole(data.role);

            setStatus({ isError: false, message: "Successfully logged in" });
            router.push("/");
        } catch (e) {
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
                <title>Add User</title>
            </Head>
            <div className="flex flex-col h-screen">
                <div className="flex-shrink-0 flex-grow-0">
                    <Nav />
                </div>
                <div className="flex-grow flex justify-center items-center">
                    <div className="w-full">
                        <Formik
                            initialValues={{
                                email: "",
                                password: "",
                                passwordConfirm: "",
                                role: "",
                            }}
                            validationSchema={userSchema}
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
                                                    Register
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
                                                    {errors.password &&
                                                        touched.password && (
                                                            <div className="text-red-500 text-xs italic">
                                                                {
                                                                    errors.password
                                                                }
                                                            </div>
                                                        )}

                                                    <label
                                                        className="block text-gray-700 text-sm font-bold mb-2"
                                                        htmlFor="confirmPassword"
                                                    >
                                                        Confirm Password
                                                    </label>
                                                    <Field
                                                        className={
                                                            "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" +
                                                            (errors.confirmPassword &&
                                                            touched.confirmPassword
                                                                ? " border-red-500"
                                                                : "")
                                                        }
                                                        name="confirmPassword"
                                                        type="password"
                                                        placeholder="Confirm Password"
                                                    />
                                                    {errors.confirmPassword &&
                                                        touched.confirmPassword && (
                                                            <div className="text-red-500 text-xs italic">
                                                                {
                                                                    errors.confirmPassword
                                                                }
                                                            </div>
                                                        )}

                                                    {/* Formik select element */}
                                                    <label
                                                        className="block text-gray-700 text-sm font-bold mb-2"
                                                        htmlFor="role"
                                                    >
                                                        Role
                                                    </label>
                                                    <Field
                                                        className={
                                                            "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" +
                                                            (errors.role &&
                                                            touched.role
                                                                ? " border-red-500"
                                                                : "")
                                                        }
                                                        name="role"
                                                        component="select"
                                                    >
                                                        <option
                                                            value=""
                                                            disabled
                                                        >
                                                            Select Role
                                                        </option>
                                                        {roles.map((role) => (
                                                            <option
                                                                key={role}
                                                                value={role}
                                                            >
                                                                {role}
                                                            </option>
                                                        ))}
                                                    </Field>
                                                    {errors.role &&
                                                        touched.role && (
                                                            <div className="text-red-500 text-xs italic">
                                                                {errors.role}
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
            </div>
        </>
    );
}
