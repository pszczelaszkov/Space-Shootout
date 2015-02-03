package spaceShootout;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Stack;

public class Recycler 
{  
    private Stack<Spatial> asteroids;
    private Stack<Spatial> bonuses;
    private Stack<Spatial> segments;
    private AssetManager assetManager;
    private Node parentNode;
    
    Recycler(int maxAsteroids,int maxSegments,int maxBonuses,AssetManager assetManager)
    {   
            this.assetManager = assetManager;
            generateAsteroids(maxAsteroids);
            generateSegments(maxSegments);
            generateBonuses(maxBonuses);
    }
    
    void setParentNode(Node parentNode)
    {
        this.parentNode = parentNode;
    }
    
    Spatial getAsteroid()
    {
        if(asteroids.size() > 0)
            return asteroids.pop();
        
        return null;
    }
    
    void removeAsteroid(Spatial spatial,boolean effect)
    {             
        for(int i = spatial.getNumControls() - 1;i >= 0;i--)
            spatial.removeControl(spatial.getControl(i));
        
        asteroids.push(spatial);
        
        if(effect)
        {      
            /*ParticleEmitter explosionEffect = new ParticleEmitter("Explosion", ParticleMesh.Type.Triangle, 3);
            Material explosionMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            explosionMat.setTexture("Texture", assetManager.loadTexture("Textures/explosion.png"));
            explosionEffect.setMaterial(explosionMat);
            explosionEffect.setLowLife(0.6f);
            explosionEffect.setHighLife(0.8f);
            explosionEffect.setRandomAngle(true);
            explosionEffect.setParticlesPerSec(0);
            explosionEffect.setImagesX(1); explosionEffect.setImagesY(1); // 3x3 texture animation
            explosionEffect.setStartSize(spatial.getLocalScale().x*5);
            explosionEffect.setEndSize(40f);
            explosionEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
            explosionEffect.setStartColor( new ColorRGBA(218/255f, 235/255f, 127/255f, 1f) );  
            explosionEffect.setEndColor(ColorRGBA.Red);
            explosionEffect.setGravity(0f,0f,0f);
            explosionEffect.getParticleInfluencer().setVelocityVariation(.60f);
            explosionEffect.setLocalTranslation(spatial.getLocalTranslation());
            
            
            String userHome = System.getProperty("user.home");
            BinaryExporter exporter = BinaryExporter.getInstance();
            File file = new File(userHome+"/Models/"+"explosion.j3o");
            System.out.println(userHome+"/Models/"+"explosion.j3o");
            try 
            {
                exporter.save(explosionEffect, file);
            } catch (IOException ex) {System.out.println("o chuj");}*/
            
            ParticleEmitter explosionEffect = (ParticleEmitter)assetManager.loadModel("Effects/explosion.j3o");
            explosionEffect.setStartSize(spatial.getLocalScale().x*5);
            explosionEffect.setLocalTranslation(spatial.getLocalTranslation());
            explosionEffect.setUserData("dieTime",System.currentTimeMillis()+1000);
            explosionEffect.addControl(new SelfCheck());
            parentNode.attachChild(explosionEffect);
            explosionEffect.emitAllParticles();
        }
        spatial.removeFromParent();
    }
        
    Spatial getSegment()
    {
        if(segments.size() > 0)
            return segments.pop();
        
        return null;
    }
    
    Spatial getNewSegment()
    {
            Node segment = new Node();
            Spatial geom = assetManager.loadModel("Models/tunnel/segment.j3o");            
            segment.attachChild(geom);
            segment.setName("Segment");
            
            return segment;
    }
    
    void removeSegment(Spatial spatial)
    {
        for(int i = spatial.getNumControls() - 1;i >= 0;i--)
            spatial.removeControl(spatial.getControl(i));
        
        segments.push(spatial);
        spatial.removeFromParent();
    }
    
    private void generateAsteroids(int maxAsteroids)
    {
        asteroids = new Stack<Spatial>();
        
        for(int i = 0;i < maxAsteroids;i++)
        {
            Spatial asteroid;
            switch(FastMath.nextRandomInt(0, 3))
            {
                case 0:
                    asteroid = assetManager.loadModel("Models/whiteAsteroid/whiteAsteroid.j3o");
                break;
                case 1:
                    asteroid = assetManager.loadModel("Models/redAsteroid/redAsteroid.j3o");
                break;
                case 2:
                    asteroid = assetManager.loadModel("Models/fireAsteroid/fireAsteroid.j3o");
                break;
                case 3:
                    asteroid = assetManager.loadModel("Models/azureAsteroid/azureAsteroid.j3o");
                break;
                default:
                    asteroid = assetManager.loadModel("Models/whiteAsteroid/whiteAsteroid.j3o");
           }
           asteroid.setName("Asteroid");
           asteroids.push(asteroid);
        }
    }
    
    private void generateSegments(int maxSegments)
    {
        segments = new Stack<Spatial>();
        
        for(int i = 0;i < maxSegments;i++)
        {
            Node segment = new Node();
            Spatial geom = assetManager.loadModel("Models/tunnel/segment.j3o");            
            geom.rotate(0,0,FastMath.DEG_TO_RAD*180);
            segment.attachChild(geom);
            segment.setName("Segment");
            segments.push(segment);
        }
    }
    
    private void generateBonuses(int maxBonuses)
    {
        bonuses = new Stack<Spatial>();
        for(int i = 0;i < maxBonuses;i++)
        {
            Spatial bonus;
            switch(FastMath.nextRandomInt(0, 3))
            {
                default:
                    bonus = assetManager.loadModel("Models/slenderman/slenderman.j3o");
            }
            bonus.setName("Bonus");
            bonuses.push(bonus);
        }
    }
    
    Spatial getBonus()
    {
        if(bonuses.size() > 0)
            return bonuses.pop();
        
        return null;
    }
    
    void removeBonus(Spatial spatial,boolean effect)
    {     
        for(int i = spatial.getNumControls() - 1;i >= 0;i--)
            spatial.removeControl(spatial.getControl(i));
        
        bonuses.push(spatial);
        
        if(effect)
        {      
            ParticleEmitter explosionEffect = (ParticleEmitter)assetManager.loadModel("Effects/explosion.j3o");
            explosionEffect.setStartSize(spatial.getLocalScale().x*5);
            explosionEffect.setLocalTranslation(spatial.getLocalTranslation());
            parentNode.attachChild(explosionEffect);
            explosionEffect.emitAllParticles();
        }
        spatial.removeFromParent();
    }
}