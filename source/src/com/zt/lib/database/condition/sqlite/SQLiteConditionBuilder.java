package com.zt.lib.database.condition.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.zt.lib.database.condition.Condition;
import com.zt.lib.database.condition.IConditionBuilder;

@SuppressWarnings("unchecked")
public class SQLiteConditionBuilder implements IConditionBuilder {

	private static final String PLACE_HOLDER_NUMBER = "? ";
	private static final String AND = "AND ";
	private List<Where> mWheres;
	private List<Orderby> mOrderbys;
	private Where where;
	private Orderby orderby;
	
	public SQLiteConditionBuilder() {
		mWheres = new ArrayList<Where>();
		mOrderbys = new ArrayList<Orderby>();
	}
	
	@Override
	public IConditionBuilder where(String column) {
		where = new Where();
		where.column = column;
		return this;
	}

	@Override
	public <E> IConditionBuilder less(E arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.LESS;
		putArg(arg);
		return this;
	}

	@Override
	public <E> IConditionBuilder lessEqual(E arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.LESS_EQUAL;
		putArg(arg);
		return this;
	}

	@Override
	public <E> IConditionBuilder equal(E arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.EQUAL;
		putArg(arg);
		return this;
	}

	@Override
	public <E> IConditionBuilder notEqual(E arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.NOT_EQUAL;
		putArg(arg);
		return this;
	}

	@Override
	public <E> IConditionBuilder moreEqual(E arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.MORE_EQUAL;
		putArg(arg);
		return this;
	}

	@Override
	public <E> IConditionBuilder more(E arg) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.MORE;
		putArg(arg);
		return this;
	}

	@Override
	public <E> IConditionBuilder between(E min, E max) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.BETWEEN;
		putArg(min, max);
		return this;
	}

	@Override
	public <E> IConditionBuilder like(E pattern) {
		if (null == where) throw new NullPointerException();
		where.condition = Where.EnumCondition.LIKE;
		putArg(pattern);
		return this;
	}

	@Override
	public IConditionBuilder orderby(String column) {
		orderby = new Orderby();
		orderby.column = column;
		return this;
	}

	@Override
	public IConditionBuilder and() {
		if (null != where) {
			mWheres.add(where);
		}
		if (null != orderby) {
			mOrderbys.add(orderby);
		}
		return this;
	}

	@Override
	public Condition buildDone() {
		and();
		Condition condition = new Condition();
		condition.setSelection(createSelection());
		condition.setSelectionArgs(createSelectionArgs());
		condition.setOrderBy(createOrderby());
		return condition;
	}
	
	private <T> void putArg(T... args) {
		if (args[0] instanceof String) {
			where.argType = Where.EnumArgType.TEXT;
		} else {
			where.argType = Where.EnumArgType.NUMBER;
		}
		where.args = new ArrayList<String>();
		for (T arg : args) {
			where.args.add(arg.toString());
		}
	}
	
	private String createSelection() {
		StringBuilder sb = new StringBuilder();			
		for (Where where : mWheres) {
			sb.append(where.column);
			sb.append(where.condition.value);
			int argNumber = 0;
			if (Where.EnumCondition.BETWEEN == where.condition) {
				argNumber = 2;
			} else {
				argNumber = 1;
			}
			for (int i = 0; i < argNumber; i ++) {
				sb.append(PLACE_HOLDER_NUMBER);
				sb.append(AND);
			}
		}
		int lastAndIndex = sb.lastIndexOf(AND);
		String result = null;
		if (-1 != lastAndIndex) {
			result = sb.substring(0, lastAndIndex).toString();
		}
		return result;
	}
	
	private String[] createSelectionArgs() {
		List<String> args = new ArrayList<String>();
		for (Where where : mWheres) {
			for (String arg : where.args) {
				// if arg is boolean and type is number
				// we make true -> 1 & false -> 0
				if (Where.EnumArgType.NUMBER == where.argType) {
					if ("true".equals(arg) || "false".equals(arg)) {
						arg = (Boolean.valueOf(arg) ? 1 : 0) + "";
					}
				}
				args.add(arg);
			}
		}
		String[] result = null;
		if (!args.isEmpty()) {
			result = args.toArray(new String[args.size()]);
		}
		return result;
	}
	
	private String createOrderby() {
		StringBuilder sb = new StringBuilder();	
		for (Orderby orderby : mOrderbys) {
			sb.append(orderby.column);
			sb.append(" ");
			sb.append(AND);
		}
		int lastAndIndex = sb.lastIndexOf(AND);
		String result = null;
		if (-1 != lastAndIndex) {
			result = sb.substring(0, lastAndIndex).toString();
		}
		return result;
	}
	
	private static class Where {
		private String column;
		private EnumCondition condition;
		private List<String> args;
		private EnumArgType argType;
		
		enum EnumArgType {
			NUMBER,
			TEXT;
		}
		
		enum EnumCondition {
			LESS(" < "),
			LESS_EQUAL(" <= "),
			EQUAL(" = "),
			MORE_EQUAL(" >= "),
			MORE(" > "),
			NOT_EQUAL(" <> "),
			BETWEEN(" BETWEEN "),
			LIKE(" LIKE ");
			
			private String value;
			private EnumCondition(String value) {
				this.value = value;
			}
		}
		
	}
	
	private static class Orderby {
		private String column;
	}

}
