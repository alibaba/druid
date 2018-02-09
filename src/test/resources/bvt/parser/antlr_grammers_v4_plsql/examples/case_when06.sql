SELECT 
case
	when STaLENESS > .5 then 128
	when STaLENESS > .1 then 256
	else 0
	end  aFLaGS
	FROM employees
	