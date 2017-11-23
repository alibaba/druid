 select *
 from (s join d using(c))
 pivot
 (
 max(c_c_p) as max_ccp
 , max(d_c_p) max_dcp
 , max(d_x_p) dxp
 , count(1) cnt
 for (i, p) in
 (
 (1,1) as one_one,
 (1,2) as one_two,
 (1,3) as one_three,
 (2,1) as two_one,
 (2,2) as two_two,
 (2,3) as two_three
 )
 )
 where d_t = 'p'
