select decode(decode(decode( (select count(1) from dual), a, 1, 0), 0, 1), 1, 0) from dual
--   select decode(decode(decode(decode(x, 0, 1) , 0, 1), 1, 0 ), 0, 1) from dual
