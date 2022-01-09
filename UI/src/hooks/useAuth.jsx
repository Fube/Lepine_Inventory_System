import { useContext } from "react";
import { AuthContext } from "../pages/_app";

const useAuth = () => useContext(AuthContext);
export default useAuth;
