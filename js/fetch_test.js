const HOST = "http://192.168.1.156:8080/";
const API = "coffee/api/auth/";
const REGISTER = "p/register";
const HELLOT = "coffee/api/temp";

let reg_data = {
    "email": "pepote@mail.es",
    "password": 1234
};

let header = {
    method: "POST",
    body: JSON.stringify(reg_data),
    headers: {
        "Content-type": "application/json; charset=UTF-8",
        "Authorization": "CBS OTBhMTE1MWEtYWQwYS0zY2ZhLWFkYWEtZGFlMWNjNGU2Njk2"
    }
};

console.log("Hellooooo javascript!!!");
this.register();
// register
function register() {
    fetch(HOST + API + REGISTER, header)
        .then(res => res.json())
        .then(json => {
            console.log(json);
            this.writeDoc(json)
        })
        .catch(err => console.log(err));
};

function writeDoc(json) {
    document.getElementById("username").innerHTML = json.email;
    document.getElementById("prefix").innerHTML = json.prefix;
    document.getElementById("token").innerHTML = json.token;
}