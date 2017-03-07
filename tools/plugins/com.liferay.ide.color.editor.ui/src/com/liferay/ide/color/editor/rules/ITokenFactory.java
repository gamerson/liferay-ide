package com.liferay.ide.color.editor.rules;

import com.liferay.ide.color.editor.jedit.Type;

import org.eclipse.jface.text.rules.IToken;


public interface ITokenFactory {
	IToken makeToken(Type type);
}
