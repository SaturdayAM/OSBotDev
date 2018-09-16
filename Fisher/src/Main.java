import org.osbot.rs07.api.Walking;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.awt.*;

@ScriptManifest(author = "CaptFalcon", info = "", name = "Fisher", logo = "", version = 1)
public class Main extends Script {

    Area fishingArea = new Area (3237, 3142, 3251, 3163); //Lumbridge fishing area


    //Areas
    public Area BOAT_KARAMJA = new Area(2955,3141,2957,3144).setPlane(1); //MAKE BIGGER THAN BOAT
    public Area DOCK_KARAMJA = new Area(2945,3146,2960,3147);
    public Area DOCK_SARIM = new Area(3022,3214,3029,3223);
    public Area BOAT_SARIM  = new Area(3038,3210,3031,3223).setPlane(1);
    public Area FISHING_AREA = new Area(2919, 3173,2928,3181);
    public Area DEPOSIT_BOX = new Area(3041,3234,3047,3237);
    public Area DOCK_CUSTOMS = new Area(2953,3146,2955,3147);
    //everything Walking

    //tIME
    private long startTime;

    int animFishing =619;
    int fishCaught;
    //inAreaCheck

    int spotId = 1522;
    String netName = "Lobster pot";
    String coins = "Coins";
    String seamanName = "Seaman";
    String officerName = "Customs officer";
    String[] seamanActions = new String[10];
    String yes = "Yes please.";

    //karamjacustomsofficerstrings
    String yesJourney = "Can I journey on this ship?";
    String yesSearch = "Search away, I have nothing to hide.";
    String ok = "Ok.";
    //
    String plankName = "Gangplank";
    String crossPlank = "Cross";
    String payFare = "Pay-Fare";


    @Override
    public void onStart(){
        log("The script is starting");
        startTime = System.currentTimeMillis();
        this.getExperienceTracker().start(Skill.FISHING);
        fishCaught = 0;
    }




    @Override
    public int onLoop() throws InterruptedException {
        //TIME STUFF
        final long runTime = System.currentTimeMillis() - startTime;
        //END TIME STUFF

        lumbrigeFisher();

        return random(700, 800);

    } //end onLoop()

    private void lobsterFish() throws InterruptedException{
        if(inventory.getAmount("Coins") <=60){ //cheap coin check logout, fix in next version
            logoutTab.logOut();
            stop();
        }
        switch(getState()){
            case WALK_PLANK:
                log("Walking plank...");
                clickPlank();
                sl(); //randomize final tile
                break;

            case WALK_TO_FISH:
                log("attempting to walk to fishing area...");
                this.walking.webWalk(FISHING_AREA);
                sl();
                break;

            case FISH:
                log("Starting to fish...");
                Fish();
                sl();
                break;

            case WALK_TO_KARAMJA_DOCK:
                log("Walking to Karamja Dock...");
//              this.walking.webWalk(DOCK_KARAMJA);
                this.walking.webWalk(DOCK_CUSTOMS); //gets closer to customs officer
                break;

            case TALK_TO_CUSTOMS_OFFICER:
                findCustomsKaramja(); //selects the pay-fare option
                sl();
                payForBoatKaramja();
                sl();
                break;

            case TALK_TO_SEAMAN:
                log("Finding and talking to seaman on Port Sarim Dock...");
                findSeamanPort();
                sl();
                payForBoatPort();
                break;

            case WALK_TO_DEPOSIT:
                log("Walking to deposit box...");
                this.walking.webWalk(DEPOSIT_BOX);
                break;

            case FISHING_ANTIBAN:
                antiban();
                break;

            case MAKE_DEPOSIT:
                log("Depositing fish...");
                depositFish();
                break;

            case WALK_TO_SARIM_DOCK:
                log("Walking to Sarim Dock...");
                this.walking.webWalk(DOCK_SARIM);
                break;

            case LOST: //this means that you are not in any defined area.       // thus we will use webWalk to get back to an area.
                log("Player is lost, making back to area that is known...");
                lostPlayer();
                sl();
                break;

            case IDLE:
                log("currently idle");
                break;

        }

    }

    private void antiban() throws InterruptedException{
        if(random(0,1000)==1){
            switch(random(0,1)){
                case 0:
                    this.mouse.move(random(123,345), random(241,405));
                    break;
                case 1:
                    this.tabs.open(Tab.SKILLS);
                    sleep(random(50,100));
                    this.mouse.move(random(681,725), random(273,285));
                    sleep(random(1000,4000));
                    this.tabs.open(Tab.INVENTORY);
                    break;
            }
        }
    }

    private NPC getFishSpot(int spotId){
        NPC fishSpot = this.npcs.closest(spotId);
        return fishSpot;
    }

    private void Fish() throws InterruptedException{
        NPC fishSpot =  getFishSpot(spotId);
        if(!this.myPlayer().isAnimating()){
            int x = random(0,1);
            log("x = "+ x);
            switch(x){
                case 0:
                    this.mouse.move(random(100,500),random(100,500));
                    fishSpot.interact("Cage");
                    break;
                case 1:
                    fishSpot.hover();
                    this.mouse.click(true);
                    fishSpot.interact("Cage");
                    break;
                default:
                    fishSpot.interact("Cage");
            }

        }
    }

    public void findSeamanPort() throws InterruptedException{
        log("Finding seaman...");

        while(this.chatbox.isVisible()){
            NPC seaman = this.npcs.closestThatContains(seamanName);
            if(!seaman.isVisible()){
                this.camera.toEntity(seaman);
            } else {
                seamanActions = seaman.getActions();
                seaman.interact(seamanActions[2]);
            }
            sleep(random(1000,2000));
        }
    }

    public void findCustomsKaramja() throws InterruptedException{
        log("Finding seaman...");
        while(this.chatbox.isVisible()){
            NPC customsOfficer = this.npcs.closestThatContains(officerName);
            if(!customsOfficer.isVisible()){
                this.camera.toEntity(customsOfficer);
                sl();
            } else {
                customsOfficer.interact(payFare);
                sl();
            }
        }
    }

    public void payForBoatPort() throws InterruptedException{
        while(this.dialogues.inDialogue()){
            if(!this.dialogues.isPendingContinuation() && !this.dialogues.completeDialogue(yes)){
                this.dialogues.clickContinue();
                sleep(random(200,800));
            } else if(this.dialogues.completeDialogue(yes)) {
                this.dialogues.selectOption(1);
                sleep(random(200,800));
            }
        }
    }

    public void payForBoatKaramja() throws InterruptedException{
        while(this.dialogues.inDialogue()){
            if(!this.dialogues.isPendingContinuation() && !this.dialogues.completeDialogue(yesJourney, yesSearch, ok)){
                this.dialogues.clickContinue();
                sl();
            } else if(this.dialogues.completeDialogue(yesJourney)){
                this.dialogues.selectOption(yesJourney);
                sl();
            } else if(this.dialogues.completeDialogue(yesSearch)) {
                this.dialogues.selectOption(yesSearch);
                sl();
            } else {
                this.dialogues.selectOption(ok);
                sl();
            }
        }
    }

    public enum State{
        WALK_PLANK, WALK_TO_FISH, WALK_TO_KARAMJA_DOCK, WALK_TO_SARIM_DOCK,
        WALK_TO_DEPOSIT, LOST, FISH, TALK_TO_CUSTOMS_OFFICER, TALK_TO_SEAMAN,
        FISHING_ANTIBAN, MAKE_DEPOSIT,IDLE
    };

    public State getState(){
        log("getState() being called");
        if(BOAT_KARAMJA.contains(this.myPlayer()) || BOAT_SARIM.contains(this.myPlayer())){ //add sarim boat
            log("Position confirmed: On boat.");
            return State.WALK_PLANK;
        }   else if (DOCK_KARAMJA.contains(this.myPlayer()) && !this.inventory.isFull()){ //if at karamja&fullinvy; go to fish){
            log("You are on the karamja dock with a nonfullinvy");
            return State.WALK_TO_FISH;
        } else if(FISHING_AREA.contains(this.myPlayer()) && this.myPlayer().getAnimation()==-1
                && !this.inventory.isFull() && !myPlayer().isMoving()) {
            log("We are doing nothing. Time to fish");
            return State.FISH;
        } else if(this.inventory.isFull() && FISHING_AREA.contains(this.myPlayer())){ //if fulloffish in fish area
            return State.WALK_TO_KARAMJA_DOCK;
        }  else if (DOCK_KARAMJA.contains(this.myPlayer()) && this.inventory.isFull()){
            return State.TALK_TO_CUSTOMS_OFFICER; //also pays
        }  else if (DOCK_SARIM.contains(this.myPlayer()) && !this.inventory.isFull() ){
            return State.TALK_TO_SEAMAN;//also pays
        } else if(DOCK_SARIM.contains(this.myPlayer()) && this.inventory.isFull()){
            return State.WALK_TO_DEPOSIT;
        } else if(DEPOSIT_BOX.contains(this.myPlayer()) && this.inventory.isFull()){
            return State.MAKE_DEPOSIT;
        }else if (DEPOSIT_BOX.contains(this.myPlayer()) && !this.inventory.isFull()){
            return State.WALK_TO_SARIM_DOCK;
        } else if(!DOCK_KARAMJA.contains(this.myPlayer())
                && !DOCK_SARIM.contains(this.myPlayer())
                && !BOAT_KARAMJA.contains(this.myPlayer())
                && !FISHING_AREA.contains(this.myPlayer())
                && !DEPOSIT_BOX.contains(this.myPlayer())
                && !BOAT_SARIM.contains(this.myPlayer())){
            return State.LOST;
        } else if (animFishing == this.myPlayer().getAnimation()){
            return State.FISHING_ANTIBAN;
        } else {
            return State.IDLE;
        }


    }

    private void depositFish() throws InterruptedException{
        while(!this.myPlayer().isMoving() && this.inventory.isFull()&& !this.depositBox.isOpen()){
            log("Finding deposit box...");
            RS2Object db = this.objects.closest("Bank deposit box");
            db.interact("Deposit");
            sl();
        }
        if(this.depositBox.isOpen() && inventory.contains("Raw lobster")){
            this.depositBox.depositAllExcept(netName, coins);

        }
    }

    private void clickPlank(){
        log("We are on the boat. Finding plank...");
        this.objects.closest(plankName).interact(crossPlank);
    }

    private void sl() throws InterruptedException{ //Sleep function for random time
        sleep(random(500,1300));
    }

    private void lostPlayer() throws InterruptedException{
        this.walking.webWalk(DOCK_KARAMJA, DOCK_SARIM, DEPOSIT_BOX, FISHING_AREA );
        log("We are lost. Making way to known area.");
    }


    //EVERYTHING PAINT UNDER HERE

    public final String formatTime(final long ms){
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60; m %= 60; h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    @Override
    public void onPaint(Graphics2D g) {

        Font font = new Font("Comic Sans", Font.BOLD, 14);
        Point mP = getMouse().getPosition();
        g.setColor(Color.WHITE);
        g.drawLine(mP.x - 3, mP.y - 3, mP.x - 3, mP.y + 3);
        g.drawLine(mP.x - 3, mP.y - 3, mP.x + 3, mP.y - 3);
        g.drawLine(mP.x - 3, mP.y + 3, mP.x + 3, mP.y + 3);
        g.drawLine(mP.x + 3, mP.y + 3, mP.x + 3, mP.y - 3);
        g.setColor(Color.CYAN);
        g.drawString("Current Level: "+ getSkills().getStatic(Skill.FISHING), 10, 210);
        fishCaught = getExperienceTracker().getGainedXP(Skill.FISHING)/90;
        g.drawString("Fish caught: "+ fishCaught, 10, 230);
        g.drawString("XP Gained: "+ getExperienceTracker().getGainedXP(Skill.FISHING), 10, 250);
        g.drawString("XP / HR: "+ getExperienceTracker().getGainedXPPerHour(Skill.FISHING), 10, 270);
        g.drawString("Time to LVL: "+ formatTime(getExperienceTracker().getTimeToLevel(Skill.FISHING)), 10, 290);
        final long runTime = System.currentTimeMillis() - startTime;
        g.drawString("Time Ran: "+ formatTime(runTime), 10, 310);
        g.setColor(Color.WHITE);
        g.drawString("Pokie Fisher V1.0",10,330);
    }




    public void lumbrigeFisher() throws InterruptedException{
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
            antiban();
        }
    }



}
