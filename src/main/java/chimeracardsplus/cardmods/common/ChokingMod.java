package chimeracardsplus.cardmods.common;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.green.Choke;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ChokePower;

public class ChokingMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(ChokingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private int amount;

    public ChokingMod() {
        this(0);
    }

    public ChokingMod(int amount) {
        this.amount = amount;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard copy = makeNewInstance(card);
        if (copy != null) {
            amount = copy.cost * 2;
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 0.80F;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (Choke.ID.equals(card.cardID)) {
            return magic + amount;
        }
        return magic;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        AbstractCard copy = makeNewInstance(abstractCard);
        return copy != null && cardCheck(copy, c -> c.cost >= 1 && c.baseDamage >= 2 && usesEnemyTargeting() && (c.type == CardType.ATTACK || c.type == CardType.SKILL));
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        card.initializeDescription();
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
        if (Choke.ID.equals(card.cardID) || amount <= 0) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, String.format(CARD_TEXT[0], amount));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (Choke.ID.equals(card.cardID) || amount <= 0 || target == null) {
            return;
        }
        addToBot(new ApplyPowerAction(target, AbstractDungeon.player, new ChokePower(target, amount)));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ChokingMod(amount);
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