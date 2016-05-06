package com.alibaba.druid.sql.dialect.teradata.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TeradataIntervalExpr extends SQLExprImpl implements TeradataExpr {
	
	private SQLExpr              value;
	private TeradataIntervalUnit unit;
	
	private TeradataIntervalUnit type;
	private TeradataIntervalUnit toType;
	private Integer              precision;
	private Integer              factionalSecondsPrecision;
	private Integer              toFactionalSecondsPrecision;

	public TeradataIntervalExpr() {
		
	}
	
	public SQLExpr getValue() {
		return this.value;
	}
	
	public void setValue(SQLExpr value) {
		this.value = value;
	}
	
	public TeradataIntervalUnit getUnit() {
		return unit;
	}
	
	public void setUnit(TeradataIntervalUnit unit) {
		this.unit = unit;
	}
	
	public TeradataIntervalUnit getType() {
		return this.type;
	}
	
	public void setType(TeradataIntervalUnit type) {
		this.type = type;
	}
	
	public TeradataIntervalUnit getToType() {
		return this.toType;
	}
	
	public void setToType(TeradataIntervalUnit toType) {
		this.toType = toType;
	}
	
	public Integer getPrecision() {
        return this.precision;
    }
	
	public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getFactionalSecondsPrecision() {
        return this.factionalSecondsPrecision;
    }

    public void setFactionalSecondsPrecision(Integer factionalSecondsPrecision) {
        this.factionalSecondsPrecision = factionalSecondsPrecision;
    }	
    
    public Integer getToFactionalSecondsPrecision() {
        return this.toFactionalSecondsPrecision;
    }

    public void setToFactionalSecondsPrecision(Integer toFactionalSecondsPrecision) {
        this.toFactionalSecondsPrecision = toFactionalSecondsPrecision;
    }
	
	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TeradataIntervalExpr other = (TeradataIntervalExpr) obj;
        if (unit != other.unit) {
            return false;
        }
        if (factionalSecondsPrecision == null) {
            if (other.factionalSecondsPrecision != null) {
                return false;
            }
        } else if (!factionalSecondsPrecision.equals(other.factionalSecondsPrecision)) {
            return false;
        }
        if (precision == null) {
            if (other.precision != null) {
                return false;
            }
        } else if (!precision.equals(other.precision)) {
            return false;
        }
        if (toFactionalSecondsPrecision == null) {
            if (other.toFactionalSecondsPrecision != null) {
                return false;
            }
        } else if (!toFactionalSecondsPrecision.equals(other.toFactionalSecondsPrecision)) {
            return false;
        }
        if (toType != other.toType) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit == null)? 0 : unit.hashCode());
		result = prime * result + ((factionalSecondsPrecision == null) ? 0 : factionalSecondsPrecision.hashCode());
        result = prime * result + ((precision == null) ? 0 : precision.hashCode());
        result = prime * result + ((toFactionalSecondsPrecision == null) ? 0 : toFactionalSecondsPrecision.hashCode());
        result = prime * result + ((toType == null) ? 0 : toType.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	protected void accept0(SQLASTVisitor visitor) {
		this.accept0((TeradataASTVisitor) visitor);
	}
	
	public void accept0(TeradataASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}


}
