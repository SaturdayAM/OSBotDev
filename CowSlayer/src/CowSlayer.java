import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.EntityAPI;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import org.osbot.rs07.api.Combat;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.filter.NameFilter;

import java.util.function.BooleanSupplier;
import java.util.concurrent.TimeUnit;

/*
    ScriptManifest annotation: required by main class to feed data to OSBot script selector.
 */
@ScriptManifest(author = "CaptFalcon", name="Cow Slayer", info="A beginner script", version = 0.1,
        logo= "https://png.icons8.com/color/180/runescape.png")

//Docs at:
//https://osbot.org/api/org/osbot/rs07/script/Script.html
//Script class extends MethodProvider, allows access to OSBot API

//Map:
/*
            N y++

   x-- W         E x++

            S y--
 */
public final class CowSlayer extends Script {

    @Override
    public final void onStart(){ //called on script start
        log("Logged on start");
    }

    private enum State { //Declare states
        KILL, LOOT, WAIT, BANKING;
    }

    private State getState(){ //returns the state

        NPC cow = getNpcs().closest(new Filter<NPC>(){
            @Override
            public boolean match(NPC npc){
                return npc != null && npc.exists() &&
                        (npc.getName().equals("Cow")||npc.getName().equals("Cow calf")) && npc.isAttackable();
            }

        });

        //States:
        //Kill: if inventory not full
        //Wait: If clicked attack
        //Loot: Right after killing
        //Bank: when full
        if(cow != null && !inventory.isFull()){
            return State.KILL;
        }


        return State.WAIT;

    }


    @Override
    public final int onLoop() throws InterruptedException { //core loop for running script


        //Referrenced from: https://osbot.org/forum/topic/70292-vags-chicken-blaster-first-script-ever/?do=findComment&comment=803046
        if(myPlayer().getInteracting() != null){
            //If player is interacting, return to stop checking until out of combat
            return random(500, 1000);
        }

        //If leveled up, continue
        if(dialogues.inDialogue()){
            dialogues.clickContinue();
        }

        //if inventory is full, go to bank and deposit hides
        if(inventory.isFull()){
            if(!Banks.FALADOR_EAST.contains(myPlayer().getPosition())){
                getWalking().webWalk(Banks.FALADOR_EAST);
            } else {
                if(!getBank().isOpen()){
                    getBank().open();
                    new ConditionalSleep(2500, 3000){
                        @Override
                        public boolean condition(){
                            return getBank().isOpen();
                        }
                    }.sleep();
                } else{
                    bank.depositAll("Cowhide"); //bank -> player bank
                }
            }
            return(random(500, 1000));
        } else{//If inventory is not full and not in cow area, go to cow area

            //Player position
            Position playerPos = myPosition();

            //Center of cow pen near falador
            Position coopCenter = new Position(3032, 3305, 0); //what looks to be center tile of spot
            Position randomCenter =  new Position(random(3029,3033), random(3302, 3307), 0);

            //Rectangle Area spanning most of cow pen south of Falador. Params are corners x1y1, x2y2
            Area cowCoop = new Area(3021, 3297, 3042, 3312);

            if(!cowCoop.contains(playerPos) && !combat.isFighting()){ //if not in pen and not under attack
                getWalking().webWalk(randomCenter); //get around the center
            }

            //Find nearest attackable cow/ cow calf
            NPC cow = getNpcs().closest(new Filter<NPC>() {
                @Override
                public boolean match(NPC npc) {
                    return npc != null && npc.exists() &&
                            (npc.getName().equals("Cow")||npc.getName().equals("Cow calf")) && npc.isAttackable();
                }
            });

            if(cow == null){ // if cow is null, wait for one to spawn
                return random(500, 1000);
            }

            if(!cow.isOnScreen()){ //rotate camera to have cow in it
                camera.toEntity(cow);
                return random(500, 1000);
            }

            cow.interact("Attack");

            //Looting
            GroundItem cowhide = groundItems.closest("Cowhide");
            if((!inventory.isFull() && !myPlayer().isUnderAttack())){
                cowhide.interact("Take");
                return random(1500, 2000);
            }

        }




        return random(500, 1000); //the return value is the time between each onLoop call, in ms
    }

    @Override
    public final void onExit(){
        log("Logged on exit");
    }

}