# API Specification

## RESTFUL API

### USER API

> POST /api/auth

- BODY

```json
//BODY
{
    "username": "abc",
    "password": "abc"
}
```

- RESPONSE

```json
// + 200
{
    "token": "abc.xyz.bbb",
    "user":
    {
        "username": "abc",
        "avatar": 1,
        "point": 10000,
        "countWin": 100,
        "countDraw": 10,
        "countLose": 30
    }
}
// + 400
{
    "error":
    {
        "title": "Login failed!",
        "detail": "detail"
    }
}
```

> POST /api/register

- BODY

```json
//BODY
{
    "username": "abc",
    "password": "abc"
}
```

- RESPONSE

```json
// + 200
{
"token": "abc.xyz.bbb"
}
// + 400
{
    "error":
    {
        "title": "Error title",
        "detail": "detail"
    }
}
```

> GET /api/users?username=abc

- PARAMS

```json
username:abc
```

- HEADER

```json
{
    "authentication": "Bearer abc.xyz.bbb"
}
```

- REPONSE

```json
{
    // + 200
    "username": "abc",
    "winCount": 0,
    "loseCount": 0,
    "drawCount": 0,
    "points": 1000,
    "avatar": "7"
}

// + 404
{
    "error": {
        "detail": "Username is not existed.",
        "title": "404"
    }
}
```

### ROOM API

> POST /api/room

- HEADER

```json
{
    "authentication": "Bearer abc.xyz.bbb"
}
```

_ BODY

```json
{
    "roomName":"abc",
    "betPoint":0,
    "host":"Tran2",
    "background":1,
    "password":""
}
```

- REPONSE

```json
{
    // + 200
    "id": 1,
    "roomName": "abc",
    "betPoint": 0,
    "hasPassword": false,
    "host": "abc",
    "guest": "",
    "background": "5"
}

// + 400
{
    "error": {
        "detail": "Please pick Bet Points <= 1000",
        "title": "You don't have enough points."
    }
}
// 403 Authentication is invalid
{
    "timestamp": "2020-01-07T17:32:46.767+0000",
    "status": 403,
    "error": "Forbidden",
    "message": "Access Denied",
    "path": "/api/rooms"
}
```

> GET /api/rooms

- HEADER

```json
{
    "authentication": "Bearer abc.xyz.bbb"
}
```

- REPONSE

```json
"rooms": [
        {
            "id": 1,
            "roomName": "abc",
            "betPoint": 0,
            "hasPassword": false,
            "host": "Tran2",
            "guest": null,
            "background": null
        }
    ]
}
```

