INSERT INTO public.user_ (id, activated, activation_key, email, password_hash, password_legacy_hash, reset_date, reset_key, username) VALUES (10, TRUE, NULL, 'pkp@pkp', '$2a$10$tcrBYotkWcsuIN5tX8nOUOpBF/Ts2qwFEzXXAWQJ473ulIH5tiSKC', NULL, NULL, NULL, 'Zemke');
INSERT INTO authority (id, name) VALUES (40, 'ROLE_ADMIN');
INSERT INTO public.user_authority (user_id, authority_id) VALUES (10, 40);
INSERT INTO public.user_profile (id, about, clan, country, email, facebook, modified, skype, twitter, user_id) VALUES (20, null, null, null, null, null, null, null, null, null);
INSERT INTO public.user_setting (id, hide_email, hide_profile, user_id) VALUES (30, null, null, null);

INSERT INTO public.tournament (id, created, open, review, status, bronze_winner_id, gold_winner_id, host_id, silver_winner_id) VALUES (50, NULL, NULL, NULL, 'OPEN', NULL, NULL, 10, NULL);

INSERT INTO configuration (id, created, key, value, author_id) VALUES (10, NULL, 'rules', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum ac mauris non nisi iaculis commodo. Sed convallis bibendum dui nec posuere. Phasellus quis ante ut orci vulputate semper vitae lobortis odio. Mauris vel orci et nulla euismod eleifend. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nulla faucibus nisl sit amet ex ultricies eleifend. Suspendisse a leo fringilla ipsum venenatis posuere. Fusce placerat nunc sed fermentum pulvinar. Mauris eu quam ut urna laoreet varius sit amet non ex. Duis felis lacus, auctor accumsan cursus non, volutpat et nisl. Sed in turpis interdum augue ultricies luctus. Donec ac mattis arcu, eget lobortis massa.', 10);
