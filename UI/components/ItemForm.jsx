import * as yup from "yup";
import { Formik } from "formik";
import {
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
} from "./FormikGenericComponents";

/**
 * @typedef Item
 * @property {string} uuid
 * @property {string} name
 * @property {string} description
 * @property {string} sku
 */

/**
 *
 * @param {{...Item, editable: boolean, deletable: boolean, handleDelete: (uuid: string) => void, handleSubmit: ({ values, setSubmitting: (isSubmitting: boolean)=>void }) => }} arg0
 */
export default function ItemForm({
    uuid,
    name,
    description,
    sku,
    editable,
    deletable,
    handleDelete = () => {},
    handleSubmit = () => {},
}) {
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
                    onSubmit={handleSubmit}
                >
                    {() => (
                        <GenericForm title="Item Details">
                            <GenericFormInputErrorCombo
                                name="name"
                                type="text"
                                placeholder="Name"
                            />

                            <GenericFormInputErrorCombo
                                name="description"
                                type="text"
                                placeholder="Description"
                            />

                            <GenericFormInputErrorCombo
                                name="sku"
                                type="text"
                                placeholder="SKU"
                            />
                            <div className="flex items-center justify-end p-6">
                                <GenericSubmitButton text="Save" />
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
}
