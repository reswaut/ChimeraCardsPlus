package chimeracardsplus.cardmods.common;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;

public class IllusoryMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(IllusoryMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean addedExhaust = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        addedExhaust = !card.exhaust;
        card.exhaust = true;
        card.cost += 1;
        card.costForTurn = card.cost;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= 0 && c.baseBlock >= 1 && doesntUpgradeCost() && doesntUpgradeExhaust() && (c.type == CardType.ATTACK || c.type == CardType.SKILL)) && characterCheck(p -> hasCardWithKeywordInDeck(p, CARD_TEXT[2]));
    }

    @Override
    public float modifyBlock(float block, AbstractCard card) {
        int poison = 0;
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo.isDeadOrEscaped()) {
                continue;
            }
            AbstractPower power = mo.getPower(PoisonPower.POWER_ID);
            if (power != null) {
                poison += power.amount;
            }
        }
        return block + poison;
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new IllusoryMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.NORMAL;
    }
}
