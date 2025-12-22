package chimeracardsplus;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment.AugmentRarity;
import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus.AugmentBonusLevel;
import chimeracardsplus.helpers.GameActionInfoManager;
import chimeracardsplus.helpers.ModConfigs;
import chimeracardsplus.helpers.ResourceLoader;
import chimeracardsplus.helpers.SpecialNamingRules;
import chimeracardsplus.rewards.*;
import chimeracardsplus.rewards.CardToModifierReward.Generator;
import chimeracardsplus.rewards.ModificationRewardsManager.RewardTypeEnum;
import chimeracardsplus.screens.ModificationRewardScreen;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.map.MapRoomNode;
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
        PostInitializeSubscriber {
    public static final String MOD_ID = "chimeracardsplus";
    public static final Logger logger = LogManager.getLogger(MOD_ID);
    public static final ModConfigs configs = new ModConfigs();
    public static final ResourceLoader resourceLoader = new ResourceLoader();
    public static final SpecialNamingRules specialNamingRules = new SpecialNamingRules();
    public static final GameActionInfoManager gameActionInfoManager = new GameActionInfoManager();
    public static final ModificationRewardsManager modificationRewardsManager = new ModificationRewardsManager();

    public static String makeID(String name) {
        return MOD_ID + ':' + name;
    }

    public static void initialize() {
        BaseMod.subscribe(new ChimeraCardsPlus());
        logger.info("Main class subscribed to BaseMod.");
        BaseMod.subscribe(gameActionInfoManager);
        logger.info("Battle Action Manager subscribed to BaseMod.");
        configs.initialize();
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
        logger.info("-- {} total modifiers", total);
    }

    public static void registerRewards() {
        modificationRewardsManager.registerModificationReward(new AddModifierReward.Generator());
        modificationRewardsManager.registerModificationReward(new RemoveModifierReward.Generator());
        modificationRewardsManager.registerModificationReward(new RemoveAllModifiersReward.Generator());
        modificationRewardsManager.registerModificationReward(new TransferModifierReward.Generator());
        modificationRewardsManager.registerModificationReward(new MergeModifiersReward.Generator());
        modificationRewardsManager.registerModificationReward(new Generator());
        BaseMod.registerCustomReward(RewardTypeEnum.CARD_MODIFICATION, modificationRewardsManager::onLoad, modificationRewardsManager::onSave);
        BaseMod.addSaveField(makeID("ModificationRollChance"), modificationRewardsManager);
    }

    @Override
    public void receivePostInitialize() {
        logger.info("Initialization started.");
        configs.setupModPanel(resourceLoader.getModTexture("badge.png"));
        logger.info("- Setup mod panel.");
        registerAugments();
        logger.info("- Registered modifiers.");
        BaseMod.addCustomScreen(new ModificationRewardScreen());
        logger.info("- Registered chimera modification screen.");
        registerRewards();
        logger.info("- Registered chimera modification rewards.");
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
}
