CREATE TABLE druid_const (
	id bigint(20) AUTO_INCREMENT NOT NULL,  
	domain varchar(45) NOT NULL, 
	app varchar(45) NOT NULL, 
	type varchar(45) NOT NULL,
	hash bigint(20) NOT NULL,
	value text,
	PRIMARY KEY (id),
	UNIQUE (domain, app, type, hash)
)