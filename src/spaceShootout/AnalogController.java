package spaceShootout;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class AnalogController
{

    private Node localGuiNode;
    private Node guiNode;
    private Node analogAreaNode;
    private Node fireButtonNode;
    private Geometry analog;
    private Geometry analogArea;
    private Geometry fireButton;
    private Geometry dodgeButton;
    private Vector2f analogPosition;//x,y
    private Camera cam;
    private AssetManager assetManager;
    private GameState gameState;
    
    private int width;
    private int height;

    private float analogAreaSize;
    private float analogSize;
    private float analogAreaPosition[] = new float[2];
    private float fireButtonPosition;
    private float dodgeButtonPosition;
    
    public boolean fire;
    public boolean canDodge;
    public long lastHitAnalog;
    
    AnalogController(Node guiNode,Camera cam,AssetManager assetManager,GameState gameState)
    {
        
        localGuiNode = new Node();
        fireButtonNode = new Node();
        analogAreaNode = new Node();
        this.guiNode = guiNode;
        this.cam = cam;
        this.assetManager = assetManager;
        this.gameState = gameState;
        analogPosition = new Vector2f(0,0);
                
        localGuiNode.attachChild(fireButtonNode);
        localGuiNode.attachChild(analogAreaNode);
        attach();       
        
        fire = false;
        canDodge = false;
        width = cam.getWidth();
        height = cam.getHeight();
        analogAreaSize = height * 0.4f;
        analogSize = height * 0.2f;
        analogAreaPosition[0] = width * 0.15f;
        fireButtonPosition = width - width * 0.4f;
        dodgeButtonPosition = width * 0.45f;
        analogAreaPosition[1] = height * 0.2f;
        
        //create AnalogArea
        Quad q = new Quad(analogAreaSize,analogAreaSize);
        analogArea = new Geometry("quad",q);
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", assetManager.loadTexture("Textures/analogArea.png"));
        m.setTransparent(true);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        analogArea.setMaterial(m);
        analogArea.setLocalTranslation(-analogAreaSize/2, -analogAreaSize/2, 0);
        analogAreaNode.attachChild(analogArea);     
        analogAreaNode.setLocalTranslation(analogAreaPosition[0], analogAreaPosition[1], 0);
        //create Analog
        q = new Quad(analogSize,analogSize);
        analog = new Geometry("quad",q);
        m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", assetManager.loadTexture("Textures/analog.png"));
        m.setTransparent(true);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        analog.setMaterial(m);
        analog.setLocalTranslation(-analogSize/2,-analogSize/2,0);
        analogAreaNode.attachChild(analog);
        
        //create fireButton
        q = new Quad(analogSize,analogSize);
        fireButton = new Geometry("point",q);
        m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", assetManager.loadTexture("Textures/fireButton.png"));
        m.setTransparent(true);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        fireButton.setMaterial(m);
        fireButton.setLocalTranslation(fireButtonPosition, -analogSize/2, 0);
        analogAreaNode.attachChild(fireButton);
        
        //create dodgeButton
        q = new Quad((analogSize*0.80f)*1.6f,analogSize*0.80f);
        dodgeButton = new Geometry("point",q);
        setDodgeActive(true);
        dodgeButton.setLocalTranslation(dodgeButtonPosition, -analogSize, 0);
        analogAreaNode.attachChild(dodgeButton);
    }
    
    public void remove()
    {
        guiNode.detachChild(localGuiNode);
    }
    
    public final void attach()
    {
        guiNode.attachChild(localGuiNode);
    }
    
    public void update(int x,int y)
    {
        Ray ray = new Ray(new Vector3f(x,y,0),cam.getDirection());
        CollisionResults collisionResult = new CollisionResults();
        analogArea.collideWith(ray, collisionResult);
        if(collisionResult.size() > 0)
        {
                Vector2f newPosition = new Vector2f(collisionResult.getClosestCollision().getContactPoint().x - analogAreaPosition[0],collisionResult.getClosestCollision().getContactPoint().y - analogAreaPosition[1]); 
                analogPosition = newPosition;
                analog.setLocalTranslation(analogPosition.x-analogSize/2,analogPosition.y-analogSize/2,0);
        }
        collisionResult.clear();
        fireButton.collideWith(ray, collisionResult);
        if(collisionResult.size() > 0)
                fire = true;
        
        collisionResult.clear();
        dodgeButton.collideWith(ray, collisionResult);
        if(collisionResult.size() > 0)
        {
            if(canDodge&&gameState.maxSpeed == 100&&gameState.upgradePoints >= 100)  
            {
                gameState.audioManager.playAudioInstance("click");
                gameState.upgradePoints -= 100;
                setDodgeActive(false);
                gameState.startDodge();
            }
            else
                gameState.audioManager.playAudioInstance("alert");
            
        }
        
        analog.setLocalTranslation(analogPosition.x-analogSize/2,analogPosition.y-analogSize/2,0);
    }
    
    public void resetAnalog()
    {
        analogPosition = Vector2f.ZERO;
        analog.setLocalTranslation(analogPosition.x-analogSize/2,analogPosition.y-analogSize/2,0);
    }
    
    public void resetFire()
    {
        fire = false;
    }
    
    public void reset(int x,int y)
    {
        Ray ray = new Ray(new Vector3f(x,y,0),cam.getDirection());
        CollisionResults collisionResult = new CollisionResults();
        analogArea.collideWith(ray, collisionResult);
        if(collisionResult.size() > 0)
        {
            resetAnalog();
            return;
        }
        
        collisionResult.clear();
        fireButton.collideWith(ray, collisionResult);
        if(collisionResult.size() > 0)
        {
            resetFire();
            return;
        }
        
        //default for safety reset both
        resetFire();
        resetAnalog();
    }
    
    public Vector2f getAnalogPosition()
    {
        return analogPosition;
    }
    
    public final void setDodgeActive(boolean enabled)
    {
        Material m;
        m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        if(enabled)
        {
            canDodge = true;
            m.setTexture("ColorMap", assetManager.loadTexture("Textures/dodgeEnabled.png"));
        }
        else
        {
            canDodge = false;
            m.setTexture("ColorMap", assetManager.loadTexture("Textures/dodgeDisabled.png"));
        }
        m.setTransparent(true);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        dodgeButton.setMaterial(m);
    }
}
