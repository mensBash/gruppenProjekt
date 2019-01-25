//  CREATE SEQUENCE IF NOT EXISTS seq_busNumber NO CYCLE MINVALUE 1000 INCREMENT BY 1;

DROP TABLE IF EXISTS Route;
DROP TABLE IF EXISTS Cities;
DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS BookingEntry;
DROP TABLE IF EXISTS Customer;


CREATE TABLE IF NOT EXISTS Cities(
  name VARCHAR (128) NOT NULL PRIMARY KEY,
  country VARCHAR(128) NOT NULL,
  blind_booking BOOLEAN DEFAULT FALSE,
  picture VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS Customer(
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR (48) NOT NULL,
last_name VARCHAR(48) NOT NULL,
social_number VARCHAR (64) NOT NULL UNIQUE,
email VARCHAR (128) NOT NULL,
birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS Route (
  rid BIGINT AUTO_INCREMENT PRIMARY KEY,
  bus_number VARCHAR(128) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  start_destination VARCHAR (128) NOT NULL REFERENCES Cities(name),
  end_destination VARCHAR (128) NOT NULL REFERENCES Cities(name),
  distance DOUBLE NOT NULL,
  departure_time SMALLDATETIME ,
  arrival_time SMALLDATETIME,
  capacity INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS Booking(
  bid BIGINT AUTO_INCREMENT PRIMARY KEY,
  booking_number BIGINT NOT NULL,
  time_of_booking SMALLDATETIME NOT NULL,
  status VARCHAR(20) NOT NULL,
  total_sum DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS BookingEntry(
  bid BIGINT REFERENCES Booking(bid) ON DELETE CASCADE,
  sid BIGINT,
  rid BIGINT REFERENCES Route(rid),
  name VARCHAR(20) NOT NULL,
  surname VARCHAR(20) NOT NULL,
  PRIMARY KEY(sid, rid)
);


/* Format: YYYY-MM-DD hh:mm:ss
   Insert into Route only if where not exist select * from route, what means table route is empty
 */


 INSERT INTO Cities(name,country, blind_booking, picture) VALUES
  ('Vienna','Austria', false, null),
  ('Berlin','Germany', true, 'src/main/resources/images/berlin.jpg'),
  ('Prague','Czech Republic', true, 'src/main/resources/images/prague.jpg'),
  ('Dresden','Germany', false, null),
  ('Graz', 'Austria', false, null),
  ('Zurich', 'Switzerland', true, 'src/main/resources/images/zurich.jpg'),
  ('Cologne', 'Germany', true, 'src/main/resources/images/cologne.jpg'),
  ('Geneva','Switzerland', false, null),
  ('Bratislava', 'Slovakia', false, null),
  ('Salzburg', 'Austria', true, 'src/main/resources/images/salzburg.jpg'),
  ('Ljubljana','Slovenia', true, 'src/main/resources/images/ljubljana.jpg');


/* Routes Vienna - Berlin and Berlin - Vienna */

INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('120A',40, 'Vienna', 'Berlin', 681, 53,'2018-07-20 06:00:00' , '2018-07-20 17:00:00'),
  ('130A',60, 'Vienna', 'Berlin', 681, 53,'2018-07-21 09:00:00' , '2018-07-21 22:00:00'),
  ('120A',40, 'Vienna', 'Berlin', 681, 53,'2018-07-23 07:00:00' , '2018-07-23 17:00:00'),
  ('120A',40, 'Vienna', 'Berlin', 681, 53,'2018-07-27 09:00:00' , '2018-07-27 22:00:00'),
  ('130A',60, 'Vienna', 'Berlin', 681, 53,'2018-07-30 07:00:00' , '2018-07-30 17:00:00'),

  // back route
  ('140A',40, 'Berlin', 'Vienna', 681, 53,'2018-07-20 22:00:00' , '2018-07-21 04:00:00'),
  ('150A',60, 'Berlin', 'Vienna', 681, 53,'2018-07-22 06:00:00' , '2018-07-22 12:00:00'),
  ('140A',40, 'Berlin', 'Vienna', 681, 53,'2018-07-24 07:00:00' , '2018-07-24 17:00:00'),
  ('140A',40, 'Berlin', 'Vienna', 681, 53,'2018-07-28 06:00:00' , '2018-07-28 17:00:00'),
  ('150A',60, 'Berlin', 'Vienna', 681, 53,'2018-07-31 07:00:00' , '2018-07-31 17:00:00');

/* Routes Vienna - Prague and Prague - Vienna */
INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('220A',25, 'Vienna', 'Prague', 333, 53,'2018-07-21 17:00:00' , '2018-07-21 20:00:00'),
  ('220A',25, 'Vienna', 'Prague', 333, 53,'2018-07-24 15:00:00' , '2018-07-24 17:00:00'),
  ('220A',25, 'Vienna', 'Prague', 333, 53,'2018-07-26 07:00:00' , '2018-07-26 10:00:00'),
  ('220A',25, 'Vienna', 'Prague', 333, 53,'2018-07-28 11:00:00' , '2018-07-28 14:00:00'),
  ('220A',25, 'Vienna', 'Prague', 333, 53,'2018-07-30 14:00:00' , '2018-07-30 17:00:00'),
  ('220A',7, 'Vienna', 'Prague', 333, 53,'2018-10-01 07:00:00' , '2018-10-01 10:00:00'),
  // back route
  ('240A',25, 'Prague', 'Vienna', 333, 53,'2018-07-22 08:00:00' , '2018-07-22 11:00:00'),
  ('240A',25, 'Prague', 'Vienna', 333, 53,'2018-07-24 09:00:00' , '2018-07-24 12:00:00'),
  ('240A',25, 'Prague', 'Vienna', 333, 53,'2018-07-26 23:00:00' , '2018-07-27 02:00:00'),
  ('240A',25, 'Prague', 'Vienna', 333, 53,'2018-07-28 03:00:00' , '2018-07-28 06:00:00'),
  ('240A',25, 'Prague', 'Vienna', 333, 53,'2018-07-31 11:00:00' , '2018-07-31 14:00:00');

/* Routes Prague - Dresden and Dresden - Prague */
INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-07-21 20:15:00' , '2018-07-21 22:00:00'),
  ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-07-17 13:00:00' , '2018-07-17 14:40:00'),
  ('200B',20, 'Prague', 'Dresden', 147, 53,'2018-07-20 13:30:00' , '2018-07-20 15:10:00'),
  ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-07-24 13:00:00' , '2018-07-24 14:40:00'),
  ('200B',20, 'Prague', 'Dresden', 147, 53,'2018-07-27 13:30:00' , '2018-07-27 15:10:00'),
  //back route
  ('270A',25, 'Dresden', 'Prague', 147, 53,'2018-07-19 19:45:00' , '2018-07-19 21:25:00'),
  ('200B',20, 'Dresden', 'Prague', 147, 53,'2018-07-21 11:00:00' , '2018-07-21 13:40:00'),
  ('270A',25, 'Dresden', 'Prague', 147, 53,'2018-07-26 19:45:00' , '2018-07-26 21:25:00'),
  ('200B',20, 'Dresden', 'Prague', 147, 53,'2018-07-28 23:00:00' , '2018-07-28 00:40:00'),
  ('270A',25, 'Dresden', 'Prague', 147, 53,'2018-08-02 19:45:00' , '2018-08-02 21:25:00');

  /* Routes Dresden - Berlin and Berlin - Dresden */
  INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
   ('110C',20, 'Dresden', 'Berlin', 193, 53,'2018-07-17 16:45:00' , '2018-07-17 18:55:00'),
   ('190B',30, 'Dresden', 'Berlin', 193, 53,'2018-07-21 22:30:00' , '2018-07-21 23:40:00'),
   ('110C',20, 'Dresden', 'Berlin', 193, 53,'2018-07-24 16:45:00' , '2018-07-24 18:55:00'),
   ('110C',20, 'Dresden', 'Berlin', 193, 53,'2018-07-27 16:45:00' , '2018-07-27 18:55:00'),
   ('190B',30, 'Dresden', 'Berlin', 193, 53,'2018-07-28 06:30:00' , '2018-07-28 08:40:00'),
   ('190B',8, 'Dresden', 'Berlin', 193, 53,'2018-10-01 13:00:00' , '2018-10-01 16:00:00'),
    //back route
   ('190B',20, 'Berlin', 'Dresden', 193, 53,'2018-07-17 05:00:00' , '2018-07-17 07:10:00'),
   ('110C',30, 'Berlin', 'Dresden', 193, 53,'2018-07-21 05:00:00' , '2018-07-21 07:10:00'),
   ('190B',20, 'Berlin', 'Dresden', 193, 53,'2018-07-24 05:00:00' , '2018-07-24 07:10:00'),
   ('190C',20, 'Berlin', 'Dresden', 193, 53,'2018-07-27 05:00:00' , '2018-07-27 07:10:00'),
   ('110C',30, 'Berlin', 'Dresden', 193, 53,'2018-07-28 05:00:00' , '2018-07-28 07:10:00');

  /* Routes Vienna - Bratislava and Bratislava - Vienna */
  INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
   ('300A',8, 'Vienna', 'Bratislava', 80, 53,'2018-07-21 13:00:00' , '2018-07-21 14:00:00'),
   ('310A',10, 'Vienna', 'Bratislava', 80, 53,'2018-07-23 15:30:00' , '2018-07-23 08:40:00'),
   ('300A',8, 'Vienna', 'Bratislava', 80, 53,'2018-07-24 13:00:00' , '2018-07-24 14:00:00'),
   ('310A',10, 'Vienna', 'Bratislava', 80, 53,'2018-07-28 15:30:00' , '2018-07-28 18:55:00'),
   ('300A',8, 'Vienna', 'Bratislava', 80, 53,'2018-07-31 13:00:00' , '2018-07-31 14:00:00'),
   //back route
   ('310A',10, 'Bratislava', 'Vienna', 80, 53,'2018-07-21 07:00:00' , '2018-07-21 08:00:00'),
   ('300A',8, 'Bratislava', 'Vienna', 80, 53,'2018-07-23 08:00:00' , '2018-07-23 09:00:00'),
   ('310A',10, 'Bratislava', 'Vienna', 80, 53,'2018-07-24 07:00:00' , '2018-07-24 08:00:00'),
   ('300A',8, 'Bratislava', 'Vienna', 80, 53,'2018-07-28 08:00:00' , '2018-07-28 09:00:00'),
   ('310A',10, 'Bratislava', 'Vienna', 80, 53,'2018-07-31 07:00:00' , '2018-07-31 08:00:00');

  /* Routes Dresden - Bratislava and Bratislava - Dresden */
  INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
   ('100C',30, 'Dresden', 'Bratislava', 476, 53,'2018-07-21 01:15:00' , '2018-07-21 06:00:00'),
   ('100C',30, 'Dresden', 'Bratislava', 476, 53,'2018-07-24 16:30:00' , '2018-07-25 21:15:00'),
   ('100C',30, 'Dresden', 'Bratislava', 476, 53,'2018-07-28 01:15:00' , '2018-07-28 06:00:00'),
   ('100C',30, 'Dresden', 'Bratislava', 476, 53,'2018-08-02 16:30:00' , '2018-08-02 21:15:00'),
   ('100C',30, 'Dresden', 'Bratislava', 476, 53,'2018-08-04 01:15:00' , '2018-08-04 14:00:00'),
   //back route
   ('200C',30, 'Bratislava', 'Dresden', 467, 53,'2018-07-21 07:00:00' , '2018-07-21 08:00:00'),
   ('200C',30, 'Bratislava', 'Dresden', 476, 53,'2018-07-23 08:00:00' , '2018-07-23 09:00:00'),
   ('200C',30, 'Bratislava', 'Dresden', 476, 53,'2018-07-24 07:00:00' , '2018-07-24 08:00:00'),
   ('200C',30, 'Bratislava', 'Dresden', 476, 53,'2018-07-28 08:00:00' , '2018-07-28 09:00:00'),
   ('200C',30, 'Bratislava', 'Dresden', 476, 53,'2018-07-31 07:00:00' , '2018-07-31 08:00:00');

  /* Routes Prague - Cologne and Cologne - Prague */
  INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
    ('300A',75, 'Prague', 'Cologne', 697, 53,'2018-07-21 17:00:00' , '2018-07-21 22:00:00'),
    ('310A',75, 'Prague', 'Cologne', 697, 53,'2018-07-24 19:30:00' , '2018-07-25 00:30:00'),
    ('300A',75, 'Prague', 'Cologne', 697, 53,'2018-07-28 17:00:00' , '2018-07-28 22:00:00'),
    ('300A',75, 'Prague', 'Cologne', 697, 53,'2018-07-30 19:30:00' , '2018-07-30 00:30:00'),
    ('310A',75, 'Prague', 'Cologne', 697, 53,'2018-08-02 23:00:00' , '2018-08-03 06:00:00'),
    //back route
    ('310A',80, 'Cologne', 'Prague', 697, 53,'2018-07-21 07:00:00' , '2018-07-21 12:00:00'),
    ('300A',80, 'Cologne', 'Prague', 697, 53,'2018-07-22 03:00:00' , '2018-07-22 07:00:00'),
    ('310A',80, 'Cologne', 'Prague', 697, 53,'2018-07-24 03:00:00' , '2018-07-24 07:00:00'),
    ('300A',80, 'Cologne', 'Prague', 697, 53,'2018-07-27 20:00:00' , '2018-07-28 01:00:00'),
    ('310A',80, 'Cologne', 'Prague', 697, 53,'2018-07-31 07:00:00' , '2018-07-31 12:00:00');

  INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
    ('330A',75, 'Cologne', 'Berlin', 573, 53,'2018-07-21 23:00:00' , '2018-07-22 04:00:00'),
    ('330A',75, 'Cologne', 'Berlin', 573, 53,'2018-07-23 09:00:00' , '2018-07-23 14:00:00'),
    ('330A',75, 'Cologne', 'Berlin', 573, 53,'2018-07-24 07:00:00' , '2018-07-24 12:00:00'),
    ('330A',75, 'Cologne', 'Berlin', 573, 53,'2018-07-27 23:00:00' , '2018-07-28 04:00:00'),
    ('330A',75, 'Cologne', 'Berlin', 573, 53,'2018-07-31 07:00:00' , '2018-07-31 12:00:00'),

    ('330F',75, 'Berlin', 'Cologne', 573, 53,'2018-07-21 23:00:00' , '2018-07-22 04:00:00'),
    ('330F',75, 'Berlin', 'Cologne', 573, 53,'2018-07-23 09:00:00' , '2018-07-23 14:00:00'),
    ('330F',75, 'Berlin', 'Cologne', 573, 53,'2018-07-23 19:15:00' , '2018-07-24 01:00:00'),
    ('330F',75, 'Berlin', 'Cologne', 573, 53,'2018-07-27 23:00:00' , '2018-07-28 04:00:00'),
    ('330F',75, 'Berlin', 'Cologne', 573, 53,'2018-07-31 07:00:00' , '2018-07-31 12:00:00');


INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('400C',40, 'Vienna', 'Zurich', 742, 53,'2018-07-21 06:00:00' , '2018-07-21 13:00:00'),
  ('400C',40, 'Vienna', 'Zurich', 742, 53,'2018-07-23 08:00:00' , '2018-07-23 14:00:00'),
  ('400C',40, 'Vienna', 'Zurich', 742, 53,'2018-07-24 15:00:00' , '2018-07-24 22:00:00'),
  ('400C',40, 'Vienna', 'Zurich', 742, 53,'2018-07-30 18:00:00' , '2018-07-31 01:00:00'),
  ('400C',40, 'Vienna', 'Zurich', 742, 53,'2018-07-31 19:00:00' , '2018-08-01 02:00:00'),
  // back route
  ('410C',45, 'Zurich', 'Vienna', 742, 53,'2018-07-22 23:00:00' , '2018-07-23 05:00:00'),
  ('410C',45, 'Zurich', 'Vienna', 742, 53,'2018-07-24 11:00:00' , '2018-07-24 18:00:00'),
  ('410C',45, 'Zurich', 'Vienna', 742, 53,'2018-07-26 23:00:00' , '2018-07-27 05:00:00'),
  ('410C',45, 'Zurich', 'Vienna', 742, 53,'2018-08-01 09:00:00' , '2018-08-01 16:00:00'),
  ('410C',45, 'Zurich', 'Vienna', 742, 53,'2018-08-03 10:00:00' , '2018-08-03 17:00:00');

INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('400D',35, 'Vienna', 'Salzburg', 295, 53,'2018-07-21 08:00:00' , '2018-07-21 11:30:00'),
  ('400D',35, 'Vienna', 'Salzburg', 295, 53,'2018-07-23 10:00:00' , '2018-07-23 12:30:00'),
  ('400D',35, 'Vienna', 'Salzburg', 295, 53,'2018-07-24 12:00:00' , '2018-07-24 14:30:00'),
  ('400D',35, 'Vienna', 'Salzburg', 295, 53,'2018-07-28 18:00:00' , '2018-07-28 20:30:00'),
  ('400D',35, 'Vienna', 'Salzburg', 295, 53,'2018-07-31 15:00:00' , '2018-07-31 17:30:00'),

  ('410D',35, 'Salzburg', 'Vienna', 295, 53,'2018-07-21 05:00:00' , '2018-07-21 08:30:00'),
  ('410D',35, 'Salzburg', 'Vienna', 295, 53,'2018-07-22 08:00:00' , '2018-07-22 11:30:00'),
  ('410D',35, 'Salzburg', 'Vienna', 295, 53,'2018-07-24 17:00:00' , '2018-07-24 12:30:00'),
  ('410D',35, 'Salzburg', 'Vienna', 295, 53,'2018-07-25 12:00:00' , '2018-07-25 14:30:00'),
  ('410D',35, 'Salzburg', 'Vienna', 295, 53,'2018-07-27 18:00:00' , '2018-07-27 20:30:00'),
  ('410D',35, 'Salzburg', 'Vienna', 295, 53,'2018-07-31 15:00:00' , '2018-07-31 17:30:00');


INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('420D',35, 'Salzburg', 'Zurich', 450, 53,'2018-07-21 12:00:00' , '2018-07-21 17:00:00'),
  ('420D',35, 'Salzburg', 'Zurich', 450, 53,'2018-07-23 17:00:00' , '2018-07-23 22:00:00'),
  ('420D',35, 'Salzburg', 'Zurich', 450, 53,'2018-07-24 14:00:00' , '2018-07-24 19:00:00'),
  ('420D',35, 'Salzburg', 'Zurich', 450, 53,'2018-07-27 18:00:00' , '2018-07-27 20:30:00'),
  ('420D',35, 'Salzburg', 'Zurich', 450, 53,'2018-07-31 15:00:00' , '2018-07-31 17:30:00'),

  ('420D',35, 'Zurich', 'Salzburg', 450, 53,'2018-07-21 23:00:00' , '2018-07-22 04:00:00'),
  ('420D',35, 'Zurich', 'Salzburg', 450, 53,'2018-07-24 09:00:00' , '2018-07-24 16:30:00'),
  ('420D',35, 'Zurich', 'Salzburg', 450, 53,'2018-07-25 12:00:00' , '2018-07-24 14:30:00'),
  ('420D',35, 'Zurich', 'Salzburg', 450, 53,'2018-07-28 18:00:00' , '2018-07-28 20:30:00'),
  ('420D',35, 'Zurich', 'Salzburg', 450, 53,'2018-07-31 15:00:00' , '2018-07-31 17:30:00');

INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('450C',40, 'Vienna', 'Graz', 200, 53,'2018-07-21 08:00:00' , '2018-07-21 10:00:00'),
  ('450C',40, 'Vienna', 'Graz', 200, 53,'2018-07-23 08:00:00' , '2018-07-23 10:00:00'),
  ('450C',40, 'Vienna', 'Graz', 200, 53,'2018-07-25 15:00:00' , '2018-07-25 17:00:00'),
  ('450C',40, 'Vienna', 'Graz', 200, 53,'2018-07-30 18:00:00' , '2018-07-31 20:00:00'),
  ('450C',40, 'Vienna', 'Graz', 200, 53,'2018-07-31 19:00:00' , '2018-08-01 21:00:00'),
  ('450C',31, 'Vienna', 'Graz', 200, 53,'2018-09-21 19:00:00' , '2018-09-21 21:00:00'),
  // back route
  ('460C',45, 'Graz', 'Vienna', 200, 53,'2018-07-21 02:00:00' , '2018-07-21 04:00:00'),
  ('460C',45, 'Graz', 'Vienna', 200, 53,'2018-07-21 06:30:00' , '2018-07-21 07:30:00'),
  ('460C',45, 'Graz', 'Vienna', 200, 53,'2018-07-24 09:00:00' , '2018-07-24 10:30:00'),
  ('460C',45, 'Graz', 'Vienna', 200, 53,'2018-07-26 23:00:00' , '2018-07-27 01:00:00'),
  ('460C',31, 'Graz', 'Vienna', 200, 53,'2018-09-26 23:00:00' , '2018-09-27 01:00:00'),
  ('460C',45, 'Graz', 'Vienna', 200, 53,'2018-08-01 09:00:00' , '2018-08-01 11:00:00');

INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('470C',100, 'Graz', 'Geneva', 1000, 53,'2018-07-21 11:00:00' , '2018-07-21 21:00:00'),
  ('470C',100, 'Graz', 'Geneva', 1000, 53,'2018-07-23 11:00:00' , '2018-07-23 21:00:00'),
  ('470C',110, 'Graz', 'Geneva', 1000, 53,'2018-07-26 23:00:00' , '2018-07-27 09:00:00'),
  ('470C',100, 'Graz', 'Geneva', 1000, 53,'2018-08-01 09:00:00' , '2018-08-01 19:00:00'),
  ('470C',100, 'Graz', 'Geneva', 1000, 53,'2018-08-03 10:00:00' , '2018-08-03 20:00:00'),

  ('471C',100, 'Geneva', 'Graz', 1000, 53,'2018-07-20 19:00:00' , '2018-07-21 05:00:00'),
  ('471C',100, 'Geneva', 'Graz', 1000, 53,'2018-07-23 01:00:00' , '2018-07-23 10:30:00'),
  ('471C',110, 'Geneva', 'Graz', 1000, 53,'2018-07-27 23:00:00' , '2018-07-28 09:00:00'),
  ('471C',100, 'Geneva', 'Graz', 1000, 53,'2018-08-02 09:00:00' , '2018-08-02 19:00:00'),
  ('471C',100, 'Geneva', 'Graz', 1000, 53,'2018-08-03 10:00:00' , '2018-08-03 20:00:00');

INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('470D',100, 'Zurich', 'Geneva', 280, 53,'2018-07-21 17:30:00' , '2018-07-21 20:30:00'),
  ('470D',100, 'Zurich', 'Geneva', 280, 53,'2018-07-23 11:00:00' , '2018-07-23 14:00:00'),
  ('470D',110, 'Zurich', 'Geneva', 280, 53,'2018-07-26 23:00:00' , '2018-07-27 02:00:00'),
  ('470D',100, 'Zurich', 'Geneva', 280, 53,'2018-08-01 09:00:00' , '2018-08-01 12:00:00'),
  ('470D',100, 'Zurich', 'Geneva', 280, 53,'2018-08-03 10:00:00' , '2018-08-03 13:00:00'),

  ('471D',100, 'Geneva', 'Zurich', 280, 53,'2018-07-22 19:00:00' , '2018-07-22 22:00:00'),
  ('471D',100, 'Geneva', 'Zurich', 280, 53,'2018-07-24 05:00:00' , '2018-07-24 08:00:00'),
  ('471D',110, 'Geneva', 'Zurich', 280, 53,'2018-07-27 23:00:00' , '2018-07-28 02:00:00'),
  ('471D',100, 'Geneva', 'Zurich', 280, 53,'2018-08-02 09:00:00' , '2018-08-02 12:00:00'),
  ('471D',100, 'Geneva', 'Zurich', 280, 53,'2018-08-03 10:00:00' , '2018-08-03 13:00:00');

INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
  ('425C',50, 'Graz', 'Ljubljana', 200, 53,'2018-07-21 11:00:00' , '2018-07-21 13:00:00'),
  ('425C',50, 'Graz', 'Ljubljana', 200, 53,'2018-07-23 11:00:00' , '2018-07-23 13:00:00'),
  ('425C',50, 'Graz', 'Ljubljana', 200, 53,'2018-07-25 20:00:00' , '2018-07-25 22:00:00'),
  ('425C',50, 'Graz', 'Ljubljana', 200, 53,'2018-07-28 10:00:00' , '2018-07-28 12:00:00'),
  ('425C',50, 'Graz', 'Ljubljana', 200, 53,'2018-08-03 10:00:00' , '2018-08-03 12:00:00'),

  ('430C',55, 'Ljubljana', 'Graz', 200, 53,'2018-07-20 19:00:00' , '2018-07-20 21:00:00'),
  ('430C',55, 'Ljubljana', 'Graz', 200, 53,'2018-07-23 06:00:00' , '2018-07-23 08:30:00'),
  ('430C',55, 'Ljubljana', 'Graz', 200, 53,'2018-07-26 20:00:00' , '2018-07-26 22:00:00'),
  ('430C',55, 'Ljubljana', 'Graz', 200, 53,'2018-08-02 09:00:00' , '2018-08-02 11:00:00'),
  ('430C',55, 'Ljubljana', 'Graz', 200, 53,'2018-08-03 07:00:00' , '2018-08-03 09:00:00');

 INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
   ('500D',80, 'Geneva', 'Cologne', 740, 53,'2018-07-21 09:00:00' , '2018-07-21 16:30:00'),
   ('500D',80, 'Cologne', 'Geneva', 740, 53,'2018-07-22 09:00:00' , '2018-07-22 16:30:00');

 INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
   ('500D',110, 'Ljubljana', 'Cologne', 985, 53,'2018-07-21 09:00:00' , '2018-07-21 16:30:00'),
   ('500D',120, 'Cologne', 'Ljubljana', 985, 53,'2018-07-23 09:00:00' , '2018-07-22 16:30:00'),
   ('500D',120, 'Ljubljana', 'Cologne', 985, 53,'2018-07-20 09:00:00' , '2018-07-22 16:30:00'),
   ('500D',120, 'Cologne', 'Ljubljana', 985, 53,'2018-07-22 09:00:00' , '2018-07-22 16:30:00');

  INSERT INTO Route (bus_number,price,start_destination,end_destination,distance,capacity,departure_time,arrival_time) VALUES
    ('120A',40, 'Vienna', 'Berlin', 681, 53,'2018-08-01 06:00:00' , '2018-08-01 17:00:00'),
    ('130A',60, 'Vienna', 'Berlin', 681, 53,'2018-08-02 09:00:00' , '2018-08-02 22:00:00'),
    ('120A',40, 'Vienna', 'Berlin', 681, 53,'2018-08-03 07:00:00' , '2018-08-03 17:00:00'),
    ('120A',40, 'Vienna', 'Berlin', 681, 53,'2018-08-04 09:00:00' , '2018-08-04 22:00:00'),
    ('130A',60, 'Vienna', 'Berlin', 681, 53,'2018-08-05 07:00:00' , '2018-08-05 17:00:00'),
    ('120A',56,'Vienna','Berlin',681,53,'2018-08-6 06:00:00','2018-08-7 17:00:00'),
    ('120A',66,'Vienna','Berlin',681,53,'2018-08-7 06:00:00','2018-08-8 17:00:00'),
    ('120A',60,'Vienna','Berlin',681,53,'2018-08-8 06:00:00','2018-08-9 17:00:00'),
    ('120A',44,'Vienna','Berlin',681,53,'2018-08-9 06:00:00','2018-08-10 17:00:00'),
    ('120A',57,'Vienna','Berlin',681,53,'2018-08-10 06:00:00','2018-08-10 17:00:00'),
    ('120A',73,'Vienna','Berlin',681,53,'2018-08-11 06:00:00','2018-08-11 17:00:00'),
    ('120A',58,'Vienna','Berlin',681,53,'2018-08-12 06:00:00','2018-08-12 17:00:00'),
    ('120A',57,'Vienna','Berlin',681,53,'2018-08-13 06:00:00','2018-08-13 17:00:00'),
    ('120A',73,'Vienna','Berlin',681,53,'2018-08-14 06:00:00','2018-08-14 17:00:00'),
    ('120A',60,'Vienna','Berlin',681,53,'2018-08-15 06:00:00','2018-08-15 17:00:00'),
    ('120A',68,'Vienna','Berlin',681,53,'2018-08-16 06:00:00','2018-08-16 17:00:00'),
    ('120A',51,'Vienna','Berlin',681,53,'2018-08-17 06:00:00','2018-08-17 17:00:00'),
    ('120A',45,'Vienna','Berlin',681,53,'2018-08-18 06:00:00','2018-08-18 17:00:00'),
    ('120A',32,'Vienna','Berlin',681,53,'2018-08-19 06:00:00','2018-08-19 17:00:00'),
    ('120A',45,'Vienna','Berlin',681,53,'2018-08-20 06:00:00','2018-08-20 17:00:00'),
    ('120A',55,'Vienna','Berlin',681,53,'2018-08-21 06:00:00','2018-08-21 17:00:00'),
    ('120A',57,'Vienna','Berlin',681,53,'2018-08-22 06:00:00','2018-08-22 17:00:00'),
    ('120A',42,'Vienna','Berlin',681,53,'2018-08-23 06:00:00','2018-08-23 17:00:00'),
    ('120A',63,'Vienna','Berlin',681,53,'2018-08-24 06:00:00','2018-08-24 17:00:00'),
    ('120A',69,'Vienna','Berlin',681,53,'2018-08-25 06:00:00','2018-08-25 17:00:00'),
    ('120A',47,'Vienna','Berlin',681,53,'2018-08-26 06:00:00','2018-08-26 17:00:00'),
    ('120A',76,'Vienna','Berlin',681,53,'2018-08-27 06:00:00','2018-08-27 17:00:00'),
    ('120A',56,'Vienna','Berlin',681,53,'2018-08-28 06:00:00','2018-08-28 17:00:00'),
    ('120A',58,'Vienna','Berlin',681,53,'2018-08-29 06:00:00','2018-08-29 17:00:00'),
    ('120A',77,'Vienna','Berlin',681,53,'2018-08-30 06:00:00','2018-08-30 17:00:00'),
    ('120A',58,'Vienna','Berlin',681,53,'2018-08-31 06:00:00','2018-08-31 17:00:00'),

   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-07-31 13:00:00' , '2018-07-31 14:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 01:00:00' , '2018-08-01 02:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 02:00:00' , '2018-08-01 03:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 03:00:00' , '2018-08-01 04:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 04:00:00' , '2018-08-01 05:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 05:00:00' , '2018-08-01 06:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 06:00:00' , '2018-08-01 07:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 07:00:00' , '2018-08-01 08:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 08:00:00' , '2018-08-01 09:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 09:00:00' , '2018-08-01 10:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 10:00:00' , '2018-08-01 11:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 11:00:00' , '2018-08-01 12:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 12:00:00' , '2018-08-01 13:40:00'),
   ('270A',15, 'Prague', 'Dresden', 147, 53,'2018-08-01 13:00:00' , '2018-08-01 14:40:00'),
   ('270A',6, 'Prague', 'Dresden', 147, 53,'2018-10-01 11:00:00' , '2018-10-01 12:40:00'),
   ('380A',35, 'Salzburg', 'Zurich', 450, 53,'2018-06-25 20:00:00' , '2018-06-26 04:30:00'),;


/*
  ('120A',22, 'vienna', 'prague', 333, 52, '2018-07-20 06:00:00' , '2018-07-20 10:00:00'),
  ('120A',20, 'prague', 'dresden', 147, 52, '2018-07-20 09:00:00' , '2018-07-20 12:00:00'),
  ('120A',22, 'prague', 'dresden', 147, 52, '2018-07-20 11:00:00' , '2018-07-20 12:00:00'),
  ('120A',20, 'prague', 'dresden', 147, 52, '2018-07-20 11:10:00' , '2018-07-20 12:00:00'),
  ('120A',25, 'dresden', 'berlin', 190, 52, '2018-07-20 12:15:00' , '2018-07-20 15:00:00'),
  //back route
  ('120A',40, 'berlin', 'vienna',681, 52, '2018-07-21 06:00:00' , '2018-07-21 17:00:00'),
  ('120A',20, 'berlin', 'dresden',190,52, '2018-07-21 06:00:00' , '2018-07-21 10:00:00'),
  ('120A',20, 'dresden', 'prague',147,52, '2018-07-21 10:00:00' , '2018-07-21 13:00:00'),
  ('120A',22, 'prague', 'vienna',333, 52, '2018-07-21 13:00:00' , '2018-07-21 17:00:00');

*/
INSERT INTO Booking(time_of_booking, booking_number , status, total_sum) VALUES
  ('2018-06-06 10:37:47.884', 1528490766509, 'COMPLETED', 40.00),
  ('2018-06-08 12:21:55.655', 1528491827420, 'COMPLETED', 300.00),
  ('2018-06-08 14:23:47.25', 1784907555095, 'COMPLETED', 500.00),
  ('2018-06-08 15:07:22.152', 1728591867205, 'COMPLETED', 75.00),
  ('2018-06-09 10:10:29.881', 1984950665096, 'COMPLETED', 540.00),
  ('2018-06-09 11:03:52.78', 1528494444204, 'COMPLETED', 105.00),
  ('2018-06-10 14:30:02.1', 2092548568547, 'COMPLETED', 400.00),
  ('2018-06-10 14:52:21.22', 2568545235625, 'COMPLETED', 60.00),
  ('2018-06-10 15:28:00.258', 1254586595122, 'COMPLETED', 35.00),
  ('2018-06-11 08:07:58.789', 2152458796523, 'COMPLETED', 750.00),
  ('2018-06-11 11:10:05.635', 1258548958795, 'COMPLETED', 75.00),
  ('2018-06-11 14:05:23.78', 3256985472544, 'COMPLETED', 70.00),
  ('2018-06-11 18:58:12.522', 2658965214777, 'COMPLETED', 100.00),
  ('2018-06-17 14:30:25.125', 2856551124855, 'COMPLETED', 35.00);

INSERT INTO Customer(name, last_name, social_number, email, birthday) VALUES
  ('Alice', 'Baker', '10081996A', 'alice_baker@yahoo.com', '1996-08-10'),
  ('Bart', 'Simpson', '17082996A', 'simpson.b@gmail.com', '1979-04-01'),
  ('Sebastian', 'Mayer', '04109965422', 'mayer1@gmail.com', '1993-09-01'),
  ('Marco', 'Stephens', '1204A66A8', 'msteph@icloud.com', '1977-03-17'),
  ('Sophie', 'Williams', '08108871050', 'smw_sophie@yahoo.com', '1887-10-08'),
  ('Emma', 'Pichler', '19855CB5', 'lol.ep@gmail.com', '1983-04-17'),
  ('Harry', 'Gordon', '00212155C5', 'dragonboy@gmail.com', '1998-06-21'),
  ('Gloria', 'Clarke', '32A558888', 'clark_gloria55.b@yahoo.com', '1991-12-28'),
  ('Anna', 'Wright', '290485B6666', 'annawright@icloud.com', '1985-04-29'),
  ('Clair', 'Underwood', '1112651060', 'cu_washington@hotmail.com', '1966-06-04'),
  ('Frank', 'Hall', '0105887A666', 'francis_hall_3@gmail.com', '1962-08-12'),
  ('Stefan', 'Schulz', '3009962BB2', 'schulzs@hotmail.com', '1996-10-04'),
  ('Luke', 'Becker', '0202988A599', 'lukeeeebecker@yahoo.com', '1988-11-02'),
  ('Han', 'Solo', '191011156A', 'sw_hansolo@gmail.com', '1942-07-13'),
  ('Marry', 'Robinson', '0808955A55A', 'poppins_lady@icloud.com', '1994-03-03'),
  ('William', 'Simon', '3122565A55', 'simon.will99@gmail.com', '1975-05-23'),
  ('Julia', 'Weber', '01012001BAB5', 'juliew@yahoo.com', '1982-09-17'),
  ('Matthias', 'Kieffer', 'A125A555', 'kieffermann@gmail.com', '1992-07-01'),
  ('Jacob', 'Ritter', '040455578D', 'cobja_star@yahoo.com', '1997-06-23'),
  ('Sophie', 'Fischer', '2606991B705', 'sopieschoice.b@icloud.com', '1985-12-17'),
  ('Jan', 'Mayr', '16558A55A', 'mayr_jan@icloud.com', '1966-02-14'),
  ('Nicolas', 'Schneider', '026658B444', 'nickschneider.m@gmail.com', '1999-11-26');


INSERT INTO BookingEntry(bid, sid, rid, name, surname) VALUES
  (1, 24, 4, 'Max', 'Mustermann'),
  (2, 32, 4, 'Tina', 'Musterfrau'),
  (2, 31, 4, 'Mark', 'Mustermann'),
  (2, 40, 14, 'Tina', 'Musterfrau'),
  (2, 39, 14, 'Mark', 'Mustermann'),
  (2, 51, 20, 'Tina', 'Musterfrau'),
  (2, 52, 20, 'Mark', 'Mustermann'),
  (2, 32, 10, 'Tina', 'Musterfrau'),
  (2, 31, 10, 'Mark', 'Mustermann'),
  (3, 21, 10, 'Alice', 'Baker'),
  (3, 14, 10, 'Stephan','Markson'),
  (3, 13, 10, 'Kim', 'Chang'),
  (3, 17, 10, 'Lisa', 'Simpson'),
  (3, 18, 10, 'Bart', 'Simpson'),
  (3, 1, 4, 'Alice', 'Baker'),
  (3, 2, 4, 'Stephan', 'Markson'),
  (3, 3, 4, 'Kim', 'Chang'),
  (3, 4, 4, 'Lisa', 'Simpson'),
  (3, 8, 4, 'Bart', 'Simpson'),
  (4, 15, 61, 'Mike', 'Brown'),
  (5, 32, 102, 'Lily', 'May'),
  (5, 31, 102, 'Tom', 'Taylor'),
  (5, 15, 110, 'Lily', 'May'),
  (5, 16, 110, 'Tom', 'Taylor'),
  (5, 32, 133, 'Lily', 'May'),
  (5, 31, 133, 'Tom', 'Taylor'),
  (5, 15, 138, 'Lily', 'May'),
  (5, 16, 138, 'Tom', 'Taylor'),
  (6, 52, 142, 'Fabian', 'Mayer'),
  (6, 3, 150, 'Fabian', 'Mayer'),
  (7, 13, 133, 'Clint', 'Eastwood'),
  (7, 14, 133, 'Mary', 'Poppins'),
  (7, 15, 133, 'Gordon', 'Clive'),
  (7, 16, 133, 'Stephi', 'Grant'),
  (8, 37, 10, 'Jack', 'Jones'),
  (9, 17, 21, 'Tom', 'Taylor'),
  (9, 17, 29, 'Lily', 'May'),
  (10, 33, 73, 'Anja', 'Person'),
  (10, 34, 73, 'David', 'Garret'),
  (10, 37, 73, 'Mick', 'Jagger'),
  (10, 13, 80, 'Anja', 'Person'),
  (10, 14, 80, 'David', 'Garret'),
  (10, 15, 80, 'Mick', 'Jagger'),
  (10, 1, 5, 'Anja', 'Person'),
  (10, 2, 5, 'David', 'Garret'),
  (10, 3, 5, 'Mick', 'Jagger'),
  (10, 13, 9, 'Anja', 'Person'),
  (10, 14, 9, 'David', 'Garret'),
  (10, 15, 9, 'Mick', 'Jagger'),
  (11, 19, 62, 'Jack', 'Jones'),
  (12, 53, 102, 'Jakob', 'Salzer'),
  (12, 55, 102, 'Kobe', 'Bryant'),
  (13, 8, 5, 'Alan', 'Paris'),
  (13, 19, 9, 'Alan', 'Paris'),
  (14,28,208, 'Emma', 'Pichler');