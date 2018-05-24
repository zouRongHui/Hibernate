package org.rone.study.hibernate.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.jdbc.Work;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.rone.study.hibernate.Employee;
import org.rone.study.hibernate.News;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class Test {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		//1.创建SessionFactory
		//不指定文件名默认是找hibernate.cfg.xml文件
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
		sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();

		//2.创建Session
		session = sessionFactory.openSession();

		//3.开启事务
		transaction = session.beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		//5.提交事务
		//首先会调用flush方法，根据session缓存中的数据更新数据表中的记录(可能是insert、delete或update)
		transaction.commit();

		//6.关闭Session
		session.close();

		//7.关闭SessionFactory
		sessionFactory.close();
	}

	@org.junit.Test
	public void testSave() {
		//save
		News news = new News("R", "Rone", new Date(new java.util.Date().getTime()));
		System.out.println(session.save(news).toString());
	}
	
	@org.junit.Test
	public void testGet() {
		//get
		News n = session.get(News.class, 1);
		System.out.println(n.toString());		
	}
	
	@org.junit.Test
	public void testUpdate() {
		//update
		News n = session.get(News.class, 1);
		n.setAuthor("TR");
		session.update(n);		
	}
	
	@org.junit.Test
	public void testSaveOrUpdate() {
		//saveOrUpdate
		News n = new News("R", "Jason", new Date(new java.util.Date().getTime()));
		n.setId(3);
		session.saveOrUpdate(n);		
	}
	
	@org.junit.Test
	public void testMerge() {
		//merge
		News n = new News("R", "Jason", new Date(new java.util.Date().getTime()));
		n.setId(50);
//		News n = session.get(News.class, 5);
		n.setTitle("C++");
		News ne = (News) session.merge(n);
		System.out.println(ne.toString());		
	}
	
	@org.junit.Test
	public void testDelete() {
		//delete
		News n = new News();
		n.setId(9);
//		News n = (News) session.get(News.class, 8);
		session.delete(n);
		System.out.println(n.toString());		
	}
	
	@org.junit.Test
	public void testEvict() {
		//evict
		News n1 = session.get(News.class, 1);
		News n2 = session.get(News.class, 2);
		n1.setAuthor("Rone");
		n2.setAuthor("Rone");
		session.evict(n1);		
	}
	
	@org.junit.Test
	public void testHQL() {
		//1. 创建Query实例
//		String hql = "from News n where n.id > ?";
		String hql = "from News n where n.id > :id";
		Query<News> query = session.createQuery(hql);
		//2. 绑定参数
//		query.setParameter(0, 0);
		query.setParameter("id", 0);

		//3. 执行查询
		List<News> listNews = query.list();
		for(News n : listNews) {
			System.out.println(n.toString());
		}
	}

	@org.junit.Test
	public void testPageQuery() {
		//分页检索
		String hql = "from News ";
		Query<News> query = session.createQuery(hql);
		int pageSize = 4;
		int pages = 1;
		//设置该页的首项
		query.setFirstResult((pages - 1) * pageSize);
		//设置每页的数据量
		query.setMaxResults(pageSize);
		List<News> listNews = query.list();
		System.out.println(listNews.toString());
	}

	@org.junit.Test
	public void testFieldQuery() {
		//投影检索，查询结果仅包含部分属性
//		String hql = "select n.id, n.title from News n";
//		Query<News> query = session.createQuery(hql);
//		List<Object[]> list = query.list();
//		for (Object[] obj : list) {
//			System.out.println(Arrays.asList(obj));
//		}
		//需要在POJO中创建一个部分属性的构造器
		String hql = "select new News(n.id, n.title) from News n";
		Query<News> query = session.createQuery(hql);
		List<News> list = query.list();
		for (News n : list) {
			System.out.println(n.toString());
		}
	}
	
	@org.junit.Test
	public void testPOJO() {
		//使用@注解的实体类
		Employee emp = new Employee("邹荣辉", "rone@rone.com");
		session.save(emp);
	}
	
	@org.junit.Test
	public void testC3P0() {
		session.doWork(new Work() {
			public void execute(Connection arg0) throws SQLException {
				System.out.println(arg0);
			}
		});
	}
	
}
