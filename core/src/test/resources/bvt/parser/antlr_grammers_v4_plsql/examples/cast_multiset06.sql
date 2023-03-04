select
	cast(collect(cattr(aname, op, to_char(val), support, confidence)) as cattrs) cl_attrs
from a

