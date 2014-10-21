package client;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;

public class Client_GUI {

	protected Shell shell;
	protected Display display;
	private CTabFolder tabFolder;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Client_GUI window = new Client_GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
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
		
		tabFolder = new CTabFolder(shell, SWT.CLOSE);
		tabFolder.setUnselectedCloseVisible(false);
		tabFolder.setSimple(false);
		tabFolder.setBounds(0, 0, 784, 542);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmClient = new MenuItem(menu, SWT.CASCADE);
		mntmClient.setText("Client");
		
		Menu menu_1 = new Menu(mntmClient);
		mntmClient.setMenu(menu_1);
		
		MenuItem mntmNewServerTab = new MenuItem(menu_1, SWT.NONE);
		mntmNewServerTab.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// called when the new server tab menu item is clicked
				// creates a new custom tab item to put in the tab folder
				final CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.NONE);
				// sets the name of it that shows on the tab
				tbtmNewItem.setText("Server");
				// creates a new composite object that holds the GUI for the server tab
				final Client_composite composite = new Client_composite(tabFolder, SWT.NONE);
				// puts the composite onto the tab
				tbtmNewItem.setControl(composite);
				// determines what the tab's index is on the tab folder
				int tabIndex = tabFolder.indexOf(tbtmNewItem);
				// brings up the newly created tab in front of the user
				tabFolder.setSelection(tabIndex);
				// tells the new tab what to do when it's close X is clicked
				tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
					public void close(CTabFolderEvent event) {
						if (event.item.equals(tbtmNewItem)) {
							composite.close();
						}
					}
				});
				
			}
		});
		mntmNewServerTab.setText("New Server Tab");
		
		

	}
	protected CTabFolder getTabFolder() {
		return tabFolder;
	}
}
