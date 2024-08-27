/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.layout;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;
import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.component.WebMessagesGrid;

/**
 * This class extends the VerticalLayout class and represents a custom layout that includes a
 * WebMessagesGrid and controls for managing the display of messages in the grid.
 */
@SuppressWarnings("squid:S1135")
public class DCVerticalLayoutWithWebMessageGrid extends VerticalLayout {
    public static final int INITIAL_PAGE_SIZE = 15;
    WebMessagesGrid grid;
    IntegerField pageSizeField = new IntegerField();
    int pageSize = INITIAL_PAGE_SIZE;

    /**
     * Constructor.
     *
     * @param grid The WebMessagesGrid to be displayed in the layout.
     */
    public DCVerticalLayoutWithWebMessageGrid(WebMessagesGrid grid) {
        this.grid = grid;
        grid.setPageSize(pageSize);
        grid.setPageSize(5); // TODO check if replacement is OK

        VerticalLayout gridControl = createGridControlLayout();

        add(gridControl);
        add(grid);

        setSizeFull();
    }

    private VerticalLayout createGridControlLayout() {
        var gridControl = new VerticalLayout();

        var pageSizeLabel = new LumoLabel("Messages displayed per page:");
        gridControl.add(pageSizeLabel);
        pageSizeField.setTitle("Display Messages");
        pageSizeField.setValue(pageSize);
        pageSizeField.setValueChangeMode(ValueChangeMode.LAZY);
        pageSizeField.addValueChangeListener(this::pageSizeChanged);
        gridControl.add(pageSizeField);

        var hideColsBtn = new Button();
        hideColsBtn.setText("Show/Hide Columns");
        hideColsBtn.addClickListener(e -> {
            var headerContent = new Div();
            var header = new NativeLabel("Select columns you want to see in the list");
            header.getStyle().set("font-weight", "bold");
            header.getStyle().set("font-style", "italic");
            headerContent.getStyle().set("text-align", "center");
            headerContent.getStyle().set("padding", "10px");
            headerContent.add(header);

            var hideableColsDialog = new Dialog();
            hideableColsDialog.add(headerContent);

            for (var colName : grid.getHideableColumnNames()) {
                var hideableCol = new LumoCheckbox(colName);
                hideableCol.setValue(grid.getHideableColumns().get(colName).isVisible());
                hideableCol.addValueChangeListener(
                    e1 -> grid.getHideableColumns().get(colName).setVisible(e1.getValue()));
                hideableColsDialog.add(hideableCol);
            }

            var closeBtn = new Button("close");
            closeBtn.addClickListener(e2 -> hideableColsDialog.close());

            hideableColsDialog.add(closeBtn);

            hideableColsDialog.open();
        });

        gridControl.add(hideColsBtn);

        return gridControl;
    }

    private void pageSizeChanged(
        AbstractField.ComponentValueChangeEvent<IntegerField, Integer>
            integerFieldIntegerComponentValueChangeEvent) {
        this.pageSize = integerFieldIntegerComponentValueChangeEvent.getValue();
        this.grid.setPageSize(pageSize);
        this.grid.reloadList();
    }
}
