# A simple implementation of blockchain
This project aims to create a simple implementation of blockchain concept and demostrate it in a user friendly way. 

## Design Concept
This project consists of two main parts: agent and interface.

### Agent
An agent stands for one peer which is able to store and mine blocks in the network. Every agent is connected to all the other agents in the network to construct a P2P distributed network. The basic functions for an agent are:
1. Send message to other agents, in order to broadcast its newly mined block
2. Receive message from other agents, in order to receive blocks mined by other agents
3. Mine, validate and grow blocks on its own bloackchain
4. Sync latest blockchain with other agents

The algorithm for mining is the key of a blockchain. In this project we only use SHA256 hash to simulate the mining procedure. 


### Interface
An interface implemented with Springboot is included in this project to demostrate the usage of the blockchain. It might make people feel like a centralized management interface, however we need to understand that agents can also run independently. The interface is RESTful and all return data is in json format. A single page application is also provided to visualize the blockchain concept in a better way.


## Quick Start

### Start server
Navigate to project root dir and start the server:
```
$ gradle bootRun
```
### Use web interface
Open http://localhost:8080/ in browser and try it from web page. Basic actions are:
- Add an agent to the network
- Delete an agent from the network
- Mine a new block and broadcast to the network. A color scheme is used to mark different blocks created by different agents.

![block chain demo](https://raw.githubusercontent.com/Will1229/Blockchain/master/image/web.PNG)

### Use rest interface
Use curl directly from command line to interact with the server:

#### Create new agent
```
curl -X POST "http://localhost:8080/agent?name=A1&port=1001"
{"name":"A1","port":1001,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"}]}

curl -X POST "http://localhost:8080/agent?name=A2&port=1002"
{"name":"A2","port":1002,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"}]}

curl -X POST "http://localhost:8080/agent?name=A3&port=1003"
{"name":"A3","port":1003,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"}]}
```

#### Mine block
```
curl -X POST "http://localhost:8080/agent/mine?agent=A1"
{"index":1,"timestamp":1502194172250,"hash":"2461f27f811df15a969391c70f136869a282224e8cc6fe8b628d16a499515d21","previousHash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480"}

curl -X POST "http://localhost:8080/mine?name=A3"
{"timestamp":1502194200235,"status":404,"error":"Not Found","message":"No message available","path":"/mine"}
```

#### Show agents and blocks
```
curl http://localhost:8080/agent?name=A1
{"name":"A1","port":1001,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"},{"index":1,"timestamp":1502194172250,"hash":"2461f27f811df15a969391c70f136869a282224e8cc6fe8b628d16a499515d21","previousHash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480"}]}

curl http://localhost:8080/agent?name=A3
{"name":"A3","port":1003,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"},{"index":1,"timestamp":1502194172250,"hash":"2461f27f811df15a969391c70f136869a282224e8cc6fe8b628d16a499515d21","previousHash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480"}]}

curl http://localhost:8080/agent/all
```

#### Remove agent
```
curl -X DELETE http://localhost:8080/agent/all
```

## Change log
- [x] 20170902 Let newly added agent sync the latest blockchain from peers.
- [x] 20170818 Add a web interface.
- [x] 20170807 Server side code base with rest interface.


This project is inspired by [naivechain](https://github.com/lhartikk/naivechain). 
