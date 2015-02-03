package spaceShootout;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.dynamic.PanelCreator;
import de.lessvoid.nifty.controls.dynamic.TextCreator;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.List;

public class GuiController extends AbstractAppState implements ScreenController
{

    AppSettings settings;
    AppStateManager stateManager;
    AudioManager audioManager;
    Main app;
    LoadingState loadingState;
    GameState gameState;
    Nifty nifty;
    Element statisticBar,statisticWidget;
    TextRenderer statisticBarText;
    Screen screen;
    boolean switchToHangar,explosions,sounds;
    public String smallFontPath;
    public String bigFontPath;
    
    GuiController(AppStateManager stateManager,AudioManager audioManager,Main app,AppSettings settings)
    {
        this.app = app;
        this.stateManager = stateManager;
        this.audioManager = audioManager;
        this.settings = settings;
        switchToHangar = explosions = sounds = true;
    }
    
    public void bind(Nifty nifty, Screen screen) 
    {
        this.nifty = nifty;
        this.screen = screen;
        statisticBarText = nifty.getScreen("hangar").findElementByName("layer").findElementByName("levelSummary").findElementByName("onlineStatistic").findElementByName("statisticBarText").getRenderer(TextRenderer.class);
        statisticWidget = nifty.getScreen("hangar").findElementByName("layer").findElementByName("levelSummary").findElementByName("onlineStatistic").findElementByName("statisticBar");
    }

    public void onStartScreen() {}

    public void onEndScreen() {}
    
    public void setLoadingState(String loadGame) throws InterruptedException
    {       
        audioManager.playAudioInstance("click");
        List<Element> elements = nifty.getCurrentScreen().getLayerElements();
        for(int i = 0;i < elements.size();i++)
        {
            elements.get(i).hide();
        }
        gameState = new GameState((Main)app,Boolean.parseBoolean(loadGame),explosions);
        loadingState = new LoadingState((Main)app,settings,this,gameState);
        stateManager.attach(loadingState);
        loadAd();
    }
    
    public void setHangarState()
    {
        nifty.gotoScreen("hangar");
        updateHangarGui();
        gameState.saveGame();
    }
    
    public void setGameState()
    {
        audioManager.playAudioInstance("click");
        audioManager.playAudioInstance("gateOpening");
        gameState.saveGame();
        nifty.gotoScreen("game");
        gameState.hangarMode = false;       
    }
    
    public void resetGameState()
    {
        gameState.reset();
    }
    
    public void upgradeDamage()
    {
        if(gameState.upgradePoints >= 5)
        {
            audioManager.playAudioInstance("click");
            gameState.upgradePoints-=5;
            gameState.damage++;
            updateHangarGui();
        }
        else
            audioManager.playAudioInstance("alert");
    }
    
    public void upgradeDodge()
    {
        if(gameState.upgradePoints >= 5)
        {
            if(gameState.dodgeMaxDistance < 3500)
            {
                audioManager.playAudioInstance("click");
                gameState.upgradePoints-=5;
                gameState.dodgeMaxDistance+=1;
                updateHangarGui();
            }
            else
                audioManager.playAudioInstance("alert");
        }
        else
            audioManager.playAudioInstance("alert");
    }
        
    public void upgradeFireRate()
    {
        if(gameState.upgradePoints >= 10)
        {
            if(gameState.exhausted > 150)
            {
                audioManager.playAudioInstance("click");
                gameState.upgradePoints-=10;
                gameState.exhausted-=2;
                updateHangarGui();
            }
            else
                audioManager.playAudioInstance("alert");
        }
        else
            audioManager.playAudioInstance("alert");
    }
    
    public void updateHangarGui()
    {
        Element element;
        TextRenderer text;
        //damage
        element = nifty.getScreen("hangar").findElementByName("layer").findElementByName("damageButton").findElementByName("damageButtonValueText");
        text = element.getRenderer(TextRenderer.class);
        text.setText(String.valueOf(gameState.damage));
        //dodge
        element = nifty.getScreen("hangar").findElementByName("layer").findElementByName("dodgeButton").findElementByName("dodgeButtonValueText");
        text = element.getRenderer(TextRenderer.class);
        text.setText(String.valueOf(gameState.dodgeMaxDistance));
        //fire rate
        element = nifty.getScreen("hangar").findElementByName("layer").findElementByName("fireRateButton").findElementByName("fireRateButtonValueText");
        text = element.getRenderer(TextRenderer.class);
        text.setText(String.valueOf(gameState.exhausted));
        //upgrade points
        element = nifty.getScreen("hangar").findElementByName("layer").findElementByName("upgradeText2");
        text = element.getRenderer(TextRenderer.class);
        text.setText(gameState.upgradePoints + " Upgrade Points Left");
        //level&&combo
        element = nifty.getScreen("hangar").findElementByName("layer").findElementByName("levelSummary").findElementByName("levelSummaryText");
        text = element.getRenderer(TextRenderer.class);
        text.setText("Level " + gameState.level + "      Combo "+ gameState.maxCombo);
        //target
        element = nifty.getScreen("hangar").findElementByName("layer").findElementByName("levelSummary").findElementByName("levelSummaryText2");
        text = element.getRenderer(TextRenderer.class);
        text.setText("Target " + gameState.target + "M");
        
        if(statisticBar != null)
            statisticBar.markForRemoval();
        statisticBarText.setText("Click To Refresh");
        
    }
    
    public void showStatistic()
    {
        audioManager.playAudioInstance("click");
        if(statisticBar != null)
            statisticBar.markForRemoval();
                
        //connection
        ServerLink serverLink = new ServerLink("prots.eu",7149);
        if(serverLink.connect())
        {
            String status = serverLink.getStatus();
            if(status.length() > 0)
            {
                //check token
                if(gameState.token.matches("") || status.matches("forceToken"))
                {
                    gameState.token = serverLink.getNewToken();
                }
                if(!serverLink.updateSession(gameState.token, gameState.level))
                {
                    statisticBarText.setText("Wrong Token Creating new");
                    gameState.token = serverLink.getNewToken();
                    return;
                }
                
                String arrowPosition = serverLink.getArrowPosition(gameState.token);
                String topLevel = String.valueOf(serverLink.getTopLevel());
                ////////////
                //updating//
                statisticBarText.setText("");
                //bar
                PanelCreator panel = new PanelCreator();
                panel.setX("20%");
                panel.setY("35%");
                panel.setWidth("45%");
                panel.setHeight("65%");
                panel.setBackgroundImage("Textures/statisticBar.png");
                panel.setChildLayout("absolute");
                statisticBar = panel.create(nifty,screen,statisticWidget);
                //bar arrow
                panel = new PanelCreator();
                panel.setX(arrowPosition);
                panel.setY("55%");
                panel.setWidth("40%");
                panel.setHeight("60%");
                panel.setBackgroundImage("Textures/statisticBarArrow.png");
                panel.create(nifty,screen,statisticBar);
                //text behind bar
                TextCreator text = new TextCreator("Text");
                text.setX("105%");
                text.setY("35%");
                text.setFont(bigFontPath);
                text.setColor("#ff0011");
                text.setText(topLevel);
                text.create(nifty,screen,statisticBar);
                
                text = new TextCreator("Text2");
                text.setX("40%");
                text.setY("0");
                text.setFont(smallFontPath);
                text.setColor("#ff0011");
                text.setText("Top Level");
                text.create(nifty,screen,statisticBar);
            }
            serverLink.disconnect();
        }
        else
        {
            statisticBarText.setText("Cant Connect");
        }
        gameState.saveGame();
    }
    
    public void loadAd()
    {
        app.android.loadAd();
    }
    
    public boolean showAd()
    {
        return app.android.showAd();
    }
    
    public void exitGame()
    {
       audioManager.playAudioInstance("click");
       gameState.saveGame();
       app.android.exitGame();
    }
    
    public void explosions()
    {
        audioManager.playAudioInstance("click");
        explosions = !explosions;
        Element element;
        ImageRenderer image;
        String filePath;
        if(explosions)
           filePath = "Textures/greenBackground.png";
        else
           filePath = "Textures/redBackground.png";
                    
        NiftyImage niftyImage = nifty.getRenderEngine().createImage(nifty.getScreen("start"), filePath, false);  
        element = nifty.getScreen("start").findElementByName("layer").findElementByName("optionsPanel").findElementByName("explosionsButton");
        image = element.getRenderer(ImageRenderer.class);
        image.setImage(niftyImage);
    }
    
    public void sounds()
    {
        audioManager.playAudioInstance("click"); 
        sounds = !sounds;
        audioManager.setEnabled(sounds);
        Element element;
        ImageRenderer image;
        String filePath;
        if(sounds)
           filePath = "Textures/greenBackground.png";
        else
           filePath = "Textures/redBackground.png";
                    
        NiftyImage niftyImage = nifty.getRenderEngine().createImage(nifty.getScreen("start"), filePath, false);  
        element = nifty.getScreen("start").findElementByName("layer").findElementByName("optionsPanel").findElementByName("soundsButton");
        image = element.getRenderer(ImageRenderer.class);
        image.setImage(niftyImage);
    }
    
    @Override
    public void update(float tpf) 
    {
    }
    
    public String getFontPath(String size)
    {
        if(size.matches("small"))
            return smallFontPath;
        else
            return bigFontPath;
    }
}
