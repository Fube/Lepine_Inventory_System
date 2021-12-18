import * as yup from "yup";

yup.addMethod(yup.string, "strongPassword", strongPasswordMethod);

function strongPasswordMethod() {
    return this.test("strongPasswordTest", function (value) {
        const { path, createError } = this;
        switch (Boolean(value)) {
            case !/^(?=.*[a-z])/.test(value):
                return createError({
                    path,
                    message: "password must include lowercase letter",
                });
            case !/^(?=.*[A-Z])/.test(value):
                return createError({
                    path,
                    message: "password must include uppercase letter",
                });
            case !/^(?=.*[0-9])/.test(value):
                return createError({
                    path,
                    message: "password must include digit",
                });
            case !/^(?=.*[!@#\$%\^&\*])/.test(value):
                return createError({
                    path,
                    message: "password must include special character",
                });
            case value.length < 8:
                return createError({
                    path,
                    message: "password must be at least 8 characters",
                });
            default:
                return true;
        }
    });
}
