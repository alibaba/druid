package com.alibaba.druid.mapping;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public class MySqlMappingVisitor extends MySqlASTVisitorAdapter {

	private final LinkedHashMap<String, Entity> entities;
	private final Map<String, SQLTableSource> tableSources = new LinkedHashMap<String, SQLTableSource>();

	public MySqlMappingVisitor(LinkedHashMap<String, Entity> entities) {
		this.entities = entities;
	}

	public Map<String, Entity> getEntities() {
		return entities;
	}

	public Entity getFirstEntity() {
		for (Map.Entry<String, Entity> entry : entities.entrySet()) {
			return entry.getValue();
		}

		return null;
	}

	public Entity getEntity(String name) {
		Entity entity = this.entities.get(name);

		if (entity == null) {
			for (Map.Entry<String, Entity> entry : entities.entrySet()) {
				if (entry.getKey().equalsIgnoreCase(name)) {
					entity = entry.getValue();
					break;
				}
			}
		}

		return entity;
	}

	public boolean visit(SQLSelectItem x) {
		x.getExpr().setParent(x);
		return true;
	}

	public boolean visit(MySqlSelectQueryBlock x) {
		if (x.getSelectList().size() == 0) {
			fillSelectList(x);
		}

		if (x.getSelectList().size() == 1) {
			if (x.getSelectList().get(0).getExpr() instanceof SQLAllColumnExpr) {
				x.getSelectList().clear();
				fillSelectList(x);
			}
		}

		if (x.getFrom() == null) {
			Entity firstEntity = this.getFirstEntity();
			SQLExprTableSource from = new SQLExprTableSource(
					new SQLIdentifierExpr(firstEntity.getName()));
			x.setFrom(from);
		}

		for (SQLSelectItem item : x.getSelectList()) {
			item.setParent(x);
		}

		return super.visit(x);
	}

	public boolean visit(SQLSelectQueryBlock x) {
		if (x instanceof MySqlSelectQueryBlock) {
			return visit((MySqlSelectQueryBlock) x);
		}

		if (x.getSelectList().size() == 0) {
			fillSelectList(x);
		}

		for (SQLSelectItem item : x.getSelectList()) {
			item.setParent(x);
		}

		return super.visit(x);
	}

	protected void fillSelectList(SQLSelectQueryBlock x) {
		Entity entity = getFirstEntity();

		for (Property item : entity.getProperties().values()) {
			x.getSelectList().add(
					new SQLSelectItem(new SQLIdentifierExpr(item.getName()),
							'"' + item.getName() + '"'));
		}
	}

	public boolean visit(SQLIdentifierExpr x) {
		String propertyName = x.getName();

		Property property = null;
		for (Entity entity : this.entities.values()) {
			property = entity.getProperty(propertyName);
			if (property != null) {
				break;
			}
		}

		if (property == null) {
			throw new DruidMappingException("property not found : "
					+ propertyName);
		}

		String dbColumName = property.getDbColumnName();
		x.setName(dbColumName);

		if (x.getParent() instanceof SQLSelectItem) {
			SQLSelectItem selectItem = (SQLSelectItem) x.getParent();
			if (selectItem.getAlias() == null) {
				selectItem.setAlias('"' + property.getName() + '"');
			}
		}

		return false;
	}

	public boolean visit(SQLSubqueryTableSource x) {
		if (x.getAlias() != null) {
			tableSources.put(x.getAlias(), x);
		}
		
		return true;
	}
	
	public boolean visit(SQLJoinTableSource x) {
		if (x.getAlias() != null) {
			tableSources.put(x.getAlias(), x);
		}
		
		return true;
	}

	public boolean visit(SQLExprTableSource x) {
		SQLExpr expr = x.getExpr();

		if (expr instanceof SQLIdentifierExpr) {
			SQLIdentifierExpr tableExpr = (SQLIdentifierExpr) expr;
			String entityName = tableExpr.getName();

			Entity entity = this.getEntity(entityName);

			if (entity == null) {
				throw new DruidMappingException("entity not foudn : "
						+ entityName);
			}

			tableExpr.setName(entity.getTableName());
		}

		if (x.getAlias() != null) {
			tableSources.put(x.getAlias(), x);
		}

		return false;
	}
}
