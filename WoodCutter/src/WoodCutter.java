import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;


@ScriptManifest(name = "Woodcutter", version = 1.2, author = "CaptFalcon", info = "", logo = "")
public class WoodCutter extends Script {

    private String location = ""; // "VARROCK_WEST" || "LUMBRIDGE"

    /*******************************************************************
     *                                                                 *
     *                  Script Class Inherited Methods                 *
     *                                                                 *
     *******************************************************************/

    @Override
    public void onStart() throws InterruptedException {
        location = "VARROCK_WEST";
        log("WoodCutter.onStart(). Location: " + location);

    }

    @Override
    public int onLoop() throws InterruptedException {
        //If leveled up, continue
        if(dialogues.inDialogue()){
            dialogues.clickContinue();
        }
        if (getInventory().isFull()){ //should bank
            bank();
        } else { //should cut tree
            if(!getTreeArea().contains(myPlayer())){
                walkToTreeArea();
            } else {
                cutTree(getTreeName());
            }
        }
        return random(500, 800);
    }



    /*******************************************************************
     *                                                                 *
     *                      Custom Helper Methods                      *
     *                                                                 *
     *******************************************************************/
    private void bank() throws InterruptedException {
        Area bankToGo;
        switch(location){
            case "VARROCK_WEST":
                log("WoodCutter.bank(), case: VARROCK_WEST");
                bankToGo = Banks.VARROCK_WEST;
                break;
            case "LUMBRIDGE":
                log("WoodCutter.bank(), case: LUMBRIDGE");
                bankToGo = Banks.LUMBRIDGE_UPPER;
                break;
            default:
                log("WoodCutter.bank(), case: DEFAULT");
                bankToGo = Banks.VARROCK_WEST;
        }

        if (!bankToGo.contains(myPlayer())) {
            getWalking().webWalk(bankToGo);
        } else {
            if (!getBank().isOpen()) {
                getBank().open();
            } else {
                getBank().depositAllExcept(axes -> axes.getName().contains(" axe"));
            }
        }

    }

    private void cutTree(String treeName) {
        RS2Object tree = getObjects().closest(treeName);
        if (!myPlayer().isAnimating() && tree != null) {
            if(!tree.isVisible()){ //rotate camera to have tree in it
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

    private void walkToTreeArea(){
        //Varrock West Bank location
        int x1 = random(3169, 3173);
        int y1 = random(3422, 3427);
        int x2 = random(3157, 3161);
        int y2 = random(3397, 3401);

        //Center of tree area at Varrock
        //Position treeCenter = new Position(3163, 3412, 0); //what looks to be center tile of spot
        Position randomCenter =  new Position(random(3161,3165), random(3410, 3415), 0);

        getWalking().webWalk(randomCenter);
    }

    private Area getTreeArea() {
        if(location == "VARROCK_WEST"){
            log("WoodCutter.getTreeArea(): Location = " + location );
            return new Area(3171, 3425, 3159, 3399);
        }
        return new Area(3171, 3425, 3159, 3399);
    }

    private String getTreeName() {
        if (getSkills().getDynamic(Skill.WOODCUTTING) >= 15){
            return "Oak";
        }
        return "Tree";
    }
}