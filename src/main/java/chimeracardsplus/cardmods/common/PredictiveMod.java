package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import CardAugments.util.FormatHelper;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.special.PredictedMod;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.NightmarePower;

public class PredictiveMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(PredictiveMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean inherentHack = true;

    public PredictiveMod() {
        priority = -100;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        inherentHack = true;
        AbstractCard preview = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(preview, new PredictedMod());
        MultiCardPreview.add(card, preview);
        InterceptUseField.interceptUse.set(card, Boolean.TRUE);
        if (card.type == CardType.POWER) {
            card.exhaust = true;
        }
        card.target = CardTarget.NONE;
        if (card.type != CardType.SKILL) {
            card.type = CardType.SKILL;
            PortraitHelper.setMaskedPortrait(card);
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return -1.0F;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return -1.0F;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return -1.0F;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= -1 && isNormalCard(c) && noShenanigans(c));
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        String frontText = "";
        if (card.isInnate) {
            frontText += CARD_TEXT[1];
        }
        if (card.selfRetain) {
            frontText += CARD_TEXT[2];
        }
        if (card.isEthereal) {
            frontText += CARD_TEXT[3];
        }
        return (frontText.isEmpty() ? "" : frontText + CARD_TEXT[5])
                + String.format(CARD_TEXT[0], FormatHelper.prefixWords(card.name, "*"))
                + (card.exhaust ? CARD_TEXT[4] : "");
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        for (AbstractCard o : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(o, PredictedMod.ID)) {
                o.upgrade();
                o.initializeDescription();
            }
        }
        card.initializeDescription();
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractCard preview = MultiCardPreview.multiCardPreview.get(card).stream().filter(o -> CardModifierManager.hasModifier(o, PredictedMod.ID)).findFirst().orElse(null);

        if (preview != null) {
            AbstractCard copy = preview.makeStatEquivalentCopy();
            if (copy.cost == -1) {
                PredictedMod mod = (PredictedMod) CardModifierManager.getModifiers(copy, PredictedMod.ID).get(0);
                mod.setEnergyOnUse(copy, card.energyOnUse);
                if (!card.freeToPlayOnce) {
                    AbstractDungeon.player.energy.use(card.energyOnUse);
                }
            }
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new NightmarePower(AbstractDungeon.player, 1, copy)));
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PredictiveMod();
    }

    @Override
    public boolean isInherent(AbstractCard card) {
        return inherentHack;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}