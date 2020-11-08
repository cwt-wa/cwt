alter table "game_stats" add column "texture" text;

update "game_stats" set texture = data::json->'texture';
