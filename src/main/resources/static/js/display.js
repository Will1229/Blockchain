"use strict";

const OUTPUT_TABLE_NAME = "output";

function displayMsg(text, color) {
    if (color === undefined) {
        color = "black";
    }
    document.getElementById("msg").innerHTML = text;
    document.getElementById("msg").style.color = color;
}

function displayAllAgents(json) {
    cleanTable(OUTPUT_TABLE_NAME);
    var agents;
    try {
        agents = JSON.parse(json);
    } catch (e) {
        displayMsg("Invalid response from server " + json, "red");
        return;
    }
    for (var i in agents) {
        displayAgent(agents[i]);
    }
}

function displayAgent(jsonAgent) {
    if (typeof jsonAgent === "string") {
        var agent;
        try {
            agent = JSON.parse(jsonAgent);
        } catch (e) {
            displayMsg("Invalid response from server " + jsonAgent, "red");
            return;
        }
    } else {
        agent = jsonAgent;
    }

    var idx = 0;
    var table = document.getElementById(OUTPUT_TABLE_NAME);
    var row = table.insertRow(table.length);
    const nameCell = row.insertCell(idx++);
    nameCell.title = agent.name;
    nameCell.innerHTML = agent.name;
    row.insertCell(idx++).innerHTML = agent.port;
    const chain = agent.blockchain;
    const blockchainCell = row.insertCell(idx++);
    for (var i in chain) {
        blockchainCell.appendChild(createBlockP(chain[i]));
    }
    blockchainCell.className = "blockchain";
    var p = document.createElement("P");
    p.appendChild(addCellButton("Mine", function () {
        mine(agent.name);
    }));
    p.appendChild(addCellButton("Delete", function () {
        deleteAgent(agent.name);
    }));
    row.insertCell(idx).appendChild(p);

    function addCellButton(name, onclick) {
        var button = document.createElement("BUTTON");
        button.className = "cellButton";
        button.appendChild(document.createTextNode(name));
        button.onclick = onclick;
        return button;
    }
}

function displayBlock(jsonBlock) {
    if (typeof jsonBlock === "string") {
        var block;
        try {
            block = JSON.parse(jsonBlock);
        } catch (e) {
            document.getElementById("msg").innerHTML = "Invalid response from server " + jsonBlock;
            return;
        }
    } else {
        block = jsonBlock;
    }
    displayMsg("New block mined:<br>" + getBlockString(block), "green");
}

function getBlockString(block) {
    return "index=" + block.index + " creator=" + block.creator + " timestamp="
        + block.timestamp + " hash=" + block.hash + " previous hash=" + block.previousHash + "<br>";
}

function createBlockP(block) {
    var p = document.createElement("P");
    p.title = "creator " + block.creator;
    p.innerHTML = "index=" + block.index + " creator=" + block.creator + " timestamp="
        + block.timestamp + " hash=" + block.hash + " previous hash=" + block.previousHash;
    console.log("create p.innerHTML" + p.innerHTML);
    return p;
}

function cleanTable(name) {
    var table = document.getElementById(name);
    table.innerHTML = "";
    var row = table.insertRow(0);
    var idx = 0;
    row.insertCell(idx++).innerHTML = "Agent name";
    row.insertCell(idx++).innerHTML = "Port";
    row.insertCell(idx++).innerHTML = "Blockchain";
    row.insertCell(idx).innerHTML = "Operations";
}