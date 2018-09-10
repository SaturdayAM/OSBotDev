package script;


import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import sections.*;

import java.awt.*;

//Adapted from the following open source tutorial island bot:
//https://github.com/Explv/Tutorial-Island


@ScriptManifest(author = "CaptFalcon", name = "Tutorial Island", info = "Completes Tutorial Island", version = 1.0, logo = "")
public final class Tutorial_Island extends Script {
    //CTRL + O to implement method overrides
    private final TutorialSection rsGuideSection = new RunescapeGuideSection();
    private final TutorialSection survivalSection = new SurvivalSection();
    private final TutorialSection cookingSection = new CookingSection();
    private final TutorialSection questSection = new QuestSection();
    private final TutorialSection miningSection = new MiningSection();
    private final TutorialSection fightingSection = new FightingSection();
    private final TutorialSection bankSection = new BankSection();
    private final TutorialSection priestSection = new PriestSection();
    private final TutorialSection wizardSection = new WizardSection();


    @Override
    public void onStart() throws InterruptedException {
        rsGuideSection.exchangeContext(getBot());
        survivalSection.exchangeContext(getBot());
        cookingSection.exchangeContext(getBot());
        questSection.exchangeContext(getBot());
        miningSection.exchangeContext(getBot());
        fightingSection.exchangeContext(getBot());
        bankSection.exchangeContext(getBot());
        priestSection.exchangeContext(getBot());
        wizardSection.exchangeContext(getBot());
    }

    @Override
    public final int onLoop() throws InterruptedException {
        if (isTutorialIslandCompleted()) {
            stop(true);
            return 0;
        }

        switch (getTutorialSection()) {
            //Sections listed sequentially
            //Script must begin after character initialization
            //but prior to speaking to the first tutor
            case 0:
            case 1:
                rsGuideSection.onLoop();
                break;
            case 2:
            case 3:
                survivalSection.onLoop();
                break;
            case 4:
            case 5:
                cookingSection.onLoop();
                break;
            case 6:
            case 7:
                questSection.onLoop();
                break;

            //Mining tutor bugs:
            // 1. If rock to prospect not on camera, bot gets stuck
            case 8:
            case 9:
                miningSection.onLoop();
                break;
            case 10:
            case 11:
            case 12:
                fightingSection.onLoop();
                break;
            case 14:
            case 15:
                bankSection.onLoop();
                break;
            case 16:
            case 17:
                priestSection.onLoop();
                break;
            case 18:
            case 19:
            case 20:
                wizardSection.onLoop();
                break;
        }
        //return value is delay in ms between loops
        return random (200, 500);
    }


    @Override
    public void onExit() throws InterruptedException {
        super.onExit();
    }

    @Override
    public void onPaint(Graphics2D iIIiiiiIiiII) {
        super.onPaint(iIIiiiiIiiII);
    }

    private int getTutorialSection() {
        //from osbot class MethodProvider
        //
        return getConfigs().get(406);
    }

    //Tutorial island completion status based on if status bar is present
    private boolean isTutorialIslandCompleted() {
        return getWidgets().getWidgetContainingText("Tutorial Island Progress") == null;
    }
}
