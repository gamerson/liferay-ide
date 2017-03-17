package com.liferay.ide.color.editor;

import com.liferay.ide.color.editor.jedit.EOLSpan;
import com.liferay.ide.color.editor.jedit.IVisitor;
import com.liferay.ide.color.editor.jedit.Mark;
import com.liferay.ide.color.editor.jedit.Mode;
import com.liferay.ide.color.editor.jedit.Rule;
import com.liferay.ide.color.editor.jedit.Span;
import com.liferay.ide.color.editor.jedit.TextSequence;
import com.liferay.ide.color.editor.jedit.Type;
import com.liferay.ide.color.editor.prefs.ColorsPreferencePage;
import com.liferay.ide.color.editor.rules.CasedPatternRule;
import com.liferay.ide.color.editor.rules.ColoringWhitespaceDetector;
import com.liferay.ide.color.editor.rules.ColoringWordDetector;
import com.liferay.ide.color.editor.rules.DelegateToken;
import com.liferay.ide.color.editor.rules.EndOfLineRule;
import com.liferay.ide.color.editor.rules.ITokenFactory;
import com.liferay.ide.color.editor.rules.StarRule;
import com.liferay.ide.color.editor.rules.TextSequenceRule;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.util.PropertyChangeEvent;


public class ColoringEditorTools {
		
	public static void add(Rule rule, List rules, ITokenFactory factory) {
		List allTypes = rule.getTypes();
		for (Iterator typeI = allTypes.iterator(); typeI.hasNext();) {
			Type type = (Type) typeI.next();
			add(rule, type, factory, rules);
		}
	}
	public static void add(final Rule rule, final Type type, ITokenFactory factory, final List rules) {
		final IToken token = factory.makeToken(type);
		final Mode mode = rule.getMode();
		final boolean ignoreCase = rule.getIgnoreCase();
		type.accept(new IVisitor() {
			public void acceptSpan(Span span) {
				IToken defaultToken = token;
				if(span.hasDelegate()) {
					Rule delegateRule = mode.getRule(span.getDelegate());
					defaultToken = new DelegateToken(type, delegateRule, span.getEnd());
				}
				/* Using a PatternRule instead of a MultiLineRule because
				 * PatternRule exposes the break on newline behavior. */
				PatternRule pat = new CasedPatternRule(span.getStart(), span.getEnd(), 
					defaultToken, mode.getDefaultRuleSet().getEscape(), span.noLineBreak(),
					ignoreCase);
				rules.add(pat);
			}
			public void acceptTextSequence(TextSequence text) {
				/* If the text sequence can be recognized as a word, don't
				 * add it. This reduces the number of partitions created. If
				 * the text sequence can not be recognized as a word add it
				 * as a text sequence.				 */
				if(isWordStart(text.getText().charAt(0))) return;
				rules.add(new TextSequenceRule(text.getText(), token, ignoreCase));
			}
			public void acceptEolSpan(EOLSpan eolSpan) {
				rules.add(new EndOfLineRule(eolSpan.getText(), token, ignoreCase));
			}
			public void acceptMark(Mark mark) {
				rules.add(new StarRule(mark, new ColoringWhitespaceDetector(),
					wordDetector, token));
			}
		});
	}
	protected static ColoringWordDetector wordDetector = new ColoringWordDetector();
	
    public ColoringEditorTools() {
    	//do nothing
    }

    protected static boolean isWordStart(char c) {
    	return wordDetector.isWordStart(c);
    }
    
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		String property = event.getProperty();
		if(property == null) return false;
		if(property.endsWith("Bold")) {
			property = property.substring(0, property.length() - ColorsPreferencePage.BOLD_SUFFIX.length());
		}
		boolean affects = property.equals(ColorsPreferencePage.COMMENT1_COLOR) ||
			property.equals(ColorsPreferencePage.COMMENT2_COLOR) ||
			property.equals(ColorsPreferencePage.LITERAL1_COLOR)  ||
			property.equals(ColorsPreferencePage.LITERAL2_COLOR)  ||
			property.equals(ColorsPreferencePage.LABEL_COLOR)  ||
			property.equals(ColorsPreferencePage.KEYWORD1_COLOR)  ||
			property.equals(ColorsPreferencePage.KEYWORD2_COLOR)  ||
			property.equals(ColorsPreferencePage.KEYWORD3_COLOR)  ||
			property.equals(ColorsPreferencePage.KEYWORD4_COLOR)  ||
			property.equals(ColorsPreferencePage.FUNCTION_COLOR)  ||
			property.equals(ColorsPreferencePage.MARKUP_COLOR)  ||
			property.equals(ColorsPreferencePage.OPERATOR_COLOR)  ||
			property.equals(ColorsPreferencePage.DIGIT_COLOR)  ||
			property.equals(ColorsPreferencePage.INVALID_COLOR)  ||
			property.equals(ColorsPreferencePage.NULL_COLOR);
		return affects;
	}

	/** 
	 * Answer the file associated with name. This handles the
	 * case of running as a plugin and running standalone which 
	 * happens during testing.
	 * 	 * @param filename	 * @return File	 */
	public static URL getFile(String filename) throws IOException {
		System.out.println("get file "  + filename);
		//			URL installURL = EditorPlugin.getDefault().getDescriptor().getInstallURL();
		URL installURL = Platform.getBundle( EditorPlugin.PLUGIN_ID ).getEntry( "/" );
		System.out.println("install url " + installURL);
		//			URL mode = Platform.resolve(new URL(installURL, filename));
		return FileLocator.resolve(new URL(installURL, filename));
	}
}