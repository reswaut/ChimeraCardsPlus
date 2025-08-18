package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.damagemods.SunderDamage;
import com.evacipated.cardcrawl.mod.stslib.damagemods.AbstractDamageModifier;
import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.blue.Sunder;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SplitMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(SplitMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= 1 && doesntUpgradeCost() && c.baseDamage >= 1 && c.type == CardType.ATTACK);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost += 1;
        card.costForTurn = card.cost;
        DamageModifierManager.addModifier(card, new SunderDamage(card.cost));
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        List<AbstractDamageModifier> mods = DamageModifierManager.modifiers(card);
        Collection<AbstractDamageModifier> toRemove = mods.stream().filter(m -> m instanceof SunderDamage).collect(Collectors.toCollection(() -> new ArrayList<>(1)));
        for (AbstractDamageModifier m : toRemove) {
            DamageModifierManager.removeModifier(card, m);
        }
        DamageModifierManager.addModifier(card, new SunderDamage(card.cost));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 1.25F;
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
        int cost = card.cost;
        if (Sunder.ID.equals(card.cardID)) {
            cost += 3;
        }
        if (cost <= 0) {
            return rawDescription;
        }
        String text = cost <= 3 ? CARD_TEXT[cost - 1] : String.format(CARD_TEXT[3], cost);
        if (Sunder.ID.equals(card.cardID)) {
            return rawDescription.replace(CARD_TEXT[4], text);
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SplitMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}