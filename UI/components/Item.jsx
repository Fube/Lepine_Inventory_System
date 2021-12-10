import * as yup from "yup";
import { Formik, Form, Field } from "formik";
import { axiosBackend } from "../config/axios";
import { useRouter } from "next/router";

/**
 * @typedef Item
 * @property {string} uuid
 * @property {string} name
 * @property {string} description
 * @property {string} sku
 */

/**
 *
 * @param {{...Item, editable: boolean}} arg0
 */
export default function Item({ uuid, name, description, sku, editable }) {
    const router = useRouter();

    const itemSchema = yup.object().shape({
        name: yup.string().required("Name is required"),
        description: yup.string().required("Description is required"),
        sku: yup.string().required("SKU is required"),
    });
    if (editable) {
        return (
            <>
                <Formik
                    initialValues={{
                        name,
                        description,
                        sku,
                    }}
                    validationSchema={itemSchema}
                    onSubmit={(values, { setSubmitting }) => {
                        setSubmitting(true);
                        axiosBackend.put(`/items/${uuid}`, values).then(() => {
                            setSubmitting(false);
                            router.back();
                        });
                    }}
                >
                    {({ errors, touched, isValid, isSubmitting, dirty }) => (
                        <Form>
                            <div className="flex justify-center">
                                <div className="w-full max-w-sm">
                                    <div className="flex flex-col break-words bg-white border-2 rounded shadow-md">
                                        <div className="font-semibold bg-gray-200 text-gray-700 py-3 px-6 mb-0">
                                            Item Details
                                        </div>
                                        <div className="p-6">
                                            <label
                                                className="block text-gray-700 text-sm font-bold mb-2"
                                                htmlFor="name"
                                            >
                                                Name
                                            </label>
                                            <Field
                                                className={
                                                    "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" +
                                                    (errors.name && touched.name
                                                        ? " border-red-500"
                                                        : "")
                                                }
                                                name="name"
                                                type="text"
                                                placeholder="Name"
                                            />
                                            {errors.name && touched.name && (
                                                <div className="text-red-500 text-xs italic">
                                                    {errors.name}
                                                </div>
                                            )}
                                            <label
                                                className="block text-gray-700 text-sm font-bold mb-2"
                                                htmlFor="description"
                                            >
                                                Description
                                            </label>
                                            <Field
                                                className={
                                                    "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" +
                                                    (errors.description &&
                                                    touched.description
                                                        ? " border-red-500"
                                                        : "")
                                                }
                                                name="description"
                                                type="text"
                                                placeholder="Description"
                                            />
                                            {errors.description &&
                                                touched.description && (
                                                    <div className="text-red-500 text-xs italic">
                                                        {errors.description}
                                                    </div>
                                                )}
                                            <label
                                                className="block text-gray-700 text-sm font-bold mb-2"
                                                htmlFor="sku"
                                            >
                                                SKU
                                            </label>
                                            <Field
                                                className={
                                                    "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline" +
                                                    (errors.sku && touched.sku
                                                        ? " border-red-500"
                                                        : "")
                                                }
                                                name="sku"
                                                type="text"
                                                placeholder="SKU"
                                            />
                                            {errors.sku && touched.sku && (
                                                <div className="text-red-500 text-xs italic">
                                                    {errors.sku}
                                                </div>
                                            )}
                                        </div>
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
                                                Save
                                            </button>
                                            <button
                                                className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded ml-4 focus:outline-none focus:shadow-outline"
                                                type="button"
                                            >
                                                Delete
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </Form>
                    )}
                </Formik>
            </>
        );
    }
}
