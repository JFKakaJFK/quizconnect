insert into user (username, password, enabled, create_date, role) values ('user1', '$2a$06$4MKRUgff3iP5mUS.KSk4Fu5nJiaVhAPhCWMziYyHUrQkHpivlr.Xe', TRUE, '2016-01-01 00:00:00', 'MANAGER')
insert into user (username, password, enabled, create_date, role) values ('user2', '$2a$06$4MKRUgff3iP5mUS.KSk4Fu5nJiaVhAPhCWMziYyHUrQkHpivlr.Xe', TRUE, '2016-01-01 00:00:00', 'MANAGER')
insert into user (username, password, enabled, create_date, role) values ('user3', '$2a$06$4MKRUgff3iP5mUS.KSk4Fu5nJiaVhAPhCWMziYyHUrQkHpivlr.Xe', TRUE, '2016-01-01 00:00:00', 'PLAYER')
insert into user (username, password, enabled, create_date, role) values ('user4', '$2a$06$4MKRUgff3iP5mUS.KSk4Fu5nJiaVhAPhCWMziYyHUrQkHpivlr.Xe', TRUE, '2016-01-01 00:00:00', 'PLAYER')
insert into user (username, password, enabled, create_date, role) values ('user5', '$2a$06$4MKRUgff3iP5mUS.KSk4Fu5nJiaVhAPhCWMziYyHUrQkHpivlr.Xe', TRUE, '2016-01-01 00:00:00', 'PLAYER')
insert into user (username, password, enabled, create_date, role) values ('user6', '$2a$06$4MKRUgff3iP5mUS.KSk4Fu5nJiaVhAPhCWMziYyHUrQkHpivlr.Xe', TRUE, '2016-01-01 00:00:00', 'PLAYER')
insert into user (username, password, enabled, create_date, role) values ('user7', '$2a$06$4MKRUgff3iP5mUS.KSk4Fu5nJiaVhAPhCWMziYyHUrQkHpivlr.Xe', TRUE, '2016-01-01 00:00:00', 'PLAYER')

insert into manager (id, user_username, email, institution) values (101, 'user1', 'user1@email.asdf', 'institution_01')
insert into manager (id, user_username, email, institution) values (102, 'user2', 'user2@email.asdf', 'institution_02')

insert into player (id, user_username, creator_id, avatar_path) values (201, 'user3', 101, '<change to valid path>')
insert into player (id, user_username, creator_id, avatar_path) values (202, 'user4', 101, '<change to valid path>')
insert into player (id, user_username, creator_id, avatar_path) values (203, 'user5', 101, '<change to valid path>')
insert into player (id, user_username, creator_id, avatar_path) values (204, 'user6', 102, '<change to valid path>')
insert into player (id, user_username, creator_id, avatar_path) values (205, 'user7', 102, '<change to valid path>')

insert into player_played_with_last (player_id, played_with_last) values (201, 'user4')
insert into player_played_with_last (player_id, played_with_last) values (201, 'user5')
insert into player_played_with_last (player_id, played_with_last) values (201, 'user6')
insert into player_played_with_last (player_id, played_with_last) values (202, 'user3')
insert into player_played_with_last (player_id, played_with_last) values (202, 'user7')


