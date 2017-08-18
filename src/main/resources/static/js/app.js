"use strict";

function displayTest(text) {
    if (text === null || text === undefined || text === "") {
        text = "Done."
    }
    document.getElementById("test").innerHTML = text;
}

function getAllAgents() {
    sendHttpRequest("GET", "http://localhost:8080/agent/all", null, displayTest);
}

function deleteAllAgents() {
    sendHttpRequest("DELETE", "http://localhost:8080/agent/all", null, displayTest);
}

var getNextPort = (function () {
    var nextPort = 1000;
    return function () {
        return nextPort += 1;
    }
})();

function createAgent() {
    var name = document.getElementById("agentNameAdd").value;
    var port = getNextPort();
    sendHttpRequest("POST", "http://localhost:8080/agent?name=" + name + "&port=" + port, null, displayTest);
}

function deleteAgent() {
    var name = document.getElementById("agentNameDel").value;
    sendHttpRequest("DELETE", "http://localhost:8080/agent?name=" + name, null, displayTest);
}

function getAgent() {
    var name = document.getElementById("agentNameGet").value;
    sendHttpRequest("GET", "http://localhost:8080/agent?name=" + name, null, displayTest);
}

function resetView() {
    displayTest("");
}

function sendHttpRequest(action, theUrl, data, callback) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
            callback(xmlHttp.responseText);
        }
    };
    xmlHttp.open(action, theUrl, true);
    xmlHttp.send(data);
}