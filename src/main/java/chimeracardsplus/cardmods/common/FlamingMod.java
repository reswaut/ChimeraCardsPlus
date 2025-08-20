package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.FlameBarrier;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.FlameBarrierPower;
import com.megacrit.cardcrawl.vfx.combat.FlameBarrierEffect;

public class FlamingMod extends AbstractAugmentPlus implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(FlamingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.cost >= -1 && abstractCard.baseBlock >= 4;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block * 0.75F;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (FlameBarrier.ID.equals(card.cardID)) {
            return magic + baseVal(card);
        }
        return magic;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (FlameBarrier.ID.equals(card.cardID)) {
            return;
        }
        if (Settings.FAST_MODE) {
            addToBot(new VFXAction(AbstractDungeon.player, new FlameBarrierEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.1F));
        } else {
            addToBot(new VFXAction(AbstractDungeon.player, new FlameBarrierEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.5F));
        }
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FlameBarrierPower(AbstractDungeon.player, baseVal(card)), baseVal(card)));
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
        return abstractCard.baseBlock / 4;
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
        if (FlameBarrier.ID.equals(card.cardID)) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, String.format(CARD_TEXT[0], DESCRIPTION_KEY));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FlamingMod();
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