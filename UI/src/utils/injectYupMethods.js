import * as yup from "yup";
import { DateTime } from "luxon";

yup.addMethod(yup.string, "strongPassword", strongPasswordMethod);
yup.addMethod(yup.date, "businessDay", businessDay);

function strongPasswordMethod({ messages }) {
    return this.test("strongPasswordTest", function (value) {
        const { path, createError } = this;
        switch (Boolean(value)) {
            case !/^(?=.*[a-z])/.test(value):
                return createError({
                    path,
                    message: messages.lowercase,
                });
            case !/^(?=.*[A-Z])/.test(value):
                return createError({
                    path,
                    message: messages.uppercase,
                });
            case !/^(?=.*[0-9])/.test(value):
                return createError({
                    path,
                    message: messages.number,
                });
            case !/^(?=.*[!@#\$%\^&\*])/.test(value):
                return createError({
                    path,
                    message: messages.special,
                });
            case value.length < 8:
                return createError({
                    path,
                    message: messages.length,
                });
            default:
                return true;
        }
    });
}

function businessDay() {
    return this.test("businessDay", function (value) {
        const { path, createError } = this;
        let valid = false;
        try {
            valid = DateTime.fromJSDate(value).isBusinessDay();
        } catch (e) {
            valid = false;
        }

        if (!valid) {
            return createError({
                path,
                message: "Date must be a business day",
            });
        }
        return true;
    });
}
