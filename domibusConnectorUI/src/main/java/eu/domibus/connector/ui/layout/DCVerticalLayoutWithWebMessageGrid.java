package eu.domibus.connector.ui.layout;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;

import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.component.WebMessagesGrid;

public class DCVerticalLayoutWithWebMessageGrid extends VerticalLayout {
	
	public static final int INITIAL_PAGE_SIZE = 15;
	
	WebMessagesGrid grid;
	
	IntegerField pageSizeField = new IntegerField();
	
	int pageSize = INITIAL_PAGE_SIZE;
	
	public DCVerticalLayoutWithWebMessageGrid(WebMessagesGrid grid) {
		this.grid = grid;
		grid.setPageSize(pageSize);
		grid.setPaginatorSize(5);

		VerticalLayout gridControl = createGridControlLayout();
		
		add(gridControl);
		add(grid);
		
		setSizeFull();
	}


	
	private VerticalLayout createGridControlLayout() {
		VerticalLayout gridControl = new VerticalLayout();
		
		LumoLabel pageSizeLabel = new LumoLabel("Messages displayed per page:");
		gridControl.add(pageSizeLabel);
		pageSizeField.setTitle("Display Messages");
		pageSizeField.setValue(pageSize);
		pageSizeField.setValueChangeMode(ValueChangeMode.LAZY);
		pageSizeField.addValueChangeListener(this::pageSizeChanged);
		gridControl.add(pageSizeField);
		
		Button hideColsBtn = new Button();
		hideColsBtn.setText("Show/Hide Columns");
		hideColsBtn.addClickListener(e -> {
			Dialog hideableColsDialog = new Dialog();
			
			Div headerContent = new Div();
			Label header = new Label("Select columns you want to see in the list");
			header.getStyle().set("font-weight", "bold");
			header.getStyle().set("font-style", "italic");
			headerContent.getStyle().set("text-align", "center");
			headerContent.getStyle().set("padding", "10px");
			headerContent.add(header);
			hideableColsDialog.add(headerContent);
			
			for(String colName: grid.getHideableColumnNames()) {
				LumoCheckbox hideableCol = new LumoCheckbox(colName);
				hideableCol.setValue(grid.getHideableColumns().get(colName).isVisible());
				hideableCol.addValueChangeListener(e1 -> {
					grid.getHideableColumns().get(colName).setVisible(e1.getValue());
				});
				hideableColsDialog.add(hideableCol);
			}
			
			Button closeBtn = new Button("close");
			closeBtn.addClickListener(e2 -> hideableColsDialog.close());
			
			hideableColsDialog.add(closeBtn);
			
			hideableColsDialog.open();

		});
		
		gridControl.add(hideColsBtn);

		return gridControl;
	}
	
	private void pageSizeChanged(AbstractField.ComponentValueChangeEvent<IntegerField, Integer> integerFieldIntegerComponentValueChangeEvent) {
		this.pageSize = integerFieldIntegerComponentValueChangeEvent.getValue();
		this.grid.setPageSize(pageSize);
		this.grid.reloadList();
	}
	


}
