package spaceShootout;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

public class WeaponControl 
{
    private long exhausted;
    private AssetManager assetManager;
    
    WeaponControl(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        exhausted = System.currentTimeMillis();
    }
    
    public boolean canShoot()
    {
        if(exhausted > System.currentTimeMillis())
            return false;
        
        return true;
    }
    
    public void addExhausted(int value)
    {
        exhausted = System.currentTimeMillis() + value;
    }
    
    public Spatial getBullet()
    {
        //Line l = new Line(new Vector3f(0,0,0),new Vector3f(0,0,50));
        /*Quad quad = new Quad(5,5);
        Geometry geometry = new Geometry("line", quad);
        geometry.rotate(FastMath.DEG_TO_RAD*-130,FastMath.DEG_TO_RAD*0 , FastMath.DEG_TO_RAD*0);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //material.setColor("Color", ColorRGBA.Yellow);
        Texture texture = assetManager.loadTexture("Textures/gunshot.png");
        material.setTexture("ColorMap", texture); 
        material.setTransparent(true);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geometry.setQueueBucket(Bucket.Translucent); 
        geometry.setMaterial(material);*/
        Spatial spatial = assetManager.loadModel("Models/gunshot/gunshot.j3o");
        return spatial; 
    }
}
