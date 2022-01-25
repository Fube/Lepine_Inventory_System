import { Form, Field, useField, useFormikContext } from "formik";
import DatePicker from "react-datepicker";

/**
 *
 * @param {import("formik").FieldAttributes<any>} param0
 * @returns
 */
export function GenericFormInputErrorCombo(fieldAttributes) {
    const [inputProps, { error, touched }, helperProps] = useField(
        fieldAttributes.name
    );
    return (
        <>
            <label
                className="block text-gray-700 text-sm font-bold mb-2"
                htmlFor={fieldAttributes.name}
            >
                {fieldAttributes.placeholder}
            </label>
            <Field
                className={`shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline ${
                    error && touched ? "border-red-500" : ""
                }`}
                {...fieldAttributes}
            />
            {error && touched && (
                <div className="text-red-500 text-xs italic">{error}</div>
            )}
        </>
    );
}

/**
 *
 * @param {import("formik").FieldAttributes<any>} param0
 * @returns
 */
export function GenericFormToggleErrorCombo(fieldAttributes) {
    const [inputProps, { error, touched }, helperProps] = useField(
        fieldAttributes.name
    );
    return (
        <>
            <label
                className="block text-gray-700 text-sm font-bold mb-2"
                htmlFor={fieldAttributes.name}
            >
                {fieldAttributes.placeholder}
            </label>
            <Field
                className={`toggle toggle-accent toggle-lg ${
                    error && touched ? "border-red-500" : ""
                }`}
                {...fieldAttributes}
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

export function GenericErrorStatus() {
    const { status } = useFormikContext();
    if (
        status === undefined ||
        status === null ||
        status.isError === undefined ||
        status.isError === null
    )
        return <></>;
    return (
        <div
            className={`text-${
                status.isError ? "red" : "green"
            }-500 text-lg text-center`}
        >
            {status.message}
        </div>
    );
}

/**
 *
 * @param {import("formik").FieldAttributes<any> & {
 *    title: string;
 *    options: {value: *, text: *}[];
 * }} param0
 * @returns
 */
export function GenericFormSelectErrorCombo(fieldAttributes) {
    const [inputProps, { error, touched }, helperProps] = useField(
        fieldAttributes.name
    );
    return (
        <>
            <label
                className="block text-gray-700 text-sm font-bold mb-2"
                htmlFor={fieldAttributes.name}
            >
                {fieldAttributes.placeholder}
            </label>
            <Field
                className={
                    "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" +
                    (error && touched ? " border-red-500" : "")
                }
                component="select"
                {...fieldAttributes}
            >
                <option value="" disabled>
                    {fieldAttributes.title}
                </option>
                {fieldAttributes.options.map(({ key, value }) => (
                    <option key={value} value={value}>
                        {key}
                    </option>
                ))}
            </Field>
            {error && touched && (
                <div className="text-red-500 text-xs italic">{error}</div>
            )}
        </>
    );
}

export const DatePickerField = (fieldAttributes) => {
    const { setFieldValue } = useFormikContext();
    const [field] = useField(fieldAttributes);
    return (
        <>
            <label
                className="block text-gray-700 text-sm font-bold mb-2"
                htmlFor={fieldAttributes.name}
            >
                {fieldAttributes.placeholder}
            </label>
            <DatePicker
                minDate={fieldAttributes.minDate}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                {...field}
                {...fieldAttributes}
                selected={(field.value && new Date(field.value)) || null}
                onChange={(val) => {
                    setFieldValue(field.name, val);
                }}
            />
        </>
    );
};
