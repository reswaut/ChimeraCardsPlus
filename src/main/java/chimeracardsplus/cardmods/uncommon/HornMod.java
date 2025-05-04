package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.damagemods.GremlinHornDamage;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import java.util.ArrayList;
import java.util.List;

public class HornMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(HornMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.cost >= -1 && c.type == AbstractCard.CardType.ATTACK));
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        DamageModifierManager.addModifier(card, new GremlinHornDamage());
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        List<AbstractDamageModifier> mods = DamageModifierManager.modifiers(card);
        List<AbstractDamageModifier> toRemove = new ArrayList<>();
        for (AbstractDamageModifier m : mods) {
            if (m instanceof GremlinHornDamage) {
                toRemove.add(m);
            }
        }
        for (AbstractDamageModifier m : toRemove) {
            DamageModifierManager.removeModifier(card, m);
        }
        DamageModifierManager.addModifier(card, new GremlinHornDamage());
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
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HornMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}