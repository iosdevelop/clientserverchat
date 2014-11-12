package client;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.SWT;

public class GUI_Application_Main {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GUI_Application_Main window = new GUI_Application_Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(800, 600);
		shell.setText("SWT Application");
		
		CTabFolder tabFolder = new CTabFolder(shell, SWT.BORDER);
		tabFolder.setBounds(0, 0, 784, 562);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		// i need to create an item for the client control gui composite, add it to the tab folder
		
		// create a new custom tab item for the tab folder
		final CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.NONE);
		// set the text that shows in the tab
		tbtmNewItem.setText("Control");
		// create a new composite object that holds the GUI elements for the tab.
		final Client_Control_GUI_composite composite = new Client_Control_GUI_composite(tabFolder, SWT.None);
		// put the composite on the tab dohicky.
		tbtmNewItem.setControl(composite);
		// determine the index for the new tab (it should be a constant at this point, but why play guessing games?)
		int tabIndex = tabFolder.indexOf(tbtmNewItem);
		// put the GUI in from of the users eyes
		tabFolder.setSelection(tabIndex);

	}
}
