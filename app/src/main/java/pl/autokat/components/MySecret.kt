package pl.autokat.components

class MySecret {
    companion object {
        //spreadsheet id login
        private const val SPREADSHEET_ID_LOGIN: String = ""
        private const val SPREADSHEET_ID_LOGIN_DEV: String = ""
        fun getSpreadsheetIdLogin(): String {
            if (MyConfiguration.PRODUCTION) {
                return this.SPREADSHEET_ID_LOGIN
            }
            return this.SPREADSHEET_ID_LOGIN_DEV
        }

        //spreadsheet id catalyst
        private const val SPREADSHEET_ID_CATALYST: String = ""
        private const val SPREADSHEET_ID_CATALYST_DEV: String = ""
        fun getSpreadsheetIdCatalyst(): String {
            if (MyConfiguration.PRODUCTION) {
                return this.SPREADSHEET_ID_CATALYST
            }
            return this.SPREADSHEET_ID_CATALYST_DEV
        }

        //private key for creating jwt
        private const val JWT_PRIVATE_KEY: String =
            ""
        private const val JWT_PRIVATE_KEY_DEV: String =
            ""

        fun getPrivateKey(): String {
            if (MyConfiguration.PRODUCTION) {
                return this.JWT_PRIVATE_KEY
            }
            return this.JWT_PRIVATE_KEY_DEV
        }

        //email for creating jwt
        private const val JWT_EMAIL: String = ""
        private const val JWT_EMAIL_DEV: String = ""
        fun getEmail(): String {
            if (MyConfiguration.PRODUCTION) {
                return this.JWT_EMAIL
            }
            return this.JWT_EMAIL_DEV
        }
    }
}