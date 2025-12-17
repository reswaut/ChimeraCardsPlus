package chimeracardsplus.rewards;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.AbstractAugment.AugmentRarity;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomSavable;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus.AugmentBonusLevel;
import chimeracardsplus.helpers.Constants;
import chimeracardsplus.helpers.ModConfigs.ModificationRewardRollMethod;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModificationRewardsGenerator implements CustomSavable<Integer> {
    private static final int DEFAULT_ROLL_CHANCE = 4;
    private static final int ADD_MODIFIER_WEIGHT = 122;
    private static final int REMOVE_MODIFIER_WEIGHT = 27;
    private static final int TRANSFER_MODIFIER_WEIGHT = 73;
    private int rollChance = DEFAULT_ROLL_CHANCE;

    private static AddModifierReward randomAddModifierRewardByCard() {
        if (AbstractDungeon.player.masterDeck.isEmpty()) {
            return null;
        }
        int index = AbstractDungeon.miscRng.random(AbstractDungeon.player.masterDeck.size() - 1);
        AbstractCard card = AbstractDungeon.player.masterDeck.group.get(index);
        AugmentRarity rarity = CardAugmentsMod.rollRarity(card.rarity);

        List<AbstractAugment> validMods;
        switch (rarity) {
            case COMMON:
                validMods = CardAugmentsMod.commonMods.stream().filter(mx -> mx.canApplyTo(card) && CardAugmentsMod.isAugmentEnabled(mx)).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_MODIFIERS)));
                break;
            case UNCOMMON:
                validMods = CardAugmentsMod.uncommonMods.stream().filter(mx -> mx.canApplyTo(card) && CardAugmentsMod.isAugmentEnabled(mx)).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_MODIFIERS)));
                break;
            case RARE:
                validMods = CardAugmentsMod.rareMods.stream().filter(mx -> mx.canApplyTo(card) && CardAugmentsMod.isAugmentEnabled(mx)).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_MODIFIERS)));
                break;
            default:
                return null;
        }
        validMods = AbstractAugmentPlus.filterModsByBonusLevel(validMods, AugmentBonusLevel.NORMAL);
        if (validMods.isEmpty()) {
            return null;
        }
        AbstractAugment mod = (AbstractAugment) validMods.get(AbstractDungeon.miscRng.random(validMods.size() - 1)).makeCopy();
        return new AddModifierReward(index, mod);
    }

    private static AddModifierReward randomAddModifierRewardByModifier() {
        AugmentRarity rarity = CardAugmentsMod.rollRarity(CardRarity.SPECIAL);
        List<AbstractAugment> validMods;
        switch (rarity) {
            case COMMON:
                validMods = CardAugmentsMod.commonMods.stream().filter(CardAugmentsMod::isAugmentEnabled).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_MODIFIERS)));
                break;
            case UNCOMMON:
                validMods = CardAugmentsMod.uncommonMods.stream().filter(CardAugmentsMod::isAugmentEnabled).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_MODIFIERS)));
                break;
            case RARE:
                validMods = CardAugmentsMod.rareMods.stream().filter(CardAugmentsMod::isAugmentEnabled).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_MODIFIERS)));
                break;
            default:
                return null;
        }
        validMods = AbstractAugmentPlus.filterModsByBonusLevel(validMods, AugmentBonusLevel.NORMAL);
        if (validMods.isEmpty()) {
            return null;
        }
        AbstractAugment mod = (AbstractAugment) validMods.get(AbstractDungeon.miscRng.random(validMods.size() - 1)).makeCopy();
        List<AbstractCard> validCards = AbstractDungeon.player.masterDeck.group.stream().filter(mod::canApplyTo).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_CARDS)));
        if (validCards.isEmpty()) {
            return null;
        }
        AbstractCard card = validCards.get(AbstractDungeon.miscRng.random(validCards.size() - 1));
        int index = AbstractDungeon.player.masterDeck.group.indexOf(card);
        return new AddModifierReward(index, mod);
    }

    private static AddModifierReward randomAddModifierReward() {
        return AbstractDungeon.miscRng.randomBoolean() ? randomAddModifierRewardByCard() : randomAddModifierRewardByModifier();
    }

    private static RemoveModifierReward randomRemoveModifierReward() {
        if (AbstractDungeon.player.masterDeck.isEmpty()) {
            return null;
        }
        int index = AbstractDungeon.miscRng.random(AbstractDungeon.player.masterDeck.size() - 1);
        AbstractCard card = AbstractDungeon.player.masterDeck.group.get(index);
        List<AbstractCardModifier> validMods = CardModifierManager.modifiers(card).stream().filter(modifier -> modifier instanceof AbstractAugment).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_CARDS)));
        if (validMods.isEmpty()) {
            return null;
        }
        AbstractAugment mod = (AbstractAugment) validMods.get(AbstractDungeon.miscRng.random(validMods.size() - 1));
        return new RemoveModifierReward(index, mod);
    }

    private static TransferModifierReward randomTransferModifierReward() {
        if (AbstractDungeon.player.masterDeck.size() < 2) {
            return null;
        }
        int fromIndex = AbstractDungeon.miscRng.random(AbstractDungeon.player.masterDeck.size() - 1);
        AbstractCard fromCard = AbstractDungeon.player.masterDeck.group.get(fromIndex);
        List<AbstractCardModifier> validMods = CardModifierManager.modifiers(fromCard).stream().filter(modifier -> modifier instanceof AbstractAugment).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_CARDS)));
        if (validMods.isEmpty()) {
            return null;
        }
        AbstractAugment fromMod = (AbstractAugment) validMods.get(AbstractDungeon.miscRng.random(validMods.size() - 1));
        AbstractAugment toMod = (AbstractAugment) fromMod.makeCopy();
        List<AbstractCard> validCards = AbstractDungeon.player.masterDeck.group.stream().filter(toMod::canApplyTo).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_CARDS)));
        if (validCards.isEmpty()) {
            return null;
        }
        AbstractCard card = validCards.get(AbstractDungeon.miscRng.random(validCards.size() - 1));
        int toIndex = AbstractDungeon.player.masterDeck.group.indexOf(card);
        if (fromIndex == toIndex) {
            return null;
        }
        return new TransferModifierReward(fromIndex, toIndex, fromMod);
    }

    private AbstractModificationReward randomChimeraModificationReward() {
        int roll = AbstractDungeon.miscRng.random(ADD_MODIFIER_WEIGHT + REMOVE_MODIFIER_WEIGHT + TRANSFER_MODIFIER_WEIGHT - 1);
        AbstractModificationReward reward;
        if (roll < ADD_MODIFIER_WEIGHT) {
            reward = randomAddModifierReward();
        } else if (roll < ADD_MODIFIER_WEIGHT + REMOVE_MODIFIER_WEIGHT) {
            reward = randomRemoveModifierReward();
        } else {
            reward = randomTransferModifierReward();
        }
        if (reward == null && ChimeraCardsPlus.configs.getRollMethod() == ModificationRewardRollMethod.DYNAMIC) {
            rollChance += 1;
        }
        return reward;
    }

    private boolean rollModification() {
        switch (ChimeraCardsPlus.configs.getRollMethod()) {
            case NEVER:
                return false;
            case ALWAYS:
                return true;
            default:
                break;
        }
        boolean success = AbstractDungeon.miscRng.random(9) < rollChance;
        if (success) {
            rollChance -= 1;
        } else {
            rollChance += 1;
        }
        return success;
    }

    public void addModificationRewards(CombatRewardScreen rewardScreen) {
        if (ChimeraCardsPlus.configs.enableLinkModifierReward()) {
            int rewardListSize = rewardScreen.rewards.size();
            for (int i = 0; i < rewardListSize; ++i) {
                RewardItem item = rewardScreen.rewards.get(i);
                if (item.type != RewardType.CARD || item.relicLink != null) {
                    continue;
                }
                if (!rollModification()) {
                    continue;
                }
                AbstractModificationReward reward = randomChimeraModificationReward();
                if (reward != null) {
                    ++i;
                    ++rewardListSize;
                    rewardScreen.rewards.add(i, reward);
                    item.relicLink = reward;
                    reward.relicLink = item;
                }
            }
        } else {
            if (!rollModification()) {
                return;
            }
            AbstractModificationReward reward = randomChimeraModificationReward();
            if (reward != null) {
                rewardScreen.rewards.add(reward);
            }
        }
    }

    @Override
    public Integer onSave() {
        return rollChance;
    }

    @Override
    public void onLoad(Integer integer) {
        if (integer == null) {
            rollChance = DEFAULT_ROLL_CHANCE;
        } else {
            rollChance = integer;
        }
    }

    public static class RewardTypeEnum {
        @SpireEnum
        public static RewardType ADD_MODIFIER;
        @SpireEnum
        public static RewardType REMOVE_MODIFIER;
        @SpireEnum
        public static RewardType TRANSFER_MODIFIER;
    }
}
