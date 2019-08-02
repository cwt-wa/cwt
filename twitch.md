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


# [Videos endpoint](https://dev.twitch.tv/docs/api/reference/#get-videos)

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

# [Users endpoint](https://dev.twitch.tv/docs/api/reference/#get-users)

Upon registration of a channel from a user on CWT the user is providing his Twitch username by which the user is searched by using the Twitch API.

```
curl -H "Authorization: Bearer yourbearertokenhere" https://api.twitch.tv/helix/users?login=khamski
```

Example response:

```json
{
   "data" : [
      {
         "login" : "khamski",
         "view_count" : 11040,
         "id" : "26027047",
         "broadcaster_type" : "",
         "type" : "",
         "offline_image_url" : "",
         "profile_image_url" : "https://static-cdn.jtvnw.net/jtv_user_pictures/khamski-profile_image-10dc902e62492108-300x300.jpeg",
         "description" : "Kickass normal worms television with good quality and awesome commentators!",
         "display_name" : "Khamski"
      }
   ]
}
```

# API request events

Twitch API is to be requested when either of two events occur:

1. User visits streams overview page
~~2. New Twitch user account registered on CWT~~

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

# Current channels

```json
{
  "data" : [
    {
      "display_name" : "Khamski",
      "type" : "",
      "profile_image_url" : "https://static-cdn.jtvnw.net/jtv_user_pictures/khamski-profile_image-10dc902e62492108-300x300.jpeg",
      "view_count" : 11039,
      "broadcaster_type" : "",
      "offline_image_url" : "",
      "login" : "khamski",
      "id" : "26027047",
      "description" : "Kickass normal worms television with good quality and awesome commentators!"
    },
    {
      "type" : "",
      "profile_image_url" : "https://static-cdn.jtvnw.net/jtv_user_pictures/wormykayz-profile_image-831bf59e2fde620f-300x300.jpeg",
      "display_name" : "WormyKayz",
      "description" : "",
      "id" : "45204800",
      "login" : "wormykayz",
      "view_count" : 2114,
      "offline_image_url" : "",
      "broadcaster_type" : ""
    },
    {
      "type" : "",
      "profile_image_url" : "https://static-cdn.jtvnw.net/jtv_user_pictures/mrtpenguin-profile_image-80e6c3b1141506ff-300x300.jpeg",
      "display_name" : "MrTPenguin",
      "id" : "77989017",
      "login" : "mrtpenguin",
      "description" : "I play normal games of Worms Armageddon, mostly the intermediate scheme and mostly 1v1. I broadcast CWT and other significant games if the main broadcasters aren't.",
      "view_count" : 467,
      "broadcaster_type" : "",
      "offline_image_url" : ""
    },
    {
      "type" : "",
      "profile_image_url" : "https://static-cdn.jtvnw.net/user-default-pictures/0ecbb6c3-fecb-4016-8115-aa467b7c36ed-profile_image-300x300.jpg",
      "display_name" : "DocOne42",
      "id" : "25468719",
      "login" : "docone42",
      "description" : "",
      "offline_image_url" : "",
      "view_count" : 362,
      "broadcaster_type" : ""
    },
    {
      "login" : "drabegod",
      "id" : "141982436",
      "description" : "",
      "offline_image_url" : "",
      "view_count" : 359,
      "broadcaster_type" : "",
      "type" : "",
      "profile_image_url" : "https://static-cdn.jtvnw.net/user-default-pictures/27103734-3cda-44d6-a384-f2ab71e4bb85-profile_image-300x300.jpg",
      "display_name" : "DrAbegod"
    },
    {
      "display_name" : "WhiteRqbbit",
      "profile_image_url" : "https://static-cdn.jtvnw.net/user-default-pictures/0ecbb6c3-fecb-4016-8115-aa467b7c36ed-profile_image-300x300.jpg",
      "type" : "",
      "broadcaster_type" : "",
      "view_count" : 105,
      "offline_image_url" : "",
      "description" : "",
      "login" : "whiterqbbit",
      "id" : "136606718"
    },
    {
      "view_count" : 17475,
      "offline_image_url" : "https://static-cdn.jtvnw.net/jtv_user_pictures/4d5ae5f6-4d16-476a-ab52-b308176060e9-channel_offline_image-1920x1080.jpg",
      "broadcaster_type" : "affiliate",
      "description" : "Croatian's finest.",
      "login" : "senseiofworms",
      "id" : "104501736",
      "display_name" : "senseiofworms",
      "profile_image_url" : "https://static-cdn.jtvnw.net/jtv_user_pictures/ea3528da-2944-402f-8a40-0f93a226c0e9-profile_image-300x300.png",
      "type" : ""
    },
    {
      "display_name" : "SiDwa",
      "type" : "",
      "profile_image_url" : "https://static-cdn.jtvnw.net/jtv_user_pictures/c93502e6-805d-4439-a6b6-e412976fc86c-profile_image-300x300.png",
      "view_count" : 14,
      "offline_image_url" : "",
      "broadcaster_type" : "",
      "description" : "",
      "login" : "sidwa",
      "id" : "94422047"
    }
  ]
}
```
