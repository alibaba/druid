select su.ttype ,su.cid ,su.s_id ,sessiontimezone
from sku su
where (nvl(su.up,'n')='n' and su.ttype=:b0)
for update of su.up
order by su.d
