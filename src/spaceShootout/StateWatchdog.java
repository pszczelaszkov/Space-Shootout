package spaceShootout;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class StateWatchdog extends AbstractControl
{

    private GameState state;
    
    StateWatchdog(GameState state)
    {
        this.state = state;
    }
    
    @Override
    public void update(float tpf)
    {
        state.ready = true;
    }
    
    @Override
    protected void controlUpdate(float tpf) {}

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
