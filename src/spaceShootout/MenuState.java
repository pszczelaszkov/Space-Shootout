
package spaceShootout;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.scene.Node;
import com.jme3.input.RawInputListener;
import com.jme3.input.InputManager;

    class MenuState extends AbstractAppState implements RawInputListener
    {
        private Node rootNode;
        private Node guiNode;
        private InputManager inputManager;
        private Node localRootNode = new Node("Menu Screen RootNode");
        private Node localGuiNode = new Node("Menu Screen GuiNode");

        public MenuState(SimpleApplication app){
          this.rootNode     = app.getRootNode();
          this.guiNode       = app.getGuiNode();
          this.inputManager = app.getInputManager();
        }
        
        @Override
        public void initialize(AppStateManager stateManager, Application app) 
        {
          super.initialize(stateManager, app);
        }
        
        @Override
        public void update(float tpf) 
        {
        }
        
        @Override
        public void stateAttached(AppStateManager stateManager) 
        {
          rootNode.attachChild(localRootNode);
          guiNode.attachChild(localGuiNode);
          inputManager.addRawInputListener(this);
        }

        @Override
        public void stateDetached(AppStateManager stateManager) 
        {
          rootNode.detachChild(localRootNode);
          guiNode.detachChild(localGuiNode);
          inputManager.removeRawInputListener(this);
        }
        
        public void onJoyAxisEvent(JoyAxisEvent evt) {}             
        public void onJoyButtonEvent(JoyButtonEvent evt){}
        public void beginInput() {}
        public void endInput() {}
        public void onMouseMotionEvent(MouseMotionEvent evt) {}
        public void onMouseButtonEvent(MouseButtonEvent evt) {}
        public void onKeyEvent(KeyInputEvent evt) {}
        public void onTouchEvent(TouchEvent evt) {}
    }
