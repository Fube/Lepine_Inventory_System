/**
 * @typedef ItemProps
 * @property {string} uuid
 * @property {string} name
 * @property {string} description
 * @property {string} sku
 */

/**
 *
 * @param {ItemProps} arg0
 */
export default function Item({ uuid, name, description, sku }) {
    return (
        <>
            {uuid} {name} {description} {sku}
        </>
    );
}
