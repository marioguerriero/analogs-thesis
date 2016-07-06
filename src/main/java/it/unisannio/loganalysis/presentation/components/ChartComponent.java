package it.unisannio.loganalysis.presentation.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import it.unisannio.loganalysis.analysis.Query;
import it.unisannio.loganalysis.analysis.QueryType;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Vector;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mario on 04/07/16.
 */
public class ChartComponent extends CustomComponent {

    private Chart chart;
    private Button detachButton;


    public ChartComponent() {
        VerticalLayout layout = new VerticalLayout();

        chart = new Chart();
        chart.setSizeFull();

        //detachButton = new Button("Stacca");

        layout.addComponent(chart);
        //layout.addComponent(detachButton);

        layout.setSizeFull();

        setCompositionRoot(layout);
    }

    public void setData(Query query, ListVector data) {
        ChartType type = null;
        if(query == Query.RESOURCE_USAGE || query == Query.RESOURCE_USAGE_TIME) {
            type = ChartType.COLUMN;
            data.getElementAsVector("");
        }
        else if(query== Query.DAILY_ACTIVE_USERS || query== Query.DAILY_ACTIVE_RESOURCES || query == Query.TIME_RANGE_USAGE
                || query == Query.RESOURCE_ADDED_PER_DAY)
            type = ChartType.LINE;

        else if(query == Query.MOST_USED_OS)
            type = ChartType.PIE;

        else if (query == Query.DAILY_ACTIVITIES) {}

        Configuration configuration = chart.getConfiguration();

        switch(query) {
            case RESOURCE_USAGE:
                chart.getConfiguration();
                break;
            case RESOURCE_USAGE_TIME:
                break;
            case DAILY_ACTIVE_USERS:
                break;
            case DAILY_ACTIVE_RESOURCES:
                break;
            case DAILY_ACTIVITIES:
                break;
            case TIME_RANGE_USAGE:
                configuration.getChart().setType(ChartType.LINE);
                configuration.setTitle(QueryType.getDescription(query));

                Vector hours = data.getElementAsVector("hours");
                String[] hoursArr = new String[hours.length()];
                for(int i = 0; i < hours.length(); i++)
                    hoursArr[i] = hours.getElementAsString(i);
                configuration.getxAxis().setCategories(hoursArr);

                Vector hourCount = data.getElementAsVector("hourCount");
                Double[] hourCountArr = new Double[hourCount.length()];
                for(int i = 0; i < hourCount.length(); i++)
                    hourCountArr[i] = hourCount.getElementAsDouble(i);

                ListSeries ls = new ListSeries();
                ls.setData(hourCountArr);

                System.out.println(ls);

                configuration.addSeries(ls);

                chart.drawChart(configuration);

                break;
            case MOST_USED_OS:
                break;
            case RESOURCE_ADDED_PER_DAY:
                break;
        }

    }


}
