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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import java.util.List;

public class AimController 
{
    Node localGuiNode;
    Node pointerNode;
    Node guiNode;
    Geometry point;
    Camera cam;
    Recycler recycler;
    GameState gameState;
    
    private AudioManager audioManager;
    private int width;
    private int height;
    private int maxX;
    private int maxY;
    private float pointerSize;
    private boolean configExplosions;
   
    AimController(Node guiNode,Camera cam,AssetManager assetManager,Recycler recycler,GameState gameState,AudioManager audioManager,boolean configExplosions)
    {
        localGuiNode = new Node();
        pointerNode = new Node();
        localGuiNode.attachChild(pointerNode);
        guiNode.attachChild(localGuiNode);
        this.guiNode = guiNode;
        this.cam = cam;
        this.recycler = recycler;
        this.gameState = gameState;
        this.audioManager = audioManager;
        this.configExplosions = configExplosions;
        width = cam.getWidth();
        height = cam.getHeight();
        pointerSize = height * 0.2f;
        
        pointerNode.setLocalTranslation(width*0.05f, height*0.05f, 0);
        maxX = (int)(width*0.8f);
        maxY = (int)(height*0.8f);
                
        Quad q = new Quad(pointerSize,pointerSize);
        point = new Geometry("point",q);
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", assetManager.loadTexture("Textures/pointer.png"));
        m.setTransparent(true);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        point.setMaterial(m);
        point.setLocalTranslation((float)maxX/2, (float)maxY/2, 0);
        pointerNode.attachChild(point);
    }
          
    public void updatePointer(Vector2f offset)
    {
        Vector3f localTranslation = point.getLocalTranslation().add(offset.x, offset.y, 0);
        if(localTranslation.x < maxX && localTranslation.y < maxY && localTranslation.x > 0 && localTranslation.y > 0)
            point.move(offset.x, offset.y, 0);
    }
    
    public void shoot(Spatial bullet,Node parent,Node collisionNode)
    {
            audioManager.playAudioInstance("gunShoot");
            Vector2f click = new Vector2f(point.getWorldTranslation().x+pointerSize/2,point.getWorldTranslation().y+pointerSize/2);
            Node aimNode = new Node("AimNode");
            bullet.setUserData("moveVector", new Vector3f(0f,0f,1f));
            bullet.setUserData("speed",1000f);
            bullet.setUserData("collisionNode", collisionNode);
            bullet.setUserData("dieTime",System.currentTimeMillis()+1500);
            
            
            Vector3f start = new Vector3f(2, -3,-9f);//nose of ship     
            Vector3f targetPoint = cam.getWorldCoordinates(click, 1f).subtractLocal(start);
            aimNode.move(start);
            aimNode.lookAt(targetPoint, Vector3f.UNIT_Y);

            bullet.addControl(new ConstantMovement());
            bullet.addControl(new SelfCheck());
            
            aimNode.attachChild(bullet);
            //aimNode.attachChild(secondBullet);
            parent.attachChild(aimNode);
            //
            //check collision by ray cast(save cpu)
            Ray bulletPath = new Ray(start,targetPoint);
            CollisionResults collisionResult = new CollisionResults();
            List<Spatial> spatials = collisionNode.getChildren();
            for(int i = 0;i < spatials.size();i++)
            {
                Spatial collisionSpatial = spatials.get(i);
                collisionResult.clear();
                bulletPath.collideWith(collisionSpatial.getWorldBound(), collisionResult);
                if(collisionResult.size() > 0)//if any collision
                {
                    if(!collisionSpatial.getName().matches("Asteroid") && !collisionSpatial.getName().matches("Bonus"))
                        return;
                    
                    int health = collisionSpatial.getUserData("health");
                    int newHealth = health-gameState.damage;
                    //remove HP from object
                    collisionSpatial.setUserData("health",newHealth);
                    //die cuz 0 hp  
                    if(newHealth <= 0)
                    {
                        gameState.comboKills++;
                        gameState.comboTime = 200f;
                        gameState.maxComboScale = 2f;
                        gameState.comboEnabled = true;
                        audioManager.playAudioInstance("explosion");
                        if(collisionSpatial.getName().matches("Asteroid"))
                        {
                            gameState.upgradePoints+=collisionSpatial.getLocalScale().x - 4;
                            recycler.removeAsteroid(collisionSpatial,configExplosions);
                            if(collisionSpatial.getUserData("big"))
                            {
                                gameState.maxSpeed = 100;
                                gameState.upgradePoints+=5;
                            }
                        }
                        else
                        {
                            gameState.upgradePoints+=10;
                            recycler.removeBonus(collisionSpatial,configExplosions);
                        }
                    }
                }
            }
    }
    
    public void remove()
    {
        guiNode.detachChild(localGuiNode);
    }
    
    public void attach()
    {
        guiNode.attachChild(localGuiNode);
    }   
}
