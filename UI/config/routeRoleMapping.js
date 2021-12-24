/** @type {Map<string, [string]>} */
const roleRouteMappings = new Map();
const all = ["manager", "clerk", "salesperson"];
const managerOnly = ["manager"];

// Item mappings
roleRouteMappings.set("/items/new", managerOnly);
roleRouteMappings.set("/items", all);
roleRouteMappings.set("/items/:uuid", all);

// User mappings
roleRouteMappings.set("/users/new", managerOnly);
roleRouteMappings.set("/users", managerOnly);

export default roleRouteMappings;
