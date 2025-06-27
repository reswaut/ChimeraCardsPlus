package chimeracardsplus.patches.events;


import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.special.DecayingMod;
import chimeracardsplus.cards.preview.DecayingPreview;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.ForgottenAltar;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

import static basemod.helpers.CardModifierManager.addModifier;

public class ForgottenAlterPatches {
    private static final String ID = ChimeraCardsPlus.makeID(ForgottenAlterPatches.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static int myIndex;
    private static int healAmt = 0;

    private static int calcHeal() {
        if (AbstractDungeon.ascensionLevel < 15) {
            return (int) (AbstractDungeon.player.maxHealth * 0.25F);
        } else {
            return (int) (AbstractDungeon.player.maxHealth * 0.15F);
        }
    }

    @SpirePatch(
            clz = ForgottenAltar.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class EventInit {
        @SpirePostfixPatch
        public static void Postfix(ForgottenAltar __instance) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return;
            }
            DecayingMod augment = new DecayingMod();
            myIndex = __instance.imageEventText.optionList.size();
            if (AbstractDungeon.player.masterDeck.group.stream().filter(augment::canApplyTo).count() >= 3) {
                healAmt = calcHeal();
                __instance.imageEventText.setDialogOption(OPTIONS[0] + healAmt + OPTIONS[1], new DecayingPreview());
            } else {
                __instance.imageEventText.setDialogOption(OPTIONS[2], true);
            }
        }
    }

    @SpirePatch(
            clz = ForgottenAltar.class,
            method = "buttonEffect"
    )
    private static class ButtonLogic {
        @SpirePrefixPatch
        private static SpireReturn<Void> Prefix(ForgottenAltar __instance, @ByRef int[] buttonPressed, int ___screenNum) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return SpireReturn.Continue();
            }
            if (___screenNum != 0) {
                return SpireReturn.Continue();
            }
            if (buttonPressed[0] < myIndex) {
                return SpireReturn.Continue();
            }
            if (buttonPressed[0] > myIndex) {
                --buttonPressed[0];
                return SpireReturn.Continue();
            }

            applyModifiers();
            AbstractDungeon.player.heal(healAmt, true);

            __instance.showProceedScreen(DESCRIPTIONS[0]);
            return SpireReturn.Return();
        }

        private static void applyModifiers() {
            DecayingMod augment = new DecayingMod();
            ArrayList<AbstractCard> applicableCards = AbstractDungeon.player.masterDeck.group.stream().filter(augment::canApplyTo).collect(Collectors.toCollection(ArrayList::new));
            if (applicableCards.isEmpty()) {
                return;
            }
            if (applicableCards.size() == 1) {
                addModifier(applicableCards.get(0), augment);
                AbstractDungeon.player.bottledCardUpgradeCheck(applicableCards.get(0));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(applicableCards.get(0).makeStatEquivalentCopy()));
                return;
            }
            Collections.shuffle(applicableCards, new Random(AbstractDungeon.miscRng.randomLong()));
            addModifier(applicableCards.get(0), augment);
            AbstractDungeon.player.bottledCardUpgradeCheck(applicableCards.get(0));
            addModifier(applicableCards.get(1), new DecayingMod());
            AbstractDungeon.player.bottledCardUpgradeCheck(applicableCards.get(1));
            if (applicableCards.size() <= 2) {
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(applicableCards.get(0).makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F - 190.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(applicableCards.get(1).makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F + 190.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
                return;
            }
            addModifier(applicableCards.get(2), new DecayingMod());
            AbstractDungeon.player.bottledCardUpgradeCheck(applicableCards.get(2));
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(applicableCards.get(0).makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F - 380.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(applicableCards.get(1).makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(applicableCards.get(2).makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F + 380.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
        }
    }
}
