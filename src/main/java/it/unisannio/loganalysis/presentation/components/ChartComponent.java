package it.unisannio.loganalysis.presentation.components;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import it.unisannio.loganalysis.analysis.QueryType;
import it.unisannio.loganalysis.analysis.QueryTypeHandler;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Vector;




/**
 * Created by graziano on 04/07/16.
 */
public class ChartComponent extends CustomComponent {

    private Chart chart;



    public ChartComponent() {
        VerticalLayout layout = new VerticalLayout();


        layout.setSizeFull();
        layout.setResponsive(true);
        chart = new Chart();
        chart.setSizeFull();
        chart.setResponsive(true);


        //detachButton = new Button("Stacca");

        layout.addComponent(chart);
        //layout.addComponent(detachButton);

        layout.setSizeFull();

        setCompositionRoot(layout);
    }

    public void setData(QueryType query, ListVector data) {
      /*  ChartType type = null;
        if(query == QueryType.RESOURCE_USAGE || query == QueryType.RESOURCE_USAGE_TIME) {
            type = ChartType.COLUMN;
            data.getElementAsVector("");
        }
        else if(query== QueryType.DAILY_ACTIVE_USERS || query== QueryType.DAILY_ACTIVE_RESOURCES || query == QueryType.TIME_RANGE_USAGE
                || query == QueryType.RESOURCE_ADDED_PER_DAY)
            type = ChartType.LINE;

        else if(query == QueryType.MOST_USED_OS)
            type = ChartType.PIE;

        else if (query == QueryType.DAILY_ACTIVITIES) {}
        */

        Configuration configuration = chart.getConfiguration();
        configuration.setTitle(QueryTypeHandler.getDescription(query));

        switch(query) {
            case RESOURCE_USAGE:
                configuration.getChart().setType(ChartType.COLUMN);
                configuration.getChart().setZoomType(ZoomType.XY);

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
                configuration.getChart().setZoomType(ZoomType.XY);

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
                configuration.getChart().setZoomType(ZoomType.XY);

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
                configuration.getChart().setZoomType(ZoomType.XY);

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

                /*
                Legend legend = new Legend();
                legend.setLayout(LayoutDirection.VERTICAL);
                legend.setAlign(HorizontalAlign.LEFT);
                legend.setX(120);
                legend.setVerticalAlign(VerticalAlign.TOP);
                legend.setY(100);
                legend.setFloating(true);
                legend.setBackgroundColor(new SolidColor("#FFFFFF"));
                configuration.setLegend(legend);
                */



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
                configuration.getChart().setZoomType(ZoomType.XY);

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
                configuration.getChart().setZoomType(ZoomType.XY);

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
