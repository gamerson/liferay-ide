
package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.swt.graphics.Color;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Jure Polutnik
 */
public class GradientHelper
{

    public static Image createGradientImageFor( Composite composite, Color color1, Color color2, boolean vertical )
    {

        Rectangle rect = composite.getClientArea();
        Image image = null;

        if( vertical )
        {
            image = new Image( composite.getDisplay(), 1, Math.max( 1, rect.height ) );
        }
        else
        {
            image = new Image( composite.getDisplay(), Math.max( 1, rect.width ), 1 );
        }

        GC gc = new GC( image );
        gc.setForeground( color1 );
        gc.setBackground( color2 );

        if( vertical )
        {
            gc.fillGradientRectangle( 0, 0, 1, rect.height, true );
        }
        else
        {
            gc.fillGradientRectangle( 0, 0, rect.width, 1, false );
        }
        gc.dispose();

        return image;
    }
}
