const mappings = new Map();
const all = ["manager", "clerk", "salesperson"];
const managerOnly = ["manager"];

// Item mappings
mappings.put("/items/new", managerOnly);
mappings.put("/items", all);
mappings.put("/items/:uuid", all);

// User mappings
mappings.put("/users/new", managerOnly);
mappings.put("/users", managerOnly);

export default mappings;
