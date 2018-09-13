import org.osbot.rs07.api.Walking;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

@ScriptManifest(author = "CaptFalcon", info = "", name = "Fisher", logo = "", version = 1)
public class Main extends Script {
    Area fishingArea = new Area (3237, 3142, 3251, 3163);
    @Override
    public int onLoop() throws InterruptedException {


        if (dialogues.inDialogue()) {
            new ConditionalSleep(random(10000, 20000), 1000){
                @Override
                public boolean condition(){
                    return false;
                }
            }.sleep();
            dialogues.clickContinue();
        }

        if(getInventory().isFull()){ //Full inventory, need to bank
            log("Banking");

            if(getWalking().webWalk(Banks.LUMBRIDGE_UPPER)){ //Go to bank

                if(getBank().isOpen()){ //If bank is open, deposit
                    if(getBank().depositAllExcept("Small fishing net")){
                        new ConditionalSleep(random(1000,2000), 200){
                            @Override
                            public boolean condition(){
                                return !getInventory().isFull();
                            }
                        }.sleep();

                    }
                } else if (getBank().open()){ //Open bank
                    new ConditionalSleep(random(1000, 2000), 200){
                        @Override
                        public boolean condition(){
                            return getBank().isOpen();
                        }
                    }.sleep();
                }
            }

        } else { //Go to fishing area
            if(!fishingArea.contains(myPosition()) && !myPlayer().isAnimating()){
                getWalking().webWalk(fishingArea);
            }

            NPC fishingSpot = npcs.closest(obj -> obj != null && obj.getName().equals("Fishing spot"));
            if (!myPlayer().isAnimating()){
                if(fishingSpot != null){
                    fishingSpot.interact("Net");
                    new ConditionalSleep(random(2000, 3000), 200){
                        @Override
                        public boolean condition(){
                            return myPlayer().isAnimating();
                        }
                    }.sleep();
                    log("Now fishing at spot");
                }
            }
        }

        return random(700, 800);
    }


}
