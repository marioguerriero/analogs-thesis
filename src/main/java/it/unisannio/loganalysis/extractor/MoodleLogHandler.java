package it.unisannio.loganalysis.extractor;


import it.unisannio.loganalysis.extractor.model.*;

import java.sql.*;
import java.util.*;

/**
 * Created by paolomoriello on 29/06/16.
 */
public class MoodleLogHandler implements ILogHandler {

    private Connection connection;
    private Statement statement;
    private int id;


    public MoodleLogHandler(String dialect, String host, String port, String sourcedb, String username, String password) {
        try {
            connection = DriverManager.getConnection("jdbc:"+dialect+"://"+host+":"+port+"/"+sourcedb+"?" +
            "user="+username+"&password="+password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public Model buildModel() {
        Set<User> users = new HashSet<>();
        Set<Action> actions = new HashSet<>();
        Set<Resource> resources = new HashSet<>();
        this.id = 1;

        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();

            //adding users
            resultSet = statement.executeQuery("SELECT m_user.username, m_user.id as userid, m_user.firstname, m_user.lastname, m_user.country, m_user.lang, m_user.firstaccess, m_user.lastaccess, m_user.timecreated, m_role.shortname, m_role.id " +
                    "FROM m_role_assignments JOIN m_role ON m_role_assignments.roleid = m_role.id " +
                    "JOIN m_user ON m_user.id = m_role_assignments.userid");
            while(resultSet.next()) {
                String username = resultSet.getString("username");
                String realname = resultSet.getString("firstname") + " " + resultSet.getString("lastname");
                String country = "country";
                String lang = "lang";
                String firstaccess = "firstaccess";
                String lastaccess = "lastaccess";
                String timecreated = "timecreated";
                boolean temp = false;
                for(User u: users) {
                    if(u.getUsername().equals(username) && !temp) {
                        temp = true;
                        if(resultSet.getString("shortname") != null)
                            u.addType(resultSet.getString("shortname"));
                    }
                }
                if(!temp) {
                    Map<String, UserProperty> properties = new HashMap<>();
                    User u = new User(realname, username, properties);
                    u.setIdUser(this.id);
                    properties.put("sourceid", new UserProperty(u, "sourceid", resultSet.getString("userid")));
                    properties.put(country, new UserProperty(u, country, resultSet.getString(country)));
                    properties.put(lang, new UserProperty(u, lang, resultSet.getString(lang)));
                    properties.put(firstaccess, new UserProperty(u, firstaccess, resultSet.getString(firstaccess)+"000"));
                    properties.put(lastaccess, new UserProperty(u, lastaccess, resultSet.getString(lastaccess)+"000"));
                    properties.put(timecreated, new UserProperty(u, timecreated, resultSet.getString(timecreated)+"000"));
                    if(resultSet.getString("shortname") != null)
                        u.addType(resultSet.getString("shortname"));
                    users.add(u);
                    this.id++;
                }
            }


            //adding resource, type: category
            resultSet = statement.executeQuery("SELECT id, name, description, timemodified " +
                    "FROM  m_course_categories ");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("course_category", properties);
                r.setIdResource(this.id);
                properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                properties.put("name", new ResourceProperty(r, "name", resultSet.getString("name")));
                properties.put("description", new ResourceProperty(r, "description", resultSet.getString("description")));
                properties.put("timemodified", new ResourceProperty(r, "timemodified", resultSet.getString("timemodified")+"000"));
                resources.add(r);
                this.id++;
            }

            //adding resource, type: course
            resultSet = statement.executeQuery("SELECT id, category, fullname, format, startdate, timecreated " +
                    "FROM  m_course ");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("course", properties);
                r.setIdResource(this.id);
                properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                for(Resource rs: resources) {
                    if(rs.getType().equalsIgnoreCase("course_category") && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("category"))) {
                        properties.put("category", new ResourceProperty(r, "category", rs.getIdResource() + ""));
                        r.setIdResourceAssociated(rs);
                    }
                }
                properties.put("fullname", new ResourceProperty(r, "fullname", resultSet.getString("fullname")));
                properties.put("format", new ResourceProperty(r, "format", resultSet.getString("format")));
                properties.put("startdate", new ResourceProperty(r, "startdate", resultSet.getString("startdate")+"000"));
                properties.put("timecreated", new ResourceProperty(r, "timecreated", resultSet.getString("timecreated")+"000"));
                resources.add(r);
                this.id++;
            }


            //adding resource, type: assignment
            resultSet = statement.executeQuery("SELECT id, course, name, intro, grade, duedate " +
                    "FROM  m_assign ");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("assignment", properties);
                r.setIdResource(this.id);
                properties.put("name", new ResourceProperty(r, "name", resultSet.getString("name")));
                properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                for(Resource rs: resources) {
                    if(rs.getType().equalsIgnoreCase("course")
                            && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("course"))) {
                        properties.put("course", new ResourceProperty(r, "course", rs.getIdResource() + ""));
                        r.setIdResourceAssociated(rs);
                    }
                }
                properties.put("intro", new ResourceProperty(r, "intro", resultSet.getString("intro")));
                properties.put("grade", new ResourceProperty(r, "grade", resultSet.getString("grade")));
                properties.put("timecreated", new ResourceProperty(r, "timecreated", resultSet.getString("duedate")+"000"));
                resources.add(r);
                this.id++;
            }


            //adding action assign submission
            resultSet = statement.executeQuery("SELECT assignment, userid, timecreated, timemodified " +
                    "FROM  m_assign_submission");
            while(resultSet.next()) {
                Map<String, ActionProperty> properties = new HashMap<>();
                Action a = new Action();
                a.setMillis(resultSet.getLong("timecreated")*1000);
                a.setProperties(properties);
                a.setType('c');
                for(Resource rs: resources) {
                    if(rs.getType().equalsIgnoreCase("assignment")
                            && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("assignment")))
                        a.setResource(rs);
                }
                for(User u: users) {
                    if(u.getProperties().get("sourceid").getValue().equals(resultSet.getString("userid")))
                        a.setUserFrom(u);
                }
                actions.add(a);
            }


            //adding resource, type: comments
            resultSet = statement.executeQuery("SELECT id, content, userid, timecreated " +
                    "FROM  m_comments");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("comment", properties);
                r.setIdResource(this.id);
                properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                properties.put("content", new ResourceProperty(r, "content", resultSet.getString("content")));
                for(User u: users) {
                    if(u.getProperties().get("sourceid").getValue().equals(resultSet.getString("userid")))
                        properties.put("userid", new ResourceProperty(r, "userid", u.getIdUser()+""));
                }
                properties.put("timecreated", new ResourceProperty(r, "timecreated", resultSet.getString("timecreated")+"000"));
                resources.add(r);
                this.id++;
            }


            //adding resoure, type: event
            resultSet = statement.executeQuery("SELECT id, name, description, courseid, userid, eventtype, timestart " +
                    "FROM  m_event");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("course_event", properties);
                r.setIdResource(this.id);
                properties.put("sourceid",new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                properties.put("name", new ResourceProperty(r, "name", resultSet.getString("name")));
                properties.put("description", new ResourceProperty(r, "description", resultSet.getString("description")));
                for(Resource rs: resources) {
                    if(rs.getType().equals("course")
                            && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("courseid"))) {
                        properties.put("courseid", new ResourceProperty(r, "courseid", rs.getIdResource() + ""));
                        r.setIdResourceAssociated(rs);
                    }
                }
                for(User u: users) {
                    if(u.getProperties().get("sourceid").getValue().equals(resultSet.getString("userid")))
                        properties.put("userid", new ResourceProperty(r, "userid", u.getIdUser()+""));
                }
                properties.put("eventtype", new ResourceProperty(r, "eventtype", resultSet.getString("eventtype")));
                properties.put("timestart", new ResourceProperty(r, "timestart", resultSet.getString("timestart")+"000"));
                resources.add(r);
                this.id++;
            }


            //adding resource, type: message
            resultSet = statement.executeQuery("SELECT id, useridfrom, useridto, subject, smallmessage, timecreated " +
                    "FROM  m_message " +
                    "UNION " +
                    "SELECT id, useridfrom, useridto, subject, smallmessage, timecreated " +
                    "FROM m_message_read");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("message", properties);
                r.setIdResource(this.id);
                properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                for(User u: users) {
                    if(u.getProperties().get("sourceid").getValue().equals(resultSet.getString("useridfrom")))
                        properties.put("useridfrom", new ResourceProperty(r, "useridfrom", u.getIdUser()+""));
                    else if(u.getProperties().get("sourceid").getValue().equals(resultSet.getString("useridto")))
                        properties.put("useridto", new ResourceProperty(r, "useridto", u.getIdUser()+""));
                }
                properties.put("subject", new ResourceProperty(r, "subject", resultSet.getString("subject")));
                properties.put("content", new ResourceProperty(r, "content", resultSet.getString("smallmessage")));
                properties.put("timecreated", new ResourceProperty(r, "timecreated", resultSet.getString("timecreated")+"000"));
                resources.add(r);
                this.id++;
            }


            //adding resource, type: lesson
            resultSet = statement.executeQuery("SELECT id, course, name, intro, timemodified " +
                    "FROM  m_resource");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("lesson", properties);
                r.setIdResource(this.id);
                properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                for(Resource rs: resources) {
                    if(rs.getType().equals("course")
                            && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("course"))) {
                        properties.put("courseid", new ResourceProperty(r, "courseid", rs.getIdResource()+""));
                        r.setIdResourceAssociated(rs);
                    }
                }
                properties.put("name", new ResourceProperty(r, "name", resultSet.getString("name")));
                properties.put("intro", new ResourceProperty(r, "intro", resultSet.getString("intro")));
                properties.put("timemodified", new ResourceProperty(r, "timemodified", resultSet.getString("timemodified")+"000"));
                resources.add(r);
                this.id++;
            }

            //adding resource, type: role
            resultSet = statement.executeQuery("SELECT id, shortname " +
                    "FROM  m_role ");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("role", properties);
                r.setIdResource(this.id);
                properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                properties.put("name", new ResourceProperty(r, "name", resultSet.getString("shortname")));
                resources.add(r);
                this.id++;
            }


            //adding resource, type: module
            resultSet = statement.executeQuery("SELECT id, name " +
                    "FROM  m_modules");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("module", properties);
                r.setIdResource(this.id);
                properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                properties.put("name", new ResourceProperty(r, "name", resultSet.getString("name")));
                resources.add(r);
                this.id++;
            }

            //adding resource, type: course_modules
            resultSet = statement.executeQuery("SELECT id, course, module, added " +
                    "FROM  m_course_modules");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("course_module", properties);
                r.setIdResource(this.id);
                properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                properties.put("timecreated", new ResourceProperty(r, "timecreated", resultSet.getString("added")));

                for(Resource rs: resources) {
                    if(rs.getType().equals("course")
                            && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("course"))) {
                        properties.put("courseid", new ResourceProperty(r, "courseid", rs.getIdResource() + ""));
                    }
                    else if(rs.getType().equals("module")
                            && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("module")))
                        properties.put("moduleid", new ResourceProperty(r, "moduleid", rs.getIdResource()+""));
                }

                resources.add(r);
                this.id++;
            }



            //adding resource, type: file
            resultSet = statement.executeQuery("SELECT id, component, filearea, filepath, userid, filesize, mimetype, source, author, timecreated, timemodified " +
                    "FROM  m_files");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("file", properties);
                r.setIdResource(this.id);
                if(resultSet.getString("author") != null) {
                    properties.put("sourceid", new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                    properties.put("timecreated", new ResourceProperty(r, "timecreated", resultSet.getString("timecreated")));
                    properties.put("timemodified", new ResourceProperty(r, "timemodified", resultSet.getString("timemodified")));
                    properties.put("component", new ResourceProperty(r, "component", resultSet.getString("component")));
                    properties.put("filearea", new ResourceProperty(r, "filearea", resultSet.getString("filearea")));
                    properties.put("filepath", new ResourceProperty(r, "filepath", resultSet.getString("filepath")));
                    for(User u: users) {
                        if(u.getProperties().get("sourceid").getValue().equals(resultSet.getString("userid")))
                            properties.put("userid", new ResourceProperty(r, "userid", u.getIdUser()+""));
                    }
                    properties.put("filesize", new ResourceProperty(r, "filesize", resultSet.getString("filesize")));
                    properties.put("mimetype", new ResourceProperty(r, "mimetype", resultSet.getString("mimetype")));
                    properties.put("source", new ResourceProperty(r, "source", resultSet.getString("source")));
                    properties.put("author", new ResourceProperty(r, "author", resultSet.getString("author")));
                    resources.add(r);
                    this.id++;
                }
            }


            //adding action file submission
            resultSet = statement.executeQuery("SELECT id, userid, author, timecreated, timemodified " +
                    "FROM  m_files");
            while(resultSet.next()) {
                if(resultSet.getString("author") != null) {
                    Map<String, ActionProperty> properties = new HashMap<>();
                    Action a = new Action();
                    a.setProperties(properties);
                    if(resultSet.getString("timecreated").equals(resultSet.getString("timemodified"))) {
                        a.setType('c');
                        a.setMillis(resultSet.getLong("timecreated") * 1000);
                    }
                    else {
                        a.setType('u');
                        a.setMillis(resultSet.getLong("timemodified") * 1000);
                    }
                    for (Resource rs : resources) {
                        if (rs.getType().equalsIgnoreCase("file")
                                && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("id")))
                            a.setResource(rs);
                    }
                    for (User u : users) {
                        if (u.getProperties().get("sourceid").getValue().equals(resultSet.getString("userid")))
                            a.setUserFrom(u);
                    }
                    actions.add(a);
                }
            }


            //adding resoure, type: scorm
            resultSet = statement.executeQuery("SELECT id, course, name, maxgrade " +
                    "FROM  m_scorm");
            while(resultSet.next()) {
                Map<String, ResourceProperty> properties = new HashMap<>();
                Resource r = new Resource("scorm", properties);
                r.setIdResource(this.id);
                properties.put("sourceid",new ResourceProperty(r, "sourceid", resultSet.getString("id")));
                for(Resource rs: resources) {
                    if(rs.getType().equals("course")
                            && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("course"))) {
                        properties.put("courseid", new ResourceProperty(r, "courseid", rs.getIdResource() + ""));
                        r.setIdResourceAssociated(rs);
                    }
                }
                properties.put("name", new ResourceProperty(r, "name", resultSet.getString("name")));
                properties.put("grade", new ResourceProperty(r, "grade", resultSet.getString("maxgrade")));
                resources.add(r);
                this.id++;
            }


            //adding action crud
            resultSet = statement.executeQuery("SELECT crud, target, eventname, userid, objectid, objecttable, courseid, relateduserid, timecreated " +
                    "FROM  m_logstore_standard_log ");
            while(resultSet.next()) {
                Map<String, ActionProperty> properties = new HashMap<>();
                Action a = new Action();
                boolean read = true;
                switch (resultSet.getString("target")) {
                    case "role": {
                        for(Resource rs: resources) {
                            read = true;
                            if(rs.getType().equals("role")
                                    && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("objectid"))) {
                                a.setResource(rs);
                                break;
                            }
                            else
                                read = false;
                        }
                    }
                        break;
                    case "course": {
                        for(Resource rs: resources) {
                            read = true;
                            if(rs.getType().equals("course")
                                    && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("courseid"))) {
                                a.setResource(rs);
                                break;
                            }
                            else
                                read = false;
                        }
                    }
                        break;
                    case "comment": {
                        for(Resource rs: resources) {
                            read = true;
                            if(rs.getType().equals("comment")
                                    && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("objectid"))) {
                                a.setResource(rs);
                                break;
                            }
                            else
                                read = false;
                        }
                    }
                        break;
                    case "message": {
                        for(Resource rs: resources) {
                            read = true;
                            if(rs.getType().equals("message")
                                    && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("objectid"))) {
                                a.setResource(rs);
                                break;
                            }
                            else
                                read = false;
                        }
                    }
                        break;
                    case "course_catergory": {
                        for(Resource rs: resources) {
                            read = true;
                            if(rs.getType().equals("course_catergory")
                                    && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("objectid"))) {
                                a.setResource(rs);
                                break;
                            }
                            else
                                read = false;
                        }
                    }
                        break;
                    case "course_module": {
                        if(resultSet.getString("objecttable").equals("course_modules")) {
                            for(Resource rs: resources) {
                                read = true;
                                if(rs.getType().equals("course_module")
                                        && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("objectid"))) {
                                    a.setResource(rs);
                                    break;
                                }
                                else
                                    read = false;
                            }
                        }
                        else if(resultSet.getString("objecttable").equals("scorm")) {
                            for(Resource rs: resources) {
                                read = true;
                                if(rs.getType().equals("scorm")
                                        && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("objectid"))) {
                                    a.setResource(rs);
                                    break;
                                }
                                else
                                    read = false;
                            }
                        }
                        else if(resultSet.getString("objecttable").equals("resource")) {
                            for(Resource rs: resources) {
                                read = true;
                                if(rs.getType().equals("lesson")
                                        && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("objectid"))) {
                                    a.setResource(rs);
                                    break;
                                }
                                else
                                    read = false;
                            }
                        }
                    }
                        break;
                    case "calendar_event": {
                        for(Resource rs: resources) {
                            read = true;
                            if(rs.getType().equals("event")
                                    && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("objectid"))) {
                                a.setResource(rs);
                                break;
                            }
                            else
                                read = false;
                        }
                    }
                        break;
                    case "status": {
                        for(Resource rs: resources) {
                            read = true;
                            if(rs.getType().equals("scorm")
                                    && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("objectid"))) {
                                a.setResource(rs);
                                break;
                            }
                            else
                                read = false;
                        }
                    }
                        break;
                    case "user":
                        break;
                    case "user_profile":
                        break;

                    default:
                        read = false;
                }

                if(read) {
                    a.setMillis(resultSet.getLong("timecreated")*1000);
                    a.setProperties(properties);
                    for(User u: users) {
                        if(u.getProperties().get("sourceid").getValue().equals(resultSet.getString("userid")))
                            a.setUserFrom(u);
                    }
                    for(User u: users) {
                        if(resultSet.getString("relateduserid") != null
                                && u.getProperties().get("sourceid").getValue().equals(resultSet.getString("relateduserid")))
                            a.setUserTo(u);
                    }
                    a.setType(resultSet.getString("crud").charAt(0));
                    properties.put("eventname", new ActionProperty(a, "eventname", resultSet.getString("eventname")));
                    properties.put("target", new ActionProperty(a, "target", resultSet.getString("target")));
                    actions.add(a);
                }
            }


            //adding action scorm
            resultSet = statement.executeQuery("SELECT userid, scormid, element, value, timemodified " +
                    "FROM  m_scorm_scoes_track");
            while(resultSet.next()) {
                Map<String, ActionProperty> properties = new HashMap<>();
                Action a = new Action();
                a.setMillis(resultSet.getLong("timemodified")*1000);
                a.setProperties(properties);
                for(Resource rs: resources) {
                    if(rs.getType().equals("scorm")
                            && rs.getProperties().get("sourceid").getValue().equals(resultSet.getString("scormid")))
                        a.setResource(rs);
                }
                properties.put("target", new ActionProperty(a, "target", "scorm"));
                for(User u: users) {
                    if(u.getProperties().get("sourceid").getValue().equals(resultSet.getString("userid")))
                        a.setUserFrom(u);
                }
                String element = resultSet.getString("element");
                if(element.equals("x.start.time")) {
                    element = "starttime";
                    properties.put(element, new ActionProperty(a, element, resultSet.getString("value")));
                }
                else if (element.equals("cmi.core.total_time") || element.equals("cmi.total_time")) {
                    element = "totaltime";
                    properties.put(element, new ActionProperty(a, element, ""+millis(resultSet.getString("value"))));
                }
                a.setType('r');
                actions.add(a);
            }

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

        return new Model(resources, users, actions);
    }


    private long millis(String time) {
        String[] hms = time.split(":");
        long total = 0;
        try {
            double sec = Integer.parseInt(hms[0]) * 3600
                    + Integer.parseInt(hms[1]) * 60
                    + Double.parseDouble(hms[2]);
            total = (long) sec * 1000;
        } catch(NumberFormatException e) {

        }
        return total;
    }
}
