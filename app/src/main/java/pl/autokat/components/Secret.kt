package pl.autokat.components

class Secret {
    companion object {
        //region user
        private const val SPREADSHEET_ID_LOGIN: String =
            ""
        private const val SPREADSHEET_ID_LOGIN_DEV: String =
            ""

        fun getSpreadsheetIdLogin(): String {
            if (Configuration.PRODUCTION) {
                return this.SPREADSHEET_ID_LOGIN
            }
            return this.SPREADSHEET_ID_LOGIN_DEV
        }

        private const val SPREADSHEET_ID_CATALYST: String =
            ""
        private const val SPREADSHEET_ID_CATALYST_DEV: String =
            ""

        fun getSpreadsheetIdCatalyst(): String {
            if (Configuration.PRODUCTION) {
                return this.SPREADSHEET_ID_CATALYST
            }
            return this.SPREADSHEET_ID_CATALYST_DEV
        }

        private const val PRIVATE_KEY: String =
            ""
        private const val PRIVATE_KEY_DEV: String =
            ""

        fun getPrivateKey(): String {
            if (Configuration.PRODUCTION) {
                return this.PRIVATE_KEY
            }
            return this.PRIVATE_KEY_DEV
        }

        private const val EMAIL: String =
            ""
        private const val EMAIL_DEV: String =
            ""

        fun getEmail(): String {
            if (Configuration.PRODUCTION) {
                return this.EMAIL
            }
            return this.EMAIL_DEV
        }
        //endregion

        //region apk
        const val ID_COMPANY = 1

        private const val APK_SPREADSHEET_ID_COMPANY: String =
            ""
        private const val APK_SPREADSHEET_ID_COMPANY_DEV: String =
            ""

        fun getApkSpreadsheetIdCompany(): String {
            if (Configuration.PRODUCTION) {
                return this.APK_SPREADSHEET_ID_COMPANY
            }
            return this.APK_SPREADSHEET_ID_COMPANY_DEV
        }

        private const val APK_PRIVATE_KEY: String =
            ""
        private const val APK_PRIVATE_KEY_DEV: String =
            ""

        fun getApkPrivateKey(): String {
            if (Configuration.PRODUCTION) {
                return this.APK_PRIVATE_KEY
            }
            return this.APK_PRIVATE_KEY_DEV
        }

        private const val APK_EMAIL: String =
            ""
        private const val APK_EMAIL_DEV: String =
            ""

        fun getApkEmail(): String {
            if (Configuration.PRODUCTION) {
                return this.APK_EMAIL
            }
            return this.APK_EMAIL_DEV
        }

        //endregion
    }
}