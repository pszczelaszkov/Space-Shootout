package spaceShootout;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.input.RawInputListener;
import com.jme3.input.InputManager;
import com.jme3.ui.Picture;
import com.jme3.system.AppSettings;

    class LoadingState extends AbstractAppState implements RawInputListener
    {
        private Node rootNode;
        private Node guiNode;
        private Node localRootNode = new Node("Loading Screen RootNode");
        private Node localGuiNode = new Node("Loading Screen GuiNode");
        private GameState gameState;
        private AssetManager assetManager;
        private AppStateManager appStateManager;
        private InputManager inputManager;
        private AppSettings settings;
        private GuiController guiController;

        
        public LoadingState(Main app,AppSettings settings,GuiController guiController,GameState gameState)
        {
            this.rootNode     = app.getRootNode();
            this.guiNode       = app.getGuiNode();
            this.assetManager  = app.getAssetManager();
            this.appStateManager = app.getStateManager();
            this.inputManager = app.getInputManager();
            this.settings = settings;
            this.guiController = guiController;
            this.gameState = gameState;
        }
        
        @Override
        public void initialize(AppStateManager stateManager, Application app) 
        {
            Picture loadingScreen = new Picture("Loading Screen");
            loadingScreen.setImage(assetManager, "Textures/loadingScreen.jpg", true);
            loadingScreen.setWidth(settings.getWidth());
            loadingScreen.setHeight(settings.getHeight());
            loadingScreen.setPosition(0, 0);
            
            localGuiNode.attachChild(loadingScreen);
            gameState.setEnabled(false);
            appStateManager.attach(gameState);
        }
        
        @Override
        public void update(float tpf) 
        {           
            if(gameState.ready)
            {
                guiController.setHangarState();
                appStateManager.detach(this);
            }
        }
        
        @Override
        public void stateAttached(AppStateManager stateManager) 
        {
            rootNode.attachChild(localRootNode);
            guiNode.attachChild(localGuiNode);
            inputManager.addRawInputListener(this);
        }

        @Override
        public void stateDetached(AppStateManager stateManager) {}
        
        @Override
        public void cleanup()
        {
            rootNode.detachChild(localRootNode);
            guiNode.detachChild(localGuiNode);
            inputManager.removeRawInputListener(this);
        }
        
        public void onJoyAxisEvent(JoyAxisEvent evt) { }
        public void onJoyButtonEvent(JoyButtonEvent evt){}
        public void beginInput() {}
        public void endInput() {}
        public void onMouseMotionEvent(MouseMotionEvent evt) {}
        public void onMouseButtonEvent(MouseButtonEvent evt) {}
        public void onKeyEvent(KeyInputEvent evt) {}
        public void onTouchEvent(TouchEvent evt) {}   
    }