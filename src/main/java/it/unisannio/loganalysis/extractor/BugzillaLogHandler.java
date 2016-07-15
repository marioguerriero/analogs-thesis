package it.unisannio.loganalysis.extractor;

import it.unisannio.loganalysis.extractor.model.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by paolomoriello on 29/06/16.
 */
public class BugzillaLogHandler implements ILogHandler {

    private Connection connection = null;
    private Statement statement = null;

    private int id;
    private Set<User> users;
    private Set<Action> actions;
    private Set<Resource> resources;

    public BugzillaLogHandler(String dialect, String host, String port, String sourcedb, String username, String password) {
        try {
            connection = DriverManager.getConnection("jdbc:"+dialect+"://"+host+":"+port+"/"+sourcedb+"?" +
                    "user="+username+"&password="+password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        id = 1;
        users = new HashSet<>();
        actions = new HashSet<>();
        resources = new HashSet<>();
    }


    @Override
    public Model buildModel() {
        Model model = null;

        try {
            model = new Model(getUsers(), getResources(), getActions());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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

        return model;
    }



    private Set<User> getUsers() throws SQLException, ParseException {
        ResultSet resultSet = null;


        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT profiles.userid, login_name, realname, last_seen_date, profiles_when, name " +
                "FROM profiles " +
                "JOIN profiles_activity ON profiles.userid = profiles_activity.userid " +
                "JOIN user_group_map ON profiles.userid = user_group_map.user_id " +
                "JOIN groups ON user_group_map.group_id = groups.id");
        while(resultSet.next()) {
            String fullname = resultSet.getString("realname");
            String username = resultSet.getString("login_name");
            boolean temp = false;
            for (User u : users) {
                if (u.getUsername().equals(username) && !temp) {
                    temp = true;
                    if(resultSet.getString("name") != null)
                        u.addType(resultSet.getString("name"));
                }
            }
            if (!temp) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
                Map<String, UserProperty> properties = new HashMap<>();
                User u = new User(fullname, username, properties);
                u.setIdUser(id);
                properties.put("sourceid", new UserProperty(u, "sourceid", resultSet.getString("userid")));
                properties.put("lastaccess", new UserProperty(u, "lastaccess", simpleDateFormat.parse(resultSet.getString("last_seen_date")).getTime()+""));
                properties.put("timecreated", new UserProperty(u, "timecreated", simpleDateFormat.parse(resultSet.getString("profiles_when")).getTime()+""));
                if(resultSet.getString("name") != null)
                    u.addType(resultSet.getString("name"));
                users.add(u);
            }
            id++;
        }

        return users;
    }

    private Set<Resource> getResources() throws SQLException {
        ResultSet resultSet = null;

        //adding resource, type: product
        resultSet = statement.executeQuery("SELECT id, name, description " +
                "FROM products");
        while (resultSet.next()) {
            Map<String, ResourceProperty> properties = new HashMap<>();
            Resource r = new Resource("product", properties);
            r.setIdResource(id);
            properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
            properties.put("name", new ResourceProperty(r, "name", resultSet.getString("name")));
            properties.put("description", new ResourceProperty(r, "description", resultSet.getString("description")));
            resources.add(r);
            id++;
        }

        //adding resource, type: component
        resultSet = statement.executeQuery("SELECT id, name, description, product_id " +
                "FROM components");
        while (resultSet.next()) {
            Map<String, ResourceProperty> properties = new HashMap<>();
            Resource r = new Resource("component", properties);
            r.setIdResource(id);
            properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
            properties.put("name", new ResourceProperty(r, "name", resultSet.getString("name")));
            properties.put("description", new ResourceProperty(r, "description", resultSet.getString("description")));
            for (Resource rs : resources) {
                if (rs.getType().equals("product")
                        && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("product_id"))) {
                    properties.put("product_id", new ResourceProperty(r, "product_id", rs.getIdResource() + ""));
                    r.setIdResourceAssociated(rs);
                }
            }
            resources.add(r);
            id++;
        }

        //adding resource, type: bug
        resultSet = statement.executeQuery("SELECT bug_id, assigned_to, bug_status, " +
                "creation_ts, delta_ts, deadline, product_id, component_id " +
                "FROM bugs ");
        while (resultSet.next()) {
            Map<String, ResourceProperty> properties = new HashMap<>();
            Resource r = new Resource("bug", properties);
            r.setIdResource(id);
            for (User u : users) {
                if (u.getProperties().get("sourceid").getValue().equals(resultSet.getString("assigned_to")))
                    properties.put("userid", new ResourceProperty(r, "userid", u.getIdUser() + ""));
            }
            for (Resource rs : resources) {
                if (rs.getType().equals("component")
                        && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("component_id"))) {
                    properties.put("component_id", new ResourceProperty(r, "component_id", rs.getIdResource() + ""));
                    r.setIdResourceAssociated(rs);
                }
            }
            properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("bug_id")));
            properties.put("bug_status", new ResourceProperty(r, "bug_status", resultSet.getString("bug_status")));
            properties.put("starttime", new ResourceProperty(r, "starttime", "" + calcMillis(resultSet.getString("creation_ts"))));
            if (resultSet.getString("bug_status").equalsIgnoreCase("resolved"))
                properties.put("totaltime", new ResourceProperty(r, "totaltime", "" + calcTotal(resultSet.getString("creation_ts"), resultSet.getString("delta_ts"))));
            properties.put("timemodified", new ResourceProperty(r, "timemodified", "" + calcMillis(resultSet.getString("delta_ts"))));
            properties.put("deadline", new ResourceProperty(r, "deadline", "" + calcMillis(resultSet.getString("deadline"))));
            resources.add(r);
            id++;
        }


        return resources;
    }

    private Set<Action> getActions() throws SQLException, ParseException {
        ResultSet resultSet;
        //adding action bugs activity
        resultSet = statement.executeQuery("SELECT bugs.bug_id, who, bug_when, added, removed, op_sys " +
                "FROM  bugs_activity JOIN bugs ON bugs.bug_id = bugs_activity.bug_id");
        while(resultSet.next()) {
            Map<String, ActionProperty> properties = new HashMap<>();
            Action a = new Action();
            a.setMillis(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(resultSet.getString("bug_when")).getTime());
            a.setProperties(properties);
            for(Resource rs: resources) {
                if(rs.getType().equals("bug")
                        && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("bug_id")))
                    a.setResource(rs);
            }
            for(User u: users) {
                if(u.getProperties().get("sourceid").getValue().equals(resultSet.getString("who")))
                    a.setUserFrom(u);
            }
            properties.put("os", new ActionProperty(a, "os", resultSet.getString("op_sys")));
            properties.put("added", new ActionProperty(a, "added", calcMillisDay(resultSet.getString("added"))));
            properties.put("removed", new ActionProperty(a, "removed", resultSet.getString("removed")));
            a.setType('c');
            actions.add(a);
        }

        return actions;
    }


    private long calcTotal(String starttime, String endtime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(endtime).getTime()-simpleDateFormat.parse(starttime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String calcMillis(String time) {
        if(time == null)
            return 0+"";
        try {
            return ""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS").parse(time).getTime();
        } catch (ParseException e) {
            //e.printStackTrace();
            return time;
        }
    }

    private String calcMillisDay(String time) {
        if(time == null)
            return 0+"";
        try {
            return ""+new SimpleDateFormat("yyyy-MM-dd").parse(time).getTime();
        } catch (ParseException e) {
            //e.printStackTrace();
            return time;
        }
    }
}
