package smart.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;

public class Configuration extends SourceViewerConfiguration {
	private DoubleClickStrategy doubleClickStrategy;
	private DefaultScanner defaultScanner;
	private DefaultScanner stringScanner;
	private DefaultScanner punctScanner;
	private DefaultScanner parenScanner;
	private DefaultScanner commentScanner;
	private DefaultScanner sarKeywordScanner;
	private DefaultScanner sarEquipTypeScanner;
	private DefaultScanner varrefScanner;
	private DefaultScanner regwordScanner;
	private ColorManager colorManager;

	public Configuration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
				IDocument.DEFAULT_CONTENT_TYPE,
				PartitionScanner.SAR_REGWORD,
				PartitionScanner.SAR_VARREF,
				PartitionScanner.SAR_EQUIPTYPE,
				PartitionScanner.SAR_KEYWORD,
				PartitionScanner.SAR_COMMENT,
				PartitionScanner.SAR_PAREN,
				PartitionScanner.SAR_PUNCT,
				PartitionScanner.SAR_STRING};
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer,
			String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new DoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected DefaultScanner getSARDefaultScanner() {
		if (defaultScanner == null) {
			defaultScanner = new DefaultScanner(colorManager);
			defaultScanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(ColorConstants.DEFAULT))));
		}
		return defaultScanner;
	}
	protected DefaultScanner getSARStringScanner() {
		if (stringScanner == null) {
			stringScanner = new DefaultScanner(colorManager);
			stringScanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(ColorConstants.SAR_STRING))));
		}
		return stringScanner;
	}
	protected DefaultScanner getSARPunctScanner() {
		if (punctScanner == null) {
			punctScanner = new DefaultScanner(colorManager);
			punctScanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(ColorConstants.SAR_PUNCT), null, SWT.BOLD)));
		}
		return punctScanner;
	}
	protected DefaultScanner getSARParenScanner() {
		if (parenScanner == null) {
			parenScanner = new DefaultScanner(colorManager);
			parenScanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(ColorConstants.SAR_PAREN), null, SWT.BOLD)));
		}
		return parenScanner;
	}
	protected DefaultScanner getSARCommentScanner() {
		if (commentScanner == null) {
			commentScanner = new DefaultScanner(colorManager);
			commentScanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(ColorConstants.SAR_COMMENT))));
		}
		return commentScanner;
	}
	protected DefaultScanner getSARKeywordScanner() {
		if (sarKeywordScanner == null) {
			sarKeywordScanner = new DefaultScanner(colorManager);
			sarKeywordScanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(ColorConstants.SAR_KEYWORD), null, SWT.BOLD)));
		}
		return sarKeywordScanner;
	}
	protected DefaultScanner getSAREquipTypeScanner() {
		if (sarEquipTypeScanner == null) {
			sarEquipTypeScanner = new DefaultScanner(colorManager);
			sarEquipTypeScanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(ColorConstants.SAR_KEYWORD), null, SWT.ITALIC)));
		}
		return sarEquipTypeScanner;
	}
	protected DefaultScanner getSARVarRefScanner() {
		if (varrefScanner == null) {
			varrefScanner = new DefaultScanner(colorManager);
			varrefScanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(ColorConstants.SAR_VARREF))));
		}
		return varrefScanner;
	}
	protected DefaultScanner getSARRegWordScanner() {
		if (regwordScanner == null) {
			regwordScanner = new DefaultScanner(colorManager);
			regwordScanner.setDefaultReturnToken(
					new Token(new TextAttribute(colorManager.getColor(ColorConstants.SAR_REGWORD))));
		}
		return regwordScanner;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		
		DefaultDamagerRepairer dr =
				new DefaultDamagerRepairer(getSARRegWordScanner());
		reconciler.setDamager(dr, PartitionScanner.SAR_REGWORD);
		reconciler.setRepairer(dr, PartitionScanner.SAR_REGWORD);

		dr = new DefaultDamagerRepairer(getSARVarRefScanner());
		reconciler.setDamager(dr, PartitionScanner.SAR_VARREF);
		reconciler.setRepairer(dr, PartitionScanner.SAR_VARREF);
		
		dr = new DefaultDamagerRepairer(getSAREquipTypeScanner());
		reconciler.setDamager(dr, PartitionScanner.SAR_EQUIPTYPE);
		reconciler.setRepairer(dr, PartitionScanner.SAR_EQUIPTYPE);

		dr = new DefaultDamagerRepairer(getSARKeywordScanner());
		reconciler.setDamager(dr, PartitionScanner.SAR_KEYWORD);
		reconciler.setRepairer(dr, PartitionScanner.SAR_KEYWORD);

		dr = new DefaultDamagerRepairer(getSARCommentScanner());
		reconciler.setDamager(dr, PartitionScanner.SAR_COMMENT);
		reconciler.setRepairer(dr, PartitionScanner.SAR_COMMENT);

		dr = new DefaultDamagerRepairer(getSARParenScanner());
		reconciler.setDamager(dr, PartitionScanner.SAR_PAREN);
		reconciler.setRepairer(dr, PartitionScanner.SAR_PAREN);
		
		dr = new DefaultDamagerRepairer(getSARPunctScanner());
		reconciler.setDamager(dr, PartitionScanner.SAR_PUNCT);
		reconciler.setRepairer(dr, PartitionScanner.SAR_PUNCT);

		dr = new DefaultDamagerRepairer(getSARStringScanner());
		reconciler.setDamager(dr, PartitionScanner.SAR_STRING);
		reconciler.setRepairer(dr, PartitionScanner.SAR_STRING);
		
		dr = new DefaultDamagerRepairer(getSARDefaultScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}
	
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor((IContentAssistProcessor) new KeywordCompletionProcessor(),
				IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		return assistant;
	}

}