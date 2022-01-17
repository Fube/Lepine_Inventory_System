import * as yup from "yup";
import { Formik } from "formik";
import {
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
} from "./FormikGenericComponents";

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
        email: yup.string().required("Email is required"),
        password: yup.string().required("Password is required"),
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

                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="password"
                            type="text"
                            placeholder="Password"
                        />
                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="Confirm password"
                            type="text"
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
