package chimeracardsplus.patches.events;

import basemod.ReflectionHacks;
import basemod.ReflectionHacks.RMethod;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.rare.LiquidizingMod;
import chimeracardsplus.cards.preview.LiquidizingPreview;
import chimeracardsplus.helpers.Constants;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.Lab;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class LabPatches {
    private static final String ID = ChimeraCardsPlus.makeID(LabPatches.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static int myIndex = 0;
    private static int damageAmt = 0;

    private static int calcHpLoss() {
        if (AbstractDungeon.ascensionLevel < 15) {
            return (int) (AbstractDungeon.player.maxHealth * 0.08F);
        }
        return (int) (AbstractDungeon.player.maxHealth * 0.1F);
    }

    @SpirePatch(
            clz = Lab.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class EventInit {
        @SpirePostfixPatch
        public static void Postfix(Lab __instance) {
            if (!ChimeraCardsPlus.configs.enableEventAddons()) {
                return;
            }
            LiquidizingMod augment = new LiquidizingMod();
            myIndex = __instance.imageEventText.optionList.size();
            if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(augment::canApplyTo)) {
                damageAmt = calcHpLoss();
                __instance.imageEventText.setDialogOption(OPTIONS[0] + damageAmt + OPTIONS[1], new LiquidizingPreview());
            } else {
                __instance.imageEventText.setDialogOption(OPTIONS[2], true);
            }
        }
    }

    @SpirePatch(
            clz = Lab.class,
            method = "buttonEffect"
    )
    private static class ButtonLogic {
        @SpirePrefixPatch
        private static SpireReturn<Void> Prefix(Lab __instance, @ByRef int[] buttonPressed) {
            if (!ChimeraCardsPlus.configs.enableEventAddons()) {
                return SpireReturn.Continue();
            }
            String screen = ReflectionHacks.getPrivate(__instance, Lab.class, "screen").toString();
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
            CardCrawlGame.sound.play("BLOOD_SPLAT");
            AbstractDungeon.player.damage(new DamageInfo(null, damageAmt, DamageType.HP_LOSS));

            __instance.showProceedScreen(DESCRIPTIONS[0]);
            try {
                Field field = ReflectionHacks.getCachedField(Lab.class, "screen");
                RMethod valueOf = ReflectionHacks.privateStaticMethod(field.getType(), "valueOf", String.class);
                field.set(__instance, valueOf.invoke(null, "COMPLETE"));
            } catch (IllegalAccessException e) {
                ChimeraCardsPlus.logger.error("Failed to set event screen.", e);
            }
            return SpireReturn.Return();
        }

        private static void applyModifiers() {
            LiquidizingMod augment = new LiquidizingMod();
            ArrayList<AbstractCard> applicableCards = AbstractDungeon.player.masterDeck.group.stream().filter(augment::canApplyTo).collect(Collectors.toCollection(() -> new ArrayList<>(Constants.DEFAULT_LIST_SIZE)));
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
