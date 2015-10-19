/*
 * Created on 2005-12-27
 *
 */
package com.royalstone.security;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CodeImage 
{
	
    public CodeImage( int w, int h ) {
        width                  = w;
        height                 = h;
    }
    
    public BufferedImage getImage(String num)
    {
    	Random random = new Random();
        BufferedImage image = new BufferedImage ( width, height, BufferedImage.TYPE_INT_RGB );
        Graphics g = image.getGraphics();
        g.setColor( Color.white );
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLUE);
        Font myfont = new Font( "", Font.BOLD , 20 );
        g.setFont( myfont );
        g.drawString( num, 10, 20 ); 
          for (int i=0; i<50; i++)
        {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.drawLine(x,y,x,y);
        }
        g.dispose();
        return image;
    }    
    
    private int width          = 0;
    private int height         = 0;
}

