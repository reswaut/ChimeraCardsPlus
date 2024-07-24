package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class MaskedMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(MaskedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean used = false;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= -1) && characterCheck((p) -> p.hasRelic("Red Mask"));
    }

    @Override
    public boolean onBattleStart(AbstractCard card) {
        used = false;
        return false;
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
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public Color getGlow(AbstractCard card) {
        return (!used && GameActionManager.turn <= 1) ? Color.GOLD : null;
    }

    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!used && GameActionManager.turn <= 1) {
            AbstractRelic relic = AbstractDungeon.player.getRelic("Red Mask");
            if (relic != null) {
                relic.flash();
            }
            for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (relic != null) {
                    this.addToBot(new RelicAboveCreatureAction(mo, relic));
                }
                this.addToBot(new ApplyPowerAction(mo, AbstractDungeon.player, new WeakPower(mo, 1, false), 1, true, AbstractGameAction.AttackEffect.NONE));
            }
            used = true;
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MaskedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}