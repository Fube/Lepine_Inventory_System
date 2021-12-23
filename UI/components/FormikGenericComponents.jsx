import { Form, Field, useField, useFormikContext } from "formik";
export function GenericFormInputErrorCombo({ name, type, placeholder }) {
    const [inputProps, { error, touched }, helperProps] = useField(name);
    return (
        <>
            <label
                className="block text-gray-700 text-sm font-bold mb-2"
                htmlFor={name}
            >
                Description
            </label>
            <Field
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${
                    error && touched ? "border-red-500" : ""
                }`}
                name={name}
                type={type}
                placeholder={placeholder}
            />
            {error && touched && (
                <div className="text-red-500 text-xs italic">{error}</div>
            )}
        </>
    );
}

/**
 *
 * @param {{ title: string | import("react").Component } & import("react").PropsWithChildren } props
 * @returns
 */
export function GenericForm({ title, children }) {
    return (
        <Form>
            <div className="flex justify-center">
                <div className="w-full max-w-sm">
                    <div className="flex flex-col break-words bg-white border-2 rounded shadow-md">
                        <div className="font-semibold bg-gray-200 text-gray-700 py-3 px-6 mb-0">
                            {title}
                        </div>
                        <div className="p-6">{children}</div>
                    </div>
                </div>
            </div>
        </Form>
    );
}

export function GenericSubmitButton() {
    const { isValid, dirty, isSubmitting } = useFormikContext();
    return (
        <button
            className={`bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline ${
                !isValid || !dirty || isSubmitting
                    ? "opacity-50 cursor-not-allowed"
                    : ""
            }`}
            type="submit"
            disabled={!isValid || !dirty || isSubmitting}
        >
            Save
        </button>
    );
}
