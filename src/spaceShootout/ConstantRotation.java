package spaceShootout;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

public class ConstantRotation extends AbstractControl
{
    @Override
    public void update(float tpf)
    {
        float xRotation = spatial.getUserData("xRotation");
        float yRotation = spatial.getUserData("yRotation");
        float zRotation = spatial.getUserData("zRotation");
        spatial.rotate(xRotation*tpf,yRotation*tpf,zRotation*tpf);
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