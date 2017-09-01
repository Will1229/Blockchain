"use strict";

var testMode = false;

function getAllAgents() {
    sendHttpRequest("GET", "agent/all", null, displayAllAgents);

    if (testMode) {
        displayAllAgents('[{"name":"Agent1","port":1001,"blockchain":[{"index":0,"creator":"Agent1","timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"}]}' +
            ', {"name":"Agent2","port":1002,"blockchain":[{"index":1,"creator":"Agent2","timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"}]}]');
    }
}

function deleteAllAgents() {
    sendHttpRequest("DELETE", "agent/all", null, getAllAgents);
}

function createAgent() {
    var idx = getNextCount();
    var name = "Agent" + idx;
    var port = 3000 + idx;
    sendHttpRequest("POST", "agent?name=" + name + "&port=" + port, null, displayAgent);

    if (testMode) {
        displayAgent('{"name":"Agent1","port":1001,"blockchain":[{"index":2,"creator":"Agent1","timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"}]}');
    }
}

function deleteAgent(name) {
    sendHttpRequest("DELETE", "agent?name=" + name, null, getAllAgents);
}

function getAgent() {
    var name = document.getElementById("agentNameGet").value;
    sendHttpRequest("GET", "agent?name=" + name, null, null);
}

function mine(name) {
    sendHttpRequest("POST", "agent/mine?agent=" + name, null, getAllAgents);

    if (testMode) {
        displayBlock('{"index":1,"creator":"Agent1","timestamp":1502194172250,"hash":"2461f27f811df15a969391c70f136869a282224e8cc6fe8b628d16a499515d21","previousHash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480"}');
    }
}

function sendHttpRequest(action, url, data, callback) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
            callback(xmlHttp.responseText);
        }
    };
    xmlHttp.open(action, url, true);
    xmlHttp.send(data);
}

var getNextCount = (function () {
    if (!sessionStorage.count) {
        sessionStorage.count = 0;
    }
    return function () {
        sessionStorage.count = Number(sessionStorage.count) + 1;
        return Number(sessionStorage.count);
    }
})();