package it.unisannio.loganalysis.presentation.components;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by mario on 04/07/16.
 */
public class ChartView extends CustomComponent {

    public ChartView() {
        VerticalLayout layout = new VerticalLayout();

        layout.addComponent(new Label("Chart"));

        setCompositionRoot(layout);
    }

}
