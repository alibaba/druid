with col_generator as (
select t1.batch_id, decode(t1.action, 'sent', t1.actdate) sent,
decode(t2.action,'recv', t2.actdate) received
from test t1, test t2
where t2.batch_id(+) = t1.batch_id)
select batch_id, max(sent) sent, max(received) received
from col_generator
group by batch_id
order by 1
