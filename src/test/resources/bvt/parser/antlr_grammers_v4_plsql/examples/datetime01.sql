select from_tz(cast(to_date('1999-12-01 11:00:00','yyyy-mm-dd hh:mi:ss') as timestamp), 'america/new_york') at time zone 'america/los_angeles' "west coast time" from dual
