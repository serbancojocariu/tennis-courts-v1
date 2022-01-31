insert into guest(id, name) values(null, 'Roger Federer');
insert into guest(id, name) values(null, 'Rafael Nadal');
insert into guest(id, name) values(null, 'Novak Djokovic');
insert into guest(id, name) values(null, 'Daniil Medvedev');
insert into guest(id, name) values(null, 'Stefanos Tsitsipas');

insert into tennis_court(id, name) values(null, 'Roland Garros - Court Philippe-Chatrier');
insert into tennis_court(id, name) values(null, 'Wimbledon Centre Court');
insert into tennis_court(id, name) values(null, 'Rod Laver Arena');
insert into tennis_court(id, name) values(null, 'Louis Armstrong Stadium');
insert into tennis_court(id, name) values(null, 'Hard Rock Stadium Court');

insert into schedule (id, start_date_time, end_date_time, tennis_court_id) values (null, '2022-01-31T10:00:00.0', '2022-01-31T11:00:00.0', 1);
insert into schedule (id, start_date_time, end_date_time, tennis_court_id) values (null, '2022-01-31T11:00:00.0', '2022-01-31T12:00:00.0', 1);
insert into schedule (id, start_date_time, end_date_time, tennis_court_id) values (null, '2022-01-31T14:00:00.0', '2022-01-31T15:00:00.0', 2);
insert into schedule (id, start_date_time, end_date_time, tennis_court_id) values (null, '2022-01-31T15:00:00.0', '2022-01-31T16:00:00.0', 3);
insert into schedule (id, start_date_time, end_date_time, tennis_court_id) values (null, '2022-01-31T17:00:00.0', '2022-01-31T18:00:00.0', 4);