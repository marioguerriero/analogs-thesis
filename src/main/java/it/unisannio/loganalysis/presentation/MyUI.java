package it.unisannio.loganalysis.presentation;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import it.unisannio.loganalysis.presentation.components.*;

import javax.servlet.annotation.WebServlet;

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

        logSourceSelector = new LogSourceSelector();
        logSourceSelector.setAddSourceListener(() -> showAddSourceDialog());

        queryParameterSelector = new QueryParameterSelector();
        queryParameterSelector.setExecuteListener(() -> {

        });

        chartView = new ChartComponent();

        horizontalLayout.addComponents(queryParameterSelector, chartView);
        horizontalLayout.setSpacing(true);

        verticalLayout.addComponents(logSourceSelector, horizontalLayout);
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);

        setContent(verticalLayout);
    }

    private void showAddSourceDialog() {
        Window window = new Window();
        window.setContent(new AddLogSourceForm());
        window.center();
        addWindow(window);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
