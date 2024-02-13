export function getUser(login: string, company: string, pass: string) {
    return new Promise<{ data: boolean }>((resolve) =>
        setTimeout(() => {
            if (login == "admin") {
                resolve({ data: true });
            } else {
                resolve({ data: false });
            }
        }, 4000)
    );
}