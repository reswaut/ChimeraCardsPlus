package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.damagemods.EnergizedDamage;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RefluxingMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(RefluxingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        DamageModifierManager.addModifier(card, new EnergizedDamage());
        card.cost += 1;
        card.costForTurn = card.cost;
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        List<AbstractDamageModifier> mods = DamageModifierManager.modifiers(card);
        Collection<AbstractDamageModifier> toRemove = mods.stream().filter(m -> m instanceof EnergizedDamage).collect(Collectors.toCollection(() -> new ArrayList<>(1)));
        for (AbstractDamageModifier m : toRemove) {
            DamageModifierManager.removeModifier(card, m);
        }
        DamageModifierManager.addModifier(card, new EnergizedDamage());
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= 0 && doesntUpgradeCost() && c.baseDamage >= 3);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 4.0F / 3.0F;
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
        return new RefluxingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}