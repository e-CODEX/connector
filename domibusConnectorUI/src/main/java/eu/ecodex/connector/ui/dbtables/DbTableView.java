/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.dbtables;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.layout.DCMainLayout;
import eu.ecodex.connector.ui.utils.RoleRequired;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a database table view component.
 *
 * <p>This class is responsible for displaying a database table view and managing the interaction
 * with it.
 */
@UIScope
@Route(value = DbTableView.ROUTE, layout = DCMainLayout.class)
@PageTitle("domibusConnector - Administrator")
@RoleRequired(role = "ADMIN")
@SuppressWarnings("squid:S1135")
public class DbTableView extends VerticalLayout implements AfterNavigationObserver {
    public static final String ROUTE = "dbtables";
    private final transient DbTableService dbTableService;
    private final Div gridDiv = new Div();
    private final Binder<DbTableService.ColumnRow> binder = new Binder<>();
    private transient DbTableService.TableDefinition currentTableDefinition;
    private Grid<DbTableService.ColumnRow> currentGrid;
    private final Button createNewRowButton = new Button("Add Row");

    /**
     * Constructor.
     *
     * @param dbTableServiceOptional Optional parameter of type Optional&lt;DbTableService&gt; that
     *                               is used to provide the database table service.
     * @see DbTableService
     */
    public DbTableView(Optional<DbTableService> dbTableServiceOptional) {
        this.dbTableService = dbTableServiceOptional.orElse(null);

        if (dbTableServiceOptional.isPresent()) {
            initUI();
        } else {
            add(new NativeLabel("Hidden DB table view is not activated!"));
        }
    }

    private void initUI() {
        List<String> tables = dbTableService.getTables();
        ComboBox<String> tableChooser = new ComboBox<>();
        tableChooser.setItems(tables);
        tableChooser.addValueChangeListener(this::selectedTableChanged);
        tableChooser.setWidth("10cm");
        this.add(tableChooser);
        createNewRowButton.addClickListener(this::addButtonClicked);
        createNewRowButton.setEnabled(false);
        this.add(createNewRowButton);
        this.add(gridDiv);
        gridDiv.setSizeFull();
    }

    private void addButtonClicked(ClickEvent<Button> buttonClickEvent) {
        final var rowDialog = new Dialog();

        rowDialog.setModal(true);
        rowDialog.setHeight("80%");
        rowDialog.setWidth("80%");

        var dataBar = new HorizontalLayout();

        // generate binder
        Binder<DbTableService.ColumnRow> dialogBinder = new Binder<>();
        for (var columnDefinition : currentTableDefinition.getColumnDefinitionMap().values()) {
            var field = createFieldAndBind(dialogBinder, columnDefinition);
            dataBar.add(field);
        }
        dialogBinder.setBean(new DbTableService.ColumnRow(currentTableDefinition));

        var saveButton = new Button(VaadinIcon.CHECK.create());
        var cancelButton = new Button(VaadinIcon.CLOSE_SMALL.create());
        cancelButton.addClickListener(clickEvent -> rowDialog.close());
        saveButton.addClickListener(clickEvent -> {
            DbTableService.ColumnRow newRow = dialogBinder.getBean();
            try {
                dbTableService.createColumn(newRow);
                currentGrid.getDataProvider().refreshAll();
                rowDialog.close();
            } catch (Exception e) {
                Notification.show("Failed to create DB row: " + e.getLocalizedMessage());
            }
        });

        var verticalLayout = new VerticalLayout();
        var buttonBar = new HorizontalLayout();

        buttonBar.add(saveButton, cancelButton);

        verticalLayout.add(buttonBar, dataBar);
        rowDialog.add(verticalLayout);

        rowDialog.open();
    }

    private void selectedTableChanged(
        AbstractField.ComponentValueChangeEvent<ComboBox<String>, String>
            comboBoxStringComponentValueChangeEvent) {
        var tableName = comboBoxStringComponentValueChangeEvent.getValue();
        currentTableDefinition = dbTableService.getTableDefinition(tableName);
        currentGrid = createGrid(currentTableDefinition);
        createNewRowButton.setEnabled(true);
        gridDiv.removeAll();
        gridDiv.add(currentGrid);
    }

    private Grid<DbTableService.ColumnRow> createGrid(
        DbTableService.TableDefinition tableDefinition) {
        var columns = tableDefinition.getColumnDefinitionMap().values();
        var grid = new Grid<DbTableService.ColumnRow>();

        final var editColumn = grid.addComponentColumn(row -> this.createEditButton(grid, row));

        for (var columnDefinition : columns) {

            var field = createFieldAndBind(binder, columnDefinition);

            var columnRowColumn =
                grid.addColumn((ValueProvider<DbTableService.ColumnRow, Object>) m -> m.getCell(
                    columnDefinition.getColumnName()));
            columnRowColumn.setHeader(columnDefinition.getColumnName())
                           .setResizable(true)
                           .setFooter(columnDefinition.getColumnName());
            columnRowColumn.setEditorComponent(field);
        }

        grid.setDataProvider(dbTableService.getDataProvider(tableDefinition));
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        Editor<DbTableService.ColumnRow> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addSaveListener(this::saveChangedRow);

        var saveButton = new Button(VaadinIcon.CHECK.create(), e -> editor.save());
        var cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        var deleteButton =
            new Button(VaadinIcon.TRASH.create(), e -> this.deleteRow(e, editor.getItem()));
        var actions = new HorizontalLayout(saveButton, cancelButton, deleteButton);
        editColumn.setEditorComponent(actions);

        return grid;
    }

    private AbstractField createFieldAndBind(
        Binder<DbTableService.ColumnRow> binder, DbTableService.ColumnDefinition cd) {
        var textField = new TextField(cd.getColumnName());
        binder.bind(
            textField,
            (ValueProvider<DbTableService.ColumnRow, String>) columnRow -> Objects.toString(
                columnRow.getCell(cd.getColumnName())),
            (Setter<DbTableService.ColumnRow, String>) (columnRow, s) -> columnRow.setCell(
                cd.getColumnName(), s)
        );
        return textField;
    }

    private Button createEditButton(Grid grid, DbTableService.ColumnRow columnRow) {
        final var edit = new Button(new Icon(VaadinIcon.PENCIL));
        edit.addClickListener(event -> {
            final Editor<DbTableService.ColumnRow> editor = grid.getEditor();
            if (editor.isOpen()) {
                editor.cancel();
            }
            editor.editItem(columnRow);
        });
        return edit;
    }

    private void deleteRow(
        ClickEvent<Button> buttonClickEvent, DbTableService.ColumnRow columnRow) {
        try {
            dbTableService.deleteColumn(columnRow);
        } catch (Exception e) {
            // TODO: update exception handling
            Notification.show("Error deleting row!" + e.getLocalizedMessage());
        }
    }

    private void saveChangedRow(
        EditorSaveEvent<DbTableService.ColumnRow> columnRowEditorSaveEvent) {
        var item = columnRowEditorSaveEvent.getItem();
        try {
            dbTableService.updateColumn(item);
        } catch (Exception e) {
            // TODO: update exception handling
            Notification.show("Error updating changed row!" + e.getLocalizedMessage());
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // nothing yet
    }
}
