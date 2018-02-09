select 
ROOT,LEV,OBJ,LinK,PaTH,cycle,
    case
    when (LEV - LEaD(LEV) over (order by orD)) < 0 then 0
    else 1
    end is_LEaF
from T


