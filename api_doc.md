# Huihui Server API

## Base
- Base URL: `http://<host>:<port>`
- Content-Type: `application/json`

## Endpoints

### GET /
Request
- Method: `GET`
- Path: `/`
- Body: none

Response
- Status: `200`
- Body: `"hello"`

---

### POST /upload
Upload one or more files.

Request
- Method: `POST`
- Path: `/upload`
- Content-Type: `multipart/form-data`
- Form field:
- `files`: file[] (multiple files supported)

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    "/static/uploads/1739330100000000000_0.jpg",
    "/static/uploads/1739330100000000001_1.png"
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "save file failed: ..."
}
```

---

### POST /auth/login
Request
- Method: `POST`
- Path: `/auth/login`
- Body:
```json
{
  "username": "string",
  "password": "string"
}
```

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "token": "<jwt>"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### POST /auth/login/root
Root login (only `user_id = 1` can login via this API).

Request
- Method: `POST`
- Path: `/auth/login/root`
- Body:
```json
{
  "username": "string",
  "password": "string"
}
```

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "token": "<jwt>"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(...) or JwtError(...)"
}
```

---

### POST /topic/like
Like or unlike a topic with one API.

Request
- Method: `POST`
- Path: `/topic/like`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "topic_id": 12,
  "like": true
}
```

Behavior
- `like = true`: like topic (idempotent).
- `like = false`: unlike topic.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": null
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /topic/comment/{topic_id}
List all comments that reply to a topic.

Request
- Method: `GET`
- Path: `/topic/comment/{topic_id}`
- Headers:
- `Authorization: Bearer <jwt>`
- Path param:
- `topic_id`: number
- Behavior: excludes comments with `deleted = true`.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 101,
      "user_id": 7,
      "title": "Re: My topic",
      "content": "I agree",
      "images": null,
      "create_at": "2026-02-13T09:31:00+00:00",
      "user_info": {
        "id": 7,
        "name": "bob",
        "email": "bob@example.com",
        "profile": null
      },
      "comment_count": 0,
      "like_count": 3,
      "liked": false
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /tag
List all tags.

Request
- Method: `GET`
- Path: `/tag`
- Body: none

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "name": "Spicy",
      "image": "https://..."
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...)"
}
```

---

### POST /tag
Create a new tag.

Request
- Method: `POST`
- Path: `/tag`
- Body:
```json
{
  "name": "Spicy",
  "image": "https://cdn.example.com/tags/spicy.png"
}
```

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "id": 1,
    "name": "Spicy",
    "image": "https://cdn.example.com/tags/spicy.png"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...)"
}
```

---

### GET /tag/liked-values
Get liked-tag weight summary for current user.

Request
- Method: `GET`
- Path: `/tag/liked-values`
- Headers:
- `Authorization: Bearer <jwt>`
- Body: none

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "name": "Spicy",
      "value": 5.0
    },
    {
      "name": "Popular",
      "value": 3.0
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /restaurant
List all restaurants.

Request
- Method: `GET`
- Path: `/restaurant`
- Body: none

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "name": "Sunset Noodle House",
      "description": "Hand-pulled noodles and light broths.",
      "location": "Downtown",
      "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...)"
}
```

---

### GET /restaurant/simple
List all restaurants with minimal fields.

Request
- Method: `GET`
- Path: `/restaurant/simple`
- Body: none

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "name": "Sunset Noodle House"
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...)"
}
```

---

### GET /restaurant/list
List restaurants with pagination (root only).

Request
- Method: `GET`
- Path: `/restaurant/list`
- Headers:
- `Authorization: Bearer <jwt>`
- Query:
- `page`: number, optional, default `1`
- `page_size`: number, optional, default `10`, range `1..100`

Access
- Only root user can access (`user_id = 1`).

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "name": "Sunset Noodle House",
      "description": "Hand-pulled noodles and light broths.",
      "location": "Downtown",
      "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(...) or JwtError(...)"
}
```

---

### POST /restaurant
Create a restaurant (root only).

Request
- Method: `POST`
- Path: `/restaurant`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "name": "Sunset Noodle House",
  "description": "Hand-pulled noodles and light broths.",
  "location": "Downtown",
  "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
}
```

Notes
- `description` is optional (`string | null`).

Access
- Only root user can access (`user_id = 1`).

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "id": 9,
    "name": "Sunset Noodle House",
    "description": "Hand-pulled noodles and light broths.",
    "location": "Downtown",
    "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(...) or JwtError(...)"
}
```

---

### POST /restaurant/update
Update a restaurant (root only).

Request
- Method: `POST`
- Path: `/restaurant/update`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "id": 9,
  "name": "Sunset Noodle House",
  "description": "Hand-pulled noodles and light broths.",
  "location": "Downtown",
  "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
}
```

Notes
- `description` is optional (`string | null`).

Access
- Only root user can access (`user_id = 1`).

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "id": 9,
    "name": "Sunset Noodle House",
    "description": "Hand-pulled noodles and light broths.",
    "location": "Downtown",
    "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(RowNotFound) or SqlError(...) or JwtError(...)"
}
```

---

### GET /restaurant/foods
List all foods offered by one restaurant.

Request
- Method: `GET`
- Path: `/restaurant/foods`
- Query:
- `restaurant_id`: number, required

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 10,
      "name": "Spicy Chicken",
      "image": "https://cdn.example.com/foods/spicy-chicken.jpg"
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...)"
}
```

---

### POST /suggestion
Create a new suggestion.

Request
- Method: `POST`
- Path: `/suggestion`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "content": "Please add this new dish.",
  "images": [
    "/static/uploads/suggestion-1.jpg",
    "/static/uploads/suggestion-2.jpg"
  ],
  "type": "ADD_FOOD",
  "food_id": null,
  "restaurant_id": 2
}
```

Notes
- `images` is `string[]` in API.
- Server stores `images` as JSON string in DB.
- `type` enum: `ADD_FOOD | UPDATE_FOOD | OTHER`.
- Request field nullability:
- `content`: `string` (required, non-null)
- `images`: `string[]` (required, non-null; can be empty array)
- `type`: `ADD_FOOD | UPDATE_FOOD | OTHER` (required, non-null)
- `food_id`: `number | null` (optional)
- `restaurant_id`: `number | null` (optional)

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": 123
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JsonError(...) or JwtError(...)"
}
```

---

### GET /suggestion/my
List all suggestions created by current user.

Request
- Method: `GET`
- Path: `/suggestion/my`
- Headers:
- `Authorization: Bearer <jwt>`
- Body: none

Response item schema (`Suggestion`) nullability
- `id`: `number` (non-null)
- `content`: `string` (non-null)
- `images`: `string[] | null`
- `type`: `string` (non-null)
- `status`: `string` (non-null)
- `food`: `FoodWithTags | null`
- `restaurant`: `Restaurant | null`
- `reviewer_id`: `number | null`
- `review_comment`: `string | null`
- `user_id`: `number` (non-null)
- `created_at`: `string` (non-null)
- `reviewed_at`: `string | null`

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 123,
      "content": "Please add this new dish.",
      "images": ["/static/uploads/suggestion-1.jpg", "/static/uploads/suggestion-2.jpg"],
      "type": "ADD_FOOD",
      "status": "PENDING",
      "food": null,
      "restaurant": {
        "id": 2,
        "name": "Sunset Noodle House",
        "description": "Hand-pulled noodles and light broths.",
        "location": "Downtown",
        "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
      },
      "reviewer_id": null,
      "review_comment": null,
      "user_id": 3,
      "created_at": "2026-02-18",
      "reviewed_at": null
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /suggestion/{suggestion_id}
Get one suggestion by id.

Request
- Method: `GET`
- Path: `/suggestion/{suggestion_id}`
- Headers:
- `Authorization: Bearer <jwt>`
- Path param:
- `suggestion_id`: number

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "id": 123,
    "content": "Please add this new dish.",
    "images": ["/static/uploads/suggestion-1.jpg", "/static/uploads/suggestion-2.jpg"],
    "type": "ADD_FOOD",
    "status": "PENDING",
    "food": null,
    "restaurant": {
      "id": 2,
      "name": "Sunset Noodle House",
      "description": "Hand-pulled noodles and light broths.",
      "location": "Downtown",
      "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
    },
    "reviewer_id": null,
    "review_comment": null,
    "user_id": 3,
    "created_at": "2026-02-18",
    "reviewed_at": null
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(RowNotFound) or SqlError(...) or JwtError(...)"
}
```

---

### GET /suggestion/todo_log/{suggestion_id}/{suggestion_status}
List todo logs by suggestion id and status.

Request
- Method: `GET`
- Path: `/suggestion/todo_log/{suggestion_id}/{suggestion_status}`
- Headers:
- `Authorization: Bearer <jwt>`
- Path params:
- `suggestion_id`: number
- `suggestion_status`: string (e.g. `ACCEPTED`, `APPROVED`, `PREPARING`, `PROCESSING`, `FINISHED`, `REJECTED`, `PENDING`)

Notes
- `ACCEPTED` is treated as `APPROVED` for DB filtering.
- Response data type: `Array<{ content: string; create_time: string }>`
- If `suggestion_status` is `ACCEPTED` (or `APPROVED`), response returns only one item: the suggestion content.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "content": "Please add this new dish.",
      "create_time": "2026-02-18"
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /suggestion/list
List all suggestions with pagination (root only).

Request
- Method: `GET`
- Path: `/suggestion/list`
- Headers:
- `Authorization: Bearer <jwt>`
- Query:
- `page`: number, optional, default `1`
- `page_size`: number, optional, default `10`, range `1..100`
- `status`: string, optional, enum `PENDING | APPROVED | REJECTED | PREPARING | PROCESSING | FINISHED`
- `suggestion_type`: string, optional, enum `ADD_FOOD | UPDATE_FOOD | OTHER`

Access
- Only root user can access (`user_id = 1`).

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 123,
      "content": "Please add this new dish.",
      "images": ["/static/uploads/suggestion-1.jpg", "/static/uploads/suggestion-2.jpg"],
      "type": "ADD_FOOD",
      "status": "PENDING",
      "food": null,
      "restaurant": {
        "id": 2,
        "name": "Sunset Noodle House",
        "description": "Hand-pulled noodles and light broths.",
        "location": "Downtown",
        "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
      },
      "reviewer_id": null,
      "review_comment": null,
      "user_id": 3,
      "created_at": "2026-02-18",
      "reviewed_at": null
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(...) or JwtError(...)"
}
```

---

### GET /suggestion/list/todos
List todo suggestions with pagination (root only).

Request
- Method: `GET`
- Path: `/suggestion/list/todos`
- Headers:
- `Authorization: Bearer <jwt>`
- Query:
- `page`: number, optional, default `1`
- `page_size`: number, optional, default `10`, range `1..100`

Notes
- Todo statuses are fixed: `APPROVED | PREPARING | PROCESSING | FINISHED`.

Access
- Only root user can access (`user_id = 1`).

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 123,
      "content": "Please add this new dish.",
      "images": ["/static/uploads/suggestion-1.jpg", "/static/uploads/suggestion-2.jpg"],
      "type": "ADD_FOOD",
      "status": "PROCESSING",
      "food": null,
      "restaurant": {
        "id": 2,
        "name": "Sunset Noodle House",
        "description": "Hand-pulled noodles and light broths.",
        "location": "Downtown",
        "image": "https://cdn.example.com/restaurants/sunset-noodle.jpg"
      },
      "reviewer_id": 1,
      "review_comment": "accepted",
      "user_id": 3,
      "created_at": "2026-02-18",
      "reviewed_at": "2026-02-19"
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(...) or JwtError(...)"
}
```

---

### POST /suggestion/review
Approve or reject one suggestion (root only).

Request
- Method: `POST`
- Path: `/suggestion/review`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "suggestion_id": 123,
  "status": "APPROVED",
  "review_comment": "Looks good, will schedule implementation."
}
```

Notes
- `status` enum: `APPROVED | REJECTED`.
- Only `PENDING` suggestion can be reviewed.

Access
- Only root user can access (`user_id = 1`).

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": null
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(RowNotFound) or JwtError(...)"
}
```

---

### POST /suggestion/next_stage
Move suggestion status to next stage (root only).

Request
- Method: `POST`
- Path: `/suggestion/next_stage`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "suggestion_id": 123
}
```

Transition rule
- `APPROVED -> PREPARING`
- `PREPARING -> PROCESSING`
- `PROCESSING -> FINISHED`

Notes
- `PENDING`, `REJECTED`, `FINISHED` cannot move by this API.
- On success, server also writes one `todo_log` row.

Access
- Only root user can access (`user_id = 1`).

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": "PREPARING"
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(RowNotFound) or SqlError(...) or JwtError(...)"
}
```

---

### POST /suggestion/todo_log
Add a todo log using current suggestion status (root only).

Request
- Method: `POST`
- Path: `/suggestion/todo_log`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "suggestion_id": 123,
  "current_status": "PROCESSING",
  "log_content": "kitchen started trial cooking"
}
```

Notes
- `current_status` is validated against current DB status of this suggestion.
- `ACCEPTED` is treated as `APPROVED`.

Access
- Only root user can access (`user_id = 1`).

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": 45
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(...) or JwtError(...)"
}
```

---

### POST /auth/register
Request
- Method: `POST`
- Path: `/auth/register`
- Body:
```json
{
  "email": "user@example.com",
  "username": "string",
  "password": "string"
}
```

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "token": "<jwt>"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### POST /auth/update
Update current user information.

Request
- Method: `POST`
- Path: `/auth/update`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "email": "new_email@example.com",
  "username": "new_username",
  "profile": "https://cdn.example.com/avatar.jpg"
}
```

Notes
- All fields are optional.
- Provided fields are updated; omitted fields keep current values.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "id": 1,
    "email": "new_email@example.com",
    "name": "new_username",
    "profile": "https://cdn.example.com/avatar.jpg"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /auth/me
Request
- Method: `GET`
- Path: `/auth/me`
- Headers:
- `Authorization: Bearer <jwt>`
- Body: none

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "username",
    "profile": "https://cdn.example.com/avatar.jpg"
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /food/recommendation
Request
- Method: `GET`
- Path: `/food/recommendation`
- Headers:
- `Authorization: Bearer <jwt>`
- Params: none
- Body: none

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 10,
      "restaurant_id": 2,
      "name": "Spicy Chicken",
      "description": "...",
      "image": "https://..."
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

Notes
- No pagination.
- Repeats are allowed in MVP.
- Client calls this endpoint again when cards are exhausted.

---

### GET /food/list
List foods with pagination (root only).

Request
- Method: `GET`
- Path: `/food/list`
- Headers:
- `Authorization: Bearer <jwt>`
- Query:
- `page`: number, optional, default `1`
- `page_size`: number, optional, default `10`, range `1..100`

Access
- Only root user can access (`user_id = 1`).

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 10,
      "restaurant_id": 2,
      "restaurant_name": "Sunset Noodle House",
      "name": "Spicy Chicken",
      "description": "...",
      "image": "https://...",
      "tags": [
        {
          "id": 1,
          "name": "Spicy",
          "image": "https://..."
        },
        {
          "id": 3,
          "name": "Popular",
          "image": "https://..."
        }
      ]
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(...) or JwtError(...)"
}
```

---

### POST /food
Create a new food (root only).

Request
- Method: `POST`
- Path: `/food`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "restaurant_id": 2,
  "name": "Spicy Chicken",
  "description": "Hot and crispy.",
  "image": "https://cdn.example.com/foods/spicy-chicken.jpg",
  "tag_ids": [1, 3]
}
```

Notes
- `tag_ids` is optional.
- If `tag_ids` is omitted or empty, food is created without tags.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "id": 10,
    "restaurant_id": 2,
    "restaurant_name": "Sunset Noodle House",
    "name": "Spicy Chicken",
    "description": "Hot and crispy.",
    "image": "https://cdn.example.com/foods/spicy-chicken.jpg",
    "tags": [
      {
        "id": 1,
        "name": "Spicy",
        "image": "https://..."
      },
      {
        "id": 3,
        "name": "Popular",
        "image": "https://..."
      }
    ]
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(...) or JwtError(...)"
}
```

---

### POST /food/update
Update a food (root only), including restaurant, tags, and base fields.

Request
- Method: `POST`
- Path: `/food/update`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "id": 10,
  "restaurant_id": 3,
  "name": "Spicy Chicken (Updated)",
  "description": "Hot and crispy, updated recipe.",
  "image": "https://cdn.example.com/foods/spicy-chicken-v2.jpg",
  "tag_ids": [1, 4]
}
```

Notes
- `tag_ids` fully replaces existing food tags.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "id": 10,
    "restaurant_id": 3,
    "restaurant_name": "Brick Oven Bistro",
    "name": "Spicy Chicken (Updated)",
    "description": "Hot and crispy, updated recipe.",
    "image": "https://cdn.example.com/foods/spicy-chicken-v2.jpg",
    "tags": [
      {
        "id": 1,
        "name": "Spicy",
        "image": "https://..."
      },
      {
        "id": 4,
        "name": "Gluten-Free",
        "image": "https://..."
      }
    ]
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "PermissionDenied(...) or SqlError(...) or JwtError(...)"
}
```

---

### POST /food/recommendation/reaction
Request
- Method: `POST`
- Path: `/food/recommendation/reaction`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "food_id": 10,
  "reaction": "like",
  "source": "food_tab",
  "occurred_at": 1739330100
}
```

`reaction` enum
- `like`
- `skip`
- `dislike`

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": 123
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /food/recommendation/reaction/count
Count current user's like/dislike operations.

Request
- Method: `GET`
- Path: `/food/recommendation/reaction/count`
- Headers:
- `Authorization: Bearer <jwt>`

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": {
    "like": 12,
    "dislike": 3
  }
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /food/topTags
Get current user's top 3 most-liked food tags.

Request
- Method: `GET`
- Path: `/food/topTags`
- Headers:
- `Authorization: Bearer <jwt>`

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "name": "Spicy",
      "image": "https://..."
    },
    {
      "id": 3,
      "name": "Popular",
      "image": "https://..."
    },
    {
      "id": 8,
      "name": "Noodle",
      "image": "https://..."
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### POST /food/consecutiveSuggest
Request
- Method: `POST`
- Path: `/food/consecutiveSuggest`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "food_ids": [1, 2, 3, 4],
  "selected_food_ids": [1, 2]
}
```

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 10,
      "restaurant_id": 2,
      "name": "Spicy Chicken",
      "description": "...",
      "image": "https://..."
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### GET /topic
Fetch topics by page.

Request
- Method: `GET`
- Path: `/topic`
- Headers:
- `Authorization: Bearer <jwt>`
- Query:
- `page`: number, optional, default `1`
- Body: none
- Behavior: only returns topics with `is_top = true` and `deleted = false`.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "user_id": 3,
      "title": "My topic",
      "content": "Topic content",
      "images": ["/static/uploads/a.jpg", "/static/uploads/b.jpg"],
      "create_at": "2026-02-13T09:30:00+00:00",
      "user_info": {
        "id": 3,
        "name": "alice",
        "email": "alice@example.com",
        "profile": "https://cdn.example.com/avatar.jpg"
      },
      "comment_count": 12,
      "like_count": 34,
      "liked": true
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...)"
}
```

---

### GET /topic/my
List all topics posted by current user (no pagination).

Request
- Method: `GET`
- Path: `/topic/my`
- Headers:
- `Authorization: Bearer <jwt>`
- Body: none
- Behavior: excludes topics with `deleted = true`.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "user_id": 3,
      "title": "My topic",
      "content": "Topic content",
      "images": ["/static/uploads/a.jpg", "/static/uploads/b.jpg"],
      "create_at": "2026-02-13T09:30:00+00:00",
      "user_info": {
        "id": 3,
        "name": "alice",
        "email": "alice@example.com",
        "profile": "https://cdn.example.com/avatar.jpg"
      },
      "comment_count": 12,
      "like_count": 34,
      "liked": true
    }
  ]
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

---

### POST /topic/delete/{topic_id}
Soft-delete one topic.

Request
- Method: `POST`
- Path: `/topic/delete/{topic_id}`
- Headers:
- `Authorization: Bearer <jwt>`
- Path param:
- `topic_id`: number

Behavior
- Only topic owner can delete.
- Set `deleted = true`.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": null
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(RowNotFound) or SqlError(...) or JwtError(...)"
}
```

---

### POST /topic
Create a topic.

Request
- Method: `POST`
- Path: `/topic`
- Headers:
- `Authorization: Bearer <jwt>`
- Body:
```json
{
  "title": "My topic title",
  "content": "My topic content",
  "images": ["/static/uploads/a.jpg", "/static/uploads/b.jpg"],
  "reply_to_id": 12
}
```

Behavior
- If `reply_to_id` is `null` or omitted:
- Creates topic with `is_top = true`.
- If `reply_to_id` is provided:
- Creates topic with `is_top = false`.
- Inserts `(new_topic_id, reply_to_id)` into `reply` table.

Response (success)
- Status: `200`
- Body:
```json
{
  "code": 200,
  "message": "ok",
  "data": null
}
```

Response (error)
- Status: `200`
- Body:
```json
{
  "code": 500,
  "message": "SqlError(...) or JwtError(...)"
}
```

## Notes
- The API wraps responses in `ApiResponse`.
- Errors use `code=500` in JSON while HTTP status remains `200`.
- JWTs use `JWT_SECRET` (fallback: `dev-secret-change-me`) and expire in 24 hours.
