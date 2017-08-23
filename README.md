# A simple implementation of blockchain
This project aims to create a simple implementation of blockchain in java for inspired by [this project](https://github.com/lhartikk/naivechain). 



## Design Concept
This project consists of two main parts: agent and web interface.

### Agent
One agent stands for one peer which is able to store and mine blocks in the network. Every agent is connected to all the other agents in the network to construct a P2P distributed network. The basic functions for an agent are:
1. Send message to other agents, in order to broadcast its newly mined block
2. Receive message from other agents, in order to receive blocks mined by other agents
3. Mine, validate and grow blocks on its own bloackchain

The algorithm for mining is the key of a blockchain. In this project we only use SHA256 hash to simulate the mining procedure. 


### Web interface
A web interface implemented with Springboot is included in this project to demostrate the usage of the blockchain. It might make people feel like a centralized management interface, however we need to understand that agents can also run independently. The interface is RESTful and all return data is in json format.


## Quick Start

### Start web interface
1. Navigate to project root dir and start the server:
```
$ gradle bootRun
```
2. Open http://localhost:8080/ in browser and try it from web page:

![block chain demo](https://raw.githubusercontent.com/Will1229/Blockchain/master/image/web.PNG)

3. Or use curl directly from command line:

### Create new agent
```
curl -X POST "http://localhost:8080/agent?name=A1&port=1001"
{"name":"A1","port":1001,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"}]}

curl -X POST "http://localhost:8080/agent?name=A2&port=1002"
{"name":"A2","port":1002,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"}]}

curl -X POST "http://localhost:8080/agent?name=A3&port=1003"
{"name":"A3","port":1003,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"}]}
```

### Mine block
```
curl -X POST "http://localhost:8080/agent/mine?agent=A1"
{"index":1,"timestamp":1502194172250,"hash":"2461f27f811df15a969391c70f136869a282224e8cc6fe8b628d16a499515d21","previousHash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480"}

curl -X POST "http://localhost:8080/mine?name=A3"
{"timestamp":1502194200235,"status":404,"error":"Not Found","message":"No message available","path":"/mine"}
```

### Show agents and blocks
```
curl http://localhost:8080/agent?name=A1
{"name":"A1","port":1001,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"},{"index":1,"timestamp":1502194172250,"hash":"2461f27f811df15a969391c70f136869a282224e8cc6fe8b628d16a499515d21","previousHash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480"}]}

curl http://localhost:8080/agent?name=A3
{"name":"A3","port":1003,"blockchain":[{"index":0,"timestamp":1502193341671,"hash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480","previousHash":"ROOT_HASH"},{"index":1,"timestamp":1502194172250,"hash":"2461f27f811df15a969391c70f136869a282224e8cc6fe8b628d16a499515d21","previousHash":"4f99b67b06b6831886815ffe66a55be2e34dcefdfc16b6214710313062a8a480"}]}

curl http://localhost:8080/agent/all
```

### Remove agent
```
curl -X DELETE http://localhost:8080/agent/all
```


## Functions to be added
- Add a web interface for better demostration
- Get the lastest blockchain whenever a new agent is added into the network
- Add service discovery so that new peer can get connected to others automatically
- And much more...
