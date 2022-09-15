select 25
, +6.34
, 0.5
, 25e-03
, -1 -- Here are some valid floating-point number literals:
, 25f
, +6.34F
, 0.5d
, -1D
, (sysdate -1d)   -- here we substract "one" in decimal format
, sysdate -1m   -- here we substract "one" and "m" is column's alias
, sysdate -1dm
from dual

