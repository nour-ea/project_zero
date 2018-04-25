package com.platformia.bringsouvenir.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public abstract class AbstractHibernateDao<T extends Serializable> {

	@Autowired
	SessionFactory sessionFactory;

	protected Class<T> entityClass;

	protected Filter authFilter;

	protected String authorizedUserName;

	// SETUP METHODS

	public void setEntityClass(Class<T> entityClassToSet) {
		this.entityClass = entityClassToSet;
	}

	public void setAuthorizedUserName(String userName) {
		this.authorizedUserName = userName;
	}

	protected final Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	protected void setAuthorizerFilter() {
		if (authorizedUserName != null && !authorizedUserName.isEmpty()) {
			authFilter = getCurrentSession().enableFilter("authorizer");
			authFilter.setParameter("userName", authorizedUserName);
		}
	}

	// REQUEST METHODS

	public T findOne(long id) {
		return (T) getCurrentSession().get(entityClass, id);
	}

	public T findOne(String id) {
		return (T) getCurrentSession().get(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return getCurrentSession().createQuery("from " + entityClass.getName()).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll(List<DaoFilter> daoFilters) {

		// prepare criteria query
		@SuppressWarnings("deprecation")
		Criteria criteria = getCurrentSession().createCriteria(entityClass);
		
		// add restrictions to criteria query		
		if (daoFilters != null) {
			for (DaoFilter daoFilter : daoFilters) {
				
				if (daoFilter != null) {
					
					if( daoFilter.getCriterion()!=null)
					criteria.add( daoFilter.getCriterion() );
					
					if( daoFilter.getOrder()!=null )
					criteria.addOrder( daoFilter.getCriteriaOrder() );
				}
			}
		}
		// return criteria query result
		return criteria.list();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void create(T entity) {
		getCurrentSession().persist(entity);
		getCurrentSession().flush();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void update(T entity) {
		getCurrentSession().merge(entity);
		getCurrentSession().flush();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void delete(T entity) {
		getCurrentSession().delete(entity);
		getCurrentSession().flush();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void deleteById(long entityId) {
		T entity = findOne(entityId);
		delete(entity);
	}

}