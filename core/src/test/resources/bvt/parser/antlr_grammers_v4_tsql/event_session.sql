ALTER EVENT SESSION test_session  
ON SERVER  
STATE = start;  
GO
-- Add new events to the session
ALTER EVENT SESSION test_session ON SERVER
ADD EVENT sqlserver.database_transaction_begin,
ADD EVENT sqlserver.database_transaction_end;
GO
ALTER EVENT SESSION [TrackTempdbFileWrites]
ON SERVER
DROP EVENT sqlserver.file_write_completed,
DROP EVENT sqlserver.file_written
GO
ALTER EVENT SESSION [TrackTempdbFileWrites]
	ON SERVER
	WITH (EVENT_RETENTION_MODE = NO_EVENT_LOSS)
	GO

/* target change */
ALTER EVENT SESSION [AlteredState] ON SERVER
ADD TARGET package0.event_file ( SET filename = N'AlteredState'
, max_file_size = ( 50 )
, max_rollover_files = ( 6 ) );


CREATE EVENT SESSION [BucketizerTargetDemoRecompiles]
ON SERVER
ADD EVENT sqlserver.sql_statement_starting
(    ACTION (sqlserver.database_id) -- database_id to bucket on
),
ADD EVENT sqlserver.sp_statement_starting
(    ACTION (sqlserver.database_id) -- database_id to bucket on
)
ADD TARGET package0.asynchronous_bucketizer
(     SET source_type=1, -- specifies bucketing on Action
         source='sqlserver.database_id' -- Action to bucket on
)
WITH (MAX_DISPATCH_LATENCY = 5 SECONDS)
GO

CREATE EVENT SESSION counter_test ON SERVER
ADD EVENT sqlserver.sql_statement_completed
    (ACTION (sqlserver.sql_text)
    WHERE package0.counter = 2)
ADD TARGET package0.ring_buffer
WITH (MAX_DISPATCH_LATENCY = 1 SECONDS)

-- http://www.sqlteam.com/article/advanced-sql-server-2008-extended-events-with-examples

CREATE EVENT SESSION counter_test ON SERVER
ADD EVENT sqlserver.sql_statement_completed
    (ACTION (sqlserver.sql_text)
    WHERE package0.counter = 2)
ADD TARGET package0.ring_buffer
WITH (MAX_DISPATCH_LATENCY = 1 SECONDS)

CREATE EVENT SESSION counter_test_25 ON SERVER
ADD EVENT sqlserver.sql_statement_completed
    (ACTION (sqlserver.sql_text)
    WHERE package0.divides_by_uint64(package0.counter,4))
ADD TARGET package0.ring_buffer
WITH (MAX_DISPATCH_LATENCY = 1 SECONDS)
GO
CREATE EVENT SESSION RingBufferExampleSession ON SERVER
    ADD EVENT sqlserver.sp_statement_completed
    (
        ACTION (sqlserver.sql_text, sqlserver.tsql_stack, sqlserver.transaction_id,
		sqlserver.database_id, sqlserver.username)
        WHERE sqlserver.database_id = 8
    )
    ADD TARGET package0.ring_buffer
        (SET max_memory = 4096)
WITH (max_dispatch_latency = 1 seconds)
CREATE EVENT SESSION RingBufferExampleSession ON SERVER
    ADD EVENT sqlserver.sql_statement_completed
    (
        ACTION (sqlserver.session_resource_pool_id, sqlserver.sql_text,
		sqlserver.tsql_stack, sqlserver.username)
        WHERE sqlserver.database_id = 8
    )
    ADD TARGET package0.ring_buffer
        (SET max_memory = 4096)
WITH (max_dispatch_latency = 1 seconds)

CREATE EVENT SESSION RingBufferExampleSession ON SERVER
    ADD EVENT sqlserver.page_split
    (
        ACTION (sqlserver.sql_text, sqlserver.tsql_stack, sqlserver.username)
        WHERE sqlserver.database_id = 8
    )
    ADD TARGET package0.ring_buffer
        (SET max_memory = 4096)
WITH (max_dispatch_latency = 1 seconds)

CREATE EVENT SESSION RingBufferExampleSession ON SERVER
    ADD EVENT sqlserver.error_reported
    -- collect failed SQL statement, the SQL stack that led to the error,
    -- the database id in which the error happened and the username that ran the statement
    (
        ACTION (sqlserver.sql_text, sqlserver.tsql_stack, sqlserver.database_id,
			sqlserver.username)
        WHERE sqlserver.database_id = 8
    )
    ADD TARGET package0.ring_buffer
        (SET max_memory = 4096)
WITH (max_dispatch_latency = 1 seconds)
CREATE EVENT SESSION RingBufferExampleSession
ON SERVER
    ADD EVENT sqlserver.lock_deadlock
    (
            ACTION (sqlserver.sql_text, sqlserver.tsql_stack, sqlserver.username,
			sqlserver.session_id, sqlserver.request_id)
            WHERE sqlserver.database_id = 8
    )
    -- this is an optional part if we want to also collect other lock events.
    -- ADD EVENT sqlserver.locks_lock_timeouts
    -- (
    --        ACTION   (sqlserver.sql_text, sqlserver.tsql_stack, sqlserver.username,
    --				sqlserver.session_id, sqlserver.request_id)
    --        WHERE    sqlserver.database_id = 8
    -- ),
    -- ADD EVENT sqlserver.locks_lock_waits
    -- (
    --        ACTION   (sqlserver.sql_text, sqlserver.tsql_stack, sqlserver.username,
    --				sqlserver.session_id, sqlserver.request_id)
    --        WHERE    sqlserver.database_id = 8
    -- )
    ADD TARGET package0.ring_buffer
        (SET max_memory = 4096)
WITH (max_dispatch_latency = 1 seconds)
CREATE EVENT SESSION RingBufferExampleSession
ON SERVER
    ADD EVENT sqlserver.sql_statement_completed
        (
            ACTION (sqlserver.sql_text, sqlserver.tsql_stack, sqlserver.username)
            WHERE sqlserver.database_id = 8
		AND sqlserver.sql_statement_completed.duration > 500000
        )
    ADD TARGET package0.ring_buffer
        (SET max_memory = 4096)
WITH (max_dispatch_latency = 1 seconds)
CREATE EVENT SESSION RingBufferExampleSession ON SERVER
    ADD EVENT sqlserver.long_io_detected
        (
            ACTION (sqlserver.sql_text, sqlserver.tsql_stack, sqlserver.username)
            WHERE sqlserver.database_id = 8 and package0.counter > 10
        )
    ADD TARGET package0.ring_buffer
        (SET max_memory = 4096)
WITH (max_dispatch_latency = 1 seconds)

