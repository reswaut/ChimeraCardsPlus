package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.powers.PoisonPower;

import static chimeracardsplus.util.CardCheckHelpers.hasCardWithKeywordInDeck;

public class ToxicMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(ToxicMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean addedExhaust;

    @Override
    public void onInitialApplication(AbstractCard card) {
        addedExhaust = !card.exhaust;
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> c.cost >= -1 && (c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL) && usesEnemyTargeting() && doesntUpgradeExhaust()) && characterCheck(p -> hasCardWithKeywordInDeck(p, CARD_TEXT[2]));
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
    public void onUse(AbstractCard card, AbstractCreature cardTarget, UseCardAction action) {
        if (cardTarget != null) {
            this.addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    if (cardTarget.hasPower(PoisonPower.POWER_ID)) {
                        cardTarget.getPower(PoisonPower.POWER_ID).atStartOfTurn();
                    }
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ToxicMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}