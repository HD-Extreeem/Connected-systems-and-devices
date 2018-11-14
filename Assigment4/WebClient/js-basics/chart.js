
var hours = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
var isDay = true;
var chart;
var mainObj;
var days = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];

function setObject() {
    mainObj = getObject();
    if (mainObj != null) {
        isDay = checkIfDay(mainObj);
        var x;
        if (isDay) {
            for (i in mainObj.DateTime) {
                x = mainObj.DateTime[i].Time.split(":");
                x = parseInt(x[0]);
                hours[x] = hours[x] + 1;
                console.log(x);
            }
        }
        else {
            for (i in mainObj.DateTime) {
                x = mainObj.DateTime[i].Date.split("-");
                x = parseInt(x[2]);
                days[x] = days[x] + 1;
                console.log(x);
            }
        }
    }
}


function drawBasic() {
    var data = new google.visualization.DataTable();
    setObject();
    if (isDay) {
        data.addColumn('timeofday', 'Time of Day');
        data.addColumn('number', 'Motion');
        data.addRows([
            [{ v: [1, 0, 0], f: '01' }, hours[0]],
            [{ v: [2, 0, 0], f: '02' }, hours[1]],
            [{ v: [3, 0, 0], f: '03' }, hours[2]],
            [{ v: [4, 0, 0], f: '04' }, hours[3]],
            [{ v: [5, 0, 0], f: '05' }, hours[4]],
            [{ v: [6, 0, 0], f: '06' }, hours[5]],
            [{ v: [7, 0, 0], f: '07' }, hours[6]],
            [{ v: [8, 0, 0], f: '08' }, hours[7]],
            [{ v: [9, 0, 0], f: '09' }, hours[8]],
            [{ v: [10, 0, 0], f: '10' }, hours[9]],
            [{ v: [11, 0, 0], f: '11' }, hours[10]],
            [{ v: [12, 0, 0], f: '12' }, hours[11]],
            [{ v: [13, 0, 0], f: '13' }, hours[12]],
            [{ v: [14, 0, 0], f: '14' }, hours[13]],
            [{ v: [15, 0, 0], f: '15' }, hours[14]],
            [{ v: [16, 0, 0], f: '16' }, hours[15]],
            [{ v: [17, 0, 0], f: '17' }, hours[16]],
            [{ v: [18, 0, 0], f: '18' }, hours[17]],
            [{ v: [19, 0, 0], f: '19' }, hours[18]],
            [{ v: [20, 0, 0], f: '20' }, hours[19]],
            [{ v: [21, 0, 0], f: '21' }, hours[20]],
            [{ v: [22, 0, 0], f: '22' }, hours[21]],
            [{ v: [23, 0, 0], f: '23' }, hours[22]],
            [{ v: [24, 0, 0], f: '24' }, hours[23]],
        ]);

        var options = {
            title: 'Motion Capturing Monitoring',
            hAxis: {
                title: 'Time of Day',
                viewWindow: {
                    min: [0, 30, 0],
                    max: [24, 30, 0]
                }
            },
            vAxis: {
                title: 'Number of captured motion'
            }
        };
    }
    else {
        data.addColumn('date', 'Day od Month');
        data.addColumn('number', 'Actions');
        var dateArray = [
            [new Date(0, 0, 0), 0], [new Date(0, 0, 0), 0], [new Date(0, 0, 0), 0]
        ];
        for (var i = 1; i <= mainObj.DateTime.length; i++) {
            var item = mainObj.DateTime[i - 1].Date.split("-");
            dateArray[i - 1] = [new Date(item[0], item[1], item[2]), days[item[2]]];
        }
        data.addRows(dateArray);

        var options = {
            title: 'Action Monitoring',
            hAxis: {
                format: 'M/d/yy',
                gridlines: { count: 10 }
            },
            vAxis: {
                gridlines: { count: 10 },
                minValue: 0
            }
        };

    }
    chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
    chart.draw(data, options);
    days = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
    hours = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
}

function checkIfDay(obj) {
    var index = obj.DateTime.length;
    if ((obj.DateTime[0].Date) == (obj.DateTime[index - 1].Date)) {
        console.log("Dayyy");
        return true;
    }
    else {
        console.log("Monthh");
        return false;
    }
}


