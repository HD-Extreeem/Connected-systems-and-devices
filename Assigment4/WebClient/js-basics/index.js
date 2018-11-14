
function SellectDate() {
    var from = document.getElementById("From").value;
    var to = document.getElementById("To").value;
    from = from.replace("T", "&");
    to = to.replace("T", "&");
    from = from.replace(":", "-");
    to = to.replace(":", "-");
    from = from + "-00";
    to = to + "-00";
    document.getElementById("demo").innerHTML = from + "&" + to;
    functionGet((from + "&" + to));
}


function onLoadFunctions() {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    if (month < 10) {
        month = "0" + month;
    }
    var day = date.getDay();
    if (day < 10) {
        day = "0" + day;
    }
    var hour = date.getHours();
    if (hour < 10) {
        hour = "0" + hour;
    }
    var minutes = date.getMinutes();
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    document.getElementById("From").value = year + "-" + month + "-" + day + "T" + hour + ":" + minutes;

    document.getElementById("To").value = year + "-" + month + "-" + day + "T" + hour + ":" + minutes;
}

