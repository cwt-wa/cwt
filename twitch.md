# Rate Limiting

- `Ratelimit-Limit` — The rate at which points are added to your bucket. This is the average number of requests per minute you can make over an extended period of time.
- `Ratelimit-Remaining` — The number of points you have left to use.
- `Ratelimit-Reset` — A Unix epoch timestamp of when your bucket is reset to full.

# Authentication

## Getting

```
curl -X POST 'https://id.twitch.tv/oauth2/token?client_id=yourClientId&client_secret=yourSecret&grant_type=client_credentials'
```

Example response:

```json
{
  "access_token": "prau3ol6mg5glgek8m89ec2s9q5i3i",
  "refresh_token": "",
  "expires_in": 3600,
  "scope": [],
  "token_type": "bearer"
}
```

Valid for 60 days.

## Validating

```
curl -H "Authorization: OAuth <access token>" https://id.twitch.tv/oauth2/validate
```

Example response (when successful):

```json
{
  "client_id": "<your client ID>",
  "login": "<authorized user login>",
  "scopes": [
    "<requested scopes>"
  ],
  "user_id": "<authorized user ID>"
}
```

## Sending

``` 
curl -H "Authorization: Bearer <access token>" https://api.twitch.tv/helix/
```


# [Endpoint](https://dev.twitch.tv/docs/api/reference/#get-videos)

```
curl -H "Authorization: Bearer yourbearertokenhere" https://api.twitch.tv/helix/videos?user_id=12345
```

Example response: 

```json
{
  "data": [{
    "id": "234482848",
    "user_id": "67955580",
    "user_name": "ChewieMelodies",
    "title": "-",
    "description": "",
    "created_at": "2018-03-02T20:53:41Z",
    "published_at": "2018-03-02T20:53:41Z",
    "url": "https://www.twitch.tv/videos/234482848",
    "thumbnail_url": "https://static-cdn.jtvnw.net/s3_vods/bebc8cba2926d1967418_chewiemelodies_27786761696_805342775/thumb/thumb0-%{width}x%{height}.jpg",
    "viewable": "public",
    "view_count": 142,
    "language": "en",
    "type": "archive",
    "duration": "3h8m33s"
  }],
  "pagination":{"cursor":"eyJiIjpudWxsLCJhIjoiMTUwMzQ0MTc3NjQyNDQyMjAwMCJ9"}
}
```

In subsequent examples the `Authorization` header is omitted for brevity.

# API request events

Twitch API is to be requested when either of two events occur:

1. User visits streams overview page
2. New Twitch user account registered on CWT

**Save all data from the responses in the DB for future need.**

## User visits streams overview page

Streams are requested in two phases. 
Firstly, the user is provided with a list of streams that had already been queried and were remembered in the CWT DB.
Secondly, possible new streams are lazily queried from the Twitch API.

1. Existing streams are queried from the CWT DB and shown to the user.
2. Query for streams from Twitch API supplying all registered channels on CWT and the latest pagination cursor.
    ```
    curl https://api.twitch.tv/helix/videos?user_id=12345&user_id=54321&first=100&after=latestPaginationCursor
    ```
3. Repeat with each last pagination cursor until `data.length < limitOfRequest`.
4. Persist the last pagination cursor in `configuration` table.
5. Persist all newly obtained streams in the database.
 

## New Twitch user account registered on CWT

Users have to register their Twitch user account to CWT as a channel. Upon registration their videos are initially fetched form the Twitch API.

Do [“User visits streams overview page”](#user-visits-streams-overview-page) but with only this channel’s `user_id`.

# Future plans

- Link streams to games in the database. (part of CWT 5)
- When a channel goes live, put an alert on the CWT page.  (part of CWT 5)

