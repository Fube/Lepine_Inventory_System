export default function getRoleFromJWT(jwt) {
    if (!jwt || !jwt.split(".")[1]) {
        return null;
    }
    const jwtParts = jwt.split(".");
    const payload = JSON.parse(Buffer.from(jwtParts[1], "base64").toString());
    return payload.role.name.substring(5);
}
