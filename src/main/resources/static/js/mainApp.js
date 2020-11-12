
function getActual(id) {

    const userAction = async () => {

        const response = await fetch('/get/actual/' + id, {
            method: 'GET',
            body: myBody, // string or object
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return await response.json();
    }
}





const simpleGet = async () => {
    const response = await fetch('/manual/getAll');
    return await response.json();
}