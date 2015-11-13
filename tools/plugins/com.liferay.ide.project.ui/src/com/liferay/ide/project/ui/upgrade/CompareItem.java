package com.liferay.ide.project.ui.upgrade;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;


public class CompareItem extends BufferedContent implements ITypedElement, IModificationDate, IEditableContent {
    private String fileName;
    private long time;

    public CompareItem(String fileName) {
        this.fileName = fileName;
        this.time = System.currentTimeMillis();
    }

    /**
     * @see org.eclipse.compare.BufferedContent#createStream()
     */
    protected InputStream createStream() throws CoreException {
        try {
            return new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(new byte[0]);
    }

    /**
     * @see org.eclipse.compare.IModificationDate#getModificationDate()
     */
    public long getModificationDate() {
        return time;
    }

    /**
     * @see org.eclipse.compare.ITypedElement#getImage()
     */
    public Image getImage() {
        return CompareUI.DESC_CTOOL_NEXT.createImage();
    }

    /**
     * @see org.eclipse.compare.ITypedElement#getName()
     */
    public String getName() {
        return fileName;
    }

    /**
     * @see org.eclipse.compare.ITypedElement#getType()
     */
    public String getType() {
        return ITypedElement.TEXT_TYPE;
    }

    /**
     * @see org.eclipse.compare.IEditableContent#isEditable()
     */
    public boolean isEditable() {
        return true;
    }

    /**
     * @see org.eclipse.compare.IEditableContent#replace(org.eclipse.compare.ITypedElement, org.eclipse.compare.ITypedElement)
     */
    public ITypedElement replace(ITypedElement dest, ITypedElement src) {
        return null;
    }

    public void writeFile() {
        this.writeFile(this.fileName, this.getContent());
    }

    private void writeFile(String fileName, byte[] newContent) {
        FileOutputStream fos = null;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            fos = new FileOutputStream(file);
            fos.write(newContent);
            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            fos = null;
        }
    }
}
