package smart.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import smart.editor.keywords.SARDeclarationKeyword;
import smart.editor.keywords.SAREquipmentOperation;
import smart.editor.keywords.SAREquipmentTypeKeyword;
import smart.editor.keywords.SARKeyword;
import smart.editor.keywords.SARKeywordLevel;

public class KeywordCompletionProcessor implements IContentAssistProcessor {

	public enum ScopeAction{
		DEFINING_SECTION,
		DEFINING_SECTION_TARGET,
		DEFINING_SECTION_ITEM,
		DEFINING_SECTION_ITEM_EXPRESSION,
		DEFINING_SECTION_ITEM_SCOPED_EXPRESSION;
	}
	
	public enum ScopeSection{
		UNKNOWN,
		MACRO,
		RULE,
		ATTRIBUTE;
	}
	
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {

		List<ICompletionProposal> proposals = new LinkedList<ICompletionProposal>();

		String lastWord = lastWord(viewer.getDocument(), offset);
		ScopeAction scopeAction = getScopeAction(viewer.getDocument(), offset);
		ScopeSection scopeSection = getScopeSection(scopeAction, viewer.getDocument(), offset);
		
		if ( scopeAction == ScopeAction.DEFINING_SECTION || scopeAction == ScopeAction.DEFINING_SECTION_ITEM ){
			SARKeyword[] values = SARDeclarationKeyword.values();
			for ( int i=0; i<values.length; i++ ){
				String keyword = values[i].getKeyword();
				int level = (int) values[i].getKeywordData();
				if ( scopeAction == ScopeAction.DEFINING_SECTION && level == SARKeywordLevel.SECTION_LEVEL
					|| scopeAction == ScopeAction.DEFINING_SECTION_ITEM && level == SARKeywordLevel.SECTION_ITEM_LEVEL ){
					
					if ( keyword.startsWith(lastWord) ){
						
						if ( scopeAction == ScopeAction.DEFINING_SECTION_ITEM
								&& !values[i].toString().startsWith(scopeSection.toString()) ){
							continue;
						}
						
						String replacement = keyword;
						
						int cursorOffset = 0;
						if ( scopeAction == ScopeAction.DEFINING_SECTION ){
							replacement += ": getEquipmentType() =  {\n\n}";
							cursorOffset = -5;
						}
						else if ( scopeAction == ScopeAction.DEFINING_SECTION_ITEM ){
							replacement += ": ";
							cursorOffset = 0;
							if ( scopeSection != ScopeSection.MACRO ){
								replacement += ";";
								cursorOffset = -1;
							}
						}
						
						proposals.add(new CompletionProposal(replacement, offset - lastWord.length(),
								lastWord.length(), replacement.length() + cursorOffset,
								null, keyword.replace(" para", ""), null, null));
					}
				}
			}
		}
		else{
			SARKeyword[] values = SAREquipmentTypeKeyword.values();
			if ( scopeAction == ScopeAction.DEFINING_SECTION_TARGET ){
				for ( int i=0; i<values.length; i++ ){
					String keyword = values[i].getKeyword();

					if ( keyword.startsWith(lastWord) ){
						proposals.add(new CompletionProposal(keyword, offset - lastWord.length(),
								lastWord.length(), keyword.length(),
								null, keyword, null, null));
					}
				}
			}
			else{
				boolean shouldGetDefinedMacros = scopeSection == ScopeSection.MACRO
						&& scopeAction == ScopeAction.DEFINING_SECTION_ITEM_SCOPED_EXPRESSION;
				String[] names = SAREquipmentOperation.getOperations(viewer.getDocument(),
						offset, shouldGetDefinedMacros);
				for ( int i=0; i<names.length; i++ ){
					String operationName = names[i];

					if ( operationName.startsWith(lastWord) ){
						proposals.add(new CompletionProposal(operationName, offset - lastWord.length(),
								lastWord.length(), operationName.length(),
								null, operationName, null, null));
					}
				}
			}
		}
		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	public static String lastWord(IDocument doc, int offset) {
		try{
			for (int n = offset - 1; n >= 0; n--) {
				int c = doc.getChar(n);
				if ( !Character.isJavaIdentifierPart((char)c) ){
					return doc.get(n + 1, offset - n - 1);
				}
				if ( -1 == (n - 1) ){
					n--;
					return doc.get(n + 1, offset - n - 1);
				}
			}
			return doc.get(offset - 1, offset);
		}
		catch(BadLocationException e){}
		return "";
	}
	
	public static String nextWord(IDocument doc, int offset) {
		try{
			for (int n = offset; n < doc.getLength(); n++) {
				int c = doc.getChar(n);
				if ( !Character.isJavaIdentifierPart((char)c) ){
					return doc.get(offset - 1, n - offset + 1);
				}
			}
			return doc.get(offset - 1, doc.getLength() - offset + 1);
		}
		catch(BadLocationException e){}
		return "";
	}

	private ScopeAction getScopeAction(IDocument doc, int offset) {
		try{
			boolean openingBracketFound = false;
			boolean closingBracketFound = false;
			boolean semicolonFound = false;
			boolean sectionDefinitionFound = false;
			boolean sectionItemExpressionDefinitionFound = false;
			StringBuilder previous = new StringBuilder();
			for (int n = offset - 1; n >= 0;) {
				char c = doc.getChar(n);
				if ( Character.isJavaIdentifierPart(c) ){
					String lastWord = lastWord(doc, n + 1);
					boolean continueThis = true;
					for ( int i=0; i<SARDeclarationKeyword.values().length; i++ ){
						String keyword = SARDeclarationKeyword.values()[i].getKeyword();
						int level = (int) SARDeclarationKeyword.values()[i].getKeywordData();
						if ( (lastWord + " " + previous.toString().trim()).startsWith(keyword) ){
							if ( level == SARKeywordLevel.SECTION_LEVEL ){
								sectionDefinitionFound = true;
								continueThis = false;
							}
							else if ( level == SARKeywordLevel.SECTION_ITEM_LEVEL ){
								sectionItemExpressionDefinitionFound = true;
								continueThis = false;
							}
							break;
						}
					}
					previous.insert(0, lastWord);
					n -= lastWord.length();
					if (!continueThis){
						break;
					}
				}
				else{
					previous.insert(0, c);
					n -= 1;
					if ( openingBracketFound && c == '{' ){
						break;
					}
					else if ( c == '{' ){
						openingBracketFound = true;
					}
					else if ( closingBracketFound && c == '}' ){
						break;
					}
					else if ( c == '}' ){
						closingBracketFound = true;
					}
					else if ( c == ';' ){
						semicolonFound = true;
					}
				}
			}
			if ( sectionItemExpressionDefinitionFound ){
				if ( openingBracketFound ){
					if ( closingBracketFound ){
						return ScopeAction.DEFINING_SECTION_ITEM;
					}
					return ScopeAction.DEFINING_SECTION_ITEM_SCOPED_EXPRESSION;
				}
				else{
					if ( closingBracketFound ){
						return ScopeAction.DEFINING_SECTION;
					}
					if ( semicolonFound ){
						return ScopeAction.DEFINING_SECTION_ITEM;
					}
					return ScopeAction.DEFINING_SECTION_ITEM_EXPRESSION;
				}
			}
			else if ( sectionDefinitionFound ){
				if ( openingBracketFound ){
					if ( closingBracketFound ){
						return ScopeAction.DEFINING_SECTION;
					}
					return ScopeAction.DEFINING_SECTION_ITEM;
				}
				else{
					return ScopeAction.DEFINING_SECTION_TARGET;
				}
			}
			return ScopeAction.DEFINING_SECTION;
		}
		catch(BadLocationException e){}
		return ScopeAction.DEFINING_SECTION;
	}

	private ScopeSection getScopeSection(ScopeAction scopeAction, IDocument doc, int offset) {
		if ( scopeAction == ScopeAction.DEFINING_SECTION ){
			return ScopeSection.UNKNOWN;
		}
		try{
			StringBuilder previous = new StringBuilder();
			for (int n = offset - 1; n >= 0;) {
				char c = doc.getChar(n);
				if ( Character.isJavaIdentifierPart(c) ){
					String lastWord = lastWord(doc, n + 1);
					for ( int i=0; i<SARDeclarationKeyword.values().length; i++ ){
						String keyword = SARDeclarationKeyword.values()[i].getKeyword();
						int level = (int) SARDeclarationKeyword.values()[i].getKeywordData();
						if ( level == SARKeywordLevel.SECTION_LEVEL
								&& (lastWord + " " + previous.toString().trim()).startsWith(keyword) ){
							if ( SARDeclarationKeyword.values()[i] == SARDeclarationKeyword.MACROS_SECTION ){
								return ScopeSection.MACRO;
							}
							else if ( SARDeclarationKeyword.values()[i] == SARDeclarationKeyword.RULES_SECTION ){
								return ScopeSection.RULE;
							}
							else if ( SARDeclarationKeyword.values()[i] == SARDeclarationKeyword.ATTRIBUTES_SECTION ){
								return ScopeSection.ATTRIBUTE;
							}
						}
					}
					previous.insert(0, lastWord);
					n -= lastWord.length();
				}
				else{
					previous.insert(0, c);
					n -= 1;
				}
			}
		}
		catch(BadLocationException e){}
		return ScopeSection.UNKNOWN;
	}
	
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}