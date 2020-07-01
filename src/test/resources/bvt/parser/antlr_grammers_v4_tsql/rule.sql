CREATE RULE range_rule  
AS   
@range>= $1000 AND @range <$20000;  
CREATE RULE list_rule
AS
@list IN ('1389', '0736', '0877');
CREATE RULE pattern_rule
AS
@value LIKE '__-%[0-9]'
