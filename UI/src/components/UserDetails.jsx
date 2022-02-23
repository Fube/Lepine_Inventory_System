import { Formik } from "formik";
import { useTranslation } from "next-i18next";
import * as yup from "yup";
import {
    GenericForm,
    GenericFormInputErrorCombo,
    GenericFormSelectErrorCombo,
    GenericSubmitButton,
} from "./FormikGenericComponents";

const roles = ["Manager", "Clerk", "Salesperson"];

/**
 *
 * @param {{editable: boolean, deletable: boolean, handleDelete: (uuid: string) => void, handleSubmit: ({ values, setSubmitting: (isSubmitting: boolean)=>void }) => void } & import('@lepine/types').User} arg0
 */
export default function UserDetails({
    uuid,
    email,
    password,
    role,
    editable,
    deletable,
    handleDelete = () => {},
    handleSubmit = () => {},
}) {
    const { t: tc } = useTranslation("common");
    const { t: te } = useTranslation("errors");
    const { t: tu } = useTranslation("users");

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

    return (
        <>
            <Formik
                initialValues={{
                    uuid,
                    email,
                    role,
                }}
                validationSchema={userSchema}
                onSubmit={handleSubmit}
            >
                {() => (
                    <GenericForm title={tu("uuid.title")}>
                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="email"
                            type="text"
                            placeholder={tc("email")}
                        />

                        <GenericFormSelectErrorCombo
                            disabled={!editable}
                            name="role"
                            options={roles.map((role) => ({
                                key: role,
                                value: role,
                            }))}
                            title={tu("role.select")}
                            placeholder={tc("role")}
                        />

                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="password"
                            type="password"
                            placeholder={tc("password")}
                        />
                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="confirmPassword"
                            type="password"
                            placeholder={tc("confirm_password")}
                        />

                        <div className="flex items-center justify-end p-6">
                            {editable && (
                                <GenericSubmitButton text={tc("save")} />
                            )}
                            {deletable && (
                                <button
                                    className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded ml-4 focus:outline-none focus:shadow-outline"
                                    type="button"
                                    onClick={handleDelete}
                                >
                                    {tc("delete")}
                                </button>
                            )}
                        </div>
                    </GenericForm>
                )}
            </Formik>
        </>
    );
}
