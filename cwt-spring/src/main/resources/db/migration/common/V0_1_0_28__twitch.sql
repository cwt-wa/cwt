alter table channel add column video_cursor text;

delete from configuration where key = 'PAGINATION_CURSOR_VIDEOS_TWITCH_API';
