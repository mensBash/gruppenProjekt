//  CREATE SEQUENCE IF NOT EXISTS seq_busNumber NO CYCLE MINVALUE 1000 INCREMENT BY 1;

DROP TABLE Route;
DROP TABLE Cities;
DROP TABLE Booking;
DROP TABLE BookingEntry;
DROP TABLE Customer;


CREATE TABLE IF NOT EXISTS Cities(
  name VARCHAR (128) NOT NULL PRIMARY KEY,
  country VARCHAR(128) NOT NULL,
  blind_booking BOOLEAN DEFAULT FALSE,
  picture VARCHAR(128)
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

CREATE TABLE IF NOT EXISTS Customer(
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR (48) NOT NULL,
last_name VARCHAR(48) NOT NULL,
social_number VARCHAR (64) NOT NULL UNIQUE,
email VARCHAR (128) NOT NULL,
birthday DATE NOT NULL
);


/* Format: YYYY-MM-DD hh:mm:ss
   Insert into Route only if where not exist select * from route, what means table route is empty
 */

