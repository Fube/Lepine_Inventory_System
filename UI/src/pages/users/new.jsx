import { Field, Form, Formik } from "formik";
import { useTranslation } from "next-i18next";
import { serverSideTranslations } from "next-i18next/serverSideTranslations";
import Head from "next/head";
import { useRouter } from "next/router";
import * as yup from "yup";
import { axiosAPI, axiosBackendAuth } from "../../config/axios";
import checkEmptyAuth from "../../utils/checkEmptyAuth";

const roles = ["Manager", "Clerk", "Salesperson"];

export default function CreateUser() {
    const { t: tc } = useTranslation("common");
    const { t: te } = useTranslation("errors");
    const { t: tu } = useTranslation("users");

    const router = useRouter();

    const userSchema = yup.object().shape({
        email: yup
            .string()
            .email(te("email.valid"))
            .required(te("email.required")),
        password: yup
            .string()
            .strongPassword({
                messages: te("password.complexity", {
                    returnObjects: true,
                }),
            })
            .required(te("password.required")),
        confirmPassword: yup
            .string()
            .test("passwords-match", te("password.confirm"), function (value) {
                return this.parent.password === value;
            }),
        role: yup.string().required(te("role.required")).oneOf(roles),
    });

    const handleSubmit = async (values, { setSubmitting, setStatus }) => {
        setSubmitting(true);
        try {
            const { password, email, role } = values;
            const { data } = await axiosAPI.post("/users", {
                password,
                email,
                role: role.toUpperCase(),
            });

            setStatus({ isError: false, message: "" });
            router.push("/users");
        } catch (e) {
            console.log(e);
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
                <title>{tu("new.title")}</title>
            </Head>
            <div className="flex flex-col flex-1">
                <div className="flex-shrink-0 flex-grow-0"></div>
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
                                                    {tc("register")}
                                                </div>
                                                {dirty && status && (
                                                    <div
                                                        className={`text-${
                                                            status.isError
                                                                ? "red"
                                                                : "green"
                                                        }-500 text-lg text-center`}
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
                                                        placeholder={tc(
                                                            "email"
                                                        )}
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
                                                        placeholder={tc(
                                                            "password"
                                                        )}
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
                                                        {tc("confirm_password")}
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
                                                        placeholder={tc(
                                                            "confirm_password"
                                                        )}
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
                                                        {tc("role")}
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
                                                            {tu("role.select")}
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
                                                            {tc("register")}
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

/**
 *
 * @param {import("next/types").GetServerSidePropsContext} context
 * @returns
 */
export async function getServerSideProps(ctx) {
    const i18n = await serverSideTranslations(ctx.locale, [
        "common",
        "errors",
        "users",
    ]);

    return checkEmptyAuth(axiosBackendAuth, ctx, {
        ...i18n,
    });
}
