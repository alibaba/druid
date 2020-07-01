select *
from append
where
-- note space between '>' and '='
(length(w.numer) > = 8)
