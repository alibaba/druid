with o(obj,link) as
(
select 'a', 'b' from dual union all
select 'a', 'c' from dual union all
select      'c', 'd' from dual union all
select           'd', 'c' from dual union all
select           'd', 'e' from dual union all
select                'e', 'e' from dual
),
t(root,lev,obj,link,path) as (
select obj,1,obj,link,cast(obj||'->'||link 
as varchar2(4000))
from o 
where obj='a'  -- start with
union all
select 
  t.root,t.lev+1,o.obj,o.link,
  t.path||', '||o.obj||
    '->'
    ||o.link
from t, o 
where t.link=o.obj
)
search depth first by obj set ord
cycle obj set cycle to 1 default 0
select root,lev,obj,link,path,cycle,
    case
    when (lev - lead(lev) over (order by ord)) < 0
    then 0
    else 1
    end is_leaf
 from t

