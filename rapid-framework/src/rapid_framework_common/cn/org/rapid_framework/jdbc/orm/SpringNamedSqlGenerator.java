package cn.org.rapid_framework.jdbc.orm;

import java.util.ArrayList;
import java.util.List;

import cn.org.rapid_framework.jdbc.orm.metadata.Column;
import cn.org.rapid_framework.jdbc.orm.metadata.Table;

public class SpringNamedSqlGenerator implements SqlGenerator{
	Table table;

	public SpringNamedSqlGenerator(Table table) {
		super();
		this.table = table;
	}

	public List<Column> getColumns() {
		return table.getColumns();
	}

	public String getTableName() {
		return table.getTableName();
	}
	
	public List<Column> getPrimaryKeyColumns() {
		return table.getPrimaryKeyColumns();
	}
	
	public String getInsertSql() {
		StringBuilder sb = new StringBuilder("INSERT ").append(getTableName()).append(" (");
		for(int i = 0; i < getColumns().size(); i++) {
			Column c = getColumns().get(i);
			sb.append(c.getSqlName());
			if(i < getColumns().size() - 1) 
				sb.append(",");
		}
		sb.append(" ) VALUES ( ");
		
		for(int i = 0; i < getColumns().size(); i++) {
			Column c = getColumns().get(i);
			sb.append(":"+c.getPropertyName());
			if(i < getColumns().size() - 1) 
				sb.append(",");
		}
		sb.append(" ) ");
		return sb.toString();
	}

	public String getUpdateSql() {
		StringBuilder sb = new StringBuilder("UPDATE ").append(getTableName()).append(" SET (");
		for(int i = 0; i < getColumns().size(); i++) {
			Column c = getColumns().get(i);
			sb.append(c.getSqlName()+" = :"+c.getPropertyName());
			if(i < getColumns().size() - 1) 
				sb.append(",");
		}
		
		sb.append(" ) WHERE ");
		
		for(int i = 0; i < getPrimaryKeyColumns().size(); i++) {
			Column c = getPrimaryKeyColumns().get(i);
			sb.append(c.getSqlName()+" = :"+c.getPropertyName());
			if(i < getPrimaryKeyColumns().size() - 1) 
				sb.append(",");
		}
		return sb.toString();
	}
	
	public String getDeleteByPrimaryKeysSql() {
		StringBuilder sb = new StringBuilder("DELETE FROM ").append(getTableName());

		sb.append(" WHERE ");
		
		List<Column> primaryKeyColumns = getPrimaryKeyColumns();
		for(int i = 0; i < primaryKeyColumns.size(); i++) {
			Column c = primaryKeyColumns.get(i);
			sb.append(c.getSqlName()+" = :"+c.getPropertyName());
			if(i < primaryKeyColumns.size() - 1) 
				sb.append(" AND ");
		}
		return sb.toString();
	}
	
	public String getDeleteBySinglePkSql() {
		List<Column> primaryKeyColumns = getPrimaryKeyColumns();
		if(primaryKeyColumns.size() != 1) {
			throw new IllegalStateException("expected single primary key on table:"+getTableName()+",but was primary keys:"+primaryKeyColumns);
		}
		
		StringBuilder sb = new StringBuilder("DELETE FROM ").append(getTableName());

		sb.append(" WHERE ");
		
		Column c = primaryKeyColumns.get(0);
		sb.append(c.getSqlName()+" = :id");
		return sb.toString();
	}
	
	public String getSelectByPrimaryKeysSql() {
		StringBuilder sb = new StringBuilder("SELECT "+getColumnsSql()+" FROM " + getTableName()+" WHERE ");
		List<Column> primaryKeyColumns = getPrimaryKeyColumns();
		for(int i = 0; i < primaryKeyColumns.size(); i++) {
			Column c = primaryKeyColumns.get(i);
			sb.append(c.getSqlName()+" = :"+c.getPropertyName());
			if(i < primaryKeyColumns.size() - 1) 
				sb.append(" AND ");
		}
		return sb.toString();
	}
	
	public String getSelectBySinglePkSql() {
		List<Column> primaryKeyColumns = getPrimaryKeyColumns();
		if(primaryKeyColumns.size() != 1) {
			throw new IllegalStateException("expected single primary key on table:"+getTableName()+",but was primary keys:"+primaryKeyColumns);
		}
		
		StringBuilder sb = new StringBuilder("SELECT "+getColumnsSql()+" FROM " + getTableName()+" WHERE ");
		Column c = primaryKeyColumns.get(0);
		sb.append(c.getSqlName()+" = :id");
		return sb.toString();
	}
	
	public String getColumnsSql() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < getColumns().size(); i++) {
			Column c = getColumns().get(i);
			sb.append(c.getSqlName()+" " + c.getPropertyName());
			if(i < getColumns().size() - 1) 
				sb.append(",");
		}
		return sb.toString();
	}
	
}
