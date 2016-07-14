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

    private VerticalLayout chartLayout;
    private ComboBox selectChart;


    public ChartComponent() {
        Panel panel = new Panel("Chart");

        chartLayout = new VerticalLayout();
        chartLayout.setSizeFull();
        chartLayout.setResponsive(true);
        chartLayout.setSizeFull();
        selectChart = new ComboBox("Chart Type");
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



        chartLayout.removeAllComponents();
        chartLayout.addComponent(chart);

        switch(query) {
            case RESOURCE_USAGE: // Doppio grafico ok

                for(QueryType q : QueryTypeHandler.getQueries()) {
                    selectChart.addItem(q);
                    selectChart.setItemCaption(q, QueryTypeHandler.getDescription(q));
                }

                chartLayout.addComponent(selectChart);
                selectChart.addItem(configuration.getChart().getType());
                configuration.getChart().setType(ChartType.COLUMN);
                configuration.getChart().setZoomType(ZoomType.XY);
                Vector type = data.getElementAsVector("type");
                Vector usage = data.getElementAsVector("usage");
                DataSeries dataSeries = new DataSeries("Numero risorse");
                PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
                plotOptionsColumn.setColorByPoint(true);
                dataSeries.setPlotOptions(plotOptionsColumn);

                XAxis x = new XAxis();

                YAxis y = new YAxis();
                for(int i=0; i< type.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(type.getElementAsString(i), usage.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                    x.addCategory(type.getElementAsString(i));
                    y.addCategory(usage.getElementAsString(i));
                }
                configuration.addxAxis(x);
                configuration.addyAxis(y);

                configuration.setSeries(dataSeries);
                chart.drawChart(configuration);

                // Second chart
                Chart chart1 = new Chart();
                chart1.setResponsive(true);
                Configuration conf1 = chart1.getConfiguration();
                conf1.getChart().setType(ChartType.PIE);
                conf1.setTitle(QueryTypeHandler.getDescription(query));
                PlotOptionsPie plotOptionsPie1 = new PlotOptionsPie();
                plotOptionsPie1.setCursor(Cursor.POINTER);
                DataSeries dataSeries1 = new DataSeries();
                dataSeries1.setPlotOptions(plotOptionsPie1);

                for(int i=0; i< type.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(type.getElementAsString(i), usage.getElementAsDouble(i));
                    dataSeries1.addItemWithDrilldown(dataSeriesItem);

                }

                conf1.setSeries(dataSeries1);
                chartLayout.addComponent(chart1);
                chart1.drawChart(conf1);




                break;
            case RESOURCE_USAGE_TIME: //doppio grafico ok

                configuration.getChart().setType(ChartType.COLUMN);
                configuration.getChart().setZoomType(ZoomType.XY);

                Vector type_usage = data.getElementAsVector("type");
                Vector time = data.getElementAsVector("time");
                dataSeries = new DataSeries("Tempo di utilizzo");
                plotOptionsColumn = new PlotOptionsColumn();
                plotOptionsColumn.setColorByPoint(true);
                dataSeries.setPlotOptions(plotOptionsColumn);
                XAxis x1 = new XAxis();
                YAxis y1 = new YAxis();

                String srut="";
                for(int i=0; i< type_usage.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(type_usage.getElementAsString(i), time.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                    x1.addCategory(type_usage.getElementAsString(i));
                    y1.addCategory(time.getElementAsString(i));

                }
                configuration.setSeries(dataSeries);
                chart.drawChart(configuration);


                // Second chart
                Chart chart2 = new Chart();
                chart2.setResponsive(true);
                Configuration conf2 = chart2.getConfiguration();
                conf2.getChart().setType(ChartType.PIE);
                conf2.setTitle(QueryTypeHandler.getDescription(query));
                DataSeries dataSeries2 = new DataSeries();

                for(int i=0; i< type_usage.length(); i++){
                    if(time.getElementAsDouble(i) != 0) {
                        DataSeriesItem dataSeriesItem = new DataSeriesItem(type_usage.getElementAsString(i), time.getElementAsDouble(i));
                        dataSeries2.addItemWithDrilldown(dataSeriesItem);
                    }
                }
                PlotOptionsPie plotOptionsPie2 = new PlotOptionsPie();
                plotOptionsPie2.setCursor(Cursor.POINTER);
                dataSeries2.setPlotOptions(plotOptionsPie2);

                conf2.setSeries(dataSeries2);
                chartLayout.addComponent(chart2);
                chart2.drawChart(conf2);







                break;
            case DAILY_ACTIVE_USERS: //Doppio grafico ok, aggiungi dettagli

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


                ListSeries ls = new ListSeries("Utenti attivi");
                ls.setData(activeUsersArr);
                configuration.setSeries(ls);
                chart.drawChart(configuration);

                // Second chart
                Chart chart3 = new Chart();
                chart3.setResponsive(true);
                Configuration conf3 = chart3.getConfiguration();
                conf3.getChart().setType(ChartType.COLUMN);
                conf3.getChart().setZoomType(ZoomType.XY);
                conf3.setTitle(QueryTypeHandler.getDescription(query));

                conf3.setSeries(ls);
                chartLayout.addComponent(chart3);
                chart3.drawChart(conf3);



                break;
            case DAILY_ACTIVE_RESOURCES:   //Doppio grafico OK

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

                ls = new ListSeries("Numero Risorse");
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
            case TIME_RANGE_USAGE:      //Doppio grafico OK
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

                Chart chart5 = new Chart();
                chart5.setResponsive(true);
                Configuration conf5 = chart5.getConfiguration();
                conf5.getChart().setType(ChartType.COLUMN);
                conf5.setTitle("");

                conf5.setSeries(ls);

                chartLayout.addComponent(chart5);
                chart5.drawChart(conf5);

                break;
            case MOST_USED_OS: //Doppio grafico ok perfetto

                configuration.getChart().setType(ChartType.PIE);
                Vector os = data.getElementAsVector("os");
                Vector count = data.getElementAsVector("count");
                dataSeries = new DataSeries("Utilizzi");
                PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
                plotOptionsPie.setCursor(Cursor.POINTER);
                dataSeries.setPlotOptions(plotOptionsPie);
                for(int i=0; i< os.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(os.getElementAsString(i), count.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                }

                configuration.setSeries(dataSeries);
                chart.drawChart(configuration);

                //Second Chart
                Chart chart6 = new Chart();
                chart6.setResponsive(true);
                Configuration conf6 = new Configuration();
                conf6.setTitle(QueryTypeHandler.getDescription(query));
                conf6.getChart().setType(ChartType.COLUMN);
                conf6.getChart().setZoomType(ZoomType.XY);
                DataSeries dataSeries3 = new DataSeries("Utilizzi ");
                PlotOptionsColumn plotOptionsColumn2 = new PlotOptionsColumn();
                plotOptionsColumn2.setColorByPoint(true);
                dataSeries3.setPlotOptions(plotOptionsColumn2);

                XAxis x6 = new XAxis();
                for(int i=0; i< os.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(os.getElementAsString(i), count.getElementAsDouble(i));
                    dataSeries3.addItemWithDrilldown(dataSeriesItem);
                    x6.addCategory(os.getElementAsString(i));
                }

                conf6.addxAxis(x6);
                conf6.setSeries(dataSeries3);
                chartLayout.addComponent(chart6);
                chart6.drawChart(conf6);




            case RESOURCE_ADDED_PER_DAY:   // doppio grafico ok

                configuration.getChart().setType(ChartType.LINE);
                configuration.getChart().setZoomType(ZoomType.XY);
                days = data.getElementAsVector("days"); // ArrayIndexOutOfBoundsException: -1 ???
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

                Chart chart7 = new Chart();
                chart7.setResponsive(true);
                Configuration conf7 = chart7.getConfiguration();
                conf7.getChart().setType(ChartType.COLUMN);
                conf7.setTitle("");

                XAxis xAxis1 = new XAxis();
                xAxis1.setTitle("Numero di risorse");
                YAxis yAxis1 = new YAxis();
                yAxis1.setTitle("Orario");


                conf7.setSeries(ls);
                chartLayout.addComponent(chart7);
                chart7.drawChart(conf7);
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
        }

    }
}
