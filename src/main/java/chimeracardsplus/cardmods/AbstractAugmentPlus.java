package chimeracardsplus.cardmods;

import CardAugments.cardmods.AbstractAugment;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import java.util.ArrayList;

public abstract class AbstractAugmentPlus extends AbstractAugment {
    public static ArrayList<AbstractAugment> filterModsByBonusLevel(ArrayList<AbstractAugment> augments, AugmentBonusLevel filterLevel) {
        if (filterLevel == AugmentBonusLevel.BONUS) {
            return augments;
        }
        ArrayList<AbstractAugment> ret = new ArrayList<>(augments.size());
        for (AbstractAugment augment : augments) {
            if (!(augment instanceof AbstractAugmentPlus)) {
                ret.add(augment);
                continue;
            }
            AbstractAugmentPlus augmentPlus = (AbstractAugmentPlus) augment;
            AugmentBonusLevel level = augmentPlus.getModBonusLevel();
            if (level == AugmentBonusLevel.NORMAL || level == AugmentBonusLevel.HEALING && filterLevel == AugmentBonusLevel.HEALING) {
                ret.add(augment);
            }
        }
        return ret;
    }

    public abstract AugmentBonusLevel getModBonusLevel();

    public void onManualDiscard(AbstractCard card) {
    }

    public void onMoveToDiscard(AbstractCard card) {
    }

    public boolean onObtain(AbstractCard card) {
        return false;
    }

    public void onRemoveFromMasterDeck(AbstractCard card) {
    }

    public boolean onRoomUpdateObjects(AbstractCard card) {
        return false;
    }

    public void onUsePotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
    }

    public void onShuffle(AbstractCard card, CardGroup group) {
    }

    public boolean preDeath(AbstractCard card) {
        return false;
    }

    public enum AugmentBonusLevel {
        NORMAL,
        HEALING,
        BONUS
    }
}
