package spaceShootout;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class SelfCheck extends AbstractControl
{

     @Override
    public void update(float tpf)
    {
        //Node collisionNode = spatial.getUserData("collisionNode");//get Collision Node/s
        long dieTime = spatial.getUserData("dieTime");//get die time
        
        //too old
        if(dieTime < System.currentTimeMillis())
        {
            Node parent = spatial.getParent();
            String name = parent.getName();
            if(name != null && name.equals("AimNode")) //if is in aimNode delete aimNode
                parent.removeFromParent();
            else
                spatial.removeFromParent();
            
            HealthBar healthBar = spatial.getControl(HealthBar.class);
            if(healthBar != null)
                healthBar.remove();
        }
    }

    @Override
    public void render(RenderManager rm,ViewPort vp){}

    @Override
    public void setSpatial(Spatial spatial)
    {
        super.setSpatial(spatial);
    }
    
    @Override
    protected void controlUpdate(float tpf) 
    {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) 
    {
    }
}
