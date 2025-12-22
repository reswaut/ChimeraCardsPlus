package chimeracardsplus.rewards;

import CardAugments.cardmods.AbstractAugment;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.rewards.ModificationRewardsManager.RewardTypeEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardSave;

public class RemoveAllModifiersReward extends AbstractModificationReward {
    private AbstractCard card;

    public RemoveAllModifiersReward(int index) {
        super(RewardTypeEnum.CARD_MODIFICATION);
        card = AbstractDungeon.player.masterDeck.group.get(index);
        sourceCards.add(card);
        resultCards.add(AbstractAugmentPlus.safeCopyEquivalentCardWithoutAugments(card));
    }

    @Override
    public void replaceCard(AbstractCard oldCard, AbstractCard newCard) {
        if (!card.equals(oldCard)) {
            return;
        }
        if (CardModifierManager.modifiers(newCard).stream().anyMatch(modifier -> modifier instanceof AbstractAugment)) {
            card = newCard;
            sourceCards.set(0, newCard);
            resultCards.set(0, AbstractAugmentPlus.safeCopyEquivalentCardWithoutAugments(newCard));
        } else {
            sourceCards.clear();
            resultCards.clear();
        }
    }

    public static class Generator implements AbstractRewardGenerator<RemoveAllModifiersReward> {
        @Override
        public long getGlobalWeight() {
            return 27;
        }

        @Override
        public RemoveAllModifiersReward generate() {
            if (AbstractDungeon.player.masterDeck.isEmpty()) {
                return null;
            }
            int index = AbstractDungeon.miscRng.random(AbstractDungeon.player.masterDeck.size() - 1);
            AbstractCard card = AbstractDungeon.player.masterDeck.group.get(index);
            if (CardModifierManager.modifiers(card).stream().noneMatch(modifier -> modifier instanceof AbstractAugment)) {
                return null;
            }
            return new RemoveAllModifiersReward(index);
        }

        @Override
        public RemoveAllModifiersReward onLoad(RewardSave rewardSave) {
            Integer save = loadSaveFromReward(rewardSave, Integer.class);
            if (save == null) {
                return null;
            }
            return new RemoveAllModifiersReward(save);
        }

        @Override
        public RewardSave onSave(AbstractModificationReward customReward, int amount) {
            RemoveAllModifiersReward reward = (RemoveAllModifiersReward) customReward;
            int index = AbstractDungeon.player.masterDeck.group.indexOf(reward.card);
            if (index == -1) {
                ChimeraCardsPlus.logger.error("Failed to find card {} in master deck.", reward.card);
                return null;
            }
            return new RewardSave(reward.type.toString(), gson.toJson(index), amount, 0);
        }

        @Override
        public Class<RemoveAllModifiersReward> getRewardClass() {
            return RemoveAllModifiersReward.class;
        }
    }
}
