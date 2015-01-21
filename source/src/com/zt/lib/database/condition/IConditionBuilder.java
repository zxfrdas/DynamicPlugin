package com.zt.lib.database.condition;


public interface IConditionBuilder {
	
	IConditionBuilder where(String column);
	
	<E> IConditionBuilder less(E arg);
	
	<E> IConditionBuilder lessEqual(E arg);
	
	<E> IConditionBuilder equal(E arg);
	
	<E> IConditionBuilder notEqual(E arg);
	
	<E> IConditionBuilder moreEqual(E arg);
	
	<E> IConditionBuilder more(E arg);
	
	<E> IConditionBuilder between(E min, E max);
	
	<E> IConditionBuilder like(E pattern);
	
	IConditionBuilder orderby(String column);
	
	IConditionBuilder and();
	
	Condition buildDone();
	
}
