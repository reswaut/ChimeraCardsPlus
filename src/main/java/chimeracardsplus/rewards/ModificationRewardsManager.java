package chimeracardsplus.rewards;

import basemod.abstracts.CustomReward;
import basemod.abstracts.CustomSavable;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.helpers.Constants;
import chimeracardsplus.helpers.ModConfigs.ModificationRewardRollMethod;
import chimeracardsplus.rewards.AbstractModificationReward.AbstractRewardGenerator;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.ArrayList;
import java.util.List;

public class ModificationRewardsManager implements CustomSavable<Integer> {
    private static final int DEFAULT_ROLL_CHANCE = 4;
    private final List<AbstractRewardGenerator<? extends AbstractModificationReward>> generators = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
    private int rollChance = DEFAULT_ROLL_CHANCE;

    private static boolean rewardBottleCheck(AbstractModificationReward reward) {
        return reward.sourceCards.stream().allMatch(card -> AbstractAugmentPlus.isCardRemovable(card, true));
    }

    public void registerModificationReward(AbstractRewardGenerator<? extends AbstractModificationReward> generator) {
        generators.add(generator);
    }

    private AbstractModificationReward randomChimeraModificationReward() {
        long totalWeight = generators.stream().mapToLong(AbstractRewardGenerator::getGlobalWeight).sum();
        if (totalWeight <= 0) {
            return null;
        }
        long roll = AbstractDungeon.miscRng.random(totalWeight - 1);
        for (AbstractRewardGenerator<? extends AbstractModificationReward> generator : generators) {
            roll -= generator.getGlobalWeight();
            if (roll < 0) {
                AbstractModificationReward reward = generator.generate();
                if (reward != null && rewardBottleCheck(reward)) {
                    return reward;
                }
            }
        }
        if (ChimeraCardsPlus.configs.getRollMethod() == ModificationRewardRollMethod.DYNAMIC) {
            rollChance += 1;
        }
        return null;
    }

    private boolean failedRollModification() {
        switch (ChimeraCardsPlus.configs.getRollMethod()) {
            case NEVER:
                return true;
            case ALWAYS:
                return false;
            default:
                break;
        }
        boolean fail = AbstractDungeon.miscRng.random(9) >= rollChance;
        if (fail) {
            rollChance += 1;
        } else {
            rollChance -= 1;
        }
        return fail;
    }

    public void addRewardToRewardScreen(CombatRewardScreen rewardScreen) {
        if (ChimeraCardsPlus.configs.enableLinkModifierReward()) {
            int rewardListSize = rewardScreen.rewards.size();
            for (int i = 0; i < rewardListSize; ++i) {
                RewardItem item = rewardScreen.rewards.get(i);
                if (item.type != RewardType.CARD || item.relicLink != null) {
                    continue;
                }
                if (failedRollModification()) {
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
            if (failedRollModification()) {
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

    public AbstractModificationReward onLoad(RewardSave rewardSave) {
        if (rewardSave.amount >= generators.size()) {
            ChimeraCardsPlus.logger.error("Unable to load reward with type {} >= total types {}", rewardSave.amount, generators.size());
            return null;
        }
        return generators.get(rewardSave.amount).onLoad(rewardSave);
    }

    public RewardSave onSave(CustomReward customReward) {
        if (!(customReward instanceof AbstractModificationReward)) {
            ChimeraCardsPlus.logger.error("Illegal save reward: {}", customReward);
            return null;
        }
        for (int i = generators.size() - 1; i >= 0; --i) {
            AbstractRewardGenerator<? extends AbstractModificationReward> generator = generators.get(i);
            if (generator.getRewardClass() == customReward.getClass()) {
                return generator.onSave((AbstractModificationReward) customReward, i);
            }
        }
        ChimeraCardsPlus.logger.error("Unable to determine reward type of {}", customReward);
        return null;
    }

    public static class RewardTypeEnum {
        @SpireEnum
        public static RewardType CARD_MODIFICATION;
    }
}
