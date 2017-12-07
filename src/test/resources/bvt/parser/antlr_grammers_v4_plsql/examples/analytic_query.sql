select time_id, product
   , last_value(quantity ignore nulls) over (partition by product order by time_id) quantity
   , last_value(quantity respect nulls) over (partition by product order by time_id) quantity
   from ( select times.time_id, product, quantity 
             from inventory partition by  (product) 
                right outer join times on (times.time_id = inventory.time_id) 
   where times.time_id between to_date('01/04/01', 'dd/mm/yy') 
      and to_date('06/04/01', 'dd/mm/yy')) 
   order by  2,1;

select times.time_id, product, quantity from inventory 
   partition by  (product) 
   right outer join times on (times.time_id = inventory.time_id) 
   where times.time_id between to_date('01/04/01', 'dd/mm/yy') 
      and to_date('06/04/01', 'dd/mm/yy') 
   order by  2,1;

select deptno
   , ename
   , hiredate
   , listagg(ename, ',') within group (order by hiredate) over (partition by deptno) as employees
from emp;

 select metric_id ,bsln_guid ,timegroup ,obs_value as obs_value 
 , cume_dist () over (partition by metric_id, bsln_guid, timegroup order by obs_value ) as cume_dist 
 , count(1) over (partition by metric_id, bsln_guid, timegroup ) as n 
 , row_number () over (partition by metric_id, bsln_guid, timegroup order by obs_value) as rrank 
 , percentile_disc(:b7 ) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as mid_tail_value 
 , max(obs_value) over (partition by metric_id, bsln_guid, timegroup ) as max_val 
 , min(obs_value) over (partition by metric_id, bsln_guid, timegroup ) as min_val 
 , avg(obs_value) over (partition by metric_id, bsln_guid, timegroup ) as avg_val 
 , stddev(obs_value) over (partition by metric_id, bsln_guid, timegroup ) as sdev_val 
 , percentile_cont(0.25) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_25 
 , percentile_cont(0.5)  within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_50 
 , percentile_cont(0.75) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_75 
 , percentile_cont(0.90) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_90 
 , percentile_cont(0.95) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_95 
 , percentile_cont(0.99) within group (order by obs_value asc) over (partition by metric_id, bsln_guid, timegroup) as pctile_99
 from timegrouped_rawdata d;

select trim(both ' ' from '  a  ') from dual where trim(:a) is not null;

with
clus_tab as (
select id,
a.attribute_name aname,
a.conditional_operator op,
nvl(a.attribute_str_value,
round(decode(a.attribute_name, n.col,
a.attribute_num_value * n.scale + n.shift,
a.attribute_num_value),4)) val,
a.attribute_support support,
a.attribute_confidence confidence
from table(dbms_data_mining.get_model_details_km('km_sh_clus_sample')) t,
table(t.rule.antecedent) a,
km_sh_sample_norm n
where a.attribute_name = n.col (+) and a.attribute_confidence > 0.55
),
clust as (
select id,
cast(collect(cattr(aname, op, to_char(val), support, confidence)) as cattrs) cl_attrs
from clus_tab
group by id
),
custclus as (
select t.cust_id, s.cluster_id, s.probability
from (select
cust_id
, cluster_set(km_sh_clus_sample, null, 0.2 using *) pset
from km_sh_sample_apply_prepared
where cust_id = 101362) t,
table(t.pset) s
)
select a.probability prob, a.cluster_id cl_id,
b.attr, b.op, b.val, b.supp, b.conf
from custclus a,
(select t.id, c.*
from clust t,
table(t.cl_attrs) c) b
where a.cluster_id = b.id
order by prob desc, cl_id asc, conf desc, attr asc, val asc;

