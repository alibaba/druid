with rn as (
  select rownum rn
  from dual
  connect by level <= (select max(cases) from t1))
select pname
from t1, rn
where rn <= cases
order by pname
