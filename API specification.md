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

> POST /api/room

***Note :*** Create a room

- **REQUEST**

    - HEADER
    ```json
    {
        "Authentication": "Bearer abc.xyz.bbb"
    }
    ```
    - BODY

    ```json
    {
        "roomName":"abc",
        "betPoints":0,
        "host":"Tran2",
        "background":1,
        "password":""
    }
    ```

- **RESPONSE**
    - 200
    ```json
    {
        "id": "1",
        "roomName": "abc",
        "betPoints": 0,
        "hasPassword": false,
        "host": "abc",
        "guest": "",
        "background": "5"
    }
    ```

  - 400
  
    ```json
    {
        "error": {
            "detail": "Please pick Bet Points <= 1000",
            "title": "You don't have enough points."
        }
    }
    ```

  - 403 Authentication is invalid

    ```json
    {
        "timestamp": "2020-01-07T17:32:46.767+0000",
        "status": 403,
        "error": "Forbidden",
        "message": "Access Denied",
        "path": "/api/rooms"
    }
    ```

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
        "host": "abc",
        "guest": "",
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

>POST /api/room/join

- **REQUEST**
  - HEADER

    ```json
    {
        "Authentication": "Bearer abc.xyz.bbb"
    }
    ```

  - BODY
  
    ```json
    {
        "id" : 8aaf253097,
        "password": 12345
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
                "host": "Tran2",
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

