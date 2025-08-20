package chimeracardsplus.cardmods.rare;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.green.GrandFinale;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.GrandFinalEffect;

public class GrandMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(GrandMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean modMagic = false;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (cardCheck(card, c -> c.baseMagicNumber >= 1 && doesntDowngradeMagic())) {
            modMagic = true;
        }
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= -1 && (c.baseDamage >= 1 || c.baseBlock >= 1 || c.baseMagicNumber >= 1 && doesntDowngradeMagic()) && !GrandFinale.ID.equals(c.cardID));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage * 4.0F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block * 4.0F : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return modMagic ? magic * 4.0F : magic;
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        boolean ret = AbstractDungeon.player.drawPile.isEmpty();
        if (!ret) {
            card.cantUseMessage = CardCrawlGame.languagePack.getCardStrings("Grand Finale").UPGRADE_DESCRIPTION;
        }
        return ret;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (Settings.FAST_MODE) {
            addToBot(new VFXAction(new GrandFinalEffect(), 0.7F));
        } else {
            addToBot(new VFXAction(new GrandFinalEffect(), 1.0F));
        }
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
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GrandMod();
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