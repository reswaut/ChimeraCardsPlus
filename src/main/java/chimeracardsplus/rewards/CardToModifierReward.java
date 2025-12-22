package chimeracardsplus.rewards;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.helpers.Constants;
import chimeracardsplus.rewards.ModificationRewardsManager.RewardTypeEnum;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardSave;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardToModifierReward extends AbstractModificationReward {
    private AbstractCard card1, card2;
    private AbstractCard baseCard;
    private AbstractAugment modifier1, modifier2;

    public CardToModifierReward(int index1, int index2, int baseIndex, AbstractAugment modifier1, AbstractAugment modifier2) {
        super(RewardTypeEnum.CARD_MODIFICATION);

        card1 = AbstractDungeon.player.masterDeck.group.get(index1);
        this.modifier1 = modifier1;
        if (modifier1 != null) {
            AbstractCard resultCard = card1.makeStatEquivalentCopy();
            if (modifier1.canApplyTo(resultCard)) {
                CardModifierManager.addModifier(resultCard, modifier1);
                sourceCards.add(card1);
                resultCards.add(resultCard);
            }
        }

        card2 = AbstractDungeon.player.masterDeck.group.get(index2);
        this.modifier2 = modifier2;
        if (modifier2 != null) {
            AbstractCard resultCard = card2.makeStatEquivalentCopy();
            if (modifier2.canApplyTo(resultCard)) {
                CardModifierManager.addModifier(resultCard, modifier2);
                sourceCards.add(card2);
                resultCards.add(resultCard);
            }
        }

        baseCard = AbstractDungeon.player.masterDeck.group.get(baseIndex);
        sourceCards.add(baseCard);
    }

    @Override
    public void replaceCard(AbstractCard oldCard, AbstractCard newCard) {
        if (baseCard.equals(oldCard)) {
            if (oldCard.cardID.equals(newCard.cardID)) {
                baseCard = newCard;
                sourceCards.set(2, newCard);
            } else {
                sourceCards.clear();
                resultCards.clear();
            }
            return;
        }
        if (card1.equals(oldCard)) {
            AbstractCard resultCard = newCard.makeStatEquivalentCopy();
            AbstractAugment newModifier = (AbstractAugment) modifier1.makeCopy();
            if (newModifier != null && newModifier.canApplyTo(resultCard)) {
                card1 = newCard;
                modifier1 = newModifier;
                CardModifierManager.addModifier(resultCard, newModifier);
                sourceCards.set(0, newCard);
                resultCards.set(0, resultCard);
            } else {
                sourceCards.clear();
                resultCards.clear();
            }
        } else if (card2.equals(oldCard)) {
            AbstractCard resultCard = newCard.makeStatEquivalentCopy();
            AbstractAugment newModifier = (AbstractAugment) modifier2.makeCopy();
            if (newModifier != null && newModifier.canApplyTo(resultCard)) {
                card2 = newCard;
                modifier2 = newModifier;
                CardModifierManager.addModifier(resultCard, newModifier);
                sourceCards.set(1, newCard);
                resultCards.set(1, resultCard);
            } else {
                sourceCards.clear();
                resultCards.clear();
            }
        }
    }

    public static class Generator implements AbstractRewardGenerator<CardToModifierReward> {
        @Override
        public long getGlobalWeight() {
            return 122;
        }

        @Override
        public CardToModifierReward generate() {
            if (AbstractDungeon.player.masterDeck.size() < 3) {
                return null;
            }
            int baseIndex = AbstractDungeon.miscRng.random(AbstractDungeon.player.masterDeck.size() - 1);
            AbstractCard baseCard = AbstractDungeon.player.masterDeck.group.get(baseIndex);
            List<String> validMods = cardActionAnalyzer.getAssociatedModifiers(baseCard.cardID);
            if (validMods == null || validMods.isEmpty()) {
                return null;
            }
            AbstractAugment mod1 = (AbstractAugment) CardAugmentsMod.modMap.get(validMods.get(AbstractDungeon.miscRng.random(validMods.size() - 1))).makeCopy();
            List<AbstractCard> validCards1 = AbstractDungeon.player.masterDeck.group.stream().filter(c -> mod1.canApplyTo(c) && !c.equals(baseCard)).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_CARDS)));
            if (validCards1.isEmpty()) {
                return null;
            }
            int index1 = AbstractDungeon.player.masterDeck.group.indexOf(validCards1.get(AbstractDungeon.miscRng.random(validCards1.size() - 1)));

            AbstractAugment mod2 = (AbstractAugment) CardAugmentsMod.modMap.get(validMods.get(AbstractDungeon.miscRng.random(validMods.size() - 1))).makeCopy();
            List<AbstractCard> validCards2 = AbstractDungeon.player.masterDeck.group.stream().filter(c -> mod2.canApplyTo(c) && !c.equals(baseCard)).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.EXPECTED_CARDS)));
            if (validCards2.isEmpty()) {
                return null;
            }
            int index2 = AbstractDungeon.player.masterDeck.group.indexOf(validCards2.get(AbstractDungeon.miscRng.random(validCards2.size() - 1)));

            if (index1 == index2) {
                return null;
            }

            return new CardToModifierReward(index1, index2, baseIndex, mod1, mod2);
        }

        @Override
        public CardToModifierReward onLoad(RewardSave rewardSave) {
            SaveType save = loadSaveFromReward(rewardSave, SaveType.class);
            if (save == null) {
                return null;
            }
            return new CardToModifierReward(save.index1, save.index2, save.baseIndex, modifierFromJson(save.modifier1), modifierFromJson(save.modifier2));
        }

        @Override
        public RewardSave onSave(AbstractModificationReward customReward, int amount) {
            CardToModifierReward reward = (CardToModifierReward) customReward;
            int index1 = AbstractDungeon.player.masterDeck.group.indexOf(reward.card1);
            if (index1 == -1) {
                ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", reward.card1);
                return null;
            }
            int index2 = AbstractDungeon.player.masterDeck.group.indexOf(reward.card2);
            if (index2 == -1) {
                ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", reward.card2);
                return null;
            }
            int baseIndex = AbstractDungeon.player.masterDeck.group.indexOf(reward.baseCard);
            if (baseIndex == -1) {
                ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", reward.baseCard);
                return null;
            }
            return new RewardSave(reward.type.toString(), gson.toJson(new SaveType(index1, index2, baseIndex, modifierToJsonTree(reward.modifier1), modifierToJsonTree(reward.modifier2))), amount, 0);
        }

        @Override
        public Class<CardToModifierReward> getRewardClass() {
            return CardToModifierReward.class;
        }

        public static class SaveType {
            public int index1, index2, baseIndex;
            public JsonElement modifier1, modifier2;

            public SaveType(int index1, int index2, int baseIndex, JsonElement modifier1, JsonElement modifier2) {
                this.index1 = index1;
                this.index2 = index2;
                this.baseIndex = baseIndex;
                this.modifier1 = modifier1;
                this.modifier2 = modifier2;
            }
        }
    }
}
