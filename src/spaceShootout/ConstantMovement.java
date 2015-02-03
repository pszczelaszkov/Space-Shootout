
package spaceShootout;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

class ConstantMovement extends AbstractControl
{
            
    @Override
    public void update(float tpf)
    {
        Vector3f moveVector = spatial.getUserData("moveVector");
        moveVector = moveVector.mult((Float)spatial.getUserData("speed"));
        moveVector = moveVector.mult(tpf);      
        spatial.move(moveVector);
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
