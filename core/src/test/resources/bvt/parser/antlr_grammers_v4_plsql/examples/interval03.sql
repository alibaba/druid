select
 interval '4 5:12:10.222' day to second(3)
,interval '4 5:12' day to minute
,interval '400 5' day(3) to hour
,interval '400' day(3)
,interval '11:12:10.2222222' hour to second(7)
,interval '11:20' hour to minute
,interval '10' hour
,interval '10:22' minute to second
,interval '10' minute
,interval '4' day
,interval '25' hour
,interval '40' minute
,interval '120' hour(3)
,interval '30.12345' second(2,4)
,interval :a day

,interval '1' year
,interval '1' month
    
from dual
