CREATE TABLE druid_wall(
	id bigint(20) NOT NULL AUTO_INCREMENT, 
	domain varchar(45)  NOT NULL, 
	app varchar(45)  NOT NULL, 
	cluster varchar(45)  NOT NULL, 
	host varchar(128), 
	pid int(10)  NOT NULL, 
	collectTime datetime NOT NULL, 
	name varchar(256), 
	checkCount bigint(20), 
	hardCheckCount bigint(20), 
	violationCount bigint(20), 
	whiteListHitCount bigint(20), 
	blackListHitCount bigint(20), 
	syntaxErrorCount bigint(20), 
	violationEffectRowCount bigint(20), 
	PRIMARY KEY(id)
);
CREATE INDEX druid_wall_index ON druid_wall (collectTime, domain, app);

CREATE TABLE druid_wall_sql(
	id bigint(20) NOT NULL AUTO_INCREMENT, 
	domain varchar(45)  NOT NULL, 
	app varchar(45)  NOT NULL, 
	cluster varchar(45)  NOT NULL, 
	host varchar(128), pid int(10)  NOT NULL, 
	collectTime datetime NOT NULL, 
	sqlHash bigint(20), 
	sqlSHash bigint(20), 
	sqlSampleHash bigint(20), 
	executeCount bigint(20), 
	fetchRowCount bigint(20), 
	updateCount bigint(20), 
	syntaxError int(1), 
	violationMessage varchar(256), 
	PRIMARY KEY(id)
);
CREATE INDEX druid_wall_sql_index ON druid_wall_sql (collectTime, domain, app);

CREATE TABLE druid_wall_table(
	id bigint(20) NOT NULL AUTO_INCREMENT, 
	domain varchar(45)  NOT NULL, 
	app varchar(45)  NOT NULL, 
	cluster varchar(45)  NOT NULL, 
	host varchar(128), 
	pid int(10)  NOT NULL, 
	collectTime datetime NOT NULL, 
	name varchar(256), 
	selectCount bigint(20), 
	selectIntoCount bigint(20), 
	insertCount bigint(20), 
	updateCount bigint(20), 
	deleteCount bigint(20), 
	truncateCount bigint(20), 
	createCount bigint(20), 
	alterCount bigint(20), 
	dropCount bigint(20), 
	replaceCount bigint(20), 
	deleteDataCount bigint(20), 
	updateDataCount bigint(20), 
	insertDataCount bigint(20), 
	fetchRowCount bigint(20), 
	PRIMARY KEY(id)
);
CREATE INDEX druid_wall_table_index ON druid_wall_table (collectTime, domain, app);

CREATE TABLE druid_wall_function(
	id bigint(20) NOT NULL AUTO_INCREMENT, 
	domain varchar(45)  NOT NULL, 
	app varchar(45)  NOT NULL, 
	cluster varchar(45)  NOT NULL, 
	host varchar(128), 
	pid int(10)  NOT NULL, 
	collectTime datetime NOT NULL, 
	name varchar(256), 
	invokeCount bigint(20), 
	PRIMARY KEY(id)
);
CREATE INDEX druid_wall_function_index ON druid_wall_function (collectTime, domain, app);