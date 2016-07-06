package it.unisannio.loganalysis.extractor;

import it.unisannio.loganalysis.extractor.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.*;
import java.util.Stack;


/**
 * Created by paolomoriello on 29/06/16.
 */
public class ModelDatabaseHandler {

    private static ModelDatabaseHandler modelDatabaseHandler;
    private Configuration configuration;
    private SessionFactory sessionFactory;
    private int start_id;

    public ModelDatabaseHandler() {
        configuration = new Configuration().configure("hibernate.cfg.xml");
        sessionFactory = configuration.buildSessionFactory();
        start_id = getNextId();
    }


    public void closeSession() {
        sessionFactory.close();
    }


    public static ModelDatabaseHandler getInstance() {
        if(modelDatabaseHandler == null)
            modelDatabaseHandler = new ModelDatabaseHandler();
        return modelDatabaseHandler;
    }

    public void parseLogHandler(ILogHandler datasourcehandler) {
        Model m = datasourcehandler.buildModel(start_id);
        start_id = datasourcehandler.getId();
        setNextId(start_id);

        System.out.println("Start filling the database");

        fillDatabase(m);

        System.out.println("Database filled correctly");
    }

    private int getNextId() {
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
    }


    private void fillDatabase(Model model) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        for(User u: model.getUsers()) {
            transaction = session.beginTransaction();
            session.save(u);
            transaction.commit();
            for(UserProperty up: u.getProperties().values()) {
                //System.out.println(up.getValue());
                transaction = session.beginTransaction();
                session.save(up);
                transaction.commit();
            }
        }

        for (Resource r: model.getResources()) {
            transaction = session.beginTransaction();
            session.save(r);
            transaction.commit();
            for(ResourceProperty rp: r.getProperties().values()) {
                transaction = session.beginTransaction();
                session.save(rp);
                transaction.commit();
            }

        }

        for (Action a: model.getActions()) {
            transaction = session.beginTransaction();
            session.save(a);
            transaction.commit();
            for(ActionProperty ap: a.getProperties().values()) {
                transaction = session.beginTransaction();
                session.save(ap);
                transaction.commit();
            }
        }
        session.close();
    }
}
