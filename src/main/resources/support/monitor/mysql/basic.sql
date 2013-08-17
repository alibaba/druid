create table druid_domain (
	id bigint(20) AUTO_INCREMENT NOT NULL,
	domain varchar(45) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (domain)
);

insert into druid_domain (domain) values ('default');

create table druid_app (
	id bigint(20) AUTO_INCREMENT NOT NULL,
	domain varchar(45) NOT NULL,
	app varchar(45) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (domain, app)		  
);

insert into druid_app (domain, app) values ('default', 'default');

create table druid_cluster (
	id bigint(20) AUTO_INCREMENT NOT NULL,
	domain varchar(45) NOT NULL,
	app varchar(45) NOT NULL,
	cluster varchar(45) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (domain, app, cluster)		  
);

insert into druid_cluster (domain, app, cluster) values ('default', 'default', 'default');

create table druid_inst (
	id bigint(20) AUTO_INCREMENT NOT NULL,
	app varchar(45) NOT NULL,
	domain varchar(45) NOT NULL,
	cluster varchar(45) NOT NULL,
	host varchar(128) NOT NULL,
	ip varchar(32) NOT NULL,
	lastActiveTime datetime NOT NULL,
	lastPID bigint(20) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (domain, app, cluster, host)		  
);