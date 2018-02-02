select                                                                                                                                                    
(                                                                                                                                                              
 (                                                                                                                                                             
  select 'y' from dual                                                                                                                                         
  where exists ( select 1 from dual where 1 = 0 )                                                                                                              
 )                                                                                                                                                             
 union                                                                                                                                                         
 (                                                                                                                                                             
  select 'n' from dual                                                                                                                                         
  where not exists ( select 1 from dual where 1 = 0 )                                                                                                          
 )                                                                                                                                                             
)                                                                                                                                                              
as yes_no                                                                                                                                                      
from dual
