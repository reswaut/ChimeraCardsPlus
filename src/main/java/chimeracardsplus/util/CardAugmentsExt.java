package chimeracardsplus.util;

import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.BranchingUpgradesCard;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.MultiUpgradeCard;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class CardAugmentsExt {
    public static boolean doesntDowngradeMagicNoUseChecks(AbstractCard card) {
        AbstractCard baseCheck = card.makeCopy();
        ArrayList<AbstractCard> cardsToCheck = new ArrayList<>();
        cardsToCheck.add(baseCheck);
        AbstractCard upgradeCheck;
        AbstractCard upgradeTest;
        if (card instanceof BranchingUpgradesCard) {
            upgradeCheck = card.makeCopy();
            ((BranchingUpgradesCard)upgradeCheck).setUpgradeType(BranchingUpgradesCard.UpgradeType.NORMAL_UPGRADE);
            upgradeCheck.upgrade();
            cardsToCheck.add(upgradeCheck);
            upgradeTest = card.makeCopy();
            ((BranchingUpgradesCard)upgradeTest).setUpgradeType(BranchingUpgradesCard.UpgradeType.BRANCH_UPGRADE);
            upgradeTest.upgrade();
            cardsToCheck.add(upgradeTest);
        } else if (card instanceof MultiUpgradeCard) {
            for(int i = 0; i < ((MultiUpgradeCard)baseCheck).getUpgrades().size(); ++i) {
                upgradeTest = card.makeCopy();
                ((MultiUpgradeCard)upgradeTest).getUpgrades().get(i).upgrade();
                cardsToCheck.add(upgradeTest);
            }
        } else {
            upgradeCheck = card.makeCopy();
            upgradeCheck.upgrade();
            cardsToCheck.add(upgradeCheck);
        }
        return cardsToCheck.stream().allMatch((c) -> c.baseMagicNumber >= baseCheck.baseMagicNumber);
    }
}
