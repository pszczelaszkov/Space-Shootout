
package spaceShootout;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

class GateMovement extends AbstractControl
{

    private GameState gameState;
            
    GateMovement(GameState gameState)
    {
        this.gameState = gameState;
    }
    
    @Override
    public void update(float tpf)
    {
        if(!gameState.hangarMode)
        {
            float[] currentRotation = spatial.getLocalRotation().toAngles(null);
            float targetAngle = spatial.getUserData("targetRotation");
            if(currentRotation[1] > targetAngle)
            {
                if(currentRotation[1] - 0.5*tpf > targetAngle)
                    spatial.rotate(0f,-0.5f*tpf,0);
            }
            else
            {
                if(currentRotation[1] + 0.5*tpf < targetAngle)
                    spatial.rotate(0f,0.5f*tpf,0);
            }
            //check gate is open
            if(currentRotation[1] > 1.5f && currentRotation[1] < 4.7f)
            {
                gameState.setEnabled(true);
                gameState.attachGui();
            }
        }
    }

    @Override
    public void render(RenderManager rm,ViewPort vp){}

    @Override
    public void setSpatial(Spatial spatial)
    {
        super.setSpatial(spatial);
    }

    public void controlUpdate(float tpf){}

    public void controlRender(RenderManager rm,ViewPort vp){}
}
