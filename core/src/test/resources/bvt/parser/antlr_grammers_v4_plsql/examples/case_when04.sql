select
case when row_number() over (partition by bo# order by staleness, osize, obj#) = 1 then 32 else 0 end + 64 aflags
from f
