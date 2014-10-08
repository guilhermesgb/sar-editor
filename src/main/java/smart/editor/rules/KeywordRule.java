package smart.editor.rules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

import smart.editor.keywords.SARKeyword;

public class KeywordRule extends PatternRule{

	public KeywordRule(String sequence, IToken token, char escapeCharacter, boolean breaksOnEOL) {
		super(sequence, sequence, token, escapeCharacter, breaksOnEOL);
	}

	protected int lastReadCount;

	@Override
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		lastReadCount = 1;
		for (int i= 1; i < sequence.length; i++) {
			int c= scanner.read();
			lastReadCount = lastReadCount + 1;
			if (c == ICharacterScanner.EOF && eofAllowed) {
				return true;
			} else if (c != sequence[i]) {
				scanner.unread();
				for (int j= i-1; j > 0; j--)
					scanner.unread();
				lastReadCount = 0;
				return false;
			}
		}
		char c = (char) scanner.read();
		scanner.unread();
		if ( !isPunctuationOrBlank(c) ){
			return false;
		}
		return true;
	}
	
	private boolean isPunctuationOrBlank(char next) {
		return next == '{' || next == '}' || next == '|' || next == '(' || next == ':'
						   || next == ')' || next == '.' || next == ' ' || next == '!'
						   || next == '=' || next == '&' || next == ';';
	}
	@Override
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {

		if (resume) {
			return fToken;

		} else {

			int c= scanner.read();
			if ((char)c == fStartSequence[0]) {
				scanner.unread();
				int check = scanner.getColumn();
				if ( check != 0 ){
					scanner.unread();
					c = scanner.read();
					if ( (char)c == '.' || Character.isAlphabetic((char)c) || Character.isDigit((char)c) ){
						return Token.UNDEFINED;
					}
					scanner.read();
				}
				else{
					c = scanner.read();
				}
				if (sequenceDetected(scanner, fStartSequence, false)) {
					if ( lastReadCount == fStartSequence.length ){
						return fToken;
					}
				}
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}
	
	public static List<IPredicateRule> getKeywordRules(SARKeyword[] keywords, Object data){
		
		List<IPredicateRule> rules = new LinkedList<IPredicateRule>();
		
		for ( int i=0; i<keywords.length; i++ ){
			String keyword = keywords[i].getKeyword();
			rules.add(new KeywordRule(keyword, new Token(data), ' ', true));
		}
		return rules;
	}
}