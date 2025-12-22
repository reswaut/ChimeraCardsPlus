package chimeracardsplus.rewards;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.helpers.Constants;
import chimeracardsplus.rewards.ModificationRewardsManager.RewardTypeEnum;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardSave;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransferModifierReward extends AbstractModificationReward {
    private AbstractCard fromCard, toCard;
    private AbstractAugment fromModifier, toModifier = null;

    public TransferModifierReward(int fromIndex, int toIndex, AbstractAugment fromModifier) {
        super(RewardTypeEnum.CARD_MODIFICATION);
        if (fromIndex == toIndex) {
            return;
        }
        fromCard = AbstractDungeon.player.masterDeck.group.get(fromIndex);
        toCard = AbstractDungeon.player.masterDeck.group.get(toIndex);
        this.fromModifier = fromModifier;
        if (fromModifier != null && CardModifierManager.modifiers(fromCard).contains(fromModifier)) {
            toModifier = (AbstractAugment) fromModifier.makeCopy();
            AbstractCard resultToCard = toCard.makeStatEquivalentCopy();
            if (toModifier.canApplyTo(resultToCard)) {
                sourceCards.add(fromCard);
                resultCards.add(AbstractAugmentPlus.safeCopyEquivalentCardWithoutModifier(fromCard, fromModifier));
                CardModifierManager.addModifier(resultToCard, toModifier);
                sourceCards.add(toCard);
                resultCards.add(resultToCard);
            }
        }
    }

    @Override
    public void replaceCard(AbstractCard oldCard, AbstractCard newCard) {
        if (fromCard.equals(oldCard)) {
            if (fromModifier != null && CardModifierManager.hasModifier(newCard, fromModifier.identifier(oldCard))) {
                fromCard = newCard;
                fromModifier = (AbstractAugment) CardModifierManager.getModifiers(newCard, fromModifier.identifier(oldCard)).get(0);
                sourceCards.set(0, newCard);
                resultCards.set(0, AbstractAugmentPlus.safeCopyEquivalentCardWithoutModifier(newCard, fromModifier));
            } else {
                sourceCards.clear();
                resultCards.clear();
            }
        } else if (toCard.equals(oldCard)) {
            AbstractCard resultCard = newCard.makeStatEquivalentCopy();
            AbstractAugment newModifier = (AbstractAugment) toModifier.makeCopy();
            if (newModifier != null && newModifier.canApplyTo(resultCard)) {
                toCard = newCard;
                toModifier = newModifier;
                CardModifierManager.addModifier(resultCard, newModifier);
                sourceCards.set(1, newCard);
                resultCards.set(1, resultCard);
            } else {
                sourceCards.clear();
                resultCards.clear();
            }
        }
    }

    public static class Generator implements AbstractRewardGenerator<TransferModifierReward> {
        @Override
        public long getGlobalWeight() {
            return 73;
        }

        @Override
        public TransferModifierReward generate() {
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

        @Override
        public TransferModifierReward onLoad(RewardSave rewardSave) {
            SaveType save = loadSaveFromReward(rewardSave, SaveType.class);
            if (save == null) {
                return null;
            }
            return new TransferModifierReward(save.fromIndex, save.toIndex, modifierFromJson(save.fromModifier));
        }

        @Override
        public RewardSave onSave(AbstractModificationReward customReward, int amount) {
            TransferModifierReward reward = (TransferModifierReward) customReward;
            int fromIndex = AbstractDungeon.player.masterDeck.group.indexOf(reward.fromCard);
            if (fromIndex == -1) {
                ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", reward.fromCard);
                return null;
            }
            int toIndex = AbstractDungeon.player.masterDeck.group.indexOf(reward.toCard);
            if (toIndex == -1) {
                ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", reward.toCard);
                return null;
            }
            return new RewardSave(reward.type.toString(), gson.toJson(new SaveType(fromIndex, toIndex, modifierToJsonTree(reward.fromModifier))), amount, 0);
        }

        @Override
        public Class<TransferModifierReward> getRewardClass() {
            return TransferModifierReward.class;
        }

        public static class SaveType {
            public int fromIndex, toIndex;
            public JsonElement fromModifier;

            public SaveType(int fromIndex, int toIndex, JsonElement fromModifier) {
                this.fromIndex = fromIndex;
                this.toIndex = toIndex;
                this.fromModifier = fromModifier;
            }
        }
    }
}
