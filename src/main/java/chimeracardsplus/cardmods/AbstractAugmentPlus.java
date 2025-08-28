package chimeracardsplus.cardmods;

import CardAugments.cardmods.AbstractAugment;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAugmentPlus extends AbstractAugment {
    public static List<AbstractAugment> filterModsByBonusLevel(List<AbstractAugment> augments, AugmentBonusLevel filterLevel) {
        if (filterLevel == AugmentBonusLevel.BONUS) {
            return augments;
        }
        List<AbstractAugment> ret = new ArrayList<>(augments.size());
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

    public static boolean hasCardWithKeywordInDeck(AbstractPlayer p, CharSequence keyword) {
        return p.masterDeck.group.stream().anyMatch(card -> StringUtils.containsIgnoreCase(card.rawDescription, keyword));
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
