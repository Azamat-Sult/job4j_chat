CREATE TABLE if not exists role (
  id serial primary key,
  role varchar(255)
);

CREATE TABLE if not exists person (
  id serial primary key,
  username varchar(255),
  password varchar(255),
  role_id int references role(id),
  enabled boolean
);

CREATE TABLE if not exists message (
  id serial primary key,
  text varchar(255),
  author_id int references person(id),
  created TIMESTAMP
);

CREATE TABLE if not exists room (
  id serial primary key,
  name varchar(255),
  description varchar(255),
  created TIMESTAMP,
  owner_id int references person(id)
);

CREATE TABLE if not exists room_messages (
  id serial primary key,
  room_id INT references room(id),
  messages_id INT references message(id)
);