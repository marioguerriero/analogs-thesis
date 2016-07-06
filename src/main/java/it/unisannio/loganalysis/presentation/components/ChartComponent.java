package it.unisannio.loganalysis.presentation.components;

import com.mysql.fabric.xmlrpc.base.Data;
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
      /*  ChartType type = null;
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
        */

        Configuration configuration = chart.getConfiguration();
        configuration.setTitle(QueryType.getDescription(query));

        switch(query) {
            case RESOURCE_USAGE:
                configuration.getChart().setType(ChartType.COLUMN);

                Vector type = data.getElementAsVector("type");
                Vector usage = data.getElementAsVector("usage");
                DataSeries dataSeries = new DataSeries();
                PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
                plotOptionsColumn.setColorByPoint(true);
                dataSeries.setPlotOptions(plotOptionsColumn);
                for(int i=0; i< type.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(type.getElementAsString(i), usage.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                }
                configuration.setSeries(dataSeries);
                chart.drawChart(configuration);

                break;
            case RESOURCE_USAGE_TIME:

                configuration.getChart().setType(ChartType.COLUMN);

                Vector type_usage = data.getElementAsVector("type");
                Vector time = data.getElementAsVector("time");
                dataSeries = new DataSeries();
                plotOptionsColumn = new PlotOptionsColumn();
                plotOptionsColumn.setColorByPoint(true);
                dataSeries.setPlotOptions(plotOptionsColumn);
                for(int i=0; i< type_usage.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(type_usage.getElementAsString(i), time.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                }
                configuration.setSeries(dataSeries);
                chart.drawChart(configuration);

                break;
            case DAILY_ACTIVE_USERS:

                configuration.getChart().setType(ChartType.LINE);

                Vector days = data.getElementAsVector("days");
                String[] daysArr = new String[days.length()];
                for(int i = 0; i < days.length(); i++) {
                    daysArr[i] = days.getElementAsString(i);
                }


                configuration.getxAxis().setCategories(daysArr);

                Vector activeUsers = data.getElementAsVector("activeUsers");
                Double[] activeUsersArr = new Double[activeUsers.length()];
                for(int i = 0; i < activeUsers.length(); i++) {
                    System.out.println(activeUsers.getElementAsDouble(i));
                    activeUsersArr[i] = activeUsers.getElementAsDouble(i);
                }
                ListSeries ls = new ListSeries();
                ls.setData(activeUsersArr);

                configuration.setSeries(ls);

                chart.drawChart(configuration);


                break;
            case DAILY_ACTIVE_RESOURCES:

                configuration.getChart().setType(ChartType.LINE);

                 days = data.getElementAsVector("days");
                 daysArr = new String[days.length()];
                for(int i = 0; i < days.length(); i++) {
                    daysArr[i] = days.getElementAsString(i);
                }


                configuration.getxAxis().setCategories(daysArr);


                Vector activeResources = data.getElementAsVector("activeResources");
                Double[] activeResourcesArr = new Double[activeResources.length()];
                for(int i = 0; i < activeResources.length(); i++)
                    activeResourcesArr[i] = activeResources.getElementAsDouble(i);

                ls = new ListSeries();
                ls.setData(activeResourcesArr);

                configuration.setSeries(ls);

                chart.drawChart(configuration);
                break;
            case DAILY_ACTIVITIES:

                configuration.getChart().setType(ChartType.LINE);
                configuration.getChart().setZoomType(ZoomType.XY);

                days = data.getElementAsVector("days");
                daysArr = new String[days.length()];
                for(int i = 0; i < days.length(); i++) {
                    daysArr[i] = days.getElementAsString(i);
                }

                XAxis x = new XAxis();
                x.setCategories(daysArr);
                configuration.addxAxis(x);



                 activeUsers = data.getElementAsVector("activeUsers");
                activeUsersArr = new Double[activeUsers.length()];
                for(int i = 0; i < activeUsers.length(); i++) {
                  //  System.out.println(activeUsers.getElementAsDouble(i));
                    activeUsersArr[i] = activeUsers.getElementAsDouble(i);
                }

                DataSeries seriesU = new DataSeries();
                seriesU.setPlotOptions(new PlotOptionsColumn());
                seriesU.setName("Active Users");
                seriesU.setData(activeUsersArr);
               // configuration.setSeries(seriesU);


                activeResources = data.getElementAsVector("activeResources");
                activeResourcesArr = new Double[activeResources.length()];
                for(int i = 0; i < activeResources.length(); i++)
                    activeResourcesArr[i] = activeResources.getElementAsDouble(i);


                DataSeries series = new DataSeries();
                PlotOptionsSpline plotOptions = new PlotOptionsSpline();
                series.setPlotOptions(plotOptions);
                series.setName("Active Resources");
                series.setData(activeResourcesArr);
                configuration.setSeries(seriesU,series);

                chart.drawChart(configuration);



                break;
            case TIME_RANGE_USAGE:
                configuration.getChart().setType(ChartType.LINE);

                Vector hours = data.getElementAsVector("hours");
                String[] hoursArr = new String[hours.length()];
                for(int i = 0; i < hours.length(); i++) {
                    hoursArr[i] = hours.getElementAsString(i);
                }


                configuration.getxAxis().setCategories(hoursArr);

                Vector hourCount = data.getElementAsVector("hourCount");
                Double[] hourCountArr = new Double[hourCount.length()];
                for(int i = 0; i < hourCount.length(); i++)
                    hourCountArr[i] = hourCount.getElementAsDouble(i);

                 ls = new ListSeries();
                ls.setData(hourCountArr);

                configuration.setSeries(ls);

                chart.drawChart(configuration);

                break;
            case MOST_USED_OS:
                configuration.getChart().setType(ChartType.PIE);


                Vector os = data.getElementAsVector("os");
                Vector count = data.getElementAsVector("count");

                 dataSeries = new DataSeries();
                PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
                plotOptionsPie.setCursor(Cursor.POINTER);
                dataSeries.setPlotOptions(plotOptionsPie);

                for(int i=0; i< os.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(os.getElementAsString(i), count.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                }
                System.out.println(data);

                configuration.setSeries(dataSeries);

                chart.drawChart(configuration);


                break;
            case RESOURCE_ADDED_PER_DAY:

                configuration.getChart().setType(ChartType.LINE);

                 days = data.getElementAsVector("days");
                daysArr = new String[days.length()];
                for(int i = 0; i < days.length(); i++) {
                    daysArr[i] = days.getElementAsString(i);
                }


                configuration.getxAxis().setCategories(daysArr);

                Vector resourcesAdded = data.getElementAsVector("resourcesAdded");
                Double[] resourcesAddedArr = new Double[resourcesAdded.length()];
                for(int i = 0; i < resourcesAdded.length(); i++)
                    resourcesAddedArr[i] = resourcesAdded.getElementAsDouble(i);

                 ls = new ListSeries();
                ls.setData(resourcesAddedArr);

                configuration.setSeries(ls);

                chart.drawChart(configuration);



                break;
        }

    }


}
