select decode((tt || tc), '56', count(distinct cn), '57',  sum(nu)) as q
from t
where tt='500'
  and tc in ('6','7')
  and to_char(c,'mm') = '03'
  having sum(nu) > 0
group by tn, ui, (tt || tc)
order by 1
