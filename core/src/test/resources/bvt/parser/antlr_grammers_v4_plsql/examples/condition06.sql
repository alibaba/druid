 select *
from  t1, t2
where (trunc(t1.timestamp) between to_date('110226','yymmdd') and to_date('110326','yymmdd'))
and t1.code(+) = 'cn'
and t1.id(+)=t2.id
and t1.cid=t2.cid
and t1.mid = 1245714070376993504
and t1.tmst >= to_date('110226','yymmdd')
-- note: this is possible too "column_spec outer_join_sign conditional_operator
and shipper.alt_party_code(+) is null
and t2.code(+) = 'sh'
and t1.sid(+)=t2.sid
and ( ( t1.scode like 'mmm'  and t2.scode like 'xax' ) )

