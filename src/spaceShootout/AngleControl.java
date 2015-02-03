package spaceShootout;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

public class AngleControl extends AbstractControl
{
    @Override
    public void update(float tpf)
    {
            float[] currentRotation = spatial.getLocalRotation().toAngles(null);
            float targetAngle = spatial.getUserData("targetRotation");
            if(currentRotation[2] > targetAngle)
            {
                if(currentRotation[2] - 2*tpf > targetAngle)
                    spatial.rotate(0f,0f,-2*tpf);
            }
            else
            {
                if(currentRotation[2] + 2*tpf < targetAngle)
                    spatial.rotate(0f,0f,2*tpf); 
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