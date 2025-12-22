package chimeracardsplus.rewards;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.AbstractAugment.AugmentRarity;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus.AugmentBonusLevel;
import chimeracardsplus.helpers.Constants;
import chimeracardsplus.rewards.ModificationRewardsManager.RewardTypeEnum;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardSave;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddModifierReward extends AbstractModificationReward {
    private AbstractCard card;
    private AbstractAugment modifier;

    public AddModifierReward(int index, AbstractAugment modifier) {
        super(RewardTypeEnum.CARD_MODIFICATION);
        card = AbstractDungeon.player.masterDeck.group.get(index);
        this.modifier = modifier;

        if (modifier != null) {
            AbstractCard resultCard = card.makeStatEquivalentCopy();
            if (modifier.canApplyTo(resultCard)) {
                CardModifierManager.addModifier(resultCard, modifier);
                sourceCards.add(card);
                resultCards.add(resultCard);
            }
        }
    }

    @Override
    public void replaceCard(AbstractCard oldCard, AbstractCard newCard) {
        if (!card.equals(oldCard)) {
            return;
        }
        AbstractCard resultCard = newCard.makeStatEquivalentCopy();
        AbstractAugment newModifier = (AbstractAugment) modifier.makeCopy();
        if (newModifier != null && newModifier.canApplyTo(resultCard)) {
            card = newCard;
            modifier = newModifier;
            CardModifierManager.addModifier(resultCard, newModifier);
            sourceCards.set(0, newCard);
            resultCards.set(0, resultCard);
        } else {
            sourceCards.clear();
            resultCards.clear();
        }
    }

    public static class Generator implements AbstractRewardGenerator<AddModifierReward> {
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

        @Override
        public long getGlobalWeight() {
            return 122;
        }

        @Override
        public AddModifierReward generate() {
            return AbstractDungeon.miscRng.randomBoolean() ? randomAddModifierRewardByCard() : randomAddModifierRewardByModifier();
        }

        @Override
        public AddModifierReward onLoad(RewardSave rewardSave) {
            SaveType save = loadSaveFromReward(rewardSave, SaveType.class);
            if (save == null) {
                return null;
            }
            return new AddModifierReward(save.index, modifierFromJson(save.modifier));
        }

        @Override
        public RewardSave onSave(AbstractModificationReward customReward, int amount) {
            AddModifierReward reward = (AddModifierReward) customReward;
            int index = AbstractDungeon.player.masterDeck.group.indexOf(reward.card);
            if (index == -1) {
                ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", reward.card);
                return null;
            }
            return new RewardSave(reward.type.toString(), gson.toJson(new SaveType(index, modifierToJsonTree(reward.modifier))), amount, 0);
        }

        @Override
        public Class<AddModifierReward> getRewardClass() {
            return AddModifierReward.class;
        }

        public static class SaveType {
            public int index;
            public JsonElement modifier;

            public SaveType(int index, JsonElement modifier) {
                this.index = index;
                this.modifier = modifier;
            }
        }
    }
}
