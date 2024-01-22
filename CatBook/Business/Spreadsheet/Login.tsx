import * as API from './GoogleApi';
import * as Secret from './../Secret';

const URL = `${API.URL}${Secret.getSpreadSheetLoginId()}${API.URL_SUFFIX}`;

type MyLoginProps = {
    login: string;
    company?: string;
    password?: string;
};

export function getLogin(props: MyLoginProps) {
    let { headers } = API.getHeaders();
    headers.tq = `select * where A="${props.login.replace(/"/gm, "'")
        }"`;
    const options = {
        method: 'GET',
        headers,
    };
    const result = fetch(URL, options)
        .then(response => {
            console.log(response);
            if (response.status != 200)
                throw new Error();
            response.json()
        })
        .then(responseJSON => {
            console.log('is ookkkk');
            return responseJSON;
        }).catch(error => {
            console.error(error);
        });

    return "";
}