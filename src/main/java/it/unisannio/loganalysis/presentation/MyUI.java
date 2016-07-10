package it.unisannio.loganalysis.presentation;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

import it.unisannio.loganalysis.analysis.AnalyzerController;
import it.unisannio.loganalysis.analysis.TableHandler;
import it.unisannio.loganalysis.extractor.FacadeLogSource;
import it.unisannio.loganalysis.presentation.components.*;
import org.renjin.sexp.ListVector;

import javax.script.ScriptException;
import javax.servlet.annotation.WebServlet;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Title("Log Analysis Tool")
@Widgetset("it.unisannio.loganalysis.MyAppWidgetset")
public class MyUI extends UI {

    private LogSourceSelector logSourceSelector;
    private QueryParameterSelector queryParameterSelector;
    private ChartComponent chartView;


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout verticalLayout = new VerticalLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();



        try {
            logSourceSelector = new LogSourceSelector();
            logSourceSelector.setAddSourceListener(this::showAddSourceDialog);
            logSourceSelector.setValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
                try {

                    TableHandler controller = TableHandler.getInstance();
                    controller.setDbSource(valueChangeEvent.getProperty().getValue().toString());
                    controller.loadTables();
                    queryParameterSelector.updateValues();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ScriptException e) {
                    e.printStackTrace();
                } catch (TableHandler.NullDataSourceException e) {
                    e.printStackTrace();
                }
            });
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        queryParameterSelector = new QueryParameterSelector();
        queryParameterSelector.setExecuteQueryListener(() -> {
            AnalyzerController analyzerController = AnalyzerController.getInstance();
            Integer[] users = queryParameterSelector.getUsers();
            ListVector df = analyzerController.performQuery(queryParameterSelector.getQueryType(),
                    users.length > 0 ? users : null,
                    queryParameterSelector.getFrom(), queryParameterSelector.getTo(),
                    queryParameterSelector.getAttributes(),queryParameterSelector.isNormalized());
            chartView.setData(queryParameterSelector.getQueryType(), df);
        });



        chartView = new ChartComponent();

        horizontalLayout.addComponents(queryParameterSelector, chartView);
        horizontalLayout.setExpandRatio(chartView, 65);
        horizontalLayout.setExpandRatio(queryParameterSelector,35);
        horizontalLayout.setSizeFull();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setResponsive(true);


        verticalLayout.addComponents(logSourceSelector, horizontalLayout);
        verticalLayout.setExpandRatio(logSourceSelector,30);
        verticalLayout.setExpandRatio(horizontalLayout,70);
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        setContent(verticalLayout);





    }

    private void showAddSourceDialog() {
        Window window = new Window();
        AddLogSourceForm form = new AddLogSourceForm();
        window.setContent(form);

        form.setAddListener((Button.ClickListener) clickEvent -> {
            try {
                FacadeLogSource.getInstance().addDataSource(
                        form.getType(), form.getDialect(), form.getHost(), form.getPort(), form.getSourceDb(),
                        form.getUsername(), form.getpassword());
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        window.center();
        addWindow(window);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
