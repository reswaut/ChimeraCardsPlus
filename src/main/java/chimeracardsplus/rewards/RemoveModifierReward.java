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

public class RemoveModifierReward extends AbstractModificationReward {
    private AbstractCard card;
    private AbstractAugment modifier;

    public RemoveModifierReward(int index, AbstractAugment modifier) {
        super(RewardTypeEnum.REMOVE_MODIFIER);
        card = AbstractDungeon.player.masterDeck.group.get(index);
        this.modifier = modifier;

        if (modifier != null && CardModifierManager.modifiers(card).contains(modifier)) {
            sourceCards.add(card);
            resultCards.add(AbstractAugmentPlus.safeCopyEquivalentCardWithoutModifier(card, modifier));
        }
    }

    public static RemoveModifierReward onLoad(RewardSave rewardSave) {
        SaveType save;
        try {
            save = gson.fromJson(rewardSave.id, SaveType.class);
        } catch (JsonSyntaxException e) {
            ChimeraCardsPlus.logger.error("Unable to load reward save: {}", rewardSave);
            return null;
        }
        return new RemoveModifierReward(save.index, modifierFromJson(save.modifier));
    }

    public static RewardSave onSave(CustomReward customReward) {
        if (!(customReward instanceof RemoveModifierReward)) {
            ChimeraCardsPlus.logger.error("Illegal save reward: {}", customReward);
            return null;
        }
        RemoveModifierReward reward = (RemoveModifierReward) customReward;
        int index = AbstractDungeon.player.masterDeck.group.indexOf(reward.card);
        if (index == -1) {
            ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", reward.card);
            return null;
        }
        return new RewardSave(reward.type.toString(), gson.toJson(new SaveType(index, modifierToJsonTree(reward.modifier))));
    }

    @Override
    public void replaceCard(AbstractCard oldCard, AbstractCard newCard) {
        if (!card.equals(oldCard)) {
            return;
        }
        if (modifier != null && CardModifierManager.hasModifier(newCard, modifier.identifier(oldCard))) {
            card = newCard;
            modifier = (AbstractAugment) CardModifierManager.getModifiers(newCard, modifier.identifier(oldCard)).get(0);
            sourceCards.set(0, newCard);
            resultCards.set(0, AbstractAugmentPlus.safeCopyEquivalentCardWithoutModifier(newCard, modifier));
        } else {
            sourceCards.clear();
        }
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
