package chimeracardsplus.patches.events;

import basemod.ReflectionHacks;
import basemod.ReflectionHacks.RMethod;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.special.RegretfulMod;
import chimeracardsplus.cards.preview.RegretfulPreview;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheMausoleum;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class TheMausoleumPatches {
    private static final String ID = ChimeraCardsPlus.makeID(TheMausoleumPatches.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static int myIndex = 0;
    private static int goldAmt = 0;

    private static int calcGoldAmt() {
        if (AbstractDungeon.ascensionLevel < 15) {
            return 200;
        }
        return 150;
    }

    @SpirePatch(
            clz = TheMausoleum.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class EventInit {
        @SpirePostfixPatch
        public static void Postfix(TheMausoleum __instance) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return;
            }
            RegretfulMod augment = new RegretfulMod();
            __instance.imageEventText.removeDialogOption(__instance.imageEventText.optionList.size() - 1);
            myIndex = __instance.imageEventText.optionList.size();
            if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(augment::canApplyTo)) {
                goldAmt = calcGoldAmt();
                __instance.imageEventText.setDialogOption(OPTIONS[0] + goldAmt + OPTIONS[1], new RegretfulPreview());
            } else {
                __instance.imageEventText.setDialogOption(OPTIONS[2], true);
            }
            __instance.imageEventText.setDialogOption(TheMausoleum.OPTIONS[2]);
        }
    }

    @SpirePatch(
            clz = TheMausoleum.class,
            method = "buttonEffect"
    )
    private static class ButtonLogic {
        @SpirePrefixPatch
        private static SpireReturn<Void> Prefix(TheMausoleum __instance, @ByRef int[] buttonPressed) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return SpireReturn.Continue();
            }
            String screen = ReflectionHacks.getPrivate(__instance, TheMausoleum.class, "screen").toString();
            if (!Objects.equals(screen, "INTRO")) {
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

            if (AbstractDungeon.miscRng.randomBoolean()) {
                AbstractDungeon.player.gainGold(goldAmt);
                CardCrawlGame.sound.play("GOLD_GAIN");
                __instance.showProceedScreen(DESCRIPTIONS[0]);
            } else {
                __instance.showProceedScreen(DESCRIPTIONS[1]);
            }

            try {
                Field field = ReflectionHacks.getCachedField(TheMausoleum.class, "screen");
                RMethod valueOf = ReflectionHacks.privateStaticMethod(field.getType(), "valueOf", String.class);
                field.set(__instance, valueOf.invoke(null, "RESULT"));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return SpireReturn.Return();
        }

        private static void applyModifiers() {
            RegretfulMod augment = new RegretfulMod();
            ArrayList<AbstractCard> applicableCards = AbstractDungeon.player.masterDeck.group.stream().filter(augment::canApplyTo).collect(Collectors.toCollection(ArrayList::new));
            if (applicableCards.isEmpty()) {
                return;
            }
            AbstractCard cardToApply = applicableCards.get(AbstractDungeon.miscRng.random(applicableCards.size() - 1));
            CardModifierManager.addModifier(cardToApply, augment);
            AbstractDungeon.player.bottledCardUpgradeCheck(cardToApply);
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(cardToApply.makeStatEquivalentCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        }
    }
}
