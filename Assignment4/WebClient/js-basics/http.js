
var DateTime ;

/**
 * This method send a http get request to the web server and gets the motion data.
 */
function functionGet() {
fromTo = SellectDate(); // get the sellected intervall
    const Http = new XMLHttpRequest();
    const url = 'http://192.168.20.187:8888/'+fromTo;
    Http.open("GET", url,true);
    Http.send(url);
  Http.responseType ="document";

    Http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var xmlDoc = Http.responseXML;
            console.log(xmlDoc);
            var temp = xmlDoc.getElementsByTagName("body")[0].childNodes[0].nodeValue;
           DateTime = temp;
           drawBasic();
        }
    }
 
    return false;
}
/**
 * This method converts a js object to a string in json format and returns the string.
 */
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