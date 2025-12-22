package chimeracardsplus.rewards;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.rewards.ModificationRewardsManager.RewardTypeEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardSave;

public class MergeModifiersReward extends AbstractModificationReward {
    private AbstractCard card1, card2;

    public MergeModifiersReward(int index1, int index2) {
        super(RewardTypeEnum.CARD_MODIFICATION);
        card1 = AbstractDungeon.player.masterDeck.group.get(index1);
        card2 = AbstractDungeon.player.masterDeck.group.get(index2);

        AbstractCard resultCard1 = card1.makeStatEquivalentCopy();
        for (AbstractCardModifier modifier : CardModifierManager.modifiers(card2)) {
            AbstractCardModifier modifier1 = modifier.makeCopy();
            if (modifier1 instanceof AbstractAugment && ((AbstractAugment) modifier1).canApplyTo(resultCard1)) {
                CardModifierManager.addModifier(resultCard1, modifier1);
            }
        }

        AbstractCard resultCard2 = card2.makeStatEquivalentCopy();
        for (AbstractCardModifier modifier : CardModifierManager.modifiers(card1)) {
            AbstractCardModifier modifier2 = modifier.makeCopy();
            if (modifier2 instanceof AbstractAugment && ((AbstractAugment) modifier2).canApplyTo(resultCard2)) {
                CardModifierManager.addModifier(resultCard2, modifier2);
            }
        }

        sourceCards.add(card1);
        sourceCards.add(card2);
        resultCards.add(resultCard1);
        resultCards.add(resultCard2);
    }

    @Override
    public void replaceCard(AbstractCard oldCard, AbstractCard newCard) {
        boolean skip = true;
        if (card1.equals(oldCard)) {
            card1 = newCard;
            sourceCards.set(0, card1);
            skip = false;
        } else if (card2.equals(oldCard)) {
            card2 = newCard;
            sourceCards.set(1, card2);
            skip = false;
        }
        if (skip) {
            return;
        }

        boolean unchanged1 = true;
        AbstractCard resultCard1 = card1.makeStatEquivalentCopy();
        for (AbstractCardModifier modifier : CardModifierManager.modifiers(card2)) {
            AbstractCardModifier modifier1 = modifier.makeCopy();
            if (modifier1 instanceof AbstractAugment && ((AbstractAugment) modifier1).canApplyTo(resultCard1)) {
                CardModifierManager.addModifier(resultCard1, modifier1);
                unchanged1 = false;
            }
        }
        if (unchanged1) {
            sourceCards.clear();
            resultCards.clear();
            return;
        }

        boolean unchanged2 = true;
        AbstractCard resultCard2 = card2.makeStatEquivalentCopy();
        for (AbstractCardModifier modifier : CardModifierManager.modifiers(card1)) {
            AbstractCardModifier modifier2 = modifier.makeCopy();
            if (modifier2 instanceof AbstractAugment && ((AbstractAugment) modifier2).canApplyTo(resultCard2)) {
                CardModifierManager.addModifier(resultCard2, modifier2);
                unchanged2 = false;
            }
        }
        if (unchanged2) {
            sourceCards.clear();
            resultCards.clear();
            return;
        }

        resultCards.set(0, resultCard1);
        resultCards.set(1, resultCard2);
    }

    public static class Generator implements AbstractRewardGenerator<MergeModifiersReward> {
        @Override
        public long getGlobalWeight() {
            return 73;
        }

        @Override
        public MergeModifiersReward generate() {
            if (AbstractDungeon.player.masterDeck.size() < 2) {
                return null;
            }
            int index1 = AbstractDungeon.miscRng.random(AbstractDungeon.player.masterDeck.size() - 1);
            int index2 = AbstractDungeon.miscRng.random(AbstractDungeon.player.masterDeck.size() - 2);
            if (index2 >= index1) {
                index2 += 1;
            }
            MergeModifiersReward reward = new MergeModifiersReward(index1, index2);

            AbstractCard card1 = AbstractDungeon.player.masterDeck.group.get(index1);
            reward.replaceCard(card1, card1);
            if (reward.isInvalid()) {
                return null;
            }
            return reward;
        }

        @Override
        public MergeModifiersReward onLoad(RewardSave rewardSave) {
            SaveType save = loadSaveFromReward(rewardSave, SaveType.class);
            if (save == null) {
                return null;
            }
            return new MergeModifiersReward(save.index1, save.index2);
        }

        @Override
        public RewardSave onSave(AbstractModificationReward customReward, int amount) {
            MergeModifiersReward reward = (MergeModifiersReward) customReward;
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
            return new RewardSave(reward.type.toString(), gson.toJson(new SaveType(index1, index2)), amount, 0);
        }

        @Override
        public Class<MergeModifiersReward> getRewardClass() {
            return MergeModifiersReward.class;
        }

        public static class SaveType {
            public int index1, index2;

            public SaveType(int index1, int index2) {
                this.index1 = index1;
                this.index2 = index2;
            }
        }
    }
}
