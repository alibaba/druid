select bs.keep keep, bs.keep_until keep_until 
from v$backup_set bs 
union all 
select null keep, null keep_until
from v$backup_piece bp

