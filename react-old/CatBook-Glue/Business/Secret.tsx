import UserDevAPI from './lyrical-ring-412422-dad0e7bc61e1.json';

const PRODUCTION = false;

export function getSpreadsheetLoginId(): string {
    if (PRODUCTION)
        return "";
    return "1-iGo6TN3RNWMN56UQazdnGVq5IAka9FDFDJwD-0Jf4A";
}

export function getPrivateKey() {
    if (PRODUCTION)
        return "";
    return UserDevAPI.private_key;
}