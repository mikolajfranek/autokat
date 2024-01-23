import * as API from './GoogleAPI';
import * as Secret from './../Secret';

const URL = `${API.URL}${Secret.getSpreadsheetLoginId()}${API.URL_SUFFIX}`;

type MyLoginProps = {
    login: string;
    company?: string;
    password?: string;
};

export async function getLogin(props: MyLoginProps) {
    let { headers } = await API.getHeaders();
    headers.tq = `select * where A="${props.login.replace(/"/gm, "'")}"`;
    const options = {
        method: 'GET',
        headers,
    };
    return fetch(URL, options)
        .then(response => {
            if (response.status != 200)
                throw new Error();
            return response.text();
        })
        .then(responseJSON => {
            var data = JSON.parse(responseJSON.match(/{.*}/gm)[0]);
            console.log(data);
            return data;
        }).catch(error => {
            console.error(error);
        });
}