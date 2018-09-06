import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

@ScriptManifest(name = "Woodcutter", version = 1.0, author = "CaptFalcon", info = "", logo = "")
public class WoodCutter extends Script {

    @Override
    public int onLoop() throws InterruptedException {
        //If leveled up, continue
        if(dialogues.inDialogue()){
            dialogues.clickContinue();
        }
        if (shouldBank()) {
            bank();
        } else {
            cutTree(getTreeName());
        }
        return random(500, 800);
    }

    private boolean shouldBank() {
        return getInventory().isFull();
    }

    private void bank() throws InterruptedException {
        if (!Banks.VARROCK_WEST.contains(myPlayer())) {
            getWalking().webWalk(Banks.VARROCK_WEST);
        } else {
            if (!getBank().isOpen()) {
                getBank().open();
            } else {
                getBank().depositAllExcept(axes -> axes.getName().contains(" axe"));
            }
        }
    }

    private void cutTree(String treeName) {
        if (!getTreeArea().contains(myPlayer())) {
            int x1 = random(3169, 3173);
            int y1 = random(3422, 3427);
            int x2 = random(3157, 3161);
            int y2 = random(3397, 3401 );

            //Center of tree area
            Position treeCenter = new Position(3163, 3412, 0); //what looks to be center tile of spot
            Position randomCenter =  new Position(random(3161,3165), random(3410, 3415), 0);

            getWalking().webWalk(randomCenter);
        } else {
            RS2Object tree = getObjects().closest(treeName);
            if (!myPlayer().isAnimating() && tree != null) {

                if(!tree.isOnScreen()){ //rotate camera to have cow in it
                    camera.toEntity(tree);
                }

                if (tree.interact("Chop down")) {

                    new ConditionalSleep(3500, 5000) {
                        @Override
                        public boolean condition() {
                            return myPlayer().isAnimating();
                        }
                    }.sleep();
                }
            }
        }
    }

    private Area getTreeArea() {

        return new Area(3171, 3425, 3159, 3399);
    }

    private String getTreeName() {
        if (getSkills().getDynamic(Skill.WOODCUTTING) >= 15){
            return "Oak";
        }
        return "Tree";
    }
}