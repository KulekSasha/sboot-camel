DROP TABLE IF EXISTS PERSON;
DROP TABLE IF EXISTS PERSON_ROLE;

create table PERSON_ROLE
  (ROLE_ID identity NOT NULL,
  NAME varchar(50) NOT NULL,

  PRIMARY KEY (ROLE_ID));

create table PERSON
  (PERSON_ID identity NOT NULL,
  ROLE_ID bigint NOT NULL,
  LOGIN varchar(50) NOT NULL,
  PASSWORD varchar(50) NOT NULL,
  EMAIL varchar(50),
  FIRST_NAME varchar(50) NOT NULL,
  LAST_NAME varchar(50) NOT NULL,
  BIRTHDAY date NOT NULL,

  FOREIGN KEY (ROLE_ID) REFERENCES public.PERSON_ROLE(ROLE_ID),
  PRIMARY KEY (PERSON_ID),
  UNIQUE KEY user_login_UNIQUE (LOGIN));

INSERT INTO PERSON_ROLE VALUES
(1, 'ADMIN'),
(2, 'USER');
--
-- INSERT INTO PERSON VALUES
-- (1,1,'testUser_1','testUser_1','testUser_1@gmail.com','Ivan','Ivanov','1986-01-01'),
-- (2,1,'testUser_2','testUser_2','testUser_2@gmail.com','Petr','Petrov','1985-02-02'),
-- (3,2,'testUser_3','testUser_3','testUser_3@gmail.com','Dmitrii','Dmitriev','1984-03-03'),
-- (4,2,'testUser_4','testUser_4','testUser_4@gmail.com','Stas','Mikhailov','1990-04-04'),
-- (5,2,'testUser_5','testUser_5','testUser_5@gmail.com','Oleg','Gazmanov','1980-05-05');
