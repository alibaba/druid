INSERT INTO druid_sql (domain, app, cluster, host, pid
	, collectTime, sqlHash, dataSource, lastStartTime, batchTotal
	, batchToMax, execSuccessCount, execNanoTotal, execNanoMax, running
	, concurrentMax, rsHoldTime, execRsHoldTime, name, file
	, dbType, execNanoMaxOccurTime, errorCount, errorLastMsg, errorLastClass
	, errorLastStackTrace, errorLastTime, updateCount, updateCountMax, fetchRowCount
	, fetchRowCountMax, inTxnCount, lastSlowParameters, clobOpenCount, blobOpenCount
	, readStringLength, readBytesLength, inputStreamOpenCount, readerOpenCount, h1
	, h10, h100, h1000, h10000, h100000
	, h1000000, hmore, eh1, eh10, eh100
	, eh1000, eh10000, eh100000, eh1000000, ehmore
	, f1, f10, f100, f1000, f10000
	, fmore, u1, u10, u100, u1000
	, u10000, umore)
VALUES (?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?, ?, ?, ?
	, ?, ?)
CREATE TABLE druid_sql (
	id bigint(20) NOT NULL, 
	domain varchar(45), 
	app varchar(45), 
	cluster varchar(45), 
	host varchar(128), 
	pid int(10), 
	collectTime datetime NOT NULL, 
	sqlHash bigint(20), 
	dataSource varchar(256), 
	lastStartTime bigint(20), 
	batchTotal bigint(20), 
	batchToMax int(10), 
	execSuccessCount bigint(20), 
	execNanoTotal bigint(20), 
	execNanoMax bigint(20), 
	running int(10), 
	concurrentMax int(10), 
	rsHoldTime bigint(20), 
	execRsHoldTime bigint(20), 
	name varchar(256), 
	file varchar(256), 
	dbType varchar(256), 
	execNanoMaxOccurTime bigint(20), 
	errorCount bigint(20), 
	errorLastMsg varchar(256), 
	errorLastClass varchar(256), 
	errorLastStackTrace varchar(256), 
	errorLastTime bigint(20), 
	updateCount bigint(20), 
	updateCountMax bigint(20), 
	fetchRowCount bigint(20), 
	fetchRowCountMax bigint(20), 
	inTxnCount bigint(20), 
	lastSlowParameters varchar(256), 
	clobOpenCount bigint(20), 
	blobOpenCount bigint(20), 
	readStringLength bigint(20), 
	readBytesLength bigint(20), 
	inputStreamOpenCount bigint(20), 
	readerOpenCount bigint(20), 
	h1 bigint(20), 
	h10 bigint(20), 
	h100 int(10), 
	h1000 int(10), 
	h10000 int(10), 
	h100000 int(10), 
	h1000000 int(10), 
	hmore int(10), 
	eh1 bigint(20), 
	eh10 bigint(20), 
	eh100 int(10), 
	eh1000 int(10), 
	eh10000 int(10), 
	eh100000 int(10), 
	eh1000000 int(10), 
	ehmore int(10), 
	f1 bigint(20), 
	f10 bigint(20), 
	f100 bigint(20), 
	f1000 int(10), 
	f10000 int(10), 
	fmore int(10), 
	u1 bigint(20), 
	u10 bigint(20), 
	u100 bigint(20), 
	u1000 int(10), 
	u10000 int(10), 
	umore int(10), 
	PRIMARY KEY (id)
)
