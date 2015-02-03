
package spaceShootout;

import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

class SegmentCreationMovement extends AbstractControl
{

    private MotionPath motionPath;
    private MotionEvent motionEvent;
            
    @Override
    public void update(float tpf){}

    @Override
    public void render(RenderManager rm,ViewPort vp){}

    @Override
    public void setSpatial(Spatial spatial)
    {     
        super.setSpatial(spatial);
        if(spatial != null)
        {
            Vector3f waypoint = spatial.getUserData("targetPosition");
            motionPath = new MotionPath();
            motionPath.addWayPoint(spatial.getLocalTranslation());
            motionPath.addWayPoint(waypoint);
            motionPath.setCurveTension(0f);
            motionPath.addListener(new SegmentCreationListener());
            motionEvent = new MotionEvent(spatial,motionPath);
            motionEvent.setSpeed(7.0f);
            motionEvent.play();
        }
    }

    public void controlUpdate(float tpf){}

    public void controlRender(RenderManager rm,ViewPort vp){}
}
