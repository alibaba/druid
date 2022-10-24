select last_name
from employees
where last_name
like '%a\_b%' escape '\'
order by last_name
