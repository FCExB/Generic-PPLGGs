package gui;

import gg.GameMapConverter;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import pplgg.Map;
//import dk.itu.mario.engine.PlayCustomized;


// Extend our ball class from Canvas
public class MapPanel<T> extends Canvas implements MouseListener {

    boolean selected;
    Map myMap;
    private PPLGGGUI<T> gui;
    private static final int blockWidth = 4;
    private static final int blockHeight = 4;
    
   private GameMapConverter<T> converter;

    public MapPanel(Map map, PPLGGGUI<T> gui, GameMapConverter<T> converter) {
        this(gui, converter);
        this.setSize( blockWidth*map.getWidth(), blockHeight*map.getHeight() );
        myMap = map;
    }


    public MapPanel(PPLGGGUI<T> gui,  GameMapConverter<T> converter) {
        this.addMouseListener(this);
        this.gui = gui;
        selected = false;
        this.converter = converter;
    }


    public void paint(Graphics g) {
        drawMap(g);
    }

    private void drawMap(Graphics graphics) {
        BufferedImage buffer = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
        Graphics g = buffer.getGraphics();
        g.setColor( Color.white);
        g.fillRect( 0, 0, getWidth(), getHeight() );
        if (myMap != null) {
            for (int x=0; x<myMap.getWidth(); x++) {
                for (int y=0; y<myMap.getHeight(); y++) {

                	g.setColor(converter.colorReprisentation(myMap.getTerrain(x, y)));
                    
                    g.fillRect( x*blockWidth, y*blockHeight, blockWidth, blockHeight );
                    g.setColor( Color.white);
                }   
            }
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        g.setColor( Color.black );
        if (!selected)
            g.drawRect( 0, 0, panelWidth-1, getHeight()-1 );
        else {
            g.drawRect( 0, 0, panelWidth-1, panelHeight-1 );
            g.drawRect( 1, 1, panelWidth-3, panelHeight-3 );
            g.drawRect( 2, 2, panelWidth-5, panelHeight-5 );
        }
        Graphics2D graphics2d = (Graphics2D)graphics;
        graphics2d.drawImage(buffer,null,0,0);
    }


    public void setMap( Map newMap ) {
        myMap = newMap;
        this.setSize( blockWidth*newMap.getWidth(), blockHeight*newMap.getHeight() );
        repaint();
    }

    @Override
    public void mouseEntered( MouseEvent arg0 ) {
        selected = true;
        repaint();

    }
    @Override
    public void mouseExited( MouseEvent arg0 ) {
        selected = false;
        repaint();

    }
    @Override
    public void mousePressed( MouseEvent arg0 ) {        

        gui.playGame(this);

    }
    @Override
    public void mouseReleased( MouseEvent arg0 ) {

    }


    @Override
    public void mouseClicked( MouseEvent e ) {

    }


    public Map getMap() {
        return myMap;
    }

}

