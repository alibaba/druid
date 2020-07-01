select
extractvalue(value(t), '/select_list_item/pos') + 1 pos,
extractvalue(value(t), '/select_list_item/value') res,
extractvalue(value(t), '/select_list_item/nonnulls') nonnulls,
extractvalue(value(t), '/select_list_item/ndv') ndv,
extractvalue(value(t), '/select_list_item/split') split,
extractvalue(value(t), '/select_list_item/rsize') rsize,
extractvalue(value(t), '/select_list_item/rowcnt') rowcnt,
extract(value(t), '/select_list_item/hash_val').getclobval() hashval
from
table
(
	xmlsequence
	(
		extract(:b1 , '/process_result/select_list_item')
	)
) t

