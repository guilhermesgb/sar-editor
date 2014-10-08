package smart.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class DocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new PartitionScanner(),
					new String[] {
						PartitionScanner.SAR_REGWORD,
						PartitionScanner.SAR_VARREF,
						PartitionScanner.SAR_EQUIPTYPE,
						PartitionScanner.SAR_KEYWORD,
						PartitionScanner.SAR_COMMENT,
						PartitionScanner.SAR_PAREN,
						PartitionScanner.SAR_PUNCT,
						PartitionScanner.SAR_STRING });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
			document.addDocumentListener(new DocumentListener());
		}
		return document;
	}
}