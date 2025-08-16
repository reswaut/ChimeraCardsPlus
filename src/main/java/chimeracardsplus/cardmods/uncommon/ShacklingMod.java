package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.DarkShackles;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType.DEBUFF;

public class ShacklingMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(ShacklingMod.class.getSimpleName());
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
        return cardCheck(card, (c) -> (c.cost >= -1 && (c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL) && doesntUpgradeExhaust() && usesEnemyTargeting()));
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (DarkShackles.ID.equals(card.cardID)) {
            return magic + 5.0F;
        }
        return magic;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature cardTarget, UseCardAction action) {
        if (DarkShackles.ID.equals(card.cardID)) {
            return;
        }
        this.addToBot(new AbstractGameAction() {
            {
                this.actionType = DEBUFF;
            }

            @Override
            public void update() {
                if (cardTarget != null) {
                    if (!cardTarget.hasPower(ArtifactPower.POWER_ID)) {
                        this.addToTop(new ApplyPowerAction(cardTarget, AbstractDungeon.player, new GainStrengthPower(cardTarget, 5), 5, true, AbstractGameAction.AttackEffect.NONE));
                    }
                    this.addToTop(new ApplyPowerAction(cardTarget, AbstractDungeon.player, new StrengthPower(cardTarget, -5), -5, true, AbstractGameAction.AttackEffect.NONE));
                }
                this.isDone = true;
            }
        });
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
        if (DarkShackles.ID.equals(card.cardID)) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ShacklingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}