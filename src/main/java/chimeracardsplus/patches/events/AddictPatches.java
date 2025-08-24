package chimeracardsplus.patches.events;


import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.special.DisgracefulMod;
import chimeracardsplus.cards.preview.DisgracefulPreview;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Addict;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class AddictPatches {
    private static final String ID = ChimeraCardsPlus.makeID(AddictPatches.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static int myIndex = 0;

    @SpirePatch(
            clz = Addict.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class EventInit {
        @SpirePostfixPatch
        public static void Postfix(Addict __instance) {
            if (!ChimeraCardsPlus.configs.enableEventAddons()) {
                return;
            }
            DisgracefulMod augment = new DisgracefulMod();
            __instance.imageEventText.removeDialogOption(__instance.imageEventText.optionList.size() - 1);
            myIndex = __instance.imageEventText.optionList.size();
            if (AbstractDungeon.player.masterDeck.group.stream().filter(c -> augment.canApplyTo(c) && c.type == CardType.ATTACK).count() >= 2L) {
                __instance.imageEventText.setDialogOption(OPTIONS[0], new DisgracefulPreview());
            } else {
                __instance.imageEventText.setDialogOption(OPTIONS[1], true);
            }
            __instance.imageEventText.setDialogOption(Addict.OPTIONS[5]);
        }
    }

    @SpirePatch(
            clz = Addict.class,
            method = "buttonEffect"
    )
    private static class ButtonLogic {
        @SpirePrefixPatch
        private static SpireReturn<Void> Prefix(Addict __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum, float ___drawX, float ___drawY) {
            if (!ChimeraCardsPlus.configs.enableEventAddons()) {
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

            AbstractRelic relic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
            applyModifiers();
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(___drawX, ___drawY, relic);

            __instance.showProceedScreen(DESCRIPTIONS[0]);
            ___screenNum[0] = 1;
            return SpireReturn.Return();
        }

        private static void applyModifiers() {
            DisgracefulMod augment = new DisgracefulMod();
            ArrayList<AbstractCard> applicableCards = AbstractDungeon.player.masterDeck.group.stream().filter(c -> augment.canApplyTo(c) && c.type == CardType.ATTACK).collect(Collectors.toCollection(ArrayList::new));
            if (applicableCards.isEmpty()) {
                return;
            }
            if (applicableCards.size() == 1) {
                CardModifierManager.addModifier(applicableCards.get(0), augment);
                AbstractDungeon.player.bottledCardUpgradeCheck(applicableCards.get(0));
                AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(applicableCards.get(0).makeStatEquivalentCopy()));
                return;
            }
            Collections.shuffle(applicableCards, new Random(AbstractDungeon.miscRng.randomLong()));
            CardModifierManager.addModifier(applicableCards.get(0), augment);
            CardModifierManager.addModifier(applicableCards.get(1), new DisgracefulMod());
            AbstractDungeon.player.bottledCardUpgradeCheck(applicableCards.get(0));
            AbstractDungeon.player.bottledCardUpgradeCheck(applicableCards.get(1));
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(applicableCards.get(0).makeStatEquivalentCopy(), Settings.WIDTH / 2.0F - 190.0F * Settings.scale, Settings.HEIGHT / 2.0F));
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(applicableCards.get(1).makeStatEquivalentCopy(), Settings.WIDTH / 2.0F + 190.0F * Settings.scale, Settings.HEIGHT / 2.0F));
        }
    }
}
