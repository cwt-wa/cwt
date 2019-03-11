alter table configuration add column description text;

update configuration
set description='Number of group members per group to advance into the playoff stage.'
where key = 'NUMBER_OF_GROUP_MEMBERS_ADVANCING';

update configuration
set description='The best of what games in groups are played in.'
where key = 'GROUP_GAMES_BEST_OF';

update configuration
set description='The best of what games in playoff stage are played in.'
where key = 'PLAYOFF_GAMES_BEST_OF';

update configuration
set description='The best of what games in thrid place final and final are played in.'
where key = 'FINAL_GAME_BEST_OF';

update configuration
set description='Relation of won rounds and points. Defines comma-separated 2-tuples that relate how many points will be given for the amount of won rounds in a game. (won rounds, points), (won rounds, points), …'
where key = 'POINTS_PATTERN';
