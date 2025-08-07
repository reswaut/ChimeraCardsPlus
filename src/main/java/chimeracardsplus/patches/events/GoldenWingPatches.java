package chimeracardsplus.patches.events;

import basemod.ReflectionHacks;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.rare.UnawakenedMod;
import chimeracardsplus.cards.preview.UnawakenedPreview;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.events.exordium.GoldenWing;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.Objects;

import static basemod.ReflectionHacks.getCachedField;
import static basemod.helpers.CardModifierManager.addModifier;

public class GoldenWingPatches {
    private static final String ID = ChimeraCardsPlus.makeID(GoldenWingPatches.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static int myIndex;
    private static boolean choseMyOption = false;
    private static int cardsToChoose = 3;

    @SpirePatch(
            clz = GoldenWing.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class EventInit {
        @SpirePostfixPatch
        public static void Postfix(GoldenWing __instance) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return;
            }
            UnawakenedMod augment = new UnawakenedMod();
            __instance.imageEventText.removeDialogOption(__instance.imageEventText.optionList.size() - 1);
            myIndex = __instance.imageEventText.optionList.size();
            if (AbstractDungeon.player.masterDeck.group.stream().filter(augment::canApplyTo).count() >= 3) {
                __instance.imageEventText.setDialogOption(OPTIONS[0], new UnawakenedPreview());
            } else {
                __instance.imageEventText.setDialogOption(OPTIONS[1], true);
            }
            __instance.imageEventText.setDialogOption(GoldenWing.OPTIONS[7]);
        }
    }

    @SpirePatch(
            clz = GoldenWing.class,
            method = "buttonEffect"
    )
    private static class ButtonLogic {
        @SpirePrefixPatch
        private static SpireReturn<Void> Prefix(GoldenWing __instance, @ByRef int[] buttonPressed) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return SpireReturn.Continue();
            }
            String screen = ReflectionHacks.getPrivate(__instance, GoldenWing.class, "screen").toString();
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

            __instance.showProceedScreen(DESCRIPTIONS[0]);
            try {
                Field field = getCachedField(GoldenWing.class, "screen");
                ReflectionHacks.RMethod valueOf = ReflectionHacks.privateStaticMethod(field.getType(), "valueOf", String.class);
                field.set(__instance, valueOf.invoke(null, "MAP"));
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            return SpireReturn.Return();
        }

        private static void applyModifiers() {
            UnawakenedMod augment = new UnawakenedMod();
            CardGroup validCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (augment.canApplyTo(c)) {
                    validCards.addToTop(c);
                }
            }
            if (cardsToChoose == 0) {
                return;
            }
            choseMyOption = true;
            cardsToChoose = Math.min(3, validCards.size());
            AbstractDungeon.gridSelectScreen.open(validCards, cardsToChoose, OPTIONS[2], false, false, false, false);
        }

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(GoldenWing __instance) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return;
            }
            __instance.imageEventText.removeDialogOption(1);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GenericEventDialog.class, "updateDialogOption");
                int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{tmp[0] + 1, tmp[1] + 1, tmp[2] + 1};
            }
        }
    }

    @SpirePatch(
            clz = GoldenWing.class,
            method = "purgeLogic"
    )
    private static class AugmentLogic {
        @SpirePrefixPatch
        private static SpireReturn<Void> Prefix(GoldenWing __instance) {
            if (!ChimeraCardsPlus.enableEventAddons()) {
                return SpireReturn.Continue();
            }
            if (!choseMyOption) {
                return SpireReturn.Continue();
            }
            if (!AbstractDungeon.isScreenUp && AbstractDungeon.gridSelectScreen.selectedCards.size() >= cardsToChoose) {
                AbstractCard card1 = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                addModifier(card1, new UnawakenedMod());
                AbstractDungeon.player.bottledCardUpgradeCheck(card1);
                if (cardsToChoose == 1) {
                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card1.makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                } else {
                    AbstractCard card2 = AbstractDungeon.gridSelectScreen.selectedCards.get(1);
                    addModifier(card2, new UnawakenedMod());
                    AbstractDungeon.player.bottledCardUpgradeCheck(card2);
                    if (cardsToChoose == 2) {
                        AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card1.makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F - 190.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card2.makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F + 190.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
                    } else {
                        AbstractCard card3 = AbstractDungeon.gridSelectScreen.selectedCards.get(2);
                        addModifier(card3, new UnawakenedMod());
                        AbstractDungeon.player.bottledCardUpgradeCheck(card3);
                        AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card1.makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F - 380.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card2.makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
                        AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(card3.makeStatEquivalentCopy(), (float) Settings.WIDTH / 2.0F + 380.0F * Settings.scale, (float) Settings.HEIGHT / 2.0F));
                    }
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                choseMyOption = false;
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
