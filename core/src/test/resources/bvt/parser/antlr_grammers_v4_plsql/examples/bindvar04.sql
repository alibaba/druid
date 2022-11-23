select *
from 
(
    select *
    from "rme" "rm" 
    where "rm".a-interval:"sys_b_07" day(:"sys_b_08") to second(:"sys_b_09")
)
