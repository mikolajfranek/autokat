const KLUCZ_API = "bbeb34ebf60ad50f7893e7440a1e2b0b";
const ADRES_API = "https://api.openweathermap.org/data/2.5/weather?";

function kodUrl(kod) {
    return `${ADRES_API}q=${kod}&units=metric&APPID=${KLUCZ_API}&lang=pl`;
}

function pobierzPrognoze(kod) {
    return fetch(kodUrl(kod))
        .then(response => response.json())
        .then(responseJSON => {
            return {
                glowne: responseJSON.weather[0].main,
                opis: responseJSON.weather[0].description,
                temp: responseJSON.main.temp
            };
        })
        .catch(error => {
            console.error(error);
        });
}

export default { pobierzPrognoze: pobierzPrognoze };