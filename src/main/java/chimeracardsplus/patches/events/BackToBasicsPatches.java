package chimeracardsplus.patches.events;

import basemod.ReflectionHacks;
import basemod.ReflectionHacks.RMethod;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.rare.StrategicMod;
import chimeracardsplus.cards.preview.StrategicPreview;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.BackToBasics;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;

import java.lang.reflect.Field;
import java.util.Objects;

public class BackToBasicsPatches {
    private static final String ID = ChimeraCardsPlus.makeID(BackToBasicsPatches.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static int myIndex = 0;

    @SpirePatch(
            clz = BackToBasics.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class EventInit {
        @SpirePostfixPatch
        public static void Postfix(BackToBasics __instance) {
            if (!ChimeraCardsPlus.configs.enableEventAddons()) {
                return;
            }
            myIndex = __instance.imageEventText.optionList.size();
            __instance.imageEventText.setDialogOption(OPTIONS[0], new StrategicPreview());
        }
    }

    @SpirePatch(
            clz = BackToBasics.class,
            method = "buttonEffect"
    )
    private static class ButtonLogic {
        @SpirePrefixPatch
        private static SpireReturn<Void> Prefix(BackToBasics __instance, @ByRef int[] buttonPressed) {
            if (!ChimeraCardsPlus.configs.enableEventAddons()) {
                return SpireReturn.Continue();
            }
            String screen = ReflectionHacks.getPrivate(__instance, BackToBasics.class, "screen").toString();
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
                Field field = ReflectionHacks.getCachedField(BackToBasics.class, "screen");
                RMethod valueOf = ReflectionHacks.privateStaticMethod(field.getType(), "valueOf", String.class);
                field.set(__instance, valueOf.invoke(null, "COMPLETE"));
            } catch (IllegalAccessException e) {
                ChimeraCardsPlus.logger.error("Failed to set event screen.", e);
            }
            return SpireReturn.Return();
        }

        private static void applyModifiers() {
            StrategicMod augment = new StrategicMod();
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.hasTag(CardTags.STARTER_DEFEND) && augment.canApplyTo(c)) {
                    CardModifierManager.addModifier(c, new StrategicMod());
                    AbstractDungeon.player.bottledCardUpgradeCheck(c);
                    AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), MathUtils.random(0.1F, 0.9F) * Settings.WIDTH, MathUtils.random(0.2F, 0.8F) * Settings.HEIGHT));
                }
            }
        }
    }
}
