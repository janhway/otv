package com.otv.daoImp;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.otv.dao.ProgramDao;
import com.otv.entity.Program;

@Repository
public class ProgramDaoImp implements ProgramDao {

	static Logger log = Logger.getLogger(ProgramDaoImp.class);
	private SessionFactory sessionFactory;
	
	@Autowired
	public ProgramDaoImp(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void saveProgram(Program program) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(program);
	}

	@Override
	public Program getProgram(long id) {
		Session session = sessionFactory.getCurrentSession();
		Program p = (Program) session.get(Program.class, id);
		return p;
	}

	@Override
	public Program getProgram(String title) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(Program.class);
		criteria.add(Restrictions.eq("title", "title"));
		List<Program> result = (List<Program>) criteria.list();
		return (result == null || result.size() == 0) ? null : result.get(0);
	}

	@Override
	public List<Program> getPrograms() {
		Session session = sessionFactory.getCurrentSession();

		List<Program> programList = (List<Program>) session.createQuery(
				"from Program ").list();
		return programList;
	}

}
