import Secret from './Secret.js';

const TOKEN_URL = "https://oauth2.googleapis.com/token";
const TOKEN_SCOPE = "scope";
const TOKEN_SCOPE_VALUE = "https://www.googleapis.com/auth/spreadsheets";
const DOCS_API_URL = "https://docs.google.com/a/google.com/spreadsheets/d/";
const DOCS_API_URL_SUFFIX = "/gviz/tq";
const DOCS_API_URL_CATALYST = `${DOCS_API_URL}${Secret.SPREADSHEET_ID_CATALYST_DEV}${DOCS_API_URL_SUFFIX}`;
const DOCS_API_PARAMETER_JSON = "tqx";
const DOCS_API_PARAMETER_JSON_VALUE = "out:json";
const DOCS_API_PARAMETER_WHERE = "tq";

function generateNewBearerToken() {
    return null;
}

//function parseToJsonFromResultDocsApi(text) {
//return JSONObject("\\{.*\\}".toRegex().find(text)!!.value)
//}

function getBearerToken() {
    return null;
}

const SPREADSHEET_CATALYST_COLUMN_ID = "A";
const SPREADSHEET_CATALYST_COLUMN_NAME = "B";
const SPREADSHEET_CATALYST_COLUMN_BRAND = "C";
const SPREADSHEET_CATALYST_COLUMN_WEIGHT = "D";
const SPREADSHEET_CATALYST_COLUMN_PLATINUM = "E";
const SPREADSHEET_CATALYST_COLUMN_PALLADIUM = "F";
const SPREADSHEET_CATALYST_COLUMN_RHODIUM = "G";
const SPREADSHEET_CATALYST_COLUMN_TYPE = "H";
const SPREADSHEET_CATALYST_COLUMN_ID_PICTURE = "I";
const SPREADSHEET_CATALYST_COLUMN_URL_PICTURE = "J";
function getCatalysts(fromRow) {
    const options = {
        method: 'GET',
        headers: {
            'Content-type': 'application/json',
            'Authorization': `Bearer ${getBearerToken()}`,
            DOCS_API_PARAMETER_JSON: DOCS_API_PARAMETER_JSON_VALUE,
            DOCS_API_PARAMETER_WHERE: `select * where ${SPREADSHEET_CATALYST_COLUMN_ID}>${fromRow}`,
        },
    };
    return fetch(DOCS_API_URL_CATALYST, options)
        .then(response => response.json())
        .then(responseJSON => {
            return responseJSON;
        }).catch(error => {
            console.error(error);
        });
}

export default { getCatalysts: getCatalysts };

