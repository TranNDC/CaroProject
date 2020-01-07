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

> GET /api/user

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
    "avatar": 1,
    "point": 10000,
    "countWin": 100,
    "countDraw": 10,
    "countLose": 30
}

// + 403
```

### ROOM API

> POST /api/room

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
    "roomname":"abc",
    "betPoint":1000,
    "password":"none",
    "host":"username"
}
// + 403
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
{
    "rooms":
    [
        {
            "roomname":"abc",
            "betPoint":1000,
            "password":"none",
            "host":"username"
        }
    ]
}
// + 403
```

