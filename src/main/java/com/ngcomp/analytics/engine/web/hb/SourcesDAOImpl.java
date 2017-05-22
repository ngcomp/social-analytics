package com.ngcomp.analytics.engine.web.hb;


import com.ngcomp.analytics.engine.domain.Sources;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository(value = "sourcesDAO")
@Transactional
public class SourcesDAOImpl implements SourcesDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List getSources() {
		List list = sessionFactory.getCurrentSession().createCriteria(Sources.class).list();
		return list;
	}

    @Override
    public Sources getSource(Long id) {
        Criteria criteria  = sessionFactory.getCurrentSession().createCriteria(Sources.class);
        criteria.add(Expression.eq("id", id));
        return (Sources) criteria.list().get(0);
    }


    @Override
    public List getSourceListForBrandId(Long brandId) {
        Criteria criteria  = sessionFactory.getCurrentSession().createCriteria(Sources.class);
//        criteria.add(Expression.eq("brandID ", brandId));
        return criteria.list();
    }

	@Override
	public void updateSource(Sources source) {
		sessionFactory.getCurrentSession().update(source);
	}


}
