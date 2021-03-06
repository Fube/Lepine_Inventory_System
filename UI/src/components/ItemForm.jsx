import * as yup from "yup";
import { Formik } from "formik";
import {
    GenericForm,
    GenericFormInputErrorCombo,
    GenericSubmitButton,
    GenericErrorStatus,
} from "./FormikGenericComponents";
import { useTranslation } from "next-i18next";

/**
 *
 * @param {{editable: boolean, deletable: boolean, handleDelete: (uuid: string) => void, handleSubmit: ({ values, setSubmitting: (isSubmitting: boolean)=>void }) => void } & import('@lepine/ui-types').Item} arg0
 */
export default function ItemForm({
    title = "Item Details",
    uuid,
    name,
    description,
    sku,
    editable,
    deletable,
    handleDelete = () => {},
    handleSubmit = () => {},
}) {
    const { t: tc } = useTranslation("common");
    const { t: ti } = useTranslation("items");
    const { t: te } = useTranslation("errors");

    const itemSchema = yup.object().shape({
        name: yup.string().required(te("item.name.required")),
        description: yup.string().required(te("item.description.required")),
        sku: yup.string().required(te("item.sku.required")),
    });

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
                    <GenericForm title={title}>
                        <GenericErrorStatus />
                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="name"
                            type="text"
                            placeholder={ti("name")}
                        />

                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="description"
                            type="text"
                            placeholder={ti("description")}
                        />

                        <GenericFormInputErrorCombo
                            disabled={!editable}
                            name="sku"
                            type="text"
                            placeholder={ti("sku")}
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
