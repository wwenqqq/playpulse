# GameShelf API Examples

These examples call the Docker Compose gateway at `http://localhost:8088`. Responses use the envelope `{ "code": 200, "message": "success", "data": ... }`.

## Register and sign in

```bash
curl -X POST http://localhost:8088/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo-player","password":"demo-password"}'

curl -X POST http://localhost:8088/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo-player","password":"demo-password"}'
```

Copy `data.token` from the sign-in response and set it for the remaining examples:

```bash
TOKEN="PASTE_JWT_HERE"
```

## Browse and search games

```bash
curl http://localhost:8088/api/games
curl "http://localhost:8088/api/games?search=minecraft"
curl http://localhost:8088/api/games/1
```

## Add a catalogue game

Authentication is required. Titles are unique.

```bash
curl -X POST http://localhost:8088/api/games \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Lantern Atlas",
    "studio":"Quiet Current",
    "releaseYear":2026,
    "genres":"Exploration, Puzzle",
    "coverTone":"amber",
    "summary":"Chart a changing archipelago by following lights left by earlier travellers."
  }'
```

## Save a game to the personal library

Allowed statuses are `WISHLIST`, `PLAYING` and `COMPLETED`.

```bash
curl -X POST http://localhost:8088/api/games/1/library \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status":"WISHLIST"}'

curl http://localhost:8088/api/games/library/mine \
  -H "Authorization: Bearer $TOKEN"
```

## Publish and read reviews

Allowed categories are `REVIEW`, `RECOMMEND` and `CRITIQUE`.

```bash
curl -X POST http://localhost:8088/api/feedback \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "gameId":1,
    "title":"A patient, memorable mystery",
    "description":"The environmental clues reward careful exploration without slowing the final act.",
    "category":"REVIEW",
    "priority":"MEDIUM"
  }'

curl "http://localhost:8088/api/feedback?size=20&sort=popular"
curl http://localhost:8088/api/feedback/mine \
  -H "Authorization: Bearer $TOKEN"
```

## Vote and comment

Replace `REVIEW_ID` with a published review ID.

```bash
curl -X POST http://localhost:8088/api/feedback/REVIEW_ID/votes \
  -H "Authorization: Bearer $TOKEN"

curl -X POST http://localhost:8088/api/feedback/REVIEW_ID/comments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"The pacing note helped me decide. Thank you."}'

curl http://localhost:8088/api/feedback/REVIEW_ID/comments
```

## Health checks

The public gateway serves the frontend. Kubernetes probes call each service directly at `/actuator/health`, `/actuator/health/readiness` and `/actuator/health/liveness`.
