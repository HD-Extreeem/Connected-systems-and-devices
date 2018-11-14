
var DateTime = { "DateTime": [{ "Date": "2018-11-3", "Time": "23:11:06" }, { "Date": "2018-11-3", "Time": "23:11:06" }, { "Date": "2018-11-4", "Time": "10:11:06" }, { "Date": "2018-11-6", "Time": "10:11:06" }, { "Date": "2018-11-6", "Time": "10:11:06" }, { "Date": "2018-11-6", "Time": "11:11:06" }, { "Date": "2018-11-6", "Time": "12:11:06" }, { "Date": "2018-11-6", "Time": "13:11:06" }, { "Date": "2018-11-6", "Time": "14:11:06" }, { "Date": "2018-11-6", "Time": "15:11:06" }] }
//var DateTime;


function functionGet(fromTo) {
    const Http = new XMLHttpRequest();
    const url = 'https//192.168.20.231:8888/' + fromTo;
    Http.open("GET", url);
    Http.send();
    Http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            //  DateTime = JSON.parse(Http.responseText);
            //  console.log(DateTime.DateTime.Date[0]);
            //setObject();
        }
    }
    return false;
}


function getObject() {
    if (DateTime != null) {
        var myObj = JSON.stringify(DateTime);
        myObj = JSON.parse(myObj);
        return myObj;
    }
    else {
        return null;
    }
}