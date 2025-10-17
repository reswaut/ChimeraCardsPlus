package chimeracardsplus;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment.AugmentRarity;
import basemod.AutoAdd;
import basemod.BaseMod;
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

import java.util.EnumMap;
import java.util.Map;

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
        BaseMod.addPower(DoomPower.class, DoomPower.POWER_ID);
        BaseMod.addPower(FrozenEyePower.class, FrozenEyePower.POWER_ID);
        BaseMod.addPower(LetterOpenerPower.class, LetterOpenerPower.POWER_ID);
        BaseMod.addPower(LoseFocusPower.class, LoseFocusPower.POWER_ID);
        BaseMod.addPower(NeurosurgePower.class, NeurosurgePower.POWER_ID);
        BaseMod.addPower(NoDamagePower.class, NoDamagePower.POWER_ID);
        BaseMod.addPower(RetributionPower.class, RetributionPower.POWER_ID);
        BaseMod.addPower(StunPlayerPower.class, StunPlayerPower.POWER_ID);
        BaseMod.addPower(TempStaticDischargePower.class, TempStaticDischargePower.POWER_ID);
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

    private static void registerAugments() {
        CardAugmentsMod.registerMod(MOD_ID, configs.getLabelText());
        Map<AugmentRarity, Integer> augmentsByRarity = new EnumMap<>(AugmentRarity.class);
        new AutoAdd(MOD_ID).packageFilter("chimeracardsplus.cardmods")
                .any(AbstractAugmentPlus.class, (info, augment) -> {
                    registerAugment(augment);
                    AugmentRarity rarity = augment.getModRarity();
                    augmentsByRarity.put(rarity, augmentsByRarity.getOrDefault(rarity, 0) + 1);
                });
        logger.info("-- Registered to CardAugment:");
        int total = 0;
        for (AugmentRarity rarity : AugmentRarity.values()) {
            int value = augmentsByRarity.getOrDefault(rarity, 0);
            total += value;
            logger.info("-- {} {} modifiers", value, rarity);
        }
        logger.info("-- {} modifiers in total", total);
    }

    @Override
    public void receivePostInitialize() {
        logger.info("Initialization started.");
        configs.setupModPanel(resourceLoader.getTexture("badge.png"));
        logger.info("- Mod panel setup complete.");
        registerPowers();
        logger.info("- Registered powers.");
        registerAugments();
        logger.info("- Registered modifiers.");
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
        PotionModifierManager.onBattleStart();
        ShuffleModifierManager.onBattleStart();
        AbstractDungeon.player.addPower(new AbstractAugmentPlusHelperPower(AbstractDungeon.player));
    }

    @Override
    public void receiveOnPlayerTurnStart() {
        PotionModifierManager.onPlayerTurnStart();
    }

    @Override
    public void receivePostPotionUse(AbstractPotion abstractPotion) {
        PotionModifierManager.onUsePotion(abstractPotion);
    }
}
