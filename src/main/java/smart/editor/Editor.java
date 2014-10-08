package smart.editor;

import org.eclipse.ui.editors.text.TextEditor;

public class Editor extends TextEditor {

	private static Editor instance;
	
	private ColorManager colorManager;
	public Editor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new Configuration(colorManager));
		setDocumentProvider(new DocumentProvider());
		instance = this;
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
		instance = null;
	}
	public static Editor getInstance(){
		return instance;
	}
}
