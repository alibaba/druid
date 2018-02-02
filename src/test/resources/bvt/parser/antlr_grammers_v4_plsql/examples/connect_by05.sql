with liste as (
  select substr(:liste, instr(','||:liste||',', ',', 1, rn),
  instr(','||:liste||',', ',', 1, rn+1) -
  instr(','||:liste||',', ',', 1, rn)-1) valeur
from (
  select rownum rn from dual
  connect by level<=length(:liste) - length(replace(:liste,',',''))+1))
select trim(valeur)
from liste
