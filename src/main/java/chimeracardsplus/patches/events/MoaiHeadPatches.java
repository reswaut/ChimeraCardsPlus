package chimeracardsplus.patches.events;


import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.special.HesitantMod;
import chimeracardsplus.cards.preview.HesitantPreview;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.MoaiHead;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static basemod.helpers.CardModifierManager.addModifier;

public class MoaiHeadPatches {
    private static final String ID = ChimeraCardsPlus.makeID(MoaiHeadPatches.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static int myIndex;
    private static int hpAmt = 0;

    private static int calcHp() {
        if (AbstractDungeon.ascensionLevel < 15) {
            return 10;
        } else {
            return 7;
        }
    }

    @SpirePatch(
            clz = MoaiHead.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class EventInit {
        @SpirePostfixPatch
        public static void Postfix(MoaiHead __instance) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return;
            }
            HesitantMod augment = new HesitantMod();
            __instance.imageEventText.removeDialogOption(__instance.imageEventText.optionList.size() - 1);
            myIndex = __instance.imageEventText.optionList.size();
            if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(augment::canApplyTo)) {
                hpAmt = calcHp();
                __instance.imageEventText.setDialogOption(OPTIONS[0] + hpAmt + OPTIONS[1], new HesitantPreview());
            } else {
                __instance.imageEventText.setDialogOption(OPTIONS[2], true);
            }
            __instance.imageEventText.setDialogOption(MoaiHead.OPTIONS[4]);
        }
    }

    @SpirePatch(
            clz = MoaiHead.class,
            method = "buttonEffect"
    )
    private static class ButtonLogic {
        @SpirePrefixPatch
        private static SpireReturn<Void> Prefix(MoaiHead __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return SpireReturn.Continue();
            }
            if (___screenNum[0] != 0) {
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
            AbstractDungeon.player.increaseMaxHp(hpAmt, true);

            __instance.showProceedScreen(DESCRIPTIONS[0]);
            ___screenNum[0] = 1;
            return SpireReturn.Return();
        }

        private static void applyModifiers() {
            HesitantMod augment = new HesitantMod();
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
            AbstractCard cardToApply = applicableCards.get(AbstractDungeon.miscRng.random(applicableCards.size() - 1));
            addModifier(cardToApply, augment);
            AbstractDungeon.player.bottledCardUpgradeCheck(cardToApply);
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(cardToApply.makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
        }
    }
}
