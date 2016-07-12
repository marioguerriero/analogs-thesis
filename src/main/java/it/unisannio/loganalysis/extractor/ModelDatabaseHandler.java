package it.unisannio.loganalysis.extractor;

import it.unisannio.loganalysis.extractor.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Collection;


/**
 * Created by paolomoriello on 29/06/16.
 */
public class ModelDatabaseHandler {

    private Configuration configuration;
    private SessionFactory sessionFactory;

    public ModelDatabaseHandler(String dbname) {
        configuration = new Configuration().configure(getClass().getResource("/hibernate.cfg.xml"));

        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost/"+dbname+"?createDatabaseIfNotExist=true");
        sessionFactory = configuration.buildSessionFactory();
    }


    public void parseLogHandler(String handlerIdentifier) {
        ILogHandler datasourcehandler = LogSourceHandler.getInstance().getSourceHandler(handlerIdentifier);
        Model m = datasourcehandler.buildModel();
        fillDatabase(m);
    }

    /*private int getNextId() {
        Connection connection;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/loganalysis?user=thesis&password=thesis");
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT next_val FROM my_sequence");
            resultSet.next();
            int i = resultSet.getInt("next_val");
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    private void setNextId(int id) {
        Connection connection;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/loganalysis?user=thesis&password=thesis");
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE my_sequence SET next_val='"+id+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/


    private void fillDatabase(Model model) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        for(User u: model.getUsers()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(u);
            transaction.commit();
            for(UserProperty up: u.getProperties().values()) {
                //System.out.println(up.getValue());
                transaction = session.beginTransaction();
                session.saveOrUpdate(up);
                transaction.commit();
            }
        }

        for (Resource r: model.getResources()) {
            //System.out.println("parent "+r.getIdResourceAssociated().getIdResource());
            //System.out.println("resource "+r.getIdResource());
            saveParent(session, model.getResources(), r);
            transaction = session.beginTransaction();
            session.saveOrUpdate(r);
            transaction.commit();
            for(ResourceProperty rp: r.getProperties().values()) {
                transaction = session.beginTransaction();
                session.saveOrUpdate(rp);
                transaction.commit();
            }

        }

        for (Action a: model.getActions()) {
            transaction = session.beginTransaction();
            session.save(a);
            transaction.commit();
            for(ActionProperty ap: a.getProperties().values()) {
                transaction = session.beginTransaction();
                session.saveOrUpdate(ap);
                transaction.commit();
            }
        }
        session.close();
        sessionFactory.close();
    }


    private void saveParent(Session session, Collection<Resource> resourceSet, Resource resource) {
        if(resource.getIdResourceAssociated() == null)
            return;
        int id = resource.getIdResourceAssociated().getIdResource();
        for(Resource r: resourceSet) {
            if(r.getIdResource() == id) {
                saveParent(session, resourceSet, r);
                Transaction transaction = session.beginTransaction();
                session.saveOrUpdate(r);
                transaction.commit();
            }
        }
    }
}
