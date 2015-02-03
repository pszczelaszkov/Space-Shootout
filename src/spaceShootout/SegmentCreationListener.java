package spaceShootout;

import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;

public class SegmentCreationListener implements MotionPathListener
{        
    public void onWayPointReach(MotionEvent e,int wp)
    {

        
        if(wp == 1)
        {
            
            e.getSpatial().setLocalTranslation((Vector3f)e.getSpatial().getUserData("targetPosition"));

        }
    }
}
