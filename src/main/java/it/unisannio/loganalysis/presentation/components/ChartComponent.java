package it.unisannio.loganalysis.presentation.components;


import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.data.Property;
import com.vaadin.ui.*;
import it.unisannio.loganalysis.analysis.QueryType;
import it.unisannio.loganalysis.analysis.QueryTypeHandler;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Vector;


/**
 * Created by graziano on 04/07/16.
 */
public class ChartComponent extends CustomComponent {

    private VerticalLayout chartLayout;
    private ComboBox selectChart;
    private Chart chart;
    private  DataSeries dataSeries;
    private PlotOptionsPie plotOptionsPie;
    private PlotOptionsColumn plotOptionsColumn;
    private XAxis x;
    private Configuration configuration;
    private ListSeries ls;
    private Panel panel;


    public ChartComponent() {
        panel = new Panel("Chart");
        chartLayout = new VerticalLayout();
        chartLayout.setSizeFull();
        chartLayout.setResponsive(true);
        chartLayout.setSizeFull();
        selectChart = new ComboBox();
        selectChart.setResponsive(true);
        selectChart.setNullSelectionAllowed(false);
        panel.setResponsive(true);
        panel.setContent(chartLayout);
        setCompositionRoot(panel);
    }



    public void setData(QueryType query, ListVector data) {
        chart = new Chart();
        chart.setResponsive(true);

        configuration = chart.getConfiguration();
        configuration.setTitle(QueryTypeHandler.getDescription(query));
        chartLayout.removeAllComponents();
        chartLayout.addComponent(chart);
        selectChart = new ComboBox("Seleziona Grafico");
        selectChart.setResponsive(true);
        selectChart.setNullSelectionAllowed(false);
        chartLayout.addComponent(selectChart);




        switch(query) {
            case RESOURCE_USAGE:
                if(!selectChart.isEmpty()) selectChart.removeAllItems();
                selectChart.addItem(ChartType.COLUMN);
                selectChart.addItem(ChartType.PIE);
                selectChart.setItemCaption(ChartType.COLUMN, "Grafico a Barre");
                selectChart.setItemCaption(ChartType.PIE, "Grafico a Torta");
                selectChart.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
                    selectChart.setNullSelectionAllowed(false);
                    if(valueChangeEvent.getProperty().getValue().toString().contains("column")){
                        configuration.getChart().setType(ChartType.COLUMN);
                        configuration.getChart().setZoomType(ZoomType.XY);
                        Vector type = data.getElementAsVector("type");
                        Vector usage = data.getElementAsVector("usage");
                        dataSeries = new DataSeries("Numero risorse");
                        plotOptionsColumn = new PlotOptionsColumn();
                        plotOptionsColumn.setColorByPoint(true);
                        dataSeries.setPlotOptions(plotOptionsColumn);
                        x= new XAxis();
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
                        chartLayout.addComponent(chart);
                        chart.drawChart(configuration);

                    }
                    else {
                        Vector type = data.getElementAsVector("type");
                        Vector usage = data.getElementAsVector("usage");
                        chart.setResponsive(true);
                        configuration= chart.getConfiguration();
                        configuration.getChart().setType(ChartType.PIE);
                        configuration.setTitle(QueryTypeHandler.getDescription(query));
                        plotOptionsPie = new PlotOptionsPie();
                        plotOptionsPie.setCursor(Cursor.POINTER);
                        dataSeries = new DataSeries();
                        dataSeries.setPlotOptions(plotOptionsPie);

                        for (int i = 0; i < type.length(); i++) {
                            if (usage.getElementAsDouble(i) != 0) {
                                DataSeriesItem dataSeriesItem = new DataSeriesItem(type.getElementAsString(i), usage.getElementAsDouble(i));
                                dataSeries.addItemWithDrilldown(dataSeriesItem);
                            }
                        }
                        configuration.setSeries(dataSeries);
                        chartLayout.addComponent(chart);
                        chart.drawChart(configuration);

                    }
                });
                break;
            case RESOURCE_USAGE_TIME: //  ---- SU BUGS NON ESISTE----

                if(!selectChart.isEmpty()) selectChart.removeAllItems();
                selectChart.addItem(ChartType.COLUMN);
                selectChart.addItem(ChartType.PIE);
                selectChart.setItemCaption(ChartType.COLUMN, "Grafico a Barre");
                selectChart.setItemCaption(ChartType.PIE, "Grafico a Torta");

                selectChart.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        selectChart.setNullSelectionAllowed(false);

                        if(valueChangeEvent.getProperty().getValue().toString().contains("column")){
                            configuration.getChart().setType(ChartType.COLUMN);
                            configuration.getChart().setZoomType(ZoomType.XY);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));


                            Vector type_usage = data.getElementAsVector("type");
                            Vector time = data.getElementAsVector("time");
                            dataSeries = new DataSeries("Tempo di utilizzo");
                            plotOptionsColumn = new PlotOptionsColumn();
                            plotOptionsColumn.setColorByPoint(true);
                            dataSeries.setPlotOptions(plotOptionsColumn);
                            XAxis x1 = new XAxis();
                            YAxis y1 = new YAxis();

                            for(int i=0; i< type_usage.length(); i++){
                                DataSeriesItem dataSeriesItem = new DataSeriesItem(type_usage.getElementAsString(i), time.getElementAsDouble(i));
                                dataSeries.addItemWithDrilldown(dataSeriesItem);
                                x1.addCategory(type_usage.getElementAsString(i));
                                y1.addCategory(time.getElementAsString(i));

                            }
                            configuration.addxAxis(x1);
                            configuration.addyAxis(y1);
                            configuration.setSeries(dataSeries);
                            chartLayout.addComponent(chart);
                            chart.drawChart(configuration);
                        }
                        else {

                            Vector type_usage = data.getElementAsVector("type");
                            Vector time = data.getElementAsVector("time");
                            chart.setResponsive(true);
                            configuration = chart.getConfiguration();
                            configuration.getChart().setType(ChartType.PIE);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));
                            dataSeries = new DataSeries();

                            for(int i=0; i< type_usage.length(); i++){
                                if(time.getElementAsDouble(i) != 0) {
                                    DataSeriesItem dataSeriesItem = new DataSeriesItem(type_usage.getElementAsString(i), time.getElementAsDouble(i));
                                    dataSeries.addItemWithDrilldown(dataSeriesItem);
                                }
                            }
                            plotOptionsPie = new PlotOptionsPie();
                            plotOptionsPie.setCursor(Cursor.POINTER);
                            dataSeries.setPlotOptions(plotOptionsPie);
                            configuration.setSeries(dataSeries);
                            chartLayout.addComponent(chart);
                            chart.drawChart(configuration);
                        }
                    }
                });
                break;
            case DAILY_ACTIVE_USERS:
                if(!selectChart.isEmpty()) selectChart.removeAllItems();
                selectChart.addItem(ChartType.LINE);
                selectChart.addItem(ChartType.COLUMN);
                selectChart.setItemCaption(ChartType.COLUMN, "Grafico a Barre");
                selectChart.setItemCaption(ChartType.LINE, "Grafico a Linee");

                selectChart.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        selectChart.setNullSelectionAllowed(false);

                        if(valueChangeEvent.getProperty().getValue().toString().contains("line")){
                            configuration.getChart().setType(ChartType.LINE);
                            configuration.getChart().setZoomType(ZoomType.XY);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));


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


                            ls = new ListSeries("Utenti attivi");
                            ls.setData(activeUsersArr);
                            configuration.setSeries(ls);
                            chart.drawChart(configuration);
                        }
                        else{

                            chart.setResponsive(true);
                            configuration = chart.getConfiguration();
                            configuration.getChart().setType(ChartType.COLUMN);
                            configuration.getChart().setZoomType(ZoomType.XY);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));

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

                            x = new XAxis();
                            String[] daysArr2 = new String[days.length()];
                            for(int i = 0; i < days.length(); i++) {
                                daysArr[i] = days.getElementAsString(i);
                                x.addCategory(days.getElementAsString(i));
                            }
                            configuration.addxAxis(x);
                            configuration.setSeries(ls);
                            chartLayout.addComponent(chart);
                            chart.drawChart(configuration);
                        }
                    }
                });
                break;
            case DAILY_ACTIVE_RESOURCES:
                if(!selectChart.isEmpty()) selectChart.removeAllItems();
                selectChart.addItem(ChartType.LINE);
                selectChart.addItem(ChartType.COLUMN);
                selectChart.setItemCaption(ChartType.COLUMN, "Grafico a Barre");
                selectChart.setItemCaption(ChartType.LINE, "Grafico a Linee");

                selectChart.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        selectChart.setNullSelectionAllowed(false);

                        if(valueChangeEvent.getProperty().getValue().toString().contains("line")){
                            configuration.getChart().setType(ChartType.LINE);
                            configuration.getChart().setZoomType(ZoomType.XY);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));
                            Vector days = data.getElementAsVector("days");
                            String []daysArr = new String[days.length()];
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
                        }
                        else{

                            chart.setResponsive(true);
                            configuration = chart.getConfiguration();
                            configuration.getChart().setType(ChartType.COLUMN);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));

                            Vector days = data.getElementAsVector("days");
                            String []daysArr = new String[days.length()];


                            x= new XAxis();

                            String[] daysArr4 = new String[days.length()];
                            for(int i = 0; i < days.length(); i++) {
                                daysArr[i] = days.getElementAsString(i);
                                x.addCategory(days.getElementAsString(i));
                            }
                            configuration.addxAxis(x);
                            configuration.setSeries(ls);
                            chartLayout.addComponent(chart);
                            chart.drawChart(configuration);

                        }

                    }
                });

                break;
            case TIME_RANGE_USAGE:

                if(!selectChart.isEmpty()) selectChart.removeAllItems();
                selectChart.addItem(ChartType.LINE);
                selectChart.addItem(ChartType.COLUMN);
                selectChart.setItemCaption(ChartType.COLUMN, "Grafico a Barre");
                selectChart.setItemCaption(ChartType.LINE, "Grafico a Linee");

                selectChart.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        selectChart.setNullSelectionAllowed(false);

                        if(valueChangeEvent.getProperty().getValue().toString().contains("line")){

                            configuration.getChart().setType(ChartType.LINE);
                            configuration.getChart().setZoomType(ZoomType.XY);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));

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
                        }
                        else{

                            chart.setResponsive(true);
                            configuration = chart.getConfiguration();
                            configuration.getChart().setType(ChartType.COLUMN);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));


                            configuration.setSeries(ls);

                            chartLayout.addComponent(chart);
                            chart.drawChart(configuration);
                        }

                    }
                });
                break;
            case MOST_USED_OS:
                if(!selectChart.isEmpty()) selectChart.removeAllItems();
                selectChart.addItem(ChartType.LINE);
                selectChart.addItem(ChartType.PIE);
                selectChart.setItemCaption(ChartType.PIE, "Grafico a Torta");
                selectChart.setItemCaption(ChartType.LINE, "Grafico a Barre");

                selectChart.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        selectChart.setNullSelectionAllowed(false);

                        if(valueChangeEvent.getProperty().getValue().toString().contains("pie")){

                            configuration.getChart().setType(ChartType.PIE);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));

                            Vector os = data.getElementAsVector("os");
                            Vector count = data.getElementAsVector("count");
                            dataSeries = new DataSeries("Utilizzi");
                            plotOptionsPie = new PlotOptionsPie();
                            plotOptionsPie.setCursor(Cursor.POINTER);
                            dataSeries.setPlotOptions(plotOptionsPie);
                            for(int i=0; i< os.length(); i++){
                                DataSeriesItem dataSeriesItem = new DataSeriesItem(os.getElementAsString(i), count.getElementAsDouble(i));
                                dataSeries.addItemWithDrilldown(dataSeriesItem);
                            }

                            configuration.setSeries(dataSeries);
                            chartLayout.addComponent(chart);
                            chart.drawChart(configuration);

                        }
                        else{

                            chart.setResponsive(true);

                            configuration.setTitle(QueryTypeHandler.getDescription(query));
                            configuration.getChart().setType(ChartType.COLUMN);
                            configuration.getChart().setZoomType(ZoomType.XY);
                            Vector os = data.getElementAsVector("os");
                            Vector count = data.getElementAsVector("count");
                            dataSeries = new DataSeries("Utilizzi ");
                            plotOptionsColumn = new PlotOptionsColumn();
                            plotOptionsColumn.setColorByPoint(true);
                            dataSeries.setPlotOptions(plotOptionsColumn);

                            x= new XAxis();
                            for(int i=0; i< os.length(); i++){
                                DataSeriesItem dataSeriesItem = new DataSeriesItem(os.getElementAsString(i), count.getElementAsDouble(i));
                                dataSeries.addItemWithDrilldown(dataSeriesItem);
                                x.addCategory(os.getElementAsString(i));
                            }

                            configuration.addxAxis(x);
                            configuration.setSeries(dataSeries);
                            chartLayout.addComponent(chart);
                            chart.drawChart(configuration);
                        }
                    }
                });
                break;
            case RESOURCE_ADDED_PER_DAY:
                if(!selectChart.isEmpty()) selectChart.removeAllItems();
                selectChart.addItem(ChartType.LINE);
                selectChart.addItem(ChartType.COLUMN);
                selectChart.setItemCaption(ChartType.COLUMN, "Grafico a Barre");
                selectChart.setItemCaption(ChartType.LINE, "Grafico a Linee");

                selectChart.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        selectChart.setNullSelectionAllowed(false);

                        if(valueChangeEvent.getProperty().getValue().toString().contains("line")){
                            configuration.getChart().setType(ChartType.LINE);
                            configuration.getChart().setZoomType(ZoomType.XY);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));

                            Vector days = data.getElementAsVector("days"); // ArrayIndexOutOfBoundsException: -1 ???
                            String[] daysArr = new String[days.length()];
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
                            chartLayout.addComponent(chart);
                            chart.drawChart(configuration);

                        }
                        else {


                            chart.setResponsive(true);
                            configuration= chart.getConfiguration();
                            configuration.getChart().setType(ChartType.COLUMN);
                            configuration.setTitle(QueryTypeHandler.getDescription(query));
                            Vector days = data.getElementAsVector("days"); // ArrayIndexOutOfBoundsException: -1 ???
                            String[] daysArr = new String[days.length()];
                            x= new XAxis();


                            x.setTitle("Numero di risorse");
                            YAxis y= new YAxis();
                            y.setTitle("Orario");


                            XAxis xAxis2 = new XAxis();
                            String[] daysArr3 = new String[days.length()];
                            for(int i = 0; i < days.length(); i++) {
                                daysArr[i] = days.getElementAsString(i);
                                xAxis2.addCategory(days.getElementAsString(i));
                            }

                            configuration.addxAxis(xAxis2);
                            if(ls != null) configuration.setSeries(ls);
                            chartLayout.addComponent(chart);
                            chart.drawChart(configuration);

                        }

                    }
                });
                break;
            case DAILY_ACTIVITIES:
                chartLayout.removeComponent(selectChart);
                configuration.getChart().setType(ChartType.LINE);
                configuration.getChart().setZoomType(ZoomType.XY);
                configuration.setTitle(QueryTypeHandler.getDescription(query));

                Vector days = data.getElementAsVector("days");
                String[]  daysArr = new String[days.length()];
                for(int i = 0; i < days.length(); i++) {
                    daysArr[i] = days.getElementAsString(i);
                }

                x = new XAxis();
                x.setCategories(daysArr);
                configuration.addxAxis(x);

                Vector activeUsers = data.getElementAsVector("activeUsers");
                Double[] activeUsersArr = new Double[activeUsers.length()];
                for(int i = 0; i < activeUsers.length(); i++) {
                    activeUsersArr[i] = activeUsers.getElementAsDouble(i);

                }

                DataSeries seriesU = new DataSeries();
                seriesU.setPlotOptions(new PlotOptionsColumn());
                seriesU.setName("Active Users");
                seriesU.setData(activeUsersArr);
                Vector activeResources = data.getElementAsVector("activeResources");
                Double[] activeResourcesArr = new Double[activeResources.length()];


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
