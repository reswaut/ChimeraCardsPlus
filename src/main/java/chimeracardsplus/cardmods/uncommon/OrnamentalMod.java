package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class OrnamentalMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(OrnamentalMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.type == AbstractCard.CardType.ATTACK && c.cost >= -1))
                && characterCheck((p) -> p.hasRelic("Ornamental Fan"));
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
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractRelic relic = AbstractDungeon.player.getRelic("Ornamental Fan");
        if (relic != null && relic.counter == 0) {
            this.addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, 4));
        }
    }

    @Override
    public Color getGlow(AbstractCard card) {
        AbstractRelic relic = AbstractDungeon.player.getRelic("Ornamental Fan");
        if (relic != null && relic.counter == 2) {
            return Color.GOLD;
        }
        return null;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new OrnamentalMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}