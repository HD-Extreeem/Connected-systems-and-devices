
//var DateTime = { "DateTime": [{ "Date": "2018-11-3", "Time": "23:11:06" }, { "Date": "2018-11-3", "Time": "23:11:06" }, { "Date": "2018-11-4", "Time": "10:11:06" }, { "Date": "2018-11-6", "Time": "10:11:06" }, { "Date": "2018-11-6", "Time": "10:11:06" }, { "Date": "2018-11-6", "Time": "11:11:06" }, { "Date": "2018-11-6", "Time": "12:11:06" }, { "Date": "2018-11-6", "Time": "13:11:06" }, { "Date": "2018-11-6", "Time": "14:11:06" }, { "Date": "2018-11-6", "Time": "15:11:06" }] }
var DateTime;


function functionGet(fromTo) {
    fromTo="2018-11-06&14-18-28&2018-11-15&13-27-26";
    const Http = new XMLHttpRequest();
    const url = 'http://localhost:8888/'+fromTo;
 //   const url='https://jsonplaceholder.typicode.com/posts';
    Http.open("GET", url);
    Http.send(fromTo);
    Http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            DateTime = JSON.parse(Http.responseText);
            console.log(Http.responseText);
            console.log(DateTime.DateTime.Date[0]);
            setObject();
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