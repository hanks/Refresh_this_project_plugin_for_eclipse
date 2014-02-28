package refresh_this_project.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */

public class RefreshThisProjectAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	// get UISynchronize injected as field
	
	
	public static final int STATUS_OK = 0;
	public static final int STATUS_NO_SELECT_IN_PE = 1;
	public static final int STATUS_NO_SELECT_IN_PE_AND_EDITOR = 2;
	public static final int STATUS_ERROR = 3;
	
	
	
	/**
	 * The constructor.
	 */
	public RefreshThisProjectAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		
		// get active editor info
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		
		// use in inner class job
		final IWorkbenchPage page = window.getActivePage();
		
		Job job = new Job("Refresh this project") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
                Display display = PlatformUI.getWorkbench().getDisplay();
                display.asyncExec(new Runnable() {
					@Override 
					public void run() {
						// get status bar of eclipse
						IWorkbenchPart part = page.getActivePart();
						IWorkbenchPartSite site = part.getSite();
						IActionBars actionBars  = null;
						if (site instanceof IViewSite) {
							IViewSite vSite = (IViewSite) site;
							actionBars =  vSite.getActionBars();
						} else if (site instanceof IEditorSite) {
							IEditorSite vSite = (IEditorSite) site;
							actionBars =  vSite.getActionBars();
						}
						
						if (actionBars != null) {
							actionBars.getStatusLineManager().setMessage("Starting to refresh this project...");
						}
					}
				});

				try {
					int statusCode = doRefreshAction(page, monitor);
					
					switch (statusCode) {
					case STATUS_OK:						
						// show finish message in status bar
		                display.asyncExec(new Runnable() {	
							@Override 
							public void run() {
								// get status bar of eclipse
								IWorkbenchPart part = page.getActivePart();
								IWorkbenchPartSite site = part.getSite();
								IActionBars actionBars  = null;
								if (site instanceof IViewSite) {
									IViewSite vSite = (IViewSite) site;
									actionBars =  vSite.getActionBars();
								} else if (site instanceof IEditorSite) {
									IEditorSite vSite = (IEditorSite) site;
									actionBars =  vSite.getActionBars();
								}
								
								if (actionBars != null) {
									actionBars.getStatusLineManager().setMessage("This project was refreshed successfully!");
								}
							}
						});
						break;
					case STATUS_NO_SELECT_IN_PE:
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								showMessage("No selected file in package explorer!");
							}
						});
						break;
					case STATUS_NO_SELECT_IN_PE_AND_EDITOR:
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								showMessage("No selected file in package explorer or active editor!");
							}
						});
						break;
					case STATUS_ERROR:
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								showMessage("Refresh Error!!");
							}
						});
						
						break;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return Status.OK_STATUS;
			}
		};
		
		job.schedule();
	}
	
	/**
	 * Refresh the whole project by selected file in editor or package explorer 
	 * @param page
	 */
	public int doRefreshAction(IWorkbenchPage page, IProgressMonitor monitor) {
		
		IEditorPart editor = page.getActiveEditor();
		
		int statusCode = STATUS_OK;
		
//		// for test
//		try {
//			Thread.sleep (5000);
//		} catch (Throwable th) {
//			
//		}

		if (editor != null) {
			IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
			IFile file = input.getFile();
			IProject project = file.getProject();
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
				statusCode = STATUS_ERROR;
			}
		} else {
			// check selection from package explorer 
			ISelectionService service = window.getSelectionService();
			IStructuredSelection package_exploer_selection = (IStructuredSelection) service
					.getSelection("org.eclipse.jdt.ui.PackageExplorer");
			if (package_exploer_selection != null) {
				Object obj = package_exploer_selection.getFirstElement();
				if (obj == null) {
					statusCode = STATUS_NO_SELECT_IN_PE;
				} else {
					// get file info for selection from package explorer
					IResource resource = ((ICompilationUnit)obj).getResource();
					
					if (resource.getType() == IResource.FILE) {
					    IFile ifile = (IFile) resource;
					    IProject project = ifile.getProject();
						try {
							project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						} catch (CoreException e) {
							e.printStackTrace();
							statusCode = STATUS_ERROR;
						}
					}
				}
			} else {
				statusCode = STATUS_NO_SELECT_IN_PE_AND_EDITOR;
			}
		}
		
		return statusCode;
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			window.getShell(),
			"Refresh this project",
			message);
	}
}
