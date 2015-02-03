package spaceShootout;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;

public class Main extends SimpleApplication {

    public SensorController sensorController;
    public GuiController guiController;
    public AndroidBridge android;
    public static void main(String[] args) 
    {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL2);
        settings.setAudioRenderer(AppSettings.LWJGL_OPENAL);
        app.setSettings(settings);
       // app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() 
    {
        //basic init methods
        setDisplayStatView(false);
        setDisplayFps(false);
        flyCam.setEnabled(false);
        sensorController = new SensorController(true);
        guiController = new GuiController(stateManager,new AudioManager(rootNode,assetManager),this,settings);
        if(settings.getHeight() < 700)
        {
            guiController.smallFontPath = "Interface/Fonts/normal/PerfectDarkBRKsmall.fnt";
            guiController.bigFontPath = "Interface/Fonts/normal/PerfectDarkBRK.fnt";
        }
        else
        {
            guiController.smallFontPath = "Interface/Fonts/big/PerfectDarkBRKsmall.fnt";
            guiController.bigFontPath = "Interface/Fonts/big/PerfectDarkBRK.fnt";
        }
        stateManager.attach(guiController);
        //
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
        assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/Gui.xml", "start",guiController);
        guiViewPort.addProcessor(niftyDisplay);
    }
    
    @Override
    public void simpleUpdate(float tpf) {}

    @Override
    public void simpleRender(RenderManager rm) {}
    
    public void setAndroidListener(AndroidBridge android)
    {
        this.android = android;
    }
}