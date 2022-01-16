/** @type {Map<string, [string]>} */
const roleRouteMappings = new Map();
const all = ["manager", "clerk", "salesperson"];
const managerOnly = ["manager"];

// Item mappings
roleRouteMappings.set("/items", all);
roleRouteMappings.set("/items/new", managerOnly);
roleRouteMappings.set("/items/:uuid", all);

// User mappings
roleRouteMappings.set("/users", managerOnly);
roleRouteMappings.set("/users/new", managerOnly);

// Warehouse mappings
roleRouteMappings.set("/warehouses", all);
roleRouteMappings.set("/warehouses/new", managerOnly);

// Stock mappings
roleRouteMappings.set("/stock", all);
roleRouteMappings.set("/stock/new", managerOnly);

export default roleRouteMappings;
