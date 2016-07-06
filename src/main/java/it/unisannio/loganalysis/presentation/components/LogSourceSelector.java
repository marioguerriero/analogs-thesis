package it.unisannio.loganalysis.presentation.components;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import it.unisannio.loganalysis.analysis.Query;
import it.unisannio.loganalysis.analysis.QueryController;
import it.unisannio.loganalysis.extractor.FacadeLogSource;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by mario on 03/07/16.
 */
public class LogSourceSelector extends CustomComponent {

    private ComboBox logSources;
    private Button addSourceBtn;

    private AddSourceListener addSourceListener = null;

    public LogSourceSelector() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        logSources = new ComboBox();
        FacadeLogSource facadeLogSource = FacadeLogSource.getInstance();
        logSources.addItems(facadeLogSource.getDataSources());

        logSources.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            try {

                QueryController controller = QueryController.getInstance();
                controller.setDbSource(valueChangeEvent.getProperty().getValue().toString());
                controller.loadTables();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ScriptException e) {
                e.printStackTrace();
            } catch (QueryController.NullDataSourceException e) {
                e.printStackTrace();
            }
        });

        addSourceBtn = new Button("Aggiungi");
        addSourceBtn.addClickListener((Button.ClickListener) clickEvent -> {
            if(addSourceListener != null)
                addSourceListener.addSourceClicked();
        });

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(new Label("Sorgente di dati di log"));
        layout.addComponent(logSources);
        layout.addComponent(addSourceBtn);

        layout.setSizeUndefined();
        layout.setSpacing(true);

        setCompositionRoot(layout);
    }

    public void setAddSourceListener(AddSourceListener addSourceListener) {
        this.addSourceListener = addSourceListener;
    }

    public interface AddSourceListener {
        void addSourceClicked();
    }
}
