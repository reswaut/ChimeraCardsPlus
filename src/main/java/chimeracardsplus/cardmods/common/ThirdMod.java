package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.ScryAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.purple.CutThroughFate;
import com.megacrit.cardcrawl.cards.purple.JustLucky;
import com.megacrit.cardcrawl.cards.purple.ThirdEye;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.ThirdEyeEffect;

import java.util.stream.Stream;

public class ThirdMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(ThirdMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return (abstractCard.baseDamage >= 2 || abstractCard.baseBlock >= 2) && abstractCard.cost >= -1 && (abstractCard.type == CardType.ATTACK || abstractCard.type == CardType.SKILL);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage * 0.75F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block * 0.75F : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (Stream.of(CutThroughFate.ID, JustLucky.ID, ThirdEye.ID).anyMatch(s -> s.equals(card.cardID))) {
            return magic + 3.0F;
        }
        return magic;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (Stream.of(CutThroughFate.ID, JustLucky.ID, ThirdEye.ID).anyMatch(s -> s.equals(card.cardID))) {
            return;
        }
        addToBot(new VFXAction(new ThirdEyeEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY)));
        addToBot(new ScryAction(3));
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
        if (Stream.of(CutThroughFate.ID, JustLucky.ID, ThirdEye.ID).anyMatch(s -> s.equals(card.cardID))) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ThirdMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}