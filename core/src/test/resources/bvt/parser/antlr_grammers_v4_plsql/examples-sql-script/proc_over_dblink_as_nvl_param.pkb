create or replace package body pack as

procedure test(inId number)
as
	vSum number;
	vPaymentsDate number;
begin
	
	vSum := nvl(pack_1.getSum@dblink(inId=>inId, vPaymentsDate => pack_2.getLastDay(4, 3)), 0);  

end;


end;