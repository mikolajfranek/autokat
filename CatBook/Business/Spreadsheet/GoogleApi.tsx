import Key from './../main-nova-412011-f3a44323b34c.json';

export const URL = "https://docs.google.com/a/google.com/spreadsheets/d/";
export const URL_SUFFIX = "/gviz/tq";

export function getHeaders() {
    return {
        headers: {
            'Content-type': 'application/json',
            'Authorization': `Bearer ${getBearerToken()}`,
            'tqx': 'out:json',
            'tq' : 'ASSIGN QUERY HERE',
        }
    }
}

function getBearerToken() {
    //POTRZEBUJE ZEWNETRZNYCH MODULOW
    return null;
}

function getAccessToken(){
    return null;
}