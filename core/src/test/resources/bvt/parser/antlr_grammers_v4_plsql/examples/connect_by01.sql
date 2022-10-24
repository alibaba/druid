with o as
(
	select 'a' obj, 'b' link from dual union all
	select 'a', 'c' from dual union all
	select      'c', 'd' from dual union all
	select           'd', 'c' from dual union all
	select           'd', 'e' from dual union all
	select                'e', 'e' from dual
)
select 
  connect_by_root obj root,
  level,
  obj,link,
  sys_connect_by_path(obj||'->'||link,','),
  connect_by_iscycle,
  connect_by_isleaf
from o
connect by nocycle obj=prior link
start with obj='a'

