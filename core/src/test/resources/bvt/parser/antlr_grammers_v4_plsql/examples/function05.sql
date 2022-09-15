select count(*)
  from employees
  where lnnvl(commission_pct >= .2)
