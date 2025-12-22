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

public class RemoveModifierReward extends AbstractModificationReward {
    private AbstractCard card;
    private AbstractAugment modifier;

    public RemoveModifierReward(int index, AbstractAugment modifier) {
        super(RewardTypeEnum.CARD_MODIFICATION);
        card = AbstractDungeon.player.masterDeck.group.get(index);
        this.modifier = modifier;

        if (modifier != null && CardModifierManager.modifiers(card).contains(modifier)) {
            sourceCards.add(card);
            resultCards.add(AbstractAugmentPlus.safeCopyEquivalentCardWithoutModifier(card, modifier));
        }
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
            resultCards.clear();
        }
    }

    public static class Generator implements AbstractRewardGenerator<RemoveModifierReward> {
        @Override
        public long getGlobalWeight() {
            return 27;
        }

        @Override
        public RemoveModifierReward generate() {
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

        @Override
        public RemoveModifierReward onLoad(RewardSave rewardSave) {
            SaveType save = loadSaveFromReward(rewardSave, SaveType.class);
            if (save == null) {
                return null;
            }
            return new RemoveModifierReward(save.index, modifierFromJson(save.modifier));
        }

        @Override
        public RewardSave onSave(AbstractModificationReward customReward, int amount) {
            RemoveModifierReward reward = (RemoveModifierReward) customReward;
            int index = AbstractDungeon.player.masterDeck.group.indexOf(reward.card);
            if (index == -1) {
                ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", reward.card);
                return null;
            }
            return new RewardSave(reward.type.toString(), gson.toJson(new SaveType(index, modifierToJsonTree(reward.modifier))), amount, 0);
        }

        @Override
        public Class<RemoveModifierReward> getRewardClass() {
            return RemoveModifierReward.class;
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
