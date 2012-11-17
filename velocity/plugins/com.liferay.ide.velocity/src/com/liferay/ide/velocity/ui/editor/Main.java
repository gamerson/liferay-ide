/*
 * Created on 23.05.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package com.liferay.ide.velocity.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.liferay.ide.velocity.ui.LangMessages;

public class Main
{

    static int cnt = 0;

    public static void main(String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText(LangMessages.Main_prbrowser);
        shell.setLayout(new FillLayout());
        TabFolder t = new TabFolder(shell, SWT.NONE);
        TabItem i1 = new TabItem(t, SWT.NONE);
        TabItem i2 = new TabItem(t, SWT.NONE);
        Button b = new Button(t, SWT.PUSH);
        final Browser browser = new Browser(t, SWT.NONE);
        b.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event e)
            {
                browser.setText(LangMessages.Main_html_body_hello + cnt + LangMessages.Main_body_html);
                cnt++;
            }
        });
        i1.setControl(b);
        i2.setControl(browser);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

    public static void main1(String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText(LangMessages.Main_prbrowser);
        Button b = new Button(shell, SWT.PUSH);
        b.setBounds(0, 0, 100, 50);
        final Button b2 = new Button(shell, SWT.TOGGLE);
        b2.setBounds(200, 0, 100, 50);
        final Browser browser = new Browser(shell, SWT.NONE);
        browser.setBounds(100, 100, 200, 200);
        b.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event e)
            {
                browser.setText(LangMessages.Main_html_body_hello + cnt + LangMessages.Main_body_html);
                cnt++;
            }
        });
        b2.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event e)
            {
                boolean flag = b2.getSelection();
                if (!flag) System.out.println(LangMessages.Main_make_browser_visible + flag);
                browser.setVisible(flag);
            }
        });
        shell.setSize(400, 400);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
    }
}