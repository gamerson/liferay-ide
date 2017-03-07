package com.liferay.ide.color.editor.rules;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class ColoringWhitespaceDetector implements IWhitespaceDetector {

    public boolean isWhitespace(char c) {
        return Character.isWhitespace(c);
    }

}
