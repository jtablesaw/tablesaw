package com.github.lwhite1.tablesaw.plotting.glimpse;

import com.jogamp.opengl.util.FPSAnimator;
import com.metsci.glimpse.canvas.GlimpseCanvas;
import com.metsci.glimpse.canvas.NewtSwingGlimpseCanvas;
import com.metsci.glimpse.gl.util.GLUtils;
import com.metsci.glimpse.layout.GlimpseLayout;
import com.metsci.glimpse.layout.GlimpseLayoutProvider;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLOffscreenAutoDrawable;
import javax.media.opengl.GLProfile;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Provides static utility methods for initializing a Swing JFrame, adding a GlimpseCanvas,
 * and adding a GlimpseLayout from one of the example classes.
 */
public class Display
{
  private GlimpseCanvas canvas;
  private JFrame frame;
  private GlimpseLayout layout;

  Display(GlimpseCanvas canvas, JFrame frame, GlimpseLayout layout )
  {
    super( );
    this.canvas = canvas;
    this.frame = frame;
    this.layout = layout;
  }

  public GlimpseCanvas getCanvas( )
  {
    return canvas;
  }

  public JFrame getFrame( )
  {
    return frame;
  }

  public GlimpseLayout getLayout( )
  {
    return layout;
  }

  public static Display showWithSwing( GlimpseLayoutProvider layoutProvider, String profileString ) throws Exception
  {
    return showWithSwing( layoutProvider, GLProfile.get( profileString ) );
  }

  public static Display showWithSwing( GlimpseLayoutProvider layoutProvider, GLProfile profile ) throws Exception
  {
    // generate a GLContext by constructing a small off-screen framebuffer
    final GLOffscreenAutoDrawable glDrawable = GLUtils.newOffscreenDrawable( profile );

    GLContext context = glDrawable.getContext( );

    // create a SwingGlimpseCanvas which shares the context
    // other canvases could also be created which all share resources through this context
    final NewtSwingGlimpseCanvas canvas = new NewtSwingGlimpseCanvas( context );

    // create a top level GlimpseLayout which we can add painters and other layouts to
    GlimpseLayout layout = layoutProvider.getLayout( );
    canvas.addLayout( layout );

    // set a look and feel on the canvas (this will be applied to all attached layouts and painters)
    // the look and feel affects default colors, fonts, etc...
    canvas.setLookAndFeel( new TablesawLookAndFeel());

    // attach an animator which repaints the canvas in a loop
    // usually only one FPSAnimator is necessary (multiple GlimpseCanvas
    // can be repainted from the same FPSAnimator)
   // GLAnimatorControl animator = new FPSAnimator( 120 );
   // animator.add( canvas.getGLDrawable( ) );
   // animator.start( );



    // create a Swing Frame to contain the GlimpseCanvas
    final JFrame frame = new JFrame( "Tablesaw");

    // This listener is added before adding the SwingGlimpseCanvas to the frame because
    // NEWTGLCanvas adds its own WindowListener and this WindowListener should receive the WindowEvent first
    // (although I'm now not sure how much this matters)
    frame.addWindowListener( new WindowAdapter( )
    {
      @Override
      public void windowClosing( WindowEvent e )
      {
        // dispose of GlimpseLayouts and GlimpsePainters attached to GlimpseCanvas
        canvas.disposeAttached( );
        // destroy heavyweight canvas and GLContext
        canvas.destroy( );
      }
    } );

    // make the frame visible
    frame.setSize( 800, 800 );
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setVisible( true );

    // add the GlimpseCanvas to the frame
    // this must be done on the Swing EDT to avoid JOGL crashes
    // when removing the canvas from the frame
    // it should also be done after the frame has been made visible or
    // the GlimpseCanvas GLEventListener.reshape( ) may be called
    // with spurious sizes (possible NEWT bug)
    SwingUtilities.invokeAndWait( new Runnable( )
    {
      @Override
      public void run( )
      {
        frame.add( canvas );
        frame.validate( );
      }
    } );

    Display display = new Display( canvas, frame, layout );
    return display;
  }

  public static Display showWithSwing( GlimpseLayoutProvider layoutProvider ) throws Exception
  {
    return showWithSwing( layoutProvider, GLUtils.getDefaultGLProfile( ) );
  }

  public static void showWithSwing( GlimpseLayoutProvider layoutProviderA, GlimpseLayoutProvider layoutProviderB ) throws Exception
  {
    // generate a GLContext by constructing a small offscreen framebuffer
    GLProfile glProfile = GLUtils.getDefaultGLProfile( );
    GLDrawableFactory factory = GLDrawableFactory.getFactory( glProfile );
    GLCapabilities glCapabilities = new GLCapabilities( glProfile );
    final GLOffscreenAutoDrawable glDrawable = factory.createOffscreenAutoDrawable( null, glCapabilities, null, 1, 1 );

    // trigger GLContext creation
    glDrawable.display( );
    GLContext context = glDrawable.getContext( );

    final NewtSwingGlimpseCanvas leftPanel = new NewtSwingGlimpseCanvas( context );
    leftPanel.addLayout( layoutProviderA.getLayout( ) );

    final NewtSwingGlimpseCanvas rightPanel = new NewtSwingGlimpseCanvas( context );
    rightPanel.addLayout( layoutProviderB.getLayout( ) );

    FPSAnimator animator = new FPSAnimator( 120 );
    animator.add( leftPanel.getGLDrawable( ) );
    animator.add( rightPanel.getGLDrawable( ) );
    animator.start( );

    WindowAdapter disposeListener = new WindowAdapter( )
    {
      @Override
      public void windowClosing( WindowEvent e )
      {
        leftPanel.disposeAttached( );
        rightPanel.disposeAttached( );
        leftPanel.destroy( );
        rightPanel.destroy( );
      }
    };

    final JFrame rightFrame = new JFrame( "Glimpse Example (Frame A)" );
    rightFrame.addWindowListener( disposeListener );

    final JFrame leftFrame = new JFrame( "Glimpse Example (Frame B)" );
    leftFrame.addWindowListener( disposeListener );

    rightFrame.setSize( 800, 800 );
    rightFrame.setLocation( 800, 0 );
    rightFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    rightFrame.setVisible( true );

    leftFrame.setSize( 800, 800 );
    leftFrame.setLocation( 0, 0 );
    leftFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    leftFrame.setVisible( true );

    SwingUtilities.invokeAndWait( new Runnable( )
    {
      @Override
      public void run( )
      {
        rightFrame.add( rightPanel );
        leftFrame.add( leftPanel );
        rightFrame.validate( );
        leftFrame.validate( );
      }
    } );
  }
}