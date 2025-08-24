package chimeracardsplus;

import CardAugments.CardAugmentsMod;
import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.*;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus.AugmentBonusLevel;
import chimeracardsplus.helpers.*;
import chimeracardsplus.powers.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.rooms.EventRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class ChimeraCardsPlus implements
        EditKeywordsSubscriber,
        EditStringsSubscriber,
        OnPlayerTurnStartSubscriber,
        OnStartBattleSubscriber,
        PostPotionUseSubscriber,
        PostInitializeSubscriber {
    public static final String MOD_ID = "chimeracardsplus";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final ModConfigs configs = new ModConfigs();
    public static final ResourceLoader resourceLoader = new ResourceLoader();
    public static final SpecialNamingRules specialNamingRules = new SpecialNamingRules();
    public static final PotionUseHelper potionUseHelper = new PotionUseHelper();
    public static final DrawPileShuffleHelper drawPileShuffleHelper = new DrawPileShuffleHelper();

    public static String makeID(String name) {
        return MOD_ID + ':' + name;
    }

    public static void initialize() {
        BaseMod.subscribe(new ChimeraCardsPlus());
        logger.info("Subscribed to BaseMod.");
        configs.initialize();
    }

    private static void registerPowers() {
        BaseMod.addPower(DeceptionPower.class, DeceptionPower.POWER_ID);
        BaseMod.addPower(FrozenEyePower.class, FrozenEyePower.POWER_ID);
        BaseMod.addPower(LetterOpenerPower.class, LetterOpenerPower.POWER_ID);
        BaseMod.addPower(LoseFocusPower.class, LoseFocusPower.POWER_ID);
        BaseMod.addPower(NoDamagePower.class, NoDamagePower.POWER_ID);
        BaseMod.addPower(RetributionPower.class, RetributionPower.POWER_ID);
        BaseMod.addPower(StunPlayerPower.class, StunPlayerPower.POWER_ID);
        BaseMod.addPower(TungstenRodPower.class, TungstenRodPower.POWER_ID);
        BaseMod.addPower(UntappedPower.class, UntappedPower.POWER_ID);
        BaseMod.addPower(VelvetChokerPower.class, VelvetChokerPower.POWER_ID);
    }

    private static void registerAugment(AbstractAugmentPlus augment) {
        CardAugmentsMod.registerAugment(augment, MOD_ID);
        if (augment.getModBonusLevel() != AugmentBonusLevel.NORMAL) {
            String id = augment.identifier(null);
            CardAugmentsMod.registerCustomBan(id, card -> {
                MapRoomNode node = AbstractDungeon.getCurrMapNode();
                if (node == null) {
                    return false;
                }
                AbstractRoom room = node.getRoom();
                return room != null && room.phase == RoomPhase.COMBAT && !Settings.isDebug;
            });
            CardAugmentsMod.registerCustomBan(id, card -> {
                MapRoomNode node = AbstractDungeon.getCurrMapNode();
                if (node == null) {
                    return false;
                }
                AbstractRoom room = node.getRoom();
                return room instanceof EventRoom && room.event instanceof TheLibrary;
            });
        }
    }

    @Override
    public void receivePostInitialize() {
        logger.info("Initialization started.");
        ModPanel modPanel = configs.setupModPanel();
        BaseMod.registerModBadge(resourceLoader.getTexture("badge.png"), configs.getName(), configs.getAuthor(), configs.getDesc(), modPanel);
        registerPowers();
        CardAugmentsMod.registerMod(MOD_ID, configs.getLabelText());
        new AutoAdd(MOD_ID).packageFilter("chimeracardsplus.cardmods")
                .any(AbstractAugmentPlus.class, (info, augment) -> registerAugment(augment));
        logger.info("Initialization complete.");
    }

    @Override
    public void receiveEditKeywords() {
        resourceLoader.loadKeywords();
        logger.info("Loaded keywords.");
    }

    @Override
    public void receiveEditStrings() {
        resourceLoader.loadStrings();
        logger.info("Loaded localization strings.");
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        potionUseHelper.onBattleStart(abstractRoom);
        drawPileShuffleHelper.onBattleStart(abstractRoom);
        AbstractDungeon.player.addPower(new AbstractAugmentPlusHelperPower(AbstractDungeon.player));
    }

    @Override
    public void receiveOnPlayerTurnStart() {
        potionUseHelper.onPlayerTurnStart();
    }

    @Override
    public void receivePostPotionUse(AbstractPotion abstractPotion) {
        potionUseHelper.onUsePotion(abstractPotion);
    }
}
