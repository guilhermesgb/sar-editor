package smart.editor;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

public class DocumentListener implements IDocumentListener {

//	private final static String MARKER_TYPE = "__sar_editor_plugin.__sar_compilingError";

	public void documentAboutToBeChanged(DocumentEvent event) {

	}

	@Override
	public void documentChanged(DocumentEvent event) {

		Editor textEditor = Editor.getInstance();

		if ( textEditor != null ){

//			Map<String, Object> attributes= new HashMap<String, Object>(11);
//			attributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
//			attributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
//			attributes.put(IMarker.CHAR_START, 1);
//			attributes.put(IMarker.CHAR_END, 10);
//			attributes.put(IMarker.LINE_NUMBER, 1);
//			attributes.put(IMarker.MESSAGE, "Syntax error");
//
//			try {
//				MarkerUtilities.createMarker(getResource(textEditor), attributes, MARKER_TYPE);
//			} catch (CoreException x) {
//				Shell shell= textEditor.getSite().getShell();
//
//				ErrorDialog.openError(shell, "Deu erro", "nao sei qual foi mas deu", x.getStatus());
//			}
		}
	}

//	private IResource getResource(SAREditor textEditor) {
//		IEditorInput input= textEditor.getEditorInput();
//		return (IResource) ((IAdaptable) input).getAdapter(IResource.class);
//	}
}