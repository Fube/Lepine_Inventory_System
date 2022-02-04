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
roleRouteMappings.set("/stocks", all);
roleRouteMappings.set("/stocks/new", managerOnly);

// Shipment mappings
roleRouteMappings.set("/shipments", all);
roleRouteMappings.set("/shipments/new", ["manager", "salesperson"]);

// Stats mappings
roleRouteMappings.set("/stats", all);

export default roleRouteMappings;
