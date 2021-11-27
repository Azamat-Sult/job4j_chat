INSERT INTO role (role) VALUES ('ROLE_MODERATOR');
INSERT INTO role (role) VALUES ('ROLE_USER');

INSERT INTO person (username, enabled, password, role_id)
VALUES ('admin', true, '$2a$10$ZQbDwyi0F31Er0SeO2n6xOEA.G7BjkCX6L0.N6M7hWAeqF6OszpzC',
(select id from role where role = 'ROLE_MODERATOR'));

INSERT INTO room (name, description, created, owner_id) VALUES ('Где купить видеокарту подешевле?',
 'Майнинг маст дай!!!', '2021-11-16 19:44:58', (select id from person where username = 'admin'));

INSERT INTO message (text, author_id, created) VALUES ('Попробуй через продавцов сетевых магазинов типа DNS',
 (select id from person where username = 'admin'), '2021-11-17 19:44:58');

INSERT INTO room_messages (room_id, messages_id) VALUES (
(select id from room where name = 'Где купить видеокарту подешевле?'),
 (select id from message where text = 'Попробуй через продавцов сетевых магазинов типа DNS')
);