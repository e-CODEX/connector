package eu.domibus.connector.ui.dbtables;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
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
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.utils.RoleRequired;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@UIScope
@Route(value = DbTableView.ROUTE, layout = DCMainLayout.class)
@PageTitle("domibusConnector - Administrator")
@RoleRequired(role = "ADMIN")
public class DbTableView extends VerticalLayout implements AfterNavigationObserver {
    public static final String ROUTE = "dbtables";

    private final transient DbTableService dbTableService;
    private final Div gridDiv = new Div();
    private final Button createNewRowButton = new Button("Add Row");
    private final Binder<DbTableService.ColumnRow> binder = new Binder<>();
    private transient DbTableService.TableDefinition currentTableDefinition;
    private Grid<DbTableService.ColumnRow> currentGrid;

    public DbTableView(Optional<DbTableService> dbTableServiceOptional) {
        this.dbTableService = dbTableServiceOptional.orElse(null);

        if (dbTableServiceOptional.isPresent()) {
            initUI();
        } else {
            add(new Label("Hidden DB table view is not activated!"));
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
        final Dialog rowDialog = new Dialog();

        rowDialog.setModal(true);
        rowDialog.setHeight("80%");
        rowDialog.setWidth("80%");

        VerticalLayout verticalLayout = new VerticalLayout();
        HorizontalLayout buttonBar = new HorizontalLayout();
        HorizontalLayout dataBar = new HorizontalLayout();

        // generate binder
        Binder<DbTableService.ColumnRow> dialogBinder = new Binder<>();
        for (DbTableService.ColumnDefinition cd : currentTableDefinition.getColumnDefinitionMap().values()) {
            AbstractField field = createFieldAndBind(dialogBinder, cd);
            dataBar.add(field);
        }
        dialogBinder.setBean(new DbTableService.ColumnRow(currentTableDefinition));

        Button saveButton = new Button(VaadinIcon.CHECK.create());
        Button cancelButton = new Button(VaadinIcon.CLOSE_SMALL.create());
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
        buttonBar.add(saveButton, cancelButton);

        verticalLayout.add(buttonBar, dataBar);
        rowDialog.add(verticalLayout);

        rowDialog.open();
    }

    private void selectedTableChanged(AbstractField.ComponentValueChangeEvent<ComboBox<String>, String> comboBoxStringComponentValueChangeEvent) {
        String tableName = comboBoxStringComponentValueChangeEvent.getValue();
        currentTableDefinition = dbTableService.getTableDefinition(tableName);
        currentGrid = createGrid(currentTableDefinition);
        createNewRowButton.setEnabled(true);
        gridDiv.removeAll();
        gridDiv.add(currentGrid);
    }

    private Grid<DbTableService.ColumnRow> createGrid(DbTableService.TableDefinition tableDefinition) {
        Collection<DbTableService.ColumnDefinition> columns = tableDefinition.getColumnDefinitionMap().values();

        Grid<DbTableService.ColumnRow> grid = new Grid<>();

        final Grid.Column<DbTableService.ColumnRow> editColumn =
                grid.addComponentColumn(row -> this.createEditButton(grid, row));

        for (DbTableService.ColumnDefinition cd : columns) {

            AbstractField field = createFieldAndBind(binder, cd);

            Grid.Column<DbTableService.ColumnRow> columnRowColumn =
                    grid.addColumn((ValueProvider<DbTableService.ColumnRow, Object>) m -> m.getCell(cd.getColumnName()));
            columnRowColumn.setHeader(cd.getColumnName())
                           .setResizable(true)
                           .setFooter(cd.getColumnName());
            columnRowColumn.setEditorComponent(field);
        }

        grid.setDataProvider(dbTableService.getDataProvider(tableDefinition));
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        Editor<DbTableService.ColumnRow> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addSaveListener(this::saveChangedRow);

        Button saveButton = new Button(VaadinIcon.CHECK.create(), e -> editor.save());
        Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> this.deleteRow(e, editor.getItem()));
        HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton, deleteButton);
        editColumn.setEditorComponent(actions);

        return grid;
    }

    private AbstractField createFieldAndBind(
            Binder<DbTableService.ColumnRow> binder,
            DbTableService.ColumnDefinition cd) {
        TextField tf = new TextField(cd.getColumnName());
        binder.bind(
                tf,
                (ValueProvider<DbTableService.ColumnRow, String>) columnRow -> Objects.toString(columnRow.getCell(cd.getColumnName())),
                (Setter<DbTableService.ColumnRow, String>) (columnRow, s) -> columnRow.setCell(
                        cd.getColumnName(),
                        s
                )
        );
        return tf;
    }

    private Button createEditButton(Grid grid, DbTableService.ColumnRow columnRow) {
        final Button edit = new Button(new Icon(VaadinIcon.PENCIL));
        edit.addClickListener(event -> {
            final Editor<DbTableService.ColumnRow> editor = grid.getEditor();
            if (editor.isOpen()) {
                editor.cancel();
            }
            editor.editItem(columnRow);
        });
        return edit;
    }

    private void deleteRow(ClickEvent<Button> buttonClickEvent, DbTableService.ColumnRow columnRow) {
        try {
            dbTableService.deleteColumn(columnRow);
        } catch (Exception e) {
            // TODO: update exception handling
            Notification.show("Error deleting row!" + e.getLocalizedMessage());
        }
    }

    private void saveChangedRow(EditorSaveEvent<DbTableService.ColumnRow> columnRowEditorSaveEvent) {
        DbTableService.ColumnRow item = columnRowEditorSaveEvent.getItem();
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
