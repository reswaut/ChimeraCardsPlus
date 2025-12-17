package chimeracardsplus.rewards;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.CustomReward;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.rewards.ModificationRewardsGenerator.RewardTypeEnum;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardSave;

public class TransferModifierReward extends AbstractModificationReward {
    private AbstractCard fromCard, toCard;
    private AbstractAugment fromModifier, toModifier = null;

    public TransferModifierReward(int fromIndex, int toIndex, AbstractAugment fromModifier) {
        super(RewardTypeEnum.TRANSFER_MODIFIER);
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

    public static TransferModifierReward onLoad(RewardSave rewardSave) {
        SaveType save;
        try {
            save = gson.fromJson(rewardSave.id, SaveType.class);
        } catch (JsonSyntaxException e) {
            ChimeraCardsPlus.logger.error("Unable to load reward save: {}", rewardSave);
            return null;
        }
        return new TransferModifierReward(save.fromIndex, save.toIndex, modifierFromJson(save.fromModifier));
    }

    public static RewardSave onSave(CustomReward customReward) {
        if (!(customReward instanceof TransferModifierReward)) {
            ChimeraCardsPlus.logger.error("Illegal save reward: {}", customReward);
            return null;
        }
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
        return new RewardSave(reward.type.toString(), gson.toJson(new SaveType(fromIndex, toIndex, modifierToJsonTree(reward.fromModifier))));
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
            }
        }
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
