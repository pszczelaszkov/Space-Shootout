package spaceShootout;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeSystem;
import com.jme3.texture.Texture;
import java.io.File;
import java.io.IOException;
import java.util.List;

    
//deachowanie local gui noda powoduje kosmiczne artefakty
    class GameState extends AbstractAppState implements RawInputListener
    {
        private Node rootNode;
        private Node guiNode;
        private Node tunnel;
        private Node surface;
        private Node explosions;
        private Node localRootNode;
        private Node localGuiNode;
        private Node healthBars;
        private Node player;
        private Camera cam;
        private AssetManager assetManager;
        public AudioManager audioManager;
        private InputManager inputManager;
        private SensorController sensorController;
        private AnalogController analogController;
        private AimController aimController;
        private WeaponControl weaponControl;
        private GuiController guiController;
        private Recycler recycler;
        private BitmapFont guiFont;
        private Spatial playerModel;
        private Spatial backgroundBox;
        private ParticleEmitter afterburn;
        private float tunnelDepth;
        private float surfaceDepth;
        private float distanceTraveled;
        private float distanceToSpawn;
        private float speed;
        private int surfaceTextureId;
        private BitmapText guiDistanceTraveled;
        private BitmapText guiScore;
        private BitmapText combo;
        public String token;
        public float comboTime;
        public float comboScale;
        public float maxComboScale;
        public int maxCombo;
        public int dodgeDistance;
        public int comboKills;
        public boolean comboEnabled;      
        public float maxSpeed;
        public int dodgeMaxDistance;
        public boolean alive;
        public boolean ready;
        public boolean hangarMode;
        public boolean passEventsToGui;
        public boolean bigAppeared;
        public boolean finished;
        public boolean initialAd;
        public boolean dodge;
        boolean configExplosions;
        //savable
        int exhausted,damage,strafe,level,upgradePoints,target;
        public GameState(Main app,boolean loadGame,boolean configExplosions)
        {
            alive = true;
            ready = false;
            hangarMode = true;
            passEventsToGui = false;
            bigAppeared = false;
            finished = false;
            initialAd = false;
            comboEnabled = false;
            dodge = false;
            localRootNode = new Node("Game Screen RootNode");
            localGuiNode = new Node("Game Screen GuiNode");
            player = new Node("Player Node");
            localGuiNode.setQueueBucket(Bucket.Gui);
            tunnel = new Node();
            surface = new Node();
            explosions = new Node();
            healthBars = new Node();
            this.rootNode     = app.getRootNode();
            this.guiNode       = app.getGuiNode();
            this.assetManager  = app.getAssetManager();
            this.inputManager = app.getInputManager();
            this.sensorController = app.sensorController;
            this.cam = app.getCamera();
            this.tunnelDepth = 0;
            this.surfaceDepth = 0;
            this.distanceToSpawn = 0;
            this.distanceTraveled = 0;
            this.speed = 0;
            this.maxSpeed = 100;
            this.exhausted = 300;
            this.damage = 100;
            this.strafe = 10;
            this.dodgeMaxDistance = 1000;
            this.level = 0;
            this.upgradePoints = 20;
            this.comboKills = 0;
            this.comboScale = 1f;
            this.maxComboScale = 1f;
            this.maxCombo = 0;
            this.token = "";
            this.dodgeDistance = 0;
            this.surfaceTextureId = FastMath.nextRandomInt(0, 3);
            this.guiController = app.guiController;
            this.audioManager = app.guiController.audioManager;
            this.configExplosions = configExplosions;
            this.guiFont = assetManager.loadFont(guiController.bigFontPath);
            this.guiDistanceTraveled = new BitmapText(guiFont, false);
            this.guiScore = new BitmapText(guiFont, false);
            this.combo = new BitmapText(guiFont, false);
            
            nextLevel();
            if(loadGame)
                loadGame();
            saveGame();
        }
        
        @Override
        public void initialize(AppStateManager stateManager, Application app) 
        {
            super.initialize(stateManager, app);
            recycler = new Recycler(15,5,2,assetManager);
            recycler.setParentNode(explosions);
            analogController = new AnalogController(localGuiNode,cam,assetManager,this);
            analogController.remove();
            aimController = new AimController(localGuiNode,cam,assetManager,recycler,this,audioManager,configExplosions);
            aimController.remove();
            weaponControl = new WeaponControl(assetManager);

            guiDistanceTraveled.setColor(ColorRGBA.White);            
            guiDistanceTraveled.setLocalTranslation(cam.getWidth()-guiFont.getPreferredSize()*15,cam.getHeight()*0.95f,0);
            guiScore.setColor(new ColorRGBA(255/255f, 255/255f,168/255f, 1f));            
            guiScore.setLocalTranslation(cam.getWidth()*0.01f,cam.getHeight()*0.95f,0);
            combo.setColor(new ColorRGBA(255/255f, 255/255f,168/255f, 1f));            
            combo.setLocalTranslation(cam.getWidth()*0.01f,cam.getHeight()*0.85f,0);
            guiDistanceTraveled.setSize(guiFont.getCharSet().getRenderedSize());
            guiScore.setSize(guiFont.getCharSet().getRenderedSize());
            combo.setSize(guiFont.getCharSet().getRenderedSize());
            //loadBackground
            backgroundBox = assetManager.loadModel("Models/background/background.j3o");
            backgroundBox.setLocalScale(40f);
            backgroundBox.move(0,0,-400f);
            //
            //loadPlayer
            playerModel = assetManager.loadModel("Models/spaceShip/spaceShip.j3o");
            playerModel.setModelBound(new BoundingBox());
            playerModel.updateModelBound();
            player.setUserData("targetRotation", 0f);
            player.addControl(new AngleControl());
            player.attachChild(playerModel);
            //playerBackFire
            afterburn = new ParticleEmitter("Afterburn", ParticleMesh.Type.Triangle, 30);
            Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            fireMat.setTexture("Texture", assetManager.loadTexture("Textures/flame.png"));
            afterburn.setMaterial(fireMat);
            afterburn.setImagesX(2); afterburn.setImagesY(2); // 2x2 texture animation
            afterburn.setEndColor( new ColorRGBA(0f, 0.5f, 1f, 1f) );   // blue
            afterburn.setStartColor( new ColorRGBA(1f, 1f, 0f, 0.5f) ); // yellow
            afterburn.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 1f));
            afterburn.setStartSize(0.6f);
            afterburn.setEndSize(0.1f);
            afterburn.setGravity(0f,0f,0f);
            afterburn.setLowLife(1.0f);
            afterburn.setHighLife(1.5f);
            afterburn.getParticleInfluencer().setVelocityVariation(0f);
            afterburn.move(0,0,3f);
            player.attachChild(afterburn);
            player.move(0,-3f,-10f);

            //add objects
            localRootNode.attachChild(backgroundBox);
            localRootNode.attachChild(player);
            localRootNode.attachChild(tunnel);
            localRootNode.attachChild(surface);
            localRootNode.attachChild(explosions);
            localRootNode.attachChild(healthBars);
            localGuiNode.attachChild(guiDistanceTraveled);
            localGuiNode.attachChild(guiScore);
            localGuiNode.attachChild(combo);
            
            addHangar();            
            addSurface();
            addSurface();
                       
            //cam.setLocation(new Vector3f(0,50,-20f));
            //cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            localRootNode.addControl(new StateWatchdog(this));
        }
        
        @Override
        public void update(float tpf) 
        {
            if(tpf > 2)
                return;
           //localGuiNode.detachAllChildren();
           /* BitmapText axis = new BitmapText(guiFont, false);          
            axis.setSize(guiFont.getCharSet().getRenderedSize());      // font size
            axis.setColor(ColorRGBA.Blue);                             // font color
            axis.setText("X: "+ sensorController.getX() +  "\n" + "Y: "+ sensorController.getY() +  "\n" + "Z: "+ sensorController.getZ());
            axis.setLocalTranslation(300, 100, 0); // position
            localGuiNode.attachChild(axis);
            */   
            if(alive && !finished)
            {
                guiDistanceTraveled.setText("Target "+ (int)Math.max(0,(target - distanceTraveled)) +" M");
                guiScore.setText("Pts "+ upgradePoints);
            }
            else
            {
                guiDistanceTraveled.setText("");
                guiScore.setText("");
            }
            if(dodge)
            {
                dodgeDistance-=speed*tpf;
                if(dodgeDistance < 0)
                {
                    stopDodge();
                }
            }
            
            if(comboEnabled && alive && !finished)
            {
                if(comboScale == maxComboScale)
                    maxComboScale = 1f;
                if(comboScale > maxComboScale)
                {
                    comboScale-=tpf*15;
                    comboScale = Math.max(maxComboScale, comboScale);
                }
                else
                {
                    comboScale+=tpf*15;
                    comboScale = Math.min(maxComboScale,comboScale);
                }
                combo.setLocalScale(comboScale);
                combo.setText("Combo " + comboKills);
                comboTime-=speed*tpf;
                if(comboTime < 1)
                {         
                    if(comboKills > maxCombo)
                        maxCombo = comboKills;
                    upgradePoints+=comboKills+(int)(comboKills*0.5f);
                    comboKills = 0;
                    comboScale = 1f;
                    maxComboScale = 1f;
                    comboEnabled = false;
                }
            }
            else
                combo.setText("");
             
            if(analogController.fire)
            {
                 if(weaponControl.canShoot())
                 {
                     aimController.shoot(weaponControl.getBullet(), localRootNode, tunnel);
                     weaponControl.addExhausted(exhausted);
                 }
             }
             aimController.updatePointer(analogController.getAnalogPosition().mult(tpf).mult(3));
             
            //
            //relocating tunnel and rotation
            float strafeMove = FastMath.clamp(-sensorController.getX(), -2f, 2f)*tpf*strafe;
            //assume that we can strafe left and right
            boolean canStrafe = true;
            
            if(speed > maxSpeed)
            {
                setEngineLevel(0);
                speed-=tpf*25;
                speed = Math.max(maxSpeed, speed);
                if(speed == maxSpeed)
                    setEngineLevel(1);
            }
            else
            {
                setEngineLevel(2);
                speed+=tpf*35;
                speed = Math.min(maxSpeed,speed);
                if(speed == maxSpeed)
                    setEngineLevel(1);
            }
            tunnel.move(0, 0, speed*tpf);
            surface.move(0, 0, speed*tpf);
            explosions.move(0, 0, speed*tpf);
            distanceTraveled += speed*tpf; 
            distanceToSpawn -= speed*tpf;
            
            if(tunnel.getLocalTranslation().getX() < -30f && strafeMove < 0)
                canStrafe = false;
            if(tunnel.getLocalTranslation().getX() > 30f && strafeMove > 0)
                canStrafe = false;
            
            if(canStrafe && alive && !finished && distanceTraveled > 70)    
            {
                tunnel.move(strafeMove, 0, 0);
                surface.move(strafeMove, 0, 0);
                explosions.move(strafeMove, 0, 0);
                player.setUserData("targetRotation",FastMath.DEG_TO_RAD*(FastMath.clamp(-sensorController.getX()*10,-45f,45f)));
            }
            else
                player.setUserData("targetRotation",0f);
            
            if(distanceToSpawn <= 0)
            {
                distanceToSpawn = 100;
                addAsteroids(false);
                if(FastMath.nextRandomInt(1, 20) == 1)
                    addBonus();
            }
            if(!bigAppeared && distanceTraveled > 10000)
            {
                bigAppeared = true;
                addAsteroids(true);
                stopDodge();
                if(alive)
                {
                    if(level < 10)
                        maxSpeed = 45;
                    else if(level > 10 && level < 50)
                        maxSpeed = 55;
                    else
                        maxSpeed = 65;
                }           
            }
            if(!finished && distanceTraveled > target)
            {
                maxSpeed = 0;
                finished = true;
            }
            if(finished && speed < 1)
            {                     
                //in case user didnt die before finish
                if(guiController.showAd())
                    initialAd = false;
                
                nextLevel();
                guiController.resetGameState();
            }
            if(!alive && speed < 1)
            {                           
                guiController.resetGameState();
            }
            
            //removing past segments
            List<Spatial> spatials = tunnel.getChildren();
            for(int i = spatials.size()-1; i >= 0;i--)
            {
                Spatial spatial = spatials.get(i);        
                if(spatial.getWorldTranslation().getZ() > 50f)
                {
                    if(spatial.getName().matches("Asteroid"))
                    {
                        recycler.removeAsteroid(spatial,false);
                        if(spatial.getUserData("big"))
                            if(alive)
                                maxSpeed = 100;
                    }
                    else if(spatial.getName().matches("Bonus"))
                        recycler.removeBonus(spatial,false);                   
                    else
                        tunnel.detachChild(spatial);
                }
            }
            //removing past surface
            spatials = surface.getChildren();
            for(int i = spatials.size()-1; i >= 0;i--)
            {
                Spatial spatial = spatials.get(i);        
                if(spatial.getWorldTranslation().getZ() > 200f)
                {
                    spatial.setLocalTranslation(0f,0f,surfaceDepth);    
                    surfaceDepth-=400;
                }
            }
            
            CollisionResults collisionResult = new CollisionResults();
            tunnel.collideWith(player.getWorldBound(), collisionResult);
            if(collisionResult.size() > 0)
            {    
                detachGui();             
                audioManager.playAudioInstance("explosion");
                alive = false;
                maxSpeed = 0;
                ParticleEmitter explosionEffect = (ParticleEmitter)assetManager.loadModel("Effects/explosion.j3o");
                explosionEffect.setStartSize(10f);
                explosionEffect.setLocalTranslation(-tunnel.getLocalTranslation().x,0,-distanceTraveled-player.getLocalTranslation().z);
                explosionEffect.setUserData("dieTime",System.currentTimeMillis()+1000);
                explosionEffect.addControl(new SelfCheck());
                
                player.move(0,0,30f);
                player.setUserData("targetRotation",0f);
                      
                if(configExplosions)
                    explosions.attachChild(explosionEffect);
                
                explosionEffect.emitAllParticles();
            }
        }
        
        @Override
        public void stateAttached(AppStateManager stateManager) 
        {
            rootNode.attachChild(localRootNode);
            guiNode.attachChild(localGuiNode);
            inputManager.addRawInputListener(this);
        }

        @Override
        public void stateDetached(AppStateManager stateManager) {}
        
        @Override
        public void cleanup()
        {
            rootNode.detachChild(localRootNode);
            guiNode.detachChild(localGuiNode);
            inputManager.removeRawInputListener(this);
        }
        
        public void addSurface()
        {
           Spatial segment;
           Material material;
           Texture texture;
           String texturesNames[] = {"marble","fireMarble","redMarble","azureMarble"};
           segment = assetManager.loadModel("Models/surface/surface.j3o");           
           material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
           texture = assetManager.loadTexture("Textures/"+texturesNames[surfaceTextureId] + ".jpg");
           material.setTexture("ColorMap", texture);
           segment.setMaterial(material);
            
           segment.move(0f,0f,surfaceDepth);
           surface.attachChild(segment);   
           surfaceDepth-=400;
           
        }
        
        public void addHangar()
        {
            Node segment = new Node();
            Node leftGate = new Node();
            Node rightGate = new Node();
            Spatial element;
            GateMovement gateMovement;
            
            //leftgate
            Spatial gate = assetManager.loadModel("Models/gate/leftGate.j3o");
            gate.move(8,0,0);
            leftGate.move(-16f,3,-49f);
            leftGate.attachChild(gate);
            leftGate.setUserData("targetRotation", FastMath.DEG_TO_RAD*90f);
            gateMovement = new GateMovement(this);
            leftGate.addControl(gateMovement);
            segment.attachChild(leftGate);
            //rightgate
            gate = assetManager.loadModel("Models/gate/rightGate.j3o");
            gate.move(-8,0,0);
            rightGate.move(16f,3,-49f);
            rightGate.attachChild(gate);
            rightGate.setUserData("targetRotation", FastMath.DEG_TO_RAD*-90f);
            gateMovement = new GateMovement(this);
            rightGate.addControl(gateMovement);
            segment.attachChild(rightGate);
            
            //floor 
            element = recycler.getNewSegment();
            if(element == null)
                return;
            element.move(0,-2f,-32f);
            segment.attachChild(element);
            //
            element = recycler.getNewSegment();
            if(element == null)
                return;
            element.move(0,-2f,0f);
            segment.attachChild(element);
            
            //ceiling
            element = recycler.getNewSegment();
            if(element == null)
                return;
            element.move(0,11f,-32f);
            segment.attachChild(element);
            //
            element = recycler.getNewSegment();
            if(element == null)
                return;
            element.move(0,11f,0f);
            segment.attachChild(element);
            
            //left
            element = recycler.getNewSegment();
            if(element == null)
                return;
            element.move(-12f,0,-32f);
            element.rotate(0,0,FastMath.DEG_TO_RAD*-65);
            segment.attachChild(element);
            //
            element = recycler.getNewSegment();
            if(element == null)
                return;
            element.move(-12f,0,0f);
            element.rotate(0,0,FastMath.DEG_TO_RAD*-65);
            segment.attachChild(element);
            
            //right
            element = recycler.getNewSegment();
            if(element == null)
                return;
            element.move(12f,0,-32f);
            element.rotate(0,0,FastMath.DEG_TO_RAD*65);
            segment.attachChild(element);
            //
            element = recycler.getNewSegment();
            if(element == null)
                return;
            element.move(12f,0,0f);
            element.rotate(0,0,FastMath.DEG_TO_RAD*65);
            segment.attachChild(element);
            
            segment.setName("Segment");
            segment.move(0f, -2f, tunnelDepth);
            segment.setUserData("solid", true);
            tunnel.attachChild(segment);
            tunnelDepth-=32f;
        }
        
        public void addSegment()
        {

            Spatial segment = recycler.getSegment();
            if(segment == null)
                return;
            SegmentCreationMovement segmentCreationMovement;
            /*Node segment = new Node();
            Geometry geom;
            Box b;
            Material wallMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            
            //floor
            b = new Box(16,1,16);
            geom = new Geometry("Box", b);
            wallMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/hangar_floor.PNG"));
            geom.setMaterial(wallMaterial);*/
            //segment.move(0,-40,-50f);
            //segment.rotate(0,0,FastMath.DEG_TO_RAD*180);
            //segment.setUserData("targetPosition", new Vector3f(0,-6f,0));
            //segmentCreationMovement = new SegmentCreationMovement();
            //segment.addControl(segmentCreationMovement);
            //segment.attachChild(geom);
        /*   //ceiling
            b = new Box(15,1,10);
            geom = new Geometry("Box", b);
            wallMaterial.setTexture("ColorMap", wallTexture);
            geom.setMaterial(wallMaterial);
            geom.move(0,40f,-50f);
            geom.setUserData("targetPosition", new Vector3f(0,6f,0));
            segmentCreationMovement = new SegmentCreationMovement();
            geom.addControl(segmentCreationMovement);
            segment.attachChild(geom);
            */
            //move to end
            segment.setUserData("solid", true);
            segment.move(0, 30, tunnelDepth);
/*          CollisionShape segmentShape = CollisionShapeFactory.createMeshShape(segment);
            RigidBodyControl segmentRigid = new RigidBodyControl(segmentShape, 1f);
            segmentRigid.setMass(1.0f);
            segmentRigid.setKinematic(true);
            segment.addControl(segmentRigid);
            bulletAppState.getPhysicsSpace().add(segmentRigid);*/
            
           // segment.setName("Segment");
            tunnel.attachChild(segment);
                
            tunnelDepth-=32f;
        }
        
        public void addAsteroids(boolean big)
        {
            Spatial asteroid = recycler.getAsteroid();
            if(asteroid == null)
                return;
            
            ConstantRotation constantRotation;
            SegmentCreationMovement segmentCreationMovement;
            HealthBar healthBar;
            int health = 100*(level*2);
            if(big)
            {
                asteroid.setLocalScale(FastMath.nextRandomInt(50,60));
                asteroid.setLocalTranslation(0f,50f,-distanceTraveled-500f);
                
                health *= 10;
                if(dodge)
                    health = health/2;
                asteroid.setUserData("big", true);
            }
            else
            {
                int scale;
                if(level < 3)
                    scale = FastMath.nextRandomInt(1,2);
                else
                    scale = FastMath.nextRandomInt(1,4);
                
                health *= scale;
                asteroid.setLocalScale(scale + 4);
                if(dodge)
                   asteroid.setLocalTranslation(new Vector3f(FastMath.nextRandomInt(-40, 40),FastMath.nextRandomInt(-6, 0),FastMath.nextRandomInt((int)-distanceTraveled-300,(int)-distanceTraveled-200)));
                else
                   asteroid.setLocalTranslation(FastMath.nextRandomInt(-150, 150),50f,-distanceTraveled-500f);
                
                asteroid.setUserData("big", false);
            }     
            asteroid.setUserData("targetPosition", new Vector3f(FastMath.nextRandomInt(-40, 40),FastMath.nextRandomInt(-6, 0),FastMath.nextRandomInt((int)-distanceTraveled-400,(int)-distanceTraveled-300)));           
            asteroid.setUserData("xRotation", (float)FastMath.nextRandomInt(-1, 1));
            asteroid.setUserData("yRotation", (float)FastMath.nextRandomInt(-1, 1));
            asteroid.setUserData("zRotation", (float)FastMath.nextRandomInt(-1, 1));
            asteroid.setUserData("health",health);
            asteroid.setUserData("maxHealth", health);
            asteroid.setUserData("solid", false);     
            constantRotation = new ConstantRotation();
            segmentCreationMovement = new SegmentCreationMovement();
            healthBar = new HealthBar(healthBars,assetManager);
            if(!big)
                asteroid.addControl(constantRotation);
            asteroid.addControl(segmentCreationMovement);
            asteroid.addControl(healthBar);
            tunnel.attachChild(asteroid);
            if(dodge)
            {
                comboKills++;
                upgradePoints+=asteroid.getLocalScale().x - 4;
                comboTime = 200f;
                maxComboScale = 2f;
                comboEnabled = true;
                if(!big)
                    recycler.removeAsteroid(asteroid, true);
                
                audioManager.playAudioInstance("explosion");
            }
        }
       
        public void addBonus()
        {
            //Node segment = new Node();
            Spatial bonus = recycler.getBonus();
            if(bonus == null)
                return;
            
            ConstantRotation constantRotation;
            SegmentCreationMovement segmentCreationMovement;
            HealthBar healthBar;
            bonus.setLocalScale(FastMath.nextRandomInt(5,10));
            if(dodge)
                bonus.setLocalTranslation(new Vector3f(FastMath.nextRandomInt(-40, 40),FastMath.nextRandomInt(-6, 0),FastMath.nextRandomInt((int)-distanceTraveled-300,(int)-distanceTraveled-200)));
            else
                bonus.setLocalTranslation(FastMath.nextRandomInt(-150, 150),50f,-distanceTraveled-500f);
            
            int health = 100*(level*2);
            bonus.setUserData("targetPosition", new Vector3f(FastMath.nextRandomInt(-40, 40),FastMath.nextRandomInt(-6, 0),FastMath.nextRandomInt(/*-6, 6*/(int)-distanceTraveled-400,(int)-distanceTraveled-300)));           
            bonus.setUserData("xRotation", (float)FastMath.nextRandomInt(-1, 1));
            bonus.setUserData("yRotation", (float)FastMath.nextRandomInt(-1, 1));
            bonus.setUserData("zRotation", (float)FastMath.nextRandomInt(-1, 1));
            bonus.setUserData("health",health);
            bonus.setUserData("maxHealth", health);
            bonus.setUserData("solid", false);
            constantRotation = new ConstantRotation();
            segmentCreationMovement = new SegmentCreationMovement();
            healthBar = new HealthBar(healthBars,assetManager);
            bonus.addControl(constantRotation);
            bonus.addControl(segmentCreationMovement);
            bonus.addControl(healthBar);
            tunnel.attachChild(bonus);
            if(dodge)
            {
                comboKills++;
                upgradePoints+=10;
                comboTime = 200f;
                maxComboScale = 2f;
                comboEnabled = true;
                recycler.removeBonus(bonus, true);
                audioManager.playAudioInstance("explosion");
            }
        }
        
        
        public void detachGui()
        {
            passEventsToGui = false;
            analogController.remove();
            aimController.remove();
            analogController.resetAnalog();
            analogController.resetFire();
        }
        
        public void attachGui()
        {
            passEventsToGui = true;    
            analogController.attach();
            aimController.attach();
        }
                
        public void reset()
        {        
            setEnabled(false);
            alive = true;
            finished = false;
            bigAppeared = false;
            hangarMode = true;
            dodge = false;
            analogController.setDodgeActive(true);
            //recycling remain asteroids
            List<Spatial> spatials = tunnel.getChildren();
            for(int i = spatials.size()-1; i >= 0;i--)
            {              
                Spatial spatial = spatials.get(i);
                if(spatial.getName().matches("Asteroid"))
                    recycler.removeAsteroid(spatial,false);
                else if(spatial.getName().matches("Bonus"))
                    recycler.removeBonus(spatial,false);
            }
            localRootNode.detachChild(tunnel);
            localRootNode.detachChild(surface);
            localRootNode.detachChild(explosions);
            healthBars.detachAllChildren();
            detachGui();
            //for safety create new nodes
            tunnel = new Node();
            surface = new Node();
            explosions = new Node();
            localRootNode.attachChild(tunnel);
            localRootNode.attachChild(surface);
            localRootNode.attachChild(explosions);
            //reinitialize
            tunnelDepth = 0;
            surfaceDepth = 0;
            distanceToSpawn = 0;
            distanceTraveled = 0;
            dodgeDistance = 0;
            maxSpeed = 100;
            surfaceTextureId = FastMath.nextRandomInt(0, 3);
            recycler.setParentNode(explosions);
            //clear the memory for the new world
            System.gc();
            //spawn world
            addHangar();            
            addSurface();
            addSurface();
            
            player.setLocalTranslation(0,-3,-7f);
            player.setUserData("targetRotation",0f);
            guiController.setHangarState();
            
            if(!initialAd)
            {                           
                if(guiController.showAd())
                    initialAd = true;
            }
        }
        
        public final void nextLevel()
        {
            if(level < 1000000)
            {
                level++;
                target = FastMath.nextRandomInt(14000, 24000);
                if(level < 2)
                    target = 6000;
            }
        }

        public final void loadGame()
        {
            String applicationPath = JmeSystem.getStorageFolder().getAbsolutePath();
            BinaryImporter importer = BinaryImporter.getInstance();
            File file = new File(applicationPath + "/saveData");
            try
            {
                DataStructure data = (DataStructure)importer.load(file);
                damage = data.damage;
                exhausted = data.exhausted;
                level = data.level;
                dodgeMaxDistance = data.dodgeMaxDistance;
                target = data.target;
                upgradePoints = data.upgradePoints;
                maxCombo = data.maxCombo;
                token = data.token;
            } catch (IOException e) 
            {
                System.out.println(e.getMessage());
            }
        }
        
        public final void saveGame()
        {            
            String applicationPath = JmeSystem.getStorageFolder().getAbsolutePath();
            BinaryExporter exporter = BinaryExporter.getInstance();
            File file = new File(applicationPath + "/saveData");
            DataStructure data = new DataStructure();
            data.damage = damage;
            data.exhausted = exhausted;
            data.level = level;
            data.dodgeMaxDistance = dodgeMaxDistance;
            data.target = target;
            data.upgradePoints = upgradePoints;
            data.maxCombo = maxCombo;
            data.token = token;
            
            try 
            {
                exporter.save(data, file);
            } catch (IOException e) 
            {
                System.out.println(e.getMessage());
            }
        }
        
        public void startDodge()
        {
            
            if(alive && !finished)
            {
                //destroy all asteroids
                audioManager.playAudioInstance("explosion");
                comboTime = 200f;
                maxComboScale = 2f;
                List<Spatial> spatials = tunnel.getChildren();
                for(int i = spatials.size()-1; i >= 0;i--)
                {              
                    Spatial spatial = spatials.get(i);
                    if(spatial.getName().matches("Asteroid"))
                    {
                        comboKills++;
                        upgradePoints+=spatial.getLocalScale().x - 4;
                        comboEnabled = true;
                        recycler.removeAsteroid(spatial,true);
                    }
                    else if(spatial.getName().matches("Bonus"))
                    {
                        comboKills++;
                        upgradePoints++;
                        comboEnabled = true;
                        recycler.removeBonus(spatial,true);
                    }
                }
                dodge = true;
                maxSpeed = 250;
                dodgeDistance = dodgeMaxDistance;
            }
            
        }
        
        public void stopDodge()
        {
            if(alive && !finished)
            {
                dodge = false;
                maxSpeed = 100;
            }
        }
          
        public void setEngineLevel(int level)
        {
            switch(level)
            {
                case 0:
                    afterburn.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 1));
                break;
                case 1:
                    afterburn.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 4));
                break;
                case 2:
                    afterburn.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 7));
                break;
            }
        }
        
        public void onJoyAxisEvent(JoyAxisEvent evt) { }
        public void onJoyButtonEvent(JoyButtonEvent evt){}
        public void beginInput() {}
        public void endInput() {}
        public void onMouseMotionEvent(MouseMotionEvent evt) {}
        public void onMouseButtonEvent(MouseButtonEvent evt) {
              /*if(!passEventsToGui)
                   return;
                       if(analogController == null)
                return;
                        analogController.update((int)evt.getX(),(int)evt.getY());*/}
        
        public void onKeyEvent(KeyInputEvent evt) {}
        public void onTouchEvent(TouchEvent evt) 
        {    
            if(!passEventsToGui)
                   return;
            
            if(analogController == null)
                return;
            
            if(evt.getType() == TouchEvent.Type.MOVE)
                analogController.update((int)evt.getX(),(int)evt.getY());
            if(evt.getType() == TouchEvent.Type.DOWN)
                analogController.update((int)evt.getX(),(int)evt.getY());

            if(evt.getType() == TouchEvent.Type.UP)
            {
                 analogController.reset((int)evt.getX(),(int)evt.getY());
            }
        }
    }