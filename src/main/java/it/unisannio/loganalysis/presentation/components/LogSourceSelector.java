package it.unisannio.loganalysis.presentation.components;

import com.vaadin.ui.*;

/**
 * Created by mario on 03/07/16.
 */
public class LogSourceSelector extends CustomComponent {

    private ComboBox logSources;
    private Button addSourceBtn;

    private AddSourceListener addSourceListener = null;

    public LogSourceSelector() {
        logSources = new ComboBox();

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
