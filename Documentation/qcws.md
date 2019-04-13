# QRWebSocketConnetion

Each client requests game info & players on lobby join and keeps an instance of the game state locally. The game state gets updated by the server, which is the single source of truth. Player events can trigger server events, which in turn update all players local states. UI is dictated by local game state, (partial) rerender on state changes. (Write JS functions to rerender parts of view)

The local state could look like this:

```javascript
let state = {
  // LOBBY STATE
  pin: 123344,
  difficulty: "easy",
  mode: "normal",
  questionSets: [
    "Silicon Valley Trivia",
    "Bitcoin or Shitcoin?",
  ],
  score: "42",
  alivePingInterval: "30000",
  players: [
    {
      id: 122,
      name: "user3",
      avatar: "/avatars/user1/923486742842334_200x200.png",
      ready: false,
    },
    {
      id: 123,
      name: "user4",
      avatar: "/avatars/user1/923486742842334_200x200.png",
      ready: true,
    },
    {
      id: 124,
      name: "user5",
      avatar: "/avatars/user1/923486742842334_200x200.png",
      ready: false,
    },
  ],
  // GAME STATE
  jokersLeft: 1,
  questions: [
    {
      questionId: 233,
      questionSet: "Silicon Valley Trivia",
      type: "text",
      question: "Who is the CEO of Hooli?",
      playerId: 123,
      timeRemaining: 15340,
      answers: [
        {
          id: 0,
            answer: "Gavin Belson",
            playerId: 123,
          },
          {
            id: 1,
            answer: "Big Head",
            playerId: 124,
          },
          {
            id: 2,
            answer: "Jeff Bezos",
            playerId: 124,
          },
          {
            id: 3,
            answer: "Batman",
            playerId: 125,
          },
        ]
      }
  ],
};
```

## Client Events

`@Deprecated`, probably one WS channel for whole room or requestmapping API
Each clent has his own channel to the quizroom. In this channel he can send `Player` events.

Endpoint: `/qr/{PIN}/{Player.id}`

For each of the possible events, there is a `JS` function to trigger the WS call. Some of them will be triggered by `JS` `EventListener`s, others on page load. The response is then mapped by event and the right eventHandler function gets called.

Examples:

On page load: 

1. Client wants QR info
2. `getRoomPin` is triggered
3. Request

  ```json
  {
    "event": "getRoomPin",
  }
  ```
4. The response gets mapped to handler function
5. handler function updates DOM & displays game Info

**TODO:** Maybe bundle `getRoomPin`, `getRoomDifficulty`, `getRoomQuestionSets`, initial `getRoomScore`, `getAlivePingTimeStep` & `getRoomMode` into one `getRoomInfo` call? Server notifies all users of score change, which makes this mostly obsolete.
-> This can be done on websocket layer -> one request for all info for frontend, WS Connection on backend calls all qr events...

`answerQuestion`

1. Answer is clicked
2. `answerQuestion` event is triggered
3. Via WS the request is sent:
  
  ```json
  {
    "event": "answerQuestion",
    ...
  }
  ```
4. The response gets mapped to eventHandler
5. handler may cause DOM updates

**Before WS connection is established** -> Either by bean/requestmapping ?
But needed for stats in lobby...
- `getRoomPin`
  - returns the pin of the Room
  - Request
    
    ```json
    {
      "event": "getRoomPin",
    }
    ```
  - Response
    
    ```json
    {
      "event": "getRoomPin",
      "pin": 120323,
    }
    ```
- `getRoomDifficulty`
  - Request
    
    ```json
    {
      "event": "getRoomDifficulty",
    }
    ```
  - Response
    
    ```json
    {
      "event": "getRoomDifficulty",
      "difficulty": "easy",
    }
    ```
- `getRoomMode`
  - Request
    
    ```json
    {
      "event": "getRoomMode",
    }
    ```
  - Response
    
    ```json
    {
      "event": "getRoomMode",
      "mode": "normal",
    }
    ```
- `getRoomQuestionSets`
  - Request
    
    ```json
    {
      "event": "getRoomQuestionSets",
    }
    ```
  - Response
    
    ```json
    {
      "event": "getRoomQuestionSets",
      "questionSets": [
        "Silicon Valley Trivia",
        "Bitcoin or Shitcoin?",
      ],
    }
    ```
- `getRoomScore`
  - Request
    
    ```json
    {
      "event": "getRoomScore",
    }
    ```
  - Response
    
    ```json
    {
      "event": "getRoomScore",
      "score": "42",
    }
    ```
- `getAlivePingTimeStep` (ms)
  - Request
    
    ```json
    {
      "event": "getAlivePingTimeStep",
    }
    ```
  - Response
    
    ```json
    {
      "event": "getAlivePingTimeStep",
      "alivePingInterval": "30000",
    }
    ```

With WS Connection

- `getGameInfo`
  - returns all game info needed for Lobby, called once when joined
  - Request
    
    ```json
    {
      "event": "getGameInfo",
    }
    ```
- Response
    
    ```json
    {
      "event": "getGameInfo",
      "pin": 120323,
      "difficulty": "easy",
      "mode": "normal",
      "questionSets": [
        "Silicon Valley Trivia",
        "Bitcoin or Shitcoin?",
      ],
      "score": "42",
      "alivePingInterval": "30000",
    }
    ```
- `getRoomPlayers`
  - returns players in the room, called once on lobby join
  - later updates just update `players` list (and ideally just do a partial rerender of lobby players)
    - on join, add to list
    - on readyStateChange change players ready state
    - on leave remove player
  - **NOTE:** `"ready"` & `"num"` are currently not in the specs
    -> Abstraction layer
  - Request
    
    ```json
    {
      "event": "getRoomPlayers",
    }
    ```
  - Response
    
    ```json
    {
      "event": "getRoomPlayers",
      "num": 3,
      "players": [
        {
          "id": 122,
          "name": "user3",
          "avatar": "/avatars/user1/923486742842334_200x200.png",
          "ready": "false",
        },
        {
          "id": 123,
          "name": "user4",
          "avatar": "/avatars/user1/923486742842334_200x200.png",
          "ready": "true",
        },
        {
          "id": 124,
          "name": "user5",
          "avatar": "/avatars/user1/923486742842334_200x200.png",
          "ready": "false",
        },
      ]
    }
    ```
- `getRoomPlayerCount`
  - **NOTE:** would be `@Deprecated` if `getRoomPlayers` was adjusted
  - Request
    
    ```json
    {
      "event": "getRoomPlayerCount",
    }
    ```
  - Response
    
    ```json
    {
      "event": "getRoomPlayerCount",
      "num": 3,
    }
    ```
- `getRoomPlayers`
  - returns players in the room
  - **NOTE:** would be `@Deprecated` if `getRoomPlayers` was adjusted
    -> Abstraction layer
  - Request
    
    ```json
    {
      "event": "getRoomReadyPlayers",
    }
    ```
  - Response
    
    ```json
    {
      "event": "getRoomReadyPlayers",
      "players": [
        {
          "id": 123,
          "name": "user4",
          "avatar": "/avatars/user1/923486742842334_200x200.png",
        },
      ]
    }
    ```
Game Events

**NOTE:** Depending on the layer, id either is needed or not
- if one endpoint receives all requests, id is always(also for above events) necessary(then DB query to get corresponding `Player`)|(Keep all players in memory, use the right one(id matches))
- if each player has individual endpoint, player id is not needed (each player has a `@Controller` instance mapping his endpoint and a local `Player` reference). Choose this if possible, if not easily, then other option with player references

- `readyUp`
  - Changes a `Player`s ready state, triggered on Player action
  - Request
    
    ```json
    {
      "event": "readyUp",
      "playerId": 123,
    }
    ```
  - Response probably just `HTTP 200 - STATUS OK`
- `answerQuestion`
  - A question is answered
  - TODO: Method has wrong signature... ???
  - Request
    
    ```json
    {
      "event": "answerQuestion",
      "playerId": 123,
      "questionId": 233,
      "answerId": 0,
    }
  ```
  - Response probably just `HTTP 200 - STATUS OK`
- `useJoker`
  - Reshuflle will be triggered, triggered on Player action (player local state gets updated, joker is disabled)
  - Request
    
    ```json
    {
      "event": "useJoker",
      "playerId": 123,
    }
    ```
  - Response probably just `HTTP 200 - STATUS OK`
- `leaveRoom`
  - Reshuflle will be triggered
  - Request
    
    ```json
    {
      "event": "leaveRoom",
      "playerId": 123,
    }
    ```
  - Response probably just `HTTP 200 - STATUS OK`
- `cancelTimeout`
  - only send if server initiated timeout for player && Player inactive (= Timeout in X Seconds modal not averted)
  - Request
    
    ```json
    {
      "event": "cancelTimeout",
      "playerId": 123,
    }
    ```
  - Response probably just `HTTP 200 - STATUS OK`
- `sendAlivePing`
  - Sent periodically, use smaller interval than `getAlivePingTimeStep`|`getGameInfo` dictates -> latency (e.g. `setInterval(sendAlivePing, (ALIVE_TIME_STEP - 500));` (pings > 500ms may be disconnected))
  - Request
    
    ```json
    {
      "event": "sendAlivePing",
      "playerId": 123,
    }
    ```
  - Response probably just `HTTP 200 - STATUS OK`

## Server Events

Requests now sent by server(request probably wrong name since its an event that does not get a response)

- `onReadyUp`
  - BROADCAST, sent if player ready status changes("ready" -> "not ready" possible? if yes should be `onReadyStateChange`)
  - Request
    
    ```json
    {
      "event": "onReadyUp",
      "playerId": 123,
      "totalReady": 1,
    }
    ```
  - no Response
- `onPlayerJoin`
  - BROADCAST, sent if new player joins
  - Request
    
    ```json
    {
      "event": "onPlayerJoin",
      "player": {
        "id": 124,
        "name": "user5",
        "avatar": "/avatars/user1/923486742842334_200x200.png",
        "ready": "false",
      },
    }
    ```
  - no Response
- `onPlayerJoin`
  - BROADCAST, sent if all players ready  
    -> frontend clears lobby screen and displays game UI placeholder boxes
  - Request
    
    ```json
    {
      "event": "onGameStart",
    }
    ```
  - no Response
- `onGameEnd`
  - BROADCAST, sent if game ends (triggers UI change(&WS disconnect?), only score & btn to home view)
  - final score is gotten by abstraction (via Client event `getRoomScore`)
  - Request
    
    ```json
    {
      "event": "onGameEnd",
      "finalScore": 43,
    }
    ```
  - no Response
- `onJokerUse`
  - BROADCAST, subtracts 1 of `jokersLeft` (More than 1 joker? if not maybe use bool?)
  - Request
    
    ```json
    {
      "event": "onJokerUser",
    }
    ```
  - no Response
- `onPlayerLeave`
  - BROADCAST, updates local state (removes player) & shows notification that player left (reshuffle may be incoming)
  - TODO: send full player? or just id?
  - Request
    
    ```json
    {
      "event": "onPlayerLeave",
      "playerId": 342,
      "reason": "disconnected",
    }
    ```
  - no Response
- `onScoreChange`
  - BROADCAST, updates local state & displays new Score
  - Request
   
    ```json
    {
      "event": "onScoreChange",
      "score": 43,
    }
    ```
    
  - no Response
- `onTimerSync`
  - BROADCAST, TODO? Question id??? In later game states, the timers will differ for each question
  - local remaining time is changed to server time (will cause delay of ~Ping?)
  - Request
    
    ```json
    {
      "event": "onTimerSync",
      "remaining": 581,
    }
    ```
  - no Response
- `onTimeoutStart`
  - ONLY to 1 player | or playerId gets added
  - display modal (Kicked in x seconds), any action triggers `cancelTimeout`
  - Request
    
    ```json
    {
      "event": "onTimeoutStart",
      "remaining": 581,
    }
    ```
  - no Response
- `onKick`
  - ONLY to 1 player, others get `onPlayerLeave` | or playerId gets added
  - redirect to home
  - Request
    
    ```json
    {
      "event": "onKick",
    }
    ```
  - no Response
- `assignQuestion`
  - BROADCAST
  - redirect to home
  - Request
   
    ```json
    {
      "event": "assignQuestion",
      "question": {
        "questionId": 233,
        "questionSet": "Silicon Valley Trivia",
        "type": "text",
        "question": "Who is the CEO of Hooli?",
        "playerId": 123,
        "timeRemaining": 20000,
        "answers": [
          {
            "id": 0,
            "answer": "Gavin Belson",
            "playerId": 123,
          },
          {
            "id": 1,
            "answer": "Big Head",
            "playerId": 124,
          },
          {
            "id": 2,
            "answer": "Jeff Bezos",
            "playerId": 124,
          },
          {
            "id": 3,
            "answer": "Batman",
            "playerId": 125,
          },
        ]
      }
    }
    ```
  - no Response
- `assignAnswer`
  - `@Deprecated` functionality is handled by `assignQuestion` on WS layer
- `removeQuestion`
  - BROADCAST, all players which show the question/answer to the question display placeholder and delet question from local state
  - Request
   
    ```json
    {
      "event": "removeQuestion",
      "questionId": 233,
    }
    ```
- `removeAnswer`
  - `@Deprecated` functionality is handled by `removeQuestion` on WS layer