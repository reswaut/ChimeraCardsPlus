package chimeracardsplus.cardmods;

import CardAugments.cardmods.AbstractAugment;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
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

    public static boolean isCardRemovable(AbstractCard card, boolean checkBottle) {
        CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);
        group.group.add(card);
        CardGroup retGroup = group.getPurgeableCards();
        return !(checkBottle ? CardGroup.getGroupWithoutBottledCards(retGroup) : retGroup).isEmpty();
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

    public boolean onUsePotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
        return false;
    }

    public boolean preDiscardPotion(AbstractCard card, CardGroup group, AbstractPotion potion) {
        return false;
    }

    public boolean onShuffle(AbstractCard card, CardGroup group) {
        return false;
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
