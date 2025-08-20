package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.green.CripplingPoison;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class CripplingMod extends AbstractAugmentPlus implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(CripplingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private boolean addedExhaust = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        addedExhaust = !card.exhaust;
        card.exhaust = true;
        card.cost += 1;
        card.costForTurn = card.cost;
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (!card.exhaust) {
            addedExhaust = true;
            card.exhaust = true;
        }
        card.initializeDescription();
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= 0 && doesntUpgradeCost()
                && (c.type == CardType.ATTACK || c.type == CardType.SKILL));
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (CripplingPoison.ID.equals(card.cardID)) {
            return magic + baseVal(card);
        }
        return magic;
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard abstractCard) {
        return baseVal(abstractCard);
    }

    @Override
    public int baseVal(AbstractCard abstractCard) {
        return 3 + 2 * getEffectiveUpgrades(abstractCard);
    }

    @Override
    public boolean modified(AbstractCard abstractCard) {
        return false;
    }

    @Override
    public boolean upgraded(AbstractCard abstractCard) {
        return abstractCard.timesUpgraded != 0 || abstractCard.upgraded;
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
        if (CripplingPoison.ID.equals(card.cardID)) {
            return insertAfterText(rawDescription, String.format(CARD_TEXT[2]));
        }
        return insertAfterText(rawDescription, String.format(addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1], DESCRIPTION_KEY));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!CripplingPoison.ID.equals(card.cardID)) {
                addToBot(new ApplyPowerAction(mo, AbstractDungeon.player, new PoisonPower(mo, AbstractDungeon.player, baseVal(card)), baseVal(card), true, AttackEffect.NONE));
            }
            addToBot(new ApplyPowerAction(mo, AbstractDungeon.player, new WeakPower(mo, 1, false), 1, true, AttackEffect.NONE));
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new CripplingMod();
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