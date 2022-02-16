import { Formik } from "formik";
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
        role: yup.string().required("Role is required").oneOf(roles),
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
                    <GenericForm title="User Details">
                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="email"
                            type="text"
                            placeholder="Email"
                        />

                        <GenericFormSelectErrorCombo
                            disabled={!editable}
                            name="role"
                            options={roles.map((role) => ({
                                key: role,
                                value: role,
                            }))}
                            title="Select a role"
                            placeholder="Role"
                        />

                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="password"
                            type="password"
                            placeholder="Password"
                        />
                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="confirmPassword"
                            type="password"
                            placeholder="Confirm Password"
                        />

                        <div className="flex items-center justify-end p-6">
                            {editable && <GenericSubmitButton text="Save" />}
                            {deletable && (
                                <button
                                    className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded ml-4 focus:outline-none focus:shadow-outline"
                                    type="button"
                                    onClick={handleDelete}
                                >
                                    Delete
                                </button>
                            )}
                        </div>
                    </GenericForm>
                )}
            </Formik>
        </>
    );
}
