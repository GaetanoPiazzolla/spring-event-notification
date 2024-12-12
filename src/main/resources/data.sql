insert into books (id, title, description, database_version) values (1, 'Spring Boot Basics', 'Introduction to Spring Boot', 0);
insert into books (id, title, description, database_version) values (2, 'Advanced Spring Boot', 'Deep dive into Spring Boot features', 0);
insert into books (id, title, description, database_version) values (3, 'Spring Boot Security', 'Implementing security in Spring Boot', 0);

insert into books (id, title, description, database_version) values (4, 'Spring Boot Testing', 'Testing Spring Boot applications', 0);
insert into books (id, title, description, database_version) values (5, 'Spring Boot with Kotlin', 'Using Kotlin with Spring Boot', 0);
insert into books (id, title, description, database_version) values (6, 'Spring Boot Microservices', 'Building microservices with Spring Boot', 0);

ALTER TABLE books ALTER COLUMN id RESTART WITH 8;
