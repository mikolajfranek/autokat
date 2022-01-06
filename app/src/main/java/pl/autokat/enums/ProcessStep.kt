package pl.autokat.enums

enum class ProcessStep {
    NONE,
    SUCCESS,
    UNHANDLED_EXCEPTION,
    USER_NEVER_LOGGED,
    USER_FAILED_LOGIN,
    USER_ELAPSED_DATE_LICENCE,
    USER_FAILED_SERIAL,
    NETWORK_FAILED
}