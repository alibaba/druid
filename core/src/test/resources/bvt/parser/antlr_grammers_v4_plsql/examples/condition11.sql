select distinct X
from X,Y,Z
where
    X.id = Z.id (+) 
and nvl(X.cid, '^') = nvl(Y.clientid (+), '^') 
and 0 = Lib.SKU(X.sid, nvl(Z.cid, '^')) 

