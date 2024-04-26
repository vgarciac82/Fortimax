package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;

public abstract class NotMappedManager {

	@SuppressWarnings("unchecked")
	public static List<Object[]> get(Session sess, String table, String[] fields, List<NotMappedCondition> notMappedConditions) throws HibernateException {
		
		SQLQuery sql = sess.createSQLQuery(getSQLQuery(table, fields, notMappedConditions, false));

		for (NotMappedCondition notMappedCondition : notMappedConditions) {
			sql.setParameter(notMappedCondition.getField(), notMappedCondition.getValue());
		}

		return (List<Object[]>) sql.list();
	}
	
	public static Criterion getSQLRestriction(String beforeQuery, String table, String[] fields, List<NotMappedCondition> notMappedConditions) throws Exception {
		String sql = beforeQuery+" ("+getSQLQuery(table, fields, notMappedConditions, true)+")";
		List<Object> values = new ArrayList<Object>();
		List<Type> types = new ArrayList<Type>();
		for (NotMappedCondition notMappedCondition : notMappedConditions) {
			values.add(notMappedCondition.getValue());
			if(notMappedCondition.getType()==null)
				throw new Exception("El type de "+notMappedCondition.getValue()+" no puede ser nulo");
			else
				types.add(notMappedCondition.getType());
		}
		return Restrictions.sqlRestriction(sql, values.toArray(new Object[values.size()]), types.toArray(new Type[types.size()]));
	}
	
	private static String getSQLQuery(String table, String[] fields, List<NotMappedCondition> notMappedConditions, boolean isSQLRestriction) {
		StringBuffer query = new StringBuffer();

		query.append("SELECT ");
		if(fields==null) {
			query.append("*");
		} else if (fields.length==0) {
			query.append("*");
		} else {
			boolean first = true;
			for(String field: fields) {
				if(first) {
					first = false;
				} else {
					query.append(", ");
				}
				query.append(field);
			}
		}
		query.append(" FROM ");
		query.append(table);
		query.append(" WHERE ");
		String token = "";
		for (NotMappedCondition notMappedCondition : notMappedConditions) {
			query.append(token);
			query.append(notMappedCondition.getField());
			query.append(" ");
			query.append(notMappedCondition.getCondition());
			query.append(" ");
			if(isSQLRestriction) {
				query.append("?");
			} else {
				query.append(":");
				query.append(notMappedCondition.getField());
			}
			token = " AND ";
		}
		return query.toString();
	}

}
