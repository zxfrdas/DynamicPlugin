package com.zt.lib.database.condition;

public class Condition {
	
	private String selection;
	private String[] selectionArgs;
	private String orderBy;
	
	public Condition() {
		
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String[] getSelectionArgs() {
		return selectionArgs;
	}

	public void setSelectionArgs(String[] selectionArgs) {
		this.selectionArgs = selectionArgs;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("where ").append(getSelection()).append(" ");
		for (String arg : getSelectionArgs()) {
			sb.append(arg).append(" ");
		}
		sb.append(" orderby = ").append(orderBy);
		return sb.toString();
	}
	
}
