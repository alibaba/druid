select staleness
, osize, obj#
, type#
, case when row_number() over (partition by bo# order by staleness, osize, obj#) = 1 then 64 else 0 end
  +
  case when row_number() over (partition by (select tcp0.bo# from tabcompart$ tcp0 where tcp0.obj#=st0.bo#) order by staleness, osize, obj#) = 1 then 32
  else 0 end aflags
, 0 status
, :b3 sid
, :b2 serial#
, part#, bo#
from st0