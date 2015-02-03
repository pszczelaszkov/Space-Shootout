package spaceShootout;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;

public class HealthBar extends AbstractControl 
{    
    private Node displayNode;
    Geometry bar;
    boolean attached;
    
    HealthBar(Node displayNode,AssetManager assetManager)
    {
        this.displayNode = displayNode;
        
        Quad quad = new Quad(12f,1f);
        bar = new Geometry("Health Bar", quad);
        Material barMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
        barMat.setTexture("ColorMap", assetManager.loadTexture("Textures/healthbar.PNG")); 
        barMat.setTransparent(true);
        barMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        bar.setQueueBucket(RenderQueue.Bucket.Translucent); 
        bar.setMaterial(barMat);       
        
        attached = false;
    }
    
    @Override
    public void update(float tpf)
    {
        int health = spatial.getUserData("health");
        int maxHealth = spatial.getUserData("maxHealth");
        
        if(health < maxHealth)
        {
            if(!attached)
                displayNode.attachChild(bar);
            
            attached = true;
            bar.setLocalScale((float)((float)health/(float)maxHealth), 1, 1);
            Vector3f spatialTranslation = spatial.getWorldTranslation();
            bar.setLocalTranslation(spatialTranslation.x-(bar.getLocalScale().x*5), spatialTranslation.y+(spatial.getLocalScale().y*2), spatialTranslation.z);
        }
    }

    public void remove()
    {
        displayNode.detachChild(bar);
    }
    
    @Override
    public void render(RenderManager rm,ViewPort vp){}

    @Override
    public void setSpatial(Spatial spatial)
    {
        super.setSpatial(spatial);
        if(spatial == null)
            remove();
    }

    public void controlUpdate(float tpf){}

    public void controlRender(RenderManager rm,ViewPort vp){}
    
}
