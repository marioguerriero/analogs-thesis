package it.unisannio.loganalysis.presentation.components;


import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;

import com.vaadin.ui.*;
import it.unisannio.loganalysis.analysis.QueryType;
import it.unisannio.loganalysis.analysis.QueryTypeHandler;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Vector;
//

/**
 * Created by graziano on 04/07/16.
 */
public class ChartComponent extends CustomComponent {

    private HorizontalLayout chartLayout;


    public ChartComponent() {
        Panel panel = new Panel("Chart");
        chartLayout = new HorizontalLayout();
        chartLayout.setSizeFull();
        chartLayout.setResponsive(true);
        chartLayout.setSizeFull();
        panel.setResponsive(true);
        panel.setContent(chartLayout);
        setCompositionRoot(panel);
    }



    public void setData(QueryType query, ListVector data) {
        Chart chart = new Chart();
        chart.setResponsive(true);
        Configuration configuration = chart.getConfiguration();
        configuration.setTitle(QueryTypeHandler.getDescription(query));
        chartLayout.removeAllComponents();
        chartLayout.addComponent(chart);

        switch(query) {
            case RESOURCE_USAGE:

                configuration.getChart().setType(ChartType.COLUMN);
                configuration.getChart().setZoomType(ZoomType.XY);
                Vector type = data.getElementAsVector("type");
                Vector usage = data.getElementAsVector("usage");
                DataSeries dataSeries = new DataSeries("Risorse");
                PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
                plotOptionsColumn.setColorByPoint(true);
                dataSeries.setPlotOptions(plotOptionsColumn);

                XAxis x = new XAxis();
                for(int i=0; i< type.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(type.getElementAsString(i), usage.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                    x.addCategory(type.getElementAsString(i));
                }
                configuration.addxAxis(x);


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
                for(int i = 0; i < activeResources.length(); i++) {
                    activeResourcesArr[i] = activeResources.getElementAsDouble(i);
                }

                ls = new ListSeries("Risorse");
                ls.setData(activeResourcesArr);
                configuration.setSeries(ls);
                chart.drawChart(configuration);

                // Second chart
                Chart chart4 = new Chart();
                chart4.setResponsive(true);
                Configuration conf4 = chart4.getConfiguration();
                conf4.getChart().setType(ChartType.COLUMN);
                conf4.setTitle("");

                conf4.setSeries(ls);
                chartLayout.addComponent(chart4);
                chart4.drawChart(conf4);





                break;
            case DAILY_ACTIVITIES:
                configuration.getChart().setType(ChartType.LINE);
                configuration.getChart().setZoomType(ZoomType.XY);

                days = data.getElementAsVector("days");
                daysArr = new String[days.length()];
                for(int i = 0; i < days.length(); i++) {
                    daysArr[i] = days.getElementAsString(i);
                }


                x = new XAxis();
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


                for(int i = 0; i < activeResources.length(); i++) {
                    activeResourcesArr[i] = activeResources.getElementAsDouble(i);

                }
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
                for(int i = 0; i < hourCount.length(); i++){
                    hourCountArr[i] = hourCount.getElementAsDouble(i);
                }

                ls = new ListSeries("Numero di utilizzi");
                ls.setData(hourCountArr);
                configuration.setSeries(ls);
                chart.drawChart(configuration);

                // Second chart
                Chart chart1 = new Chart();
                chart1.setResponsive(true);
                Configuration conf1 = chart1.getConfiguration();
                conf1.getChart().setType(ChartType.COLUMN);
                conf1.setTitle("");

                conf1.setSeries(ls);

                chartLayout.addComponent(chart1);
                chart1.drawChart(conf1);

                break;
            case MOST_USED_OS:

                configuration.getChart().setType(ChartType.PIE);
                Vector os = data.getElementAsVector("os");
                Vector count = data.getElementAsVector("count");
                dataSeries = new DataSeries("Utilizzi");
                PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
                plotOptionsPie.setCursor(Cursor.POINTER);
                dataSeries.setPlotOptions(plotOptionsPie);
                String sm = "";
                for(int i=0; i< os.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(os.getElementAsString(i), count.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);

                    sm = sm+os.getElementAsString(i)+": "+ count.getElementAsInt(i)+"\n";

                }
                configuration.setSeries(dataSeries);
                chart.drawChart(configuration);

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
                for(int i = 0; i < resourcesAdded.length(); i++) {
                    resourcesAddedArr[i] = resourcesAdded.getElementAsDouble(i);
                }
                ls = new ListSeries("Numero di risorse");
                XAxis xAxis = new XAxis();
                xAxis.setTitle("Orario");
                YAxis yAxis = new YAxis();
                yAxis.setTitle("Numero di risorse");
                ls.setData(resourcesAddedArr);
                configuration.setSeries(ls);
                chart.drawChart(configuration);

                // Second chart
                Chart chart2 = new Chart();
                chart2.setResponsive(true);
                Configuration conf2 = chart2.getConfiguration();
                conf2.getChart().setType(ChartType.COLUMN);
                conf2.setTitle("");
                XAxis xAxis1 = new XAxis();
                xAxis1.setTitle("Numero di risorse");
                YAxis yAxis1 = new YAxis();
                yAxis1.setTitle("Orario");

                conf2.setSeries(ls);
                chartLayout.addComponent(chart2);
                chart2.drawChart(conf2);
                break;
        }

    }






}