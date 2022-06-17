/*
 * @author: Emily Godwin
 * File: GlorbaksCows.java
 * CMSC 405
 * Date: 6/15/21
 * Description: This is a basic openGL java animation. It employs hierarchical modeling and
 *  lighting techniques to display an interesting and abstract scene. I definitely learned a lot
 *  about openGL and how to begin to explore this as an easy way to learn computer graphics and animation.
 *  I used a lot of the JOGL Example files as the basis for this, including camera.java and FourLights.java
 *  to understand lighting and to set up the camera.
 */

//imports
import java.awt.*;
import java.awt.event.*;

import java.util.Random;

import javax.swing.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.gl2.GLUT;

public class GlorbaksCows extends JPanel implements GLEventListener {

    //serialized ID
    private static final long serialVersionUID = 1L;

    //main - set up window
    public static void main(String[] args) {
        JFrame window = new JFrame("Glorbaks Cows");
        GlorbaksCows panel = new GlorbaksCows();
        window.setContentPane(panel);
        window.pack();
        window.setLocation(50,50);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    private GLJPanel display;
    private Timer animationTimer;

    private int frameNumber = 0;  // The current frame number for an animation.

    private Camera camera;

    private GLUT glut = new GLUT();
    
    private int frameCounter = 0;

  /*
   * Default Constructor
   */
    public GlorbaksCows() {
        GLCapabilities caps = new GLCapabilities(null);
        display = new GLJPanel(caps);
        display.setPreferredSize( new Dimension(600,600) );
        display.addGLEventListener(this);
        setLayout(new BorderLayout());
        add(display,BorderLayout.CENTER);
        camera = new Camera();
        camera.lookAt(12,10,35, -3.4,2,3.4, -.2,3,-.2);
        camera.setScale(28);
        camera.installTrackball(display);
        animationTimer = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                frameNumber++;
                
                display.repaint();
            }
        });
        animationTimer.setInitialDelay(500);
        animationTimer.start();
    }
    
    /*
     * Method: lights
     * creates the colored lights and provides the transforms for them.
     */
    private void lights(GL2 gl) {

        gl.glColor3d(0.5,0.5,0.5);
        
        float red[] = { 0.5F, 0, 0, 1 };
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, red, 0);  
        gl.glEnable(GL2.GL_LIGHT1);

        gl.glPushMatrix();
        gl.glRotated(-frameNumber, 0, 1, 0);
        gl.glTranslated(10, 7, 0);
        gl.glPopMatrix();
        
        
        float green[] = {0, 0.5F, 0, 1 };
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, green, 0); 
        gl.glEnable(GL2.GL_LIGHT2);

        gl.glPushMatrix();
        gl.glRotated((frameNumber+100)*0.8743, 0, 1, 0);
        gl.glTranslated(9, 8, 0);

        gl.glPopMatrix();
        
        
        float blue[] = { 0, 0, 0.5F, 1 };
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, blue, 0); 
        gl.glEnable(GL2.GL_LIGHT3);

        gl.glPushMatrix();
        gl.glRotated((frameNumber-100)*1.3057, 0, 1, 0);
        gl.glTranslated(9.5, 7.5, 0);

        gl.glPopMatrix();
        
          }

    //array for the colors
    private float[] colorArrayForHue(double hue) {
        Color c = Color.getHSBColor((float)hue, 1, 0.6F);
        return new float[] { c.getRed()/255.0F, c.getGreen()/255.0F, c.getBlue()/255.0F, 1 };
    }

    /*
     * Method: drawCylinder
     * draws a cylinder and determines it's color
     */
    private void drawCylinder(GL2 gl) {
        
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
       
        for (int i = 0; i <= 64; i++) {
            double angle = 2*Math.PI/64 * i;
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            
            gl.glColor3d(1, 1, 1);

            gl.glNormal3d( x, y, 0 );  // Normal for both vertices at this angle.
            gl.glVertex3d( x, y, 1 );  // Vertex on the top edge.
            gl.glVertex3d( x, y, -1 ); // Vertex on the bottom edge.
        }
        gl.glEnd();
        gl.glNormal3d( 0, 0, 1);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);  // Draw the top, in the plane z = 1.
        gl.glColor3d(1,1,0);  // ambient and diffuse for center
        gl.glVertex3d( 0, 0, 1);
        
        //top of cylinder color
        for (int i = 0; i <= 64; i++) {
           double angle = 2*Math.PI/64 * i;
           double x = Math.cos(angle);
           double y = Math.sin(angle);
            gl.glColor3fv(colorArrayForHue(i/64.0), 0);
            gl.glVertex3d( x, y, 1 );
        }
        
        gl.glEnd();
        
        
       gl.glNormal3f( 0, 0, -1 );  
        gl.glBegin(GL2.GL_TRIANGLE_FAN);  // Draw the bottom, in the plane z = -1
        gl.glColor3d(1,1,1);  // ambient and diffuse for center
        gl.glVertex3d( 0, 0, -1);
        for (int i = 64; i >= 0; i--) {
            double angle = 2*Math.PI/64 * i;
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            gl.glColor3fv(colorArrayForHue(i/64.0), 0);
            gl.glVertex3d( x, y, -1 );
        }
        
        gl.glEnd();
        
    }
    /**
     * Draws the scene.
     */
    public void display(GLAutoDrawable drawable) {    
        // called when the panel needs to be drawn

        GL2 gl = drawable.getGL().getGL2();

        gl.glClearColor(0,0,0,0);
        gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT ); 
        //applies the camera to the gl
        camera.apply(gl);
        /*if (frameNumber < 13){
            frameCounter = 0;
        }*/
        if((frameNumber >=0) && (frameNumber <= 75)) {
           frameCounter = frameNumber -1;
//           camera.lookAt(12,10,35, centerX,2,3.4, -.2,3,-.2);

        }
       /* else if(frameNumber > 150) {
            frameCounter = 1;
        }*/
        else if(frameNumber > 400) {
            frameCounter = 0;
        }

        camera.lookAt(12,10,35, frameCounter-10,0, 3, -.2,4,1);
        //applies te lights to the gl
        lights(gl);
    //    float zero[] = { 0, 0, 0, 1 };

        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, new float[] { 0.15F, 0.15F, 0.15F, 1 }, 0 );
       
        //draw sky
        gl.glPushMatrix();
        drawSky(gl);
        gl.glPopMatrix();
        
        //draw grass
        gl.glPushMatrix();
        drawGrass(gl);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslated(0, 0, 9);
        drawCows(gl);
        gl.glPopMatrix();
        
        //house with window
        gl.glPushMatrix();
        gl.glTranslated(14, 0, 2);
        gl.glRotated(150, 0, 1, 0);
        drawHouse(gl);
        gl.glPopMatrix();
        
        //ufo
        gl.glPushMatrix();
        gl.glTranslated(-35, 40, 0);
        if((frameNumber >=0) && (frameNumber <=80)){
            gl.glTranslated((Math.pow(frameNumber, 2)/11+25)/4, (-frameNumber-30)/2, 12);
        }
        //replays the animation
        if(frameNumber > 400) {
            frameNumber = 0;
        }
        if((frameNumber >80) && (frameNumber <400)) {
            gl.glTranslated(65, 3, 2);
        }
        gl.glRotated(160+frameNumber, 1, 1, 1);
        gl.glScaled(3, 3, 0.5);
        drawCylinder(gl);
        gl.glPopMatrix(); 
        
        //alien
        gl.glPushMatrix();
        gl.glTranslated(-34, 42, -2);
        if((frameNumber >=0) && (frameNumber <=80)){
            gl.glTranslated((Math.pow(frameNumber, 2)/11+25)/4, (-frameNumber-30)/2, 12);
        }
        //replays the animation
        if(frameNumber > 400) {
            frameNumber = 0;
        }
        if((frameNumber >80) && (frameNumber <400)) {
            gl.glTranslated(0, -1, 0);
        }
        gl.glRotated(90, 1, 1, 0);
        gl.glScaled(.7, .7, .7);
        drawAlien(gl);
        gl.glPopMatrix();
        
        //cow through window
        gl.glPushMatrix();
        gl.glTranslated(-62, 38, 11);
        gl.glTranslated(((Math.pow(frameNumber, 2))/16+75)/2, (-frameNumber-44)/2, -frameNumber/5);
        if(frameNumber >= 39) {
            gl.glTranslated(-100,  -100,  -100);
        }
        if(frameNumber == 400) {
            frameNumber = 0;
        }
        gl.glRotated(frameNumber*2, 1, 1, 0);
        drawCow(gl);
        gl.glPopMatrix();

    }
    
    /*
     * Method: drawSky
     * draws the sky using hierarchical structure,
     * also uses random to generate the meteor shower
     */
    public void drawSky(GL2 gl2) {
        Random rand = new Random();
        int r, s, t;
        for(int i = -200; i < 200; i++) {
            r = rand.nextInt(20);
            s = rand.nextInt(20);
            t = rand.nextInt(20);
            gl2.glPushMatrix();
            gl2.glTranslated(i+r, i+s, i+t);
            gl2.glScaled(.3, .3, .3);
            drawSpot(gl2);
            gl2.glPopMatrix();
        }
    }
    
    /*
     * Method: drawBack
     * draws the background sky
     */
    public void drawBack(GL2 gl2) {
        gl2.glBegin(GL2.GL_TRIANGLE_FAN);
        gl2.glVertex3d(-1, -1, 1);
        gl2.glVertex3d(1, -1, 1);
        gl2.glVertex3d(1, 1, 1);
        gl2.glVertex3d(-1, 1, 1);
        gl2.glEnd();
    }
    
    /*  
     * Method: drawGrass
     * draws the grass shape
     */
    public void drawGrass(GL2 gl2) {
        gl2.glPushMatrix();
        gl2.glColor3d(0, 1, 0);
        gl2.glTranslated(-5, 10,5);
        gl2.glRotated(90, 1, 0, 0);
        gl2.glScaled(65, 65, 16);
        drawFlat(gl2);
        gl2.glPopMatrix();
        
        //magenta
        gl2.glPushMatrix();
        gl2.glTranslated(40, 10, 7);
        gl2.glScaled(3, 3, 3);
        gl2.glRotated(frameNumber+189, 1, 0, 1);
        drawSplat(gl2, 1, 0,1);
        gl2.glPopMatrix();
        
        //green
        gl2.glPushMatrix();
        gl2.glTranslated(50, 10, 7);
        gl2.glScaled(2, 2, 2);
        gl2.glRotated(frameNumber+89, 1, 0, 0);
        drawPlanet(gl2, 0, 1, 0);
        gl2.glPopMatrix();
        
        //blue
        gl2.glPushMatrix();
        gl2.glTranslated(60, 8, 5);
        gl2.glScaled(2, 2,2);
        gl2.glRotated(frameNumber+89, 0, 1, 1);
        drawSplat(gl2, 1, 0, 0);
        gl2.glPopMatrix();
        
        //pink
        gl2.glPushMatrix();
        gl2.glTranslated(57, 1, 2);
        gl2.glScaled(4, 4, 4);
        gl2.glRotated(frameNumber+89, 0, 1, 0);
        drawPlanet(gl2, 0, 1, 1);
        gl2.glPopMatrix();
        
    }
    
    /*
     * Method: drawSplat
     * draws a sphere with a cow
     */
    public void drawSplat(GL2 gl2, int colorParamR, int colorParamG, int colorParamB) {
        gl2.glNormal3d( 0, 0, 1);
        gl2.glBegin(GL2.GL_TRIANGLE_FAN);  // Draw the top, in the plane z = 1.
        gl2.glColor3d(colorParamR,colorParamG, colorParamB);  // ambient and diffuse for center
        gl2.glVertex3d( 0, 0, 1);
        //drawEye(gl2);
        for (int i = 0; i <= 64; i++) {
            glut.glutSolidSphere(2, 24, 12); 
            gl2.glColor3fv(colorArrayForHue(i+colorParamR+colorParamG/64.0), 0);
            
        }
        
        gl2.glEnd();
        
        gl2.glPushMatrix();
        gl2.glTranslated(0, 0, 4);
        gl2.glScaled(.3, .3, .3);
        gl2.glRotated(frameNumber+90, 1, 0, 0);
        drawCow(gl2);
        gl2.glPopMatrix();
    }
    
    /*
     * Method: drawPlanet
     * draws a sphere with an eye
     */
    public void drawPlanet(GL2 gl2, int colorParamR, int colorParamG, int colorParamB) {
        gl2.glNormal3d( 0, 0, 1);
        gl2.glBegin(GL2.GL_TRIANGLE_FAN);  // Draw the top, in the plane z = 1.
        gl2.glColor3d(colorParamR,colorParamG, colorParamB);  // ambient and diffuse for center
        gl2.glVertex3d( 0, 0, 1);
        
        for (int i = 0; i <= 64; i++) {
           glut.glutSolidSphere(2, 24, 12); 
           gl2.glColor3fv(colorArrayForHue(i/64.0), 0);
         }
        
        gl2.glEnd();
        
        gl2.glPushMatrix();
        gl2.glTranslated(0, 0, 5.4);
        gl2.glScaled(.5, .5, .5);
        gl2.glRotated(frameNumber+90, 1, 0, 0);
        drawCow(gl2);
        gl2.glPopMatrix();
    }
    
    /*
     * Method: drawFlat
     * draws a grass plain
     */
    public void drawFlat(GL2 gl2) {
        gl2.glBegin(GL2.GL_TRIANGLE_FAN);
        gl2.glVertex3d(-1, -1, 1);
        gl2.glVertex3d(1, -1, 1);
        gl2.glVertex3d(1, 1, 1);
        gl2.glVertex3d(-1, 1, 1);
        gl2.glEnd();
    }
    
    /*
     * Method: drawCows
     * draws the cows using hierarchical structure
     * to call the drawCow method for each cow in the grass
     */
    public void drawCows(GL2 gl2) {
       //cow 3
       gl2.glPushMatrix();
       drawCow(gl2);
       gl2.glPopMatrix();
      
       //cow 2
       gl2.glPushMatrix();
       gl2.glTranslated(-16, 0, -2.5);
       gl2.glScaled(.3, .3, .3);
       drawCow(gl2);
       gl2.glPopMatrix();
       
       //cow 1
       gl2.glPushMatrix();
       gl2.glTranslated(-5, 0, 3);
       gl2.glScaled(1.2, 1.2, 1.2);
       drawCow(gl2);
       gl2.glPopMatrix();
       
       //cow 4
       gl2.glPushMatrix();
       gl2.glTranslated(-13, 0, 1);
       gl2.glScaled(.8, .8, .8);
       oppositeCow(gl2);
       gl2.glPopMatrix();
       
       //cow 5
       gl2.glPushMatrix();
       gl2.glTranslated(23, 0, 5);
       gl2.glScaled(.5, .5, .5);
       drawCow(gl2);
       gl2.glPopMatrix();
       
       
     //cow 6
       gl2.glPushMatrix();
       gl2.glTranslated(29, 0, 5);
       gl2.glScaled(.8, .8, .8);
       gl2.glRotated(20, 1, 0, 0);
       drawCow(gl2);
       gl2.glPopMatrix();

   }
    
    /*
     * Method: oppositeCow
     * draws cows for the opposite direction.
     */
    public void oppositeCow(GL2 gl2) {
        gl2.glColor3f(0.5f, 0.35f, 0.05f);
        glut.glutSolidSphere(2, 24, 12);

        //head
        gl2.glPushMatrix();
        gl2.glTranslated(2.5, 1, 0);
        glut.glutSolidSphere(1, 12, 12);
        gl2.glPopMatrix();
       
        gl2.glPushMatrix();
        gl2.glTranslated(0, 1, 2.4);
        gl2.glScaled(.5,  .5,  .5); 
        drawSpot(gl2);
        gl2.glPopMatrix();
        
        gl2.glTranslated(0, -.2, 0);
        drawLegs(gl2);
        gl2.glEnd();
    }
    
    /*
     * Method: drawCow
     * inner method used to draw 
     * calls drawSpot and uses glut solid spheres
     * to draw individual cows
     */
    public void drawCow(GL2 gl2) {
        gl2.glColor3f(0.5f, 0.35f, 0.05f);
        glut.glutSolidSphere(2, 24, 12);
        gl2.glPushMatrix();
        gl2.glTranslated(-2, 1, 0);
        glut.glutSolidSphere(1, 12, 12);
        gl2.glPopMatrix();
        
       
        gl2.glPushMatrix();
        gl2.glTranslated(.02, .12, 2.63);
        gl2.glScaled(.5,  .5,  .5); 
        drawSpot(gl2);
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glTranslated(.42, .22, 2.69);
        gl2.glScaled(.7,  .7,  .7); 
        drawSpot(gl2);
        gl2.glPopMatrix();
        
        drawLegs(gl2);
        gl2.glEnd();
    }
    
    /*
     * Method: drawSpot
     * draws a spot for the cows
     */
    public void drawSpot(GL2 gl2) {
        gl2.glBegin(GL2.GL_TRIANGLE_FAN);
        gl2.glColor3d(1,1,1);
        gl2.glVertex3d( 0, 0, -1);
        for (int i = 64; i >= 0; i--) {
            double angle = 2*Math.PI/64 * i;
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            gl2.glVertex3d( x, y, -1 );
        }
        gl2.glEnd();
    }
    
    /*
     * Method: drawAlien
     * draws an alien using glut spheres
     */
    public void drawAlien(GL2 gl2) {
        gl2.glPushMatrix();
        gl2.glColor3f(.98f, .625f, .12f);
        glut.glutSolidSphere(2, 24, 12);
        gl2.glPopMatrix();
        
        gl2.glPushMatrix();
        gl2.glColor3f(.98f, .625f, .12f);
        gl2.glTranslated(-2, -3, 0);
        gl2.glScaled(.5, .5, .5);
        glut.glutSolidSphere(1, 12, 6);
        gl2.glPopMatrix();
        
    }
    
    /*
     * Method: drawLeg
     * inner method to draw the basic leg shape by calling
     * drawSquare
     */
    public void drawLeg(GL2 gl2, double x, double y, double z) {
        gl2.glPushMatrix();
        gl2.glColor3f(0.5f, 0.35f, 0.05f);
        gl2.glTranslated(x, y, z);
        gl2.glScaled(1, 2.3, 1);
        drawSquare(gl2);
        gl2.glPopMatrix();
    }
    
    /*
     * Method: drawLegs
     * uses hierarchical modeling to create the legs of the cows
     */
    public void drawLegs(GL2 gl2) {
        //front right
        drawLeg(gl2, -.8, -1.8, -1.8);
        //back right
        drawLeg(gl2, .9, -1.8,-1.8);
        //front left
        drawLeg(gl2, -.8, -1.5, 1);
        //back left
        drawLeg(gl2, .9, -1.5, 1);
    }
    
    /*
     * Method: drawSquare
     * method to draw a basic square shape
     */
    private void drawSquare(GL2 gl2) {
        gl2.glBegin(GL2.GL_TRIANGLE_FAN);
        gl2.glVertex3d(-0.5, -0.5, 0.5);
        gl2.glVertex3d(0.5, -0.5, 0.5);
        gl2.glVertex3d(0.5, 0.5, 0.5);
        gl2.glVertex3d(-0.5, 0.5, 0.5);
        gl2.glEnd();
    }
    
    /*
     * Method: drawHouse
     * uses 2d arrays to create the points of the
     * house object. Uses hierarchical modeling to
     * put a square on the house for a window.
     */
    public void drawHouse(GL2 gl2) {
      //2d array to contain vertex points
        double [][] vertices = {
        { 2, -1, 2 },
        { 2, -1, -2 },
        { 2, 1, -2 },
        { 2, 1, 2 },
        { 1.5, 1.5, 0 },
        { -1.5, 1.5, 0 },
        { -2, -1, 2 },
        { -2, 1, 2 },
        { -2, 1, -2 },
        { -2, -1, -2 }
     };
    //2d array to contain face points
    int [][] faces = {
            { 0, 1, 2, 3 },
            { 3, 2, 4 },
            { 7, 3, 4, 5 },
            { 2, 8, 5, 4 },
            { 5, 8, 7 },
            { 0, 3, 7, 6 },
            { 0, 6, 9, 1 },
            { 2, 1, 9, 8 },
            { 6, 7, 8, 9 }
         };
     //2d array to contain face colors
     double[][] faceColors = {
            { 1, .8, .8 },
            { .7, .7, 1 },
            { 0, 0, 1 },
            { 0, 0, .7 },
            { .7, .7, 1 },
            { 1, 0, 0 },
            { .4, .4, .4 },
            { 1, 0, 0 },
            { 1, .8, .8 }
         };
         
            gl2.glPushMatrix();
            gl2.glRotated(85, 0, 1, 0);
            gl2.glScaled(4,4,4);

            int i,j;
            //draw the house using heirarchical modeling and a for loop
           for (i = 0; i < faces.length; i++) {
               gl2.glPushMatrix();
               gl2.glColor3d(1,1,1);
               gl2.glTranslatef(1.5f, 0f, 0f);
               gl2.glScaled(1,1, 1);
               gl2.glRotated(90, 0, 1, 0);
               drawSquare(gl2);
               gl2.glPopMatrix();
               gl2.glColor3dv(faceColors[i], 0 );
               gl2.glBegin(GL2.GL_TRIANGLE_FAN);
            for (j = 0; j < faces[i].length; j++) {
                    int vertexNum = faces[i][j];
                    gl2.glVertex3dv(vertices[vertexNum], 0 );
                }
                gl2.glEnd();
                
            }
          gl2.glPopMatrix();
        
      }

    
    /**
     * Initialization, including setting up a camera and light
     * configuration
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0, 0, 0, 1);  
        gl.glEnable(GL2.GL_DEPTH_TEST); 
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);
        gl.glMateriali(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 32);

        float dim[] = { 0.5F, 0.5F, 0.5F, 1 };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, dim, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, dim, 0);

        float red[] =  { 0.5F, 0, 0, 1};
        float reda[] = { 0.1F, 0, 0, 1};
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, reda, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, red, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, red, 0);

        float gr[] = { 0, 0.5F, 0, 1 };
        float gra[] = { 0, 0.1F, 0, 1 };
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, gra, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, gr, 0);
        gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, gr, 0);

        float bl[] = {0, 0, 0.5F, 1};
        float bla[] = {0, 0, 0.1F, 1};
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_AMBIENT, bla, 0);
        gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_DIFFUSE, bl, 0);
        gl. glLightfv(GL2.GL_LIGHT3, GL2.GL_SPECULAR, bl, 0);
    }
    
    /**
     * allows the panel to be reshaped
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    /**
     * This is called before the GLJPanel is destroyed. 
     */
    public void dispose(GLAutoDrawable drawable) {
    }
}