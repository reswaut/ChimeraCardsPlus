package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.city.Byrd;

public class TalkativeMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(TalkativeMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= -1);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            this.addToBot(new SFXAction("VO_CULTIST_1A"));
        } else if (roll == 1) {
            this.addToBot(new SFXAction("VO_CULTIST_1B"));
        } else {
            this.addToBot(new SFXAction("VO_CULTIST_1C"));
        }
        AbstractDungeon.actionManager.addToBottom(new TalkAction(true, Byrd.DIALOG[0], 1.2F, 1.2F));
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TalkativeMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}