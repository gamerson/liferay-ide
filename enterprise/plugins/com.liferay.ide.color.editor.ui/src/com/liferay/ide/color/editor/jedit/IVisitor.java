package com.liferay.ide.color.editor.jedit;

public interface IVisitor {
	void acceptSpan(Span span);
	void acceptTextSequence(TextSequence text);
	void acceptEolSpan(EOLSpan eolSpan);
	void acceptMark(Mark mark);
}
