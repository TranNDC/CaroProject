# API Specification

## RESTFUL API

### **USER API**

> POST /api/auth

- **REQUEST**
    - BODY
    ```json
    {
        "username": "abc",
        "password": "abc"
    }
    ```

- **RESPONSE**
    - 200 
    ```json
    {
        "token": "abc.xyz.bbb",
        "user":
        {
            "username": "abc",
            "avatar": 1,
            "points": 10000,
            "countWin": 100,
            "countDraw": 10,
            "countLose": 30
        }
    }
    ```
    - 400
    ```json
    {
        "error":
        {
            "title": "Login failed!",
            "detail": "Please check your input again..."
        }
    }
    ```

> POST /api/register

- **REQUEST**
  - BODY

    ```json
    {
        "username": "abc",
        "password": "abc"
    }
    ```

- **RESPONSE**
  - 200

    ```json
    {
        "token": "abc.xyz.bbb"
    }
    ```

  - 400

    ```json
    {
        "error":
        {
            "title": "Error title",
            "detail": "detail"
        }
    }
    ```

> GET /api/users?username=abc

- **REQUEST**
  - PARAMS

    ```json
    username:abc
    ```

  - HEADER

    ```json
    {
        "Authentication": "Bearer abc.xyz.bbb"
    }
    ```

- **RESPONSE**
  - 200
  - 
    ```json
    {
        "username": "abc",
        "winCount": 0,
        "loseCount": 0,
        "drawCount": 0,
        "points": 1000,
        "avatar": "7"
    }
    ```

  - 404

    ```json
    {
        "error": {
            "detail": "Username is not existed.",
            "title": "404"
        }
    }
    ```

### **ROOM API**

> GET /api/room/search

***Note :*** Check whether a room ID is valid or not

- **REQUEST**
  - HEADER

    ```json
    {
        "Authentication": "Bearer abc.xyz.bbb"
    }
    ```

  - PARAMS

    ```json
        id: 8aaf253097
    ```

- **REPONSE**
  - 200

    ```json
    {
        "id": 8aaf253097,
        "roomName": "abc",
        "betPoints": 1000,
        "hasPassword": false,
        "host": 
        {
            "username": "abc",
            "avatar": 1,
            "points": 10000,
            "countWin": 100,
            "countDraw": 10,
            "countLose": 30
        },
        "guest": null,
        "background": "5"
    }
    ```

    - 400

    ```json
    {
        "error": {
            "detail": "Invalid room ID!",
            "title": "Please check your input again ..."
        }
    }
    ```

    ```json
    {
        "error": {
            "detail": "Room is full!",
            "title": "Please pick another room ..."
        }
    }
    ```

- **RESPONSE**

> GET /api/rooms

***Note :*** Get all rooms

- **REQUEST**
  - HEADER

    ```json
    {
        "Authentication": "Bearer abc.xyz.bbb"
    }
    ```

- **RESPONSE**
  - 200

    ```json
    "rooms": [
            {
                "id": "1",
                "roomName": "abc",
                "betPoints": 0,
                "hasPassword": false,
                "host": {
                    "username": "abc",
                    "avatar": 1,
                    "points": 10000,
                    "countWin": 100,
                    "countDraw": 10,
                    "countLose": 30
                },
                "guest": null,
                "background": null
            },
            ...
        ]
    }
    ```

### **RANK API**


> GET /api/rank

***Note :*** Get top 5 highest rank users

- **REQUEST**
  - HEADER


    ```json
    {
        "Authentication": "Bearer abc.xyz.bbb"
    }
    ```

- **RESPONSE**
  - 200

    ```json
    "ranking": [
        {
            "username": "abc",
            "points" : 10000
        },
        {
            "username" : "cde",
            "points" : 9500
        },
        ...
    ]
    ```

## SOCKET API

### ROOM API

**CONNECT TO SOCKET.IO IN PORT `8090`**

```java
//ROOMDTO
public class RoomDTO{
    String id;
    String roomName;
    Integer betPoint;
    Integer guestPoint; // number of guest's win matches
    Integer hostPoint; // number of host's win matches
    Boolean isHostPlayFirst; // true: host is 1st turn, else guest is 1st turn
    Boolean hasPassword;
    UserDTO host;
    UserDTO guest;
    String background;
}

```

```java
class SocketDTO{
    String username; //sender's username
    String roomId;
}
```

### [INTERVAL] get Rooms list

- Listen in: `listen-interval-rooms`
- Data:

```json
    "rooms": [
            {
                "id": "1",
                "roomName": "abc",
                "betPoints": 0,
                "hasPassword": false,
                "host": {
                    "username": "abc",
                    "avatar": 1,
                    "points": 10000,
                    "countWin": 100,
                    "countDraw": 10,
                    "countLose": 30
                },
                "guest": null,
                "background": null
            },
            ...
        ]
    }
```

### [INTERVAL] get ranking

- Listen in `listen-interval-rank`
- Data:

```json
"ranking": [
    {
        "username": "abc",
        "points" : 10000
    },
    {
        "username" : "cde",
        "points" : 9500
    },
    ...
]
```

### Create room

- API name: `create`
- Data: 
```json
{
    "roomName":"Name",
    "username":"Host username",
    "betPoint":0,
    "password":"",
}
```

- Response API: `listen-create`
- Data:

```json
// SUCESS
{
    "code":200,
    "room": [ROOMDTO],
    "error":null,
}

// FAIL
{
    "code":400,
    "room":null,
    "error":{
        "title":"Error Title",
        "detail":"Error Detail",
    }
}

```

```plantuml
    Host -> socketio: create({roomName,username,betPoint,password})
    socketio->roomservice: createroom
    activate roomservice
    alt throw Exeption
        roomservice-->socketio: exeption
        socketio-->Host: on[listen-create] {code:400 error{ErrorDTO} // trả về trên listen-create}
    else
        roomservice-->socketio: roomDTO
        socketio-->Host: on[listen-create] {code:200 room{ROOMDTO}}
    end
    deactivate roomservice
```

### Join room

- API name: `join`
- Data: 
```json
{
    "roomId":"1",
    "username":"Guest username",
    "password":"",
}
```

- Response `GUEST` API: `listen-join`
- Data:

```json
// SUCESS
{
    "code":200,
    "room": [ROOMDTO],
    "error":null,
}

// FAIL
{
    "code":400,
    "room":null,
    "error":{
        "title":"Error Title",
        "detail":"Error Detail",
    }
}

```

- If Guest join successfully, response to `HOST`: `listen-guest-join`
- Data

```json
{
    [ROOMDTO]
}
```

```plantuml
    host-->host: [listen-guest-join] // host đã lắng nghe ở list-guest-join
    guest->socketio: join({roomId,username,password})
    socketio->service: handleJoinRoom()
    activate service
        service->redis: check and update
        alt check fail
            service-->socketio:fail
            socketio-->guest:[listen-join]{code:400, error:errorDTO}
        else
            service-->socketio:RoomDTO
            socketio-->guest:[listen-join]{code:200,room{ROOMDTO}}
            socket-->host:[listen-guest-join] {ROOMDTO} //update guest to host

        end
    deactivate service
    guest-->guest: [listen-start]
```

### Start

- `HOST` call: `start`

```json
{
    "username":"host username",
    "roomId":"1"
}
```

- `GUEST` listen: `listen-start`
- Data: `go` (string)

```plantuml
    guest-->guest: [listen-start] 
    host->socketio:start({SocketDTO})
    activate socketio
    host-->host: startGame
    socketio->service:startroom(SocketDTO)
    socketio-->guest: [listen-start]"go"
    deactivate socketio
```

### Send move

```java
class MoveDTO{
    String username;
    String roomId;
    int x;
    int y;
    String result; // ["", "win","lose", "draw"]
}
```

- PLAYER send `send-move`
- Data

```json
{
    [MOVEDTO]
}
```

- OTHER_PLAYER listen `listen-move`
- Data

```json
{
    [MOVEDTO]
}
```

```plantuml
    playerA-->playerA: [listen-move]
    playerB-->playerB: [listen-move]
    playerA->socketio: [send-move] {MoveDTO}
    activate socketio
    alt MoveDTO.result != ""
        socketio-->service: handleResult
    end
    socketio-->playerB: [listen-move] {MoveDTO}
    deactivate socketio
```

## Exit


```java
class SocketDTO{
    String username; //sender's username
    String roomId;
}
```

```plantuml
playerB-->playerB: [listen-opponent-out-room]
playerA->socketio: [exit] {SocketDTO}
socketio->service: handle ...
service-->socketio: newRoomDTO (if host exist, guest become new host)
socketio-->playerB: [listen-opponent-out-room]{roomDTO}// if game is playing, show winner board. if host leave, promote guest -> host.
playerB-->playerB: change to waiting mode
```

### Continue

```plantuml
playerB-->playerB: [listen-continue]
playerA->socketio: [continue] {SocketDTO}
socketio->service: check if A can continue
service->socketio: canContinue
alt canContinue == true
    socketio-->playerA : [listen-continue]{code:200,room:{ROOMDTO}}
    socketio-->playerB : [listen-opponent-continue]{ROOMDTO}
    alt B continue
        playerB->socketio: [continue] {SocketDTO}
        socketio->service: ...
        service->socketio: ok B can continue
        socketio-->playerB : [listen-continue]{code:200,room:{ROOMDTO}}
        socketio-->playerA : [listen-opponent-continue]{ROOMDTO}
    else
        playerB->socketio: [Exit] || disconnect
        socketio->service: handle
        service-->socketio: new RoomDTO
        socketio-->playerA: [listen-opponent-out-room]room{RoomDTO}

    end
else
    socketio-->playerA: [listen-continue]{code:400, error:{ErrorDTO}}
    socketio-->playerB: [listen-opponent-out-room]room{RoomDTO}
end


```

### Message

```java
MessageDTO{
    String username;
    String roomId;
    String message;
}
```

```plantuml
playerB-->playerB: [listen-message]
playerA-->socketio: [send-message]{MessageDTO}
activate socketio
socketio-->playerB: [listen-message]{MessageDTO}
deactivate socketio
```