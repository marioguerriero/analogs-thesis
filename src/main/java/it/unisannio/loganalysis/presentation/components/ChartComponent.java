package it.unisannio.loganalysis.presentation.components;


import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;

import com.vaadin.ui.*;
import it.unisannio.loganalysis.analysis.QueryType;
import it.unisannio.loganalysis.analysis.QueryTypeHandler;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Vector;


/**
 * Created by graziano on 04/07/16.
 */
public class ChartComponent extends CustomComponent {

    private Chart chart;
    private TextArea field;




    public ChartComponent() {

        Panel panel = new Panel("Chart");
        HorizontalLayout layout = new HorizontalLayout();

        layout.setSizeFull();
        layout.setResponsive(true);
        chart = new Chart();
        chart.setResponsive(true);
        layout.addComponent(chart);
        layout.setSizeFull();
        field = new TextArea("Descrizione Numerica");
        field.setResponsive(true);
        field.setVisible(false);
        layout.addComponent(field);
        panel.setResponsive(true);
        panel.setContent(layout);
        setCompositionRoot(panel);


    }



    public void setData(QueryType query, ListVector data) {
        Configuration configuration = chart.getConfiguration();
        configuration.setTitle(QueryTypeHandler.getDescription(query));

        switch(query) {
            case RESOURCE_USAGE:

                field.setVisible(false);
                configuration.getChart().setType(ChartType.COLUMN);
                configuration.getChart().setZoomType(ZoomType.XY);
                Vector type = data.getElementAsVector("type");
                Vector usage = data.getElementAsVector("usage");
                DataSeries dataSeries = new DataSeries();
                PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
                plotOptionsColumn.setColorByPoint(true);
                dataSeries.setPlotOptions(plotOptionsColumn);

                String sru= "";
                for(int i=0; i< type.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(type.getElementAsString(i), usage.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                 //   sru = sru+type.getElementAsString(i)+": "+ usage.getElementAsDouble(i)+"\n";

                   // field.setValue("Utilizzo delle risorse:\n"+ sru);
                  //  field.setVisible(true);

                }

                configuration.setSeries(dataSeries);
                chart.drawChart(configuration);


                break;
            case RESOURCE_USAGE_TIME:
                field.setVisible(false);
                configuration.getChart().setType(ChartType.COLUMN);
                configuration.getChart().setZoomType(ZoomType.XY);

                Vector type_usage = data.getElementAsVector("type");
                Vector time = data.getElementAsVector("time");
                dataSeries = new DataSeries();
                plotOptionsColumn = new PlotOptionsColumn();
                plotOptionsColumn.setColorByPoint(true);
                dataSeries.setPlotOptions(plotOptionsColumn);

                String srut="";
                for(int i=0; i< type_usage.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(type_usage.getElementAsString(i), time.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                 //   srut =srut+type_usage.getElementAsString(i)+": "+ time.getElementAsDouble(i);
                  //  field.setValue("Utilizzo delle risorse:\n"+ srut);
                  //  field.setVisible(true);
                }
                configuration.setSeries(dataSeries);
                chart.drawChart(configuration);



                break;
            case DAILY_ACTIVE_USERS:
                field.setVisible(false);
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

                field.setVisible(false);
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

                ls = new ListSeries();
                ls.setData(activeResourcesArr);

                configuration.setSeries(ls);

                chart.drawChart(configuration);
                break;
            case DAILY_ACTIVITIES:

                field.setVisible(false);
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
                field.setVisible(false);
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
                String sm = "";
                for(int i=0; i< os.length(); i++){
                    DataSeriesItem dataSeriesItem = new DataSeriesItem(os.getElementAsString(i), count.getElementAsDouble(i));
                    dataSeries.addItemWithDrilldown(dataSeriesItem);

                    sm = sm+os.getElementAsString(i)+": "+ count.getElementAsInt(i)+"\n";

                    field.setValue("Os utilizzati:\n"+ sm);
                    field.setVisible(true);


                }
                configuration.setSeries(dataSeries);
                chart.drawChart(configuration);



                break;
            case RESOURCE_ADDED_PER_DAY:
                field.setVisible(false);
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
                 ls = new ListSeries();
                ls.setData(resourcesAddedArr);
                configuration.setSeries(ls);
                chart.drawChart(configuration);
                break;
        }

    }






}
