select *
from hdr a
inner join sh s
inner join ca c
on c.id = s.id
on a.va = s.va

